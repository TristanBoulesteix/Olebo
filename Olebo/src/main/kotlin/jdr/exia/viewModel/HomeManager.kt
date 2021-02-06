package jdr.exia.viewModel

import jdr.exia.localization.STR_ERROR
import jdr.exia.localization.STR_LOADING
import jdr.exia.localization.Strings
import jdr.exia.model.dao.DAO
import jdr.exia.view.frames.home.HomeFrame
import jdr.exia.view.frames.home.panels.ActEditorPanel
import jdr.exia.view.frames.home.panels.ActsPanel
import jdr.exia.view.frames.home.panels.BlueprintEditorPanel
import jdr.exia.view.frames.rpg.MasterFrame
import jdr.exia.view.utils.showPopup
import jdr.exia.viewModel.observer.Action
import jdr.exia.viewModel.observer.Observable
import jdr.exia.viewModel.observer.Observer
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import javax.swing.JOptionPane

class HomeManager : Observable {
    override var observer: Observer? = null

    fun goHome() = notifyObserver(Action.Switch(ActsPanel(this)))

    /**
     * Start an act
     *
     * @param id The id of the act to launch.
     */
    fun launchAct(id: Int) = GlobalScope.launch {
        notifyObserver(Action.Dispose)

        val popup = withContext(Dispatchers.Main) {
            val loadingString = Strings[STR_LOADING]

            JOptionPane(
                "$loadingString...",
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                emptyArray()
            ).run {
                this.createDialog(parent, loadingString)
            }
        }

        val job = launch(Dispatchers.Swing) {
            withTimeout(120_000) {
                try {
                    ViewManager.initializeAct(id)
                    yield()
                    popup.dispose()
                    MasterFrame.requestFocus()
                } catch (e: TimeoutCancellationException) {
                    popup.dispose()
                    showPopup("${Strings[STR_ERROR]}: ${e.message}", null, true)
                    HomeFrame().isVisible = true
                }
            }
        }

        if (job.isActive)
            popup.isVisible = true
    }

    /**
     * Show elements
     */
    fun openObjectEditorFrame() = notifyObserver(Action.Switch(BlueprintEditorPanel(this)))

    /**
     * Show JDialog to create a new act.
     */
    fun openActCreatorFrame() = notifyObserver(Action.Switch(ActEditorPanel(this)))

    /**
     * Show JDialog to update an act.
     *
     * @param id The id of the act to update
     */
    fun updateAct(id: Int) = notifyObserver(Action.Switch(ActEditorPanel(this, DAO.getActWithId(id))))

    /**
     * Show JDialog to delete an act.
     *
     * @param id The id of the act to delete
     */
    fun deleteAct(id: Int) {
        DAO.deleteEntity(DAO.getActWithId(id))
        notifyObserver(Action.Reload)
    }
}