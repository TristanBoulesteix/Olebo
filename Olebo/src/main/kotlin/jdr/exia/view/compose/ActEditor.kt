@file:Suppress("FunctionName")

package jdr.exia.view.compose

import androidx.compose.desktop.AppManager
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jdr.exia.localization.*
import jdr.exia.model.act.Act
import jdr.exia.model.act.isValidAndEqualTo
import jdr.exia.model.utils.imageFromFile
import jdr.exia.model.utils.imageFromIconRes
import jdr.exia.view.compose.components.ButtonBuilder
import jdr.exia.view.compose.components.ContentRow
import jdr.exia.view.compose.tools.*
import jdr.exia.view.compose.ui.blue
import jdr.exia.view.compose.ui.lightOrange
import jdr.exia.viewModel.ActEditorViewModel
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import jdr.exia.model.utils.Image as Img

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActEditorView(act: Act? = null, onDone: DefaultFunction) = Column {
    val viewModel = remember { ActEditorViewModel(act) }

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxWidth().background(lightOrange).padding(15.dp)
    ) {
        val roundedShape = remember { RoundedCornerShape(25) }

        var name by remember { mutableStateOf(act?.name ?: "") }

        BasicTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth().clip(roundedShape).background(Color.White)
                .border(BorderStroke(2.dp, Color.Black), roundedShape).padding(10.dp),
            singleLine = true,
            decorationBox = { composableContent ->
                if (act != null && name.isEmpty())
                    Text(text = act.name)
                else composableContent()
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(blue).padding(15.dp)) {
        val headerScrollState = rememberScrollState()
        val listState = rememberLazyListState()

        val contentModifier = remember {
            Modifier.padding(bottom = 20.dp, end = 20.dp, start = 20.dp)
                .background(Color.White)
                .weight(1f)
                .fillMaxSize()
                .border(BorderInlined.defaultBorder)
        }

        val (sceneInCreation, setSceneInCreation) = remember { mutableStateOf<Act.SceneData?>(null) withSetter { if (it != null) viewModel.onEditDone() } }

        Box(
            modifier = Modifier.padding(top = 20.dp, end = 20.dp, start = 20.dp)
                .background(Color.White)
                .fillMaxWidth()
                .border(BorderInlined.defaultBorder)
        ) {
            Box(modifier = Modifier.verticalScroll(headerScrollState).fillMaxSize()) {
                Column {
                    ContentRow(
                        contentText = StringLocale[STR_SCENES],
                        modifier = Modifier.background(Color.White).border(BorderInlined.defaultBorder),
                        buttonBuilders =
                        if (sceneInCreation == null) {
                            listOf(
                                ButtonBuilder(
                                    icon = imageFromIconRes("create_icon"),
                                    onClick = { setSceneInCreation(Act.SceneData.default()) })
                            )
                        } else {
                            listOf(
                                ButtonBuilder(
                                    icon = imageFromIconRes("confirm_icon"),
                                    onClick = {
                                        viewModel.onAddScene(sceneInCreation).also { setSceneInCreation(null) }
                                    }),
                                ButtonBuilder(
                                    icon = imageFromIconRes("exit_icon"),
                                    onClick = { setSceneInCreation(null) })
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
            LazyColumn(
                modifier = contentModifier,
                state = listState
            ) {
                items(items = viewModel.scenes) { scene ->
                    if (viewModel.currentEditScene isValidAndEqualTo scene) {
                        val (tempCurrentEditedScene, setTempCurrentEditScene) = remember { mutableStateOf(scene) }

                        EditSceneRow(
                            data = tempCurrentEditedScene,
                            updateData = setTempCurrentEditScene,
                            onConfirmed = { viewModel.onEditConfirmed(tempCurrentEditedScene) },
                            onCanceled = viewModel::onEditDone
                        )
                    } else {
                        ContentRow(
                            contentText = scene.name,
                            buttonBuilders = listOf(
                                ButtonBuilder(
                                    imageFromIconRes("edit_icon"),
                                    onClick = {
                                        viewModel.onEditItemSelected(scene)
                                        setSceneInCreation(null)
                                    }
                                ),
                                ButtonBuilder(imageFromIconRes("delete_icon"), onClick = {})
                            )
                        )
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth().padding(15.dp)
        ) {
            OutlinedButton(onClick = onDone) {
                Text(text = StringLocale[STR_CONFIRM])
            }
            OutlinedButton(onClick = onDone) {
                Text(text = StringLocale[STR_CANCEL])
            }
        }
    }
}

@Composable
private fun EditSceneRow(
    data: Act.SceneData,
    updateData: (Act.SceneData) -> Unit,
    modifier: Modifier = Modifier,
    showButtons: Boolean = true,
    onConfirmed: DefaultFunction? = null,
    onCanceled: DefaultFunction? = null
) = Column(
    modifier = modifier.applyIf(
        condition = showButtons,
        mod = { border(bottom = BorderInlined.defaultBorder) }
    )
) {
    val defaultModifier = remember { Modifier.fillMaxWidth().padding(horizontal = 10.dp) }

    ContentRow(
        content = {
            TextField(
                data.name,
                onValueChange = { updateData(data.copy(name = it)) },
                modifier = defaultModifier,
                singleLine = true,
                placeholder = { Text(data.name.takeIf { it.isNotBlank() } ?: StringLocale[ST_ENTER_SCENE_NAME]) },
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            )
        },
        removeBottomBorder = false,
        buttonBuilders = if (showButtons && onConfirmed != null && onCanceled != null)
            listOf(
                ButtonBuilder(icon = imageFromIconRes("confirm_icon"), onClick = onConfirmed),
                ButtonBuilder(icon = imageFromIconRes("exit_icon"), onClick = onCanceled)
            ) else emptyList()
    )

    ImagePreviewContent(data = data, onUpdateData = { updateData(it) })
}

private val roundedShape = RoundedCornerShape(20.dp)

private fun Modifier.addBorderWithShape() =
    this.border(border = BorderStroke(2.dp, Color.Black), shape = roundedShape)

@Composable
private fun ImagePreviewContent(
    data: Act.SceneData,
    onUpdateData: (Act.SceneData) -> Unit
) {
    val imgExist = !data.img.isUnspecified() && File(data.img.path).let { it.exists() && it.isFile }

    Box(
        modifier = Modifier.padding(10.dp).sizeIn(maxWidth = 600.dp, maxHeight = 600.dp)
            .applyIf(condition = !imgExist, mod = Modifier::addBorderWithShape)
            .applyIf(condition = !imgExist, mod = { this.size(600.dp) })
    ) {
        if (imgExist) {
            Image(
                bitmap = imageFromFile(File(data.img.path)),
                contentDescription = null,
                Modifier.clip(roundedShape).addBorderWithShape().align(Alignment.Center)
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

                val result = fileChooser.showSaveDialog(AppManager.focusedWindow?.window)

                if (result == JFileChooser.APPROVE_OPTION && fileChooser.selectedFile.let { it.exists() && it.isFile }) {
                    onUpdateData(data.copy(img = Img(fileChooser.selectedFile.absolutePath)))
                }
            },
            modifier = Modifier.padding(10.dp).align(Alignment.Center).matchParentSize(),
            content = { Text(text = StringLocale[if (imgExist) STR_IMPORT_NEW_IMG else STR_IMPORT_IMG]) }
        )
    }
}