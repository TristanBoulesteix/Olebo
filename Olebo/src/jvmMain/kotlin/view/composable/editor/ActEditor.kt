package jdr.exia.view.composable.editor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jdr.exia.localization.*
import jdr.exia.model.act.Act
import jdr.exia.model.act.data.SceneData
import jdr.exia.model.tools.*
import jdr.exia.model.type.imageFromIconRes
import jdr.exia.model.type.imageFromPath
import jdr.exia.view.WindowStateManager
import jdr.exia.view.element.*
import jdr.exia.view.element.builder.ImageButtonBuilder
import jdr.exia.view.element.dialog.MessageDialog
import jdr.exia.view.tools.BorderBuilder
import jdr.exia.view.tools.addRoundedBorder
import jdr.exia.view.tools.applyIf
import jdr.exia.view.tools.border
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

    Header(viewModel = viewModel, act = act)

    // List of all the scenes of the edited act
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.secondaryVariant).padding(15.dp)) {
        val contentModifier = Modifier.padding(bottom = 20.dp, end = 20.dp, start = 20.dp)
            .background(Color.White)
            .weight(1f)
            .fillMaxSize()
            .border(BorderBuilder.defaultBorder)


        val (sceneInCreation, setSceneInCreation) = remember { mutableStateOf<SceneData?>(null) withSetter { newValue -> if (newValue != null) viewModel.onEditDone() } }

        Box(
            modifier = Modifier.padding(top = 20.dp, end = 20.dp, start = 20.dp)
                .background(Color.White)
                .fillMaxWidth()
                .border(BorderBuilder.defaultBorder)
        ) {
            ContentListRow(
                contentText = StringLocale[STR_SCENES],
                modifier = Modifier.background(Color.White).border(BorderBuilder.defaultBorder),
                buttonBuilders =
                if (sceneInCreation == null) {
                    listOf(
                        ImageButtonBuilder(
                            content = imageFromIconRes("create_icon"),
                            tooltip = StringLocale[STR_NEW_SCENE],
                            onClick = { setSceneInCreation(SceneData.default()) })
                    )
                } else {
                    listOf(
                        ImageButtonBuilder(
                            content = imageFromIconRes("confirm_icon"),
                            tooltip = StringLocale[STR_CONFIRM_CREATE_SCENE],
                            onClick = {
                                viewModel.onAddScene(sceneInCreation).onSuccess {
                                    setSceneInCreation(null)
                                }.onFailure {
                                    viewModel.errorMessage = StringLocale[ST_SCENE_ALREADY_EXISTS_OR_INVALID]
                                }
                            }
                        ),
                        ImageButtonBuilder(
                            content = imageFromIconRes("exit_icon"),
                            tooltip = StringLocale[STR_CANCEL],
                            onClick = { setSceneInCreation(null) }
                        )
                    )
                }
            )
        }

        var currentEditedScene: SceneData? = null

        if (sceneInCreation != null) {
            EditSceneRow(
                data = sceneInCreation,
                updateData = setSceneInCreation,
                showButtons = false,
                modifier = contentModifier
            )
        } else {
            Scenes(
                contentModifier = contentModifier,
                viewModel = viewModel,
                setSceneInCreation = setSceneInCreation,
                setCurrentEditedScene = { currentEditedScene = it }
            )
        }

        Footer(
            sceneInCreation = sceneInCreation,
            viewModel = viewModel,
            setSceneInCreation = setSceneInCreation,
            getEditedSceneData = { currentEditedScene },
            act = act,
            onDone = onDone
        )
    }

    if (viewModel.errorMessage.isNotBlank()) {
        MessageDialog(
            title = StringLocale[STR_WARNING],
            message = viewModel.errorMessage,
            onCloseRequest = { viewModel.errorMessage = "" },
            width = 450.dp,
            height = 175.dp
        )
    }
}

/**
 * Header of the Act Editor View.
 * It contains a text field to write the name of the act.
 */
@Composable
private fun Header(viewModel: ActEditorViewModel, act: Act?) {
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
}

@Composable
private fun Scenes(
    contentModifier: Modifier,
    viewModel: ActEditorViewModel,
    setCurrentEditedScene: (SceneData) -> Unit,
    setSceneInCreation: (SceneData?) -> Unit
) = when {
    viewModel.scenes.isNotEmpty() -> LazyScrollableColumn(modifier = contentModifier) {
        items(items = viewModel.scenes, key = { it }) { scene ->
            if (viewModel.currentEditScene == scene) {
                val (tempCurrentEditedScene, setTempCurrentEditScene) = remember {
                    mutableStateOf(scene) withSetter {
                        setCurrentEditedScene(it)
                    }
                }

                EditSceneRow(
                    data = tempCurrentEditedScene,
                    updateData = setTempCurrentEditScene,
                    onConfirmed = {
                        viewModel.submitEditedScene(tempCurrentEditedScene) { viewModel.errorMessage = it }
                    },
                    onCanceled = viewModel::onEditDone
                )
            } else {
                ContentListRow(
                    contentText = scene.name,
                    buttonBuilders = listOf(
                        ImageButtonBuilder(
                            content = imageFromIconRes("edit_icon"),
                            tooltip = StringLocale[STR_EDIT_SCENE_TOOLTIP],
                            onClick = {
                                viewModel.onEditItemSelected(scene)
                                setSceneInCreation(null)
                            }
                        ),
                        ImageButtonBuilder(
                            content = imageFromIconRes("delete_icon"),
                            tooltip = StringLocale[STR_DELETE_SCENE_TOOLTIP],
                            onClick = { viewModel.onRemoveScene(scene) }
                        )
                    )
                )
            }
        }
    }
    else -> Column(modifier = contentModifier, verticalArrangement = Arrangement.Center) {
        Text(
            text = StringLocale[STR_NO_SCENE],
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
        OutlinedButton(
            onClick = { setSceneInCreation(SceneData.default()) },
            content = { Text(StringLocale[STR_NEW_SCENE]) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

private inline fun ActEditorViewModel.submitEditedScene(
    tempCurrentEditedScene: SceneData,
    showError: (String) -> Unit
): SimpleResult =
    if (!onEditConfirmed(tempCurrentEditedScene)) {
        showError(StringLocale[ST_SCENE_ALREADY_EXISTS_OR_INVALID])
        Result.failure
    } else Result.success

@Composable
private fun EditSceneRow(
    data: SceneData,
    updateData: (SceneData) -> Unit,
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
                placeholder = data.name.takeIf(String::isNotBlank) ?: StringLocale[ST_ENTER_SCENE_NAME]
            )
        },
        removeBottomBorder = false,
        buttonBuilders = if (showButtons && onConfirmed != null && onCanceled != null)
            listOf(
                ImageButtonBuilder(
                    content = imageFromIconRes("confirm_icon"),
                    tooltip = StringLocale[STR_CONFIRM_EDIT_SCENE],
                    onClick = onConfirmed
                ),
                ImageButtonBuilder(
                    content = imageFromIconRes("exit_icon"),
                    tooltip = StringLocale[STR_CANCEL],
                    onClick = onCanceled
                )
            ) else emptyList()
    )

    ImagePreviewContent(data = data, onUpdateData = updateData)
}

@Composable
private fun ImagePreviewContent(
    data: SceneData,
    onUpdateData: (SceneData) -> Unit
) {
    val imgExist = !data.img.isUnspecified() && data.img.checkedImgPath?.toFile().isFileValid()

    Box(
        modifier = Modifier.padding(10.dp).sizeIn(maxWidth = 600.dp, maxHeight = 600.dp)
            .applyIf(condition = !imgExist, modifier = Modifier::addRoundedBorder)
            .applyIf(condition = !imgExist, modifier = { this.size(600.dp) })
    ) {
        if (imgExist) {
            Image(
                bitmap = data.img.checkedImgPath?.toAbsolutePath()?.toString()?.let { imageFromPath(it) }
                    ?: imageFromIconRes("not_found", "jpg"),
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

                val result = fileChooser.showSaveDialog(WindowStateManager.currentFocusedWindowScope?.window)

                if (result == JFileChooser.APPROVE_OPTION && fileChooser.selectedFile.let { it.exists() && it.isFile }) {
                    onUpdateData(data.copy(img = Img(fileChooser.selectedFile.absolutePath)))
                }
            },
            modifier = Modifier.padding(10.dp).align(Alignment.Center).matchParentSize(),
            content = { Text(text = StringLocale[if (imgExist) STR_IMPORT_NEW_IMG else STR_IMPORT_IMG]) }
        )
    }
}

@Composable
private fun Footer(
    sceneInCreation: SceneData?,
    viewModel: ActEditorViewModel,
    setSceneInCreation: (SceneData?) -> Unit,
    getEditedSceneData: () -> SceneData?,
    act: Act?,
    onDone: () -> Unit
) {
    when {
        sceneInCreation != null -> FooterRow(
            confirmText = StringLocale[STR_CONFIRM_CREATE_SCENE],
            onConfirm = { viewModel.onAddScene(sceneInCreation) },
            onDone = { setSceneInCreation(null) },
            onFailure = {
                viewModel.errorMessage = StringLocale[ST_SCENE_ALREADY_EXISTS_OR_INVALID]
            }
        )
        viewModel.currentEditScene != null -> FooterRow(
            confirmText = StringLocale[STR_CONFIRM_EDIT_SCENE],
            onConfirm = {
                getEditedSceneData().let {
                    if (it != null)
                        viewModel.submitEditedScene(it) { errorMessage -> viewModel.errorMessage = errorMessage }
                    else {
                        viewModel.onEditDone()
                        Result.success
                    }
                }
            },
            onDone = {
                setSceneInCreation(null)
                viewModel.onEditDone()
            }
        )
        else -> FooterRow(
            confirmText = StringLocale[if (act == null) STR_CONFIRM_CREATE_ACT else STR_CONFIRM_EDIT_ACT],
            onConfirm = viewModel::submitAct,
            onDone = onDone
        )
    }
}