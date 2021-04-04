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
import jdr.exia.model.utils.imageFromIcon
import jdr.exia.view.compose.components.ButtonBuilder
import jdr.exia.view.compose.components.ContentRow
import jdr.exia.view.compose.tools.BorderInlined
import jdr.exia.view.compose.tools.DefaultFunction
import jdr.exia.view.compose.tools.applyIf
import jdr.exia.view.compose.tools.border
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

        var sceneInCreation by remember { mutableStateOf(false) }

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
                        buttonBuilders = listOf(
                            ButtonBuilder(
                                icon = imageFromIcon("create_icon"),
                                onClick = { sceneInCreation = !sceneInCreation })
                        )
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.padding(bottom = 20.dp, end = 20.dp, start = 20.dp)
                .background(Color.White)
                .weight(1f)
                .fillMaxSize()
                .border(BorderInlined.defaultBorder),
            state = listState
        ) {
            items(items = viewModel.scenes) { scene ->
                if (!sceneInCreation && viewModel.currentEditScene isValidAndEqualTo scene) {
                    EditSceneRow(
                        sceneData = scene,
                        onConfirmed = viewModel::onEditConfirmed,
                        onCanceled = viewModel::onEditDone
                    )
                } else {
                    ContentRow(
                        contentText = scene.name,
                        buttonBuilders = listOf(
                            ButtonBuilder(
                                imageFromIcon("edit_icon"),
                                onClick = {
                                    viewModel.onEditItemSelected(scene)
                                    sceneInCreation = false
                                }
                            ),
                            ButtonBuilder(imageFromIcon("delete_icon"), onClick = {})
                        )
                    )
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
    sceneData: Act.SceneData? = null,
    modifier: Modifier = Modifier,
    onConfirmed: (Act.SceneData) -> Unit,
    onCanceled: DefaultFunction
) = Column(
    modifier = modifier.applyIf(
        condition = sceneData != null,
        mod = { border(bottom = BorderInlined.defaultBorder) }
    )
) {
    var data by remember { mutableStateOf(sceneData ?: Act.SceneData.default()) }

    val defaultModifier = remember { Modifier.fillMaxWidth().padding(horizontal = 10.dp) }

    ContentRow(
        content = {
            TextField(
                data.name,
                onValueChange = { data = data.copy(name = it) },
                modifier = defaultModifier,
                singleLine = true,
                placeholder = { Text(sceneData?.name ?: StringLocale[ST_ENTER_SCENE_NAME]) },
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            )
        },
        removeBottomBorder = false,
        buttonBuilders = if (sceneData != null)
            listOf(
                ButtonBuilder(icon = imageFromIcon("confirm_icon"), onClick = { onConfirmed(data) }),
                ButtonBuilder(icon = imageFromIcon("exit_icon"), onClick = onCanceled)
            ) else emptyList()
    )

    ImagePreviewContent(data = data, defaultModifier = defaultModifier, onUpdateData = { data = it })
}

private val roundedShape = RoundedCornerShape(20.dp)

private fun Modifier.addBorderWithShape() =
    this.border(border = BorderStroke(2.dp, Color.Black), shape = roundedShape)

@Composable
private fun ImagePreviewContent(
    data: Act.SceneData,
    defaultModifier: Modifier,
    onUpdateData: (Act.SceneData) -> Unit
) {
    val imgExist = !data.img.isUnspecified() && File(data.img.path).let { it.exists() && it.isFile }

    Box(
        modifier = Modifier.padding(10.dp).sizeIn(maxWidth = 600.dp, maxHeight = 600.dp)
            .applyIf(condition = !imgExist, mod = Modifier::addBorderWithShape)
    ) {
        if (imgExist)
            Image(
                bitmap = imageFromFile(File(data.img.path)),
                contentDescription = null,
                Modifier.clip(roundedShape).applyIf(condition = imgExist, mod = Modifier::addBorderWithShape)
            )

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
            modifier = defaultModifier.fillMaxWidth().padding(10.dp).align(Alignment.Center),
            content = { Text(text = StringLocale[STR_IMPORT_IMG]) }
        )
    }
}