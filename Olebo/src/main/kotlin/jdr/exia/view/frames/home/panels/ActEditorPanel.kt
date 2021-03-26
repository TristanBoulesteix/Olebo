package jdr.exia.view.frames.home.panels

import jdr.exia.localization.*
import jdr.exia.model.act.Act
import jdr.exia.model.dao.getIcon
import jdr.exia.view.utils.*
import jdr.exia.view.utils.components.templates.ItemPanel
import jdr.exia.view.utils.components.templates.ManagerAction
import jdr.exia.view.utils.components.templates.PlaceholderTextField
import jdr.exia.view.utils.components.templates.SelectorPanel
import jdr.exia.viewModel.ActCreatorManager
import jdr.exia.viewModel.HomeManager
import jdr.exia.viewModel.getArrayOfPairs
import java.awt.*
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel

class ActEditorPanel(homeManager: HomeManager, act: Act? = null) : HomePanel() {
    private val manager = ActCreatorManager(homeManager)

    private val builder = SelectorPanel.PairArrayBuilder { manager.tempScenes.getArrayOfPairs() }

    private val selectorPanel: SceneSelectorPanel

    private val nameField = PlaceholderTextField(Strings[STR_NAME])

    private var modified = false

    init {
        this.layout = BorderLayout()

        JPanel().applyAndAddTo(this, BorderLayout.NORTH) {
            this.border = BorderFactory.createEmptyBorder(15, 10, 15, 10)
            this.layout = GridBagLayout()

            this.add(nameField, gridBagConstraintsOf(weightx = 1.0, fill = GridBagConstraints.BOTH))

            this.background = BACKGROUND_COLOR_ORANGE
        }

        act?.let {
            this.nameField.text = it.name
            this.manager.updateAct(it.scenes.toMutableList(), it.id.value)
        }

        selectorPanel = SceneSelectorPanel()

        this.add(selectorPanel, BorderLayout.CENTER)

        JPanel().applyAndAddTo(this, BorderLayout.SOUTH) {
            this.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)
            this.layout = GridLayout(1, 2, 10, 15)
            this.background = BACKGROUND_COLOR_LIGHT_BLUE

            JButton(Strings[STR_CONFIRM]).applyAndAddTo(this) {
                this.addActionListener {
                    if (
                        nameField.text.isNotEmpty()
                        && manager.tempScenes.isNotEmpty()
                        && manager.saveAct(nameField.text)
                    ) {
                        homeManager.goHome()
                    } else {
                        showMessage(
                            if (manager.tempScenes.isEmpty()) Strings[ST_ACT_WITHOUT_SCENE] else Strings[ST_ACT_ALREADY_EXISTS],
                            windowAncestor,
                            MessageType.WARNING
                        )
                    }
                }
                this.border = BORDER_BUTTONS
            }

            JButton(Strings[STR_CANCEL]).applyAndAddTo(this) {
                this.addActionListener {
                    if (modified) {
                        showConfirmMessage(
                            windowAncestor,
                            Strings[ST_CANCEL_WILL_ERASE_CHANGES],
                            Strings[STR_WARNING]
                        ) {
                            homeManager.goHome()
                        }
                    } else homeManager.goHome()
                }
                this.border = BORDER_BUTTONS
            }
        }
    }

    override fun reload() = this.selectorPanel.reload()

    private fun ManagerAction.triggerModify(): ManagerAction = {
        modified = true
        this(it)
    }

    /**
     * This panel contains a JScrollpane which show the list of scenes for the current act in creation
     */
    inner class SceneSelectorPanel : SelectorPanel(builder) {
        override fun builder(id: Int, name: String): ItemPanel {
            return ScenePanel(id, name)
        }

        init {
            JPanel().applyAndAddTo(this, BorderLayout.NORTH) {
                this.layout = GridBagLayout()

                ItemPanel(0, Strings[STR_SCENES]).applyAndAddTo(
                    this,
                    gridBagConstraintsOf(fill = GridBagConstraints.BOTH, weightx = 1.0)
                ) {
                    this.border = BorderFactory.createMatteBorder(2, 2, 0, 2, Color.BLACK)
                    this.add(
                        SquareLabel(
                            getIcon("create_icon", manager.javaClass),
                            manager::createNewScene.triggerModify()
                        )
                    )

                }
            }
            this.reload()
        }

        /**
         * Display a scene and its options
         */
        private inner class ScenePanel(id: Int, name: String) : ItemPanel(id, name) {
            init {
                this.add(SquareLabel(getIcon("edit_icon", manager.javaClass), manager::updateNewScene.triggerModify()))
                this.add(
                    SquareLabel(
                        getIcon("delete_icon", manager.javaClass),
                        manager::deleteNewScene.triggerModify()
                    )
                )
            }
        }
    }
}
