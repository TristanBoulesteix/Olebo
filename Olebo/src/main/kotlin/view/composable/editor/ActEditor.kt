@file:Suppress("FunctionName")

package jdr.exia.view.composable.editor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jdr.exia.localization.*
import jdr.exia.model.act.Act
import jdr.exia.model.act.isValidAndEqualTo
import jdr.exia.model.tools.withSetter
import jdr.exia.model.type.imageFromFile
import jdr.exia.model.type.imageFromIconRes
import jdr.exia.view.WindowStateManager
import jdr.exia.view.element.*
import jdr.exia.view.element.builder.ImageButtonBuilder
import jdr.exia.view.tools.*
import jdr.exia.view.ui.blue
import jdr.exia.view.ui.roundedShape
import jdr.exia.viewModel.ActEditorViewModel
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import jdr.exia.model.type.Image as Img

@Composable
fun ActEditorView(act: Act? = null, onDone: () -> Unit) = Column {
    val viewModel = remember { ActEditorViewModel(act) }

    HeaderRow {
        val roundedShape = remember { RoundedCornerShape(25) }

        BasicTextField(
            value = viewModel.actName,
            onValueChange = { viewModel.actName = it },
            modifier = Modifier.fillMaxWidth().clip(roundedShape).background(Color.White)
                .border(BorderStroke(2.dp, Color.Black), roundedShape).padding(10.dp),
            singleLine = true,
            decorationBox = { composableContent ->
                if (viewModel.actName.isEmpty()) Text(text = act?.name ?: StringLocale[STR_INSERT_ACT_NAME])
                composableContent()
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(blue).padding(15.dp)) {
        val headerScrollState = rememberScrollState()

        val contentModifier = remember {
            Modifier.padding(bottom = 20.dp, end = 20.dp, start = 20.dp)
                .background(Color.White)
                .weight(1f)
                .fillMaxSize()
                .border(BorderBuilder.defaultBorder)
        }

        val (sceneInCreation, setSceneInCreation) = remember { mutableStateOf<Act.SceneData?>(null) withSetter { newValue -> if (newValue != null) viewModel.onEditDone() } }

        Box(
            modifier = Modifier.padding(top = 20.dp, end = 20.dp, start = 20.dp)
                .background(Color.White)
                .fillMaxWidth()
                .border(BorderBuilder.defaultBorder)
        ) {
            Box(modifier = Modifier.verticalScroll(headerScrollState).fillMaxSize()) {
                Column {
                    ContentListRow(
                        contentText = StringLocale[STR_SCENES],
                        modifier = Modifier.background(Color.White).border(BorderBuilder.defaultBorder),
                        buttonBuilders =
                        if (sceneInCreation == null) {
                            listOf(
                                ImageButtonBuilder(
                                    content = imageFromIconRes("create_icon"),
                                    onClick = { setSceneInCreation(Act.SceneData.default()) })
                            )
                        } else {
                            listOf(
                                ImageButtonBuilder(
                                    content = imageFromIconRes("confirm_icon"),
                                    onClick = {
                                        if (viewModel.onAddScene(sceneInCreation)) {
                                            setSceneInCreation(null)
                                        } else {
                                            showMessage(
                                                StringLocale[ST_SCENE_ALREADY_EXISTS_OR_INVALID],
                                                messageType = MessageType.WARNING
                                            )
                                        }
                                    }
                                ),
                                ImageButtonBuilder(
                                    content = imageFromIconRes("exit_icon"),
                                    onClick = { setSceneInCreation(null) }
                                )
                            )
                        }
                    )
                }
            }
        }

        if (sceneInCreation != null) {
            EditSceneRow(
                data = sceneInCreation,
                updateData = setSceneInCreation,
                showButtons = false,
                modifier = contentModifier
            )
        } else {
            LazyScrollableColumn(modifier = contentModifier) {
                items(items = viewModel.scenes) { scene ->
                    if (viewModel.currentEditScene isValidAndEqualTo scene) {
                        val (tempCurrentEditedScene, setTempCurrentEditScene) = remember { mutableStateOf(scene) }

                        EditSceneRow(
                            data = tempCurrentEditedScene,
                            updateData = setTempCurrentEditScene,
                            onConfirmed = {
                                if (!viewModel.onEditConfirmed(tempCurrentEditedScene)) {
                                    showMessage(
                                        StringLocale[ST_SCENE_ALREADY_EXISTS_OR_INVALID],
                                        messageType = MessageType.WARNING
                                    )
                                }
                            },
                            onCanceled = viewModel::onEditDone
                        )
                    } else {
                        ContentListRow(
                            contentText = scene.name,
                            buttonBuilders = listOf(
                                ImageButtonBuilder(
                                    content = imageFromIconRes("edit_icon"),
                                    onClick = {
                                        viewModel.onEditItemSelected(scene)
                                        setSceneInCreation(null)
                                    }
                                ),
                                ImageButtonBuilder(
                                    content = imageFromIconRes("delete_icon"),
                                    onClick = { viewModel.onRemoveScene(scene) }
                                )
                            )
                        )
                    }
                }
            }
        }

        val isEditing = sceneInCreation != null || viewModel.currentEditScene != null

        FooterRow(
            lazyResult = lazy(viewModel::submitAct),
            isEnabled = !isEditing,
            onDone = onDone,
            onCancel = {
                setSceneInCreation(null)
                viewModel.onEditDone()
            }
        )
    }
}

@Composable
private fun EditSceneRow(
    data: Act.SceneData,
    updateData: (Act.SceneData) -> Unit,
    modifier: Modifier = Modifier,
    showButtons: Boolean = true,
    onConfirmed: (() -> Unit)? = null,
    onCanceled: (() -> Unit)? = null
) = Column(
    modifier = modifier.applyIf(
        condition = showButtons,
        modifier = { border(bottom = BorderBuilder.defaultBorder) }
    )
) {
    val defaultModifier = remember { Modifier.fillMaxWidth().padding(horizontal = 10.dp) }

    ContentListRow(
        content = {
            CustomTextField(
                data.name,
                onValueChange = { updateData(data.copy(name = it)) },
                modifier = defaultModifier,
                placeholder = data.name.takeIf { it.isNotBlank() } ?: StringLocale[ST_ENTER_SCENE_NAME]
            )
        },
        removeBottomBorder = false,
        buttonBuilders = if (showButtons && onConfirmed != null && onCanceled != null)
            listOf(
                ImageButtonBuilder(content = imageFromIconRes("confirm_icon"), onClick = onConfirmed),
                ImageButtonBuilder(content = imageFromIconRes("exit_icon"), onClick = onCanceled)
            ) else emptyList()
    )

    ImagePreviewContent(data = data, onUpdateData = { updateData(it) })
}

@Composable
private fun ImagePreviewContent(
    data: Act.SceneData,
    onUpdateData: (Act.SceneData) -> Unit
) {
    val imgExist = !data.img.isUnspecified() && File(data.img.path).let { it.exists() && it.isFile }

    Box(
        modifier = Modifier.padding(10.dp).sizeIn(maxWidth = 600.dp, maxHeight = 600.dp)
            .applyIf(condition = !imgExist, modifier = Modifier::addRoundedBorder)
            .applyIf(condition = !imgExist, modifier = { this.size(600.dp) })
    ) {
        if (imgExist) {
            Image(
                bitmap = imageFromFile(File(data.img.path)),
                contentDescription = null,
                Modifier.clip(roundedShape).addRoundedBorder().align(Alignment.Center)
            )
        }

        OutlinedButton(
            onClick = {
                val fileChooser = JFileChooser().apply {
                    this.currentDirectory = File(System.getProperty("user.home"))
                    this.addChoosableFileFilter(
                        FileNameExtensionFilter("Images", *ImageIO.getReaderFileSuffixes())
                    )
                    this.isAcceptAllFileFilterUsed = false
                }

                val result = fileChooser.showSaveDialog(WindowStateManager.currentFocusedWindow)

                if (result == JFileChooser.APPROVE_OPTION && fileChooser.selectedFile.let { it.exists() && it.isFile }) {
                    onUpdateData(data.copy(img = Img(fileChooser.selectedFile.absolutePath)))
                }
            },
            modifier = Modifier.padding(10.dp).align(Alignment.Center).matchParentSize().withHandCursor(),
            content = { Text(text = StringLocale[if (imgExist) STR_IMPORT_NEW_IMG else STR_IMPORT_IMG]) }
        )
    }
}