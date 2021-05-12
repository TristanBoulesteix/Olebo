@file:Suppress("FunctionName")

package jdr.exia.view

import androidx.compose.desktop.AppManager
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jdr.exia.localization.*
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Type
import jdr.exia.model.tools.Image
import jdr.exia.model.tools.imageFromFile
import jdr.exia.model.tools.imageFromIconRes
import jdr.exia.model.tools.savePathToImage
import jdr.exia.model.utils.Result
import jdr.exia.model.utils.isCharacter
import jdr.exia.view.components.*
import jdr.exia.view.ui.blue
import jdr.exia.view.tools.*
import jdr.exia.view.ui.roundedShape
import jdr.exia.view.legacy.utils.MessageType
import jdr.exia.view.legacy.utils.showMessage
import jdr.exia.viewModel.ElementsEditorViewModel
import jdr.exia.viewModel.ElementsTabViewModel
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun ElementsView(onDone: DefaultFunction) = Column {
    val tabViewModel = remember { ElementsTabViewModel() }

    val contentViewModel = remember(tabViewModel.currentTab) { ElementsEditorViewModel(tabViewModel.currentTab) }

    Scaffold(
        backgroundColor = blue,
        topBar = {
            HeaderRow {
                Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                    tabViewModel.tabs.forEach { tab ->
                        Text(
                            text = tab.typeName,
                            fontWeight = FontWeight.Bold.takeIf { tabViewModel.currentTab == tab },
                            modifier = Modifier.applyIf(
                                condition = tabViewModel.currentTab == tab,
                                mod = { border(bottom = BorderBuilder(5.dp, Color.Black)) }
                            ).clickable { tabViewModel.onSelectTab(tab) }.padding(20.dp)
                        )
                    }
                }
            }
        },
        content = {
            Content(
                viewModel = contentViewModel,
                innerPadding = it,
                currentType = tabViewModel.currentTab
            )
        },
        bottomBar = {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp).padding(bottom = 20.dp)
            ) {
                OutlinedButton(
                    onClick = when {
                        contentViewModel.currentEditBlueprint != null -> contentViewModel::onEditDone
                        contentViewModel.blueprintInCreation != null -> contentViewModel::cancelBluprintCreation
                        else -> onDone
                    },
                    content = {
                        Text(text = StringLocale[if (contentViewModel.currentEditBlueprint == null && contentViewModel.blueprintInCreation == null) STR_BACK else STR_CANCEL])
                    }
                )
            }
        }
    )
}

private val ColumnScope.contentModifier
    get() = Modifier.padding(bottom = 20.dp, end = 20.dp, start = 20.dp)
        .background(Color.White)
        .weight(1f)
        .fillMaxSize()
        .border(BorderBuilder.defaultBorder)

@Composable
private fun Content(viewModel: ElementsEditorViewModel, innerPadding: PaddingValues, currentType: Type) =
    Box(modifier = Modifier.padding(innerPadding)) {
        Column(modifier = Modifier.fillMaxSize().padding(15.dp)) {
            val headerScrollState = rememberScrollState()

            HeaderContent(headerScrollState, currentType, viewModel)

            val blueprintInCreation = viewModel.blueprintInCreation

            when {
                blueprintInCreation != null -> CreateBlueprint(
                    blueprintInCreation,
                    viewModel::onUpdateBlueprintInCreation
                )
                viewModel.blueprints.isEmpty() -> {
                    Column(modifier = contentModifier, verticalArrangement = Arrangement.Center) {
                        Text(
                            text = StringLocale[STR_NO_ELEMENT],
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp
                        )
                        OutlinedButton(
                            onClick = viewModel::startBlueprintCreation,
                            content = { Text(StringLocale[STR_ADD_ELEMENT]) },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
                else -> ScrolableContent(viewModel)
            }
        }
    }

@Composable
private fun HeaderContent(
    headerScrollState: ScrollState,
    currentType: Type,
    viewModel: ElementsEditorViewModel
) = Box(
    modifier = Modifier.padding(top = 20.dp, end = 20.dp, start = 20.dp)
        .background(Color.White)
        .fillMaxWidth()
        .border(BorderBuilder.defaultBorder)
) {
    Box(modifier = Modifier.verticalScroll(headerScrollState).fillMaxSize()) {
        Column {
            ContentListRow(
                contentText = currentType.typeName,
                modifier = Modifier.background(Color.White).border(BorderBuilder.defaultBorder),
                buttonBuilders =
                if (viewModel.blueprintInCreation == null) {
                    if (viewModel.blueprints.isNotEmpty()) {
                        if (currentType.type.typeElement != Type.OBJECT) listOf(
                            ContentButtonBuilder(
                                content = StringLocale[STR_HP]
                            ),
                            ContentButtonBuilder(
                                content = StringLocale[STR_MP]
                            )
                        ) else {
                            emptyList()
                        } + listOf(
                            ContentButtonBuilder(
                                content = StringLocale[STR_IMG]
                            ),
                            EmptyContent
                        )
                    } else {
                        emptyList()
                    } + listOf(
                        ImageButtonBuilder(
                            content = imageFromIconRes("create_icon"),
                            onClick = viewModel::startBlueprintCreation
                        )
                    )
                } else {
                    listOf(
                        ImageButtonBuilder(
                            content = imageFromIconRes("confirm_icon"),
                            onClick = {
                                when (viewModel.onSubmitBlueprint()) {
                                    is Result.Success -> viewModel.cancelBluprintCreation()
                                    else -> showMessage(
                                        StringLocale[ST_SCENE_ALREADY_EXISTS_OR_INVALID],
                                        messageType = MessageType.WARNING
                                    )
                                }
                            }
                        ),
                        ImageButtonBuilder(
                            content = imageFromIconRes("exit_icon"),
                            onClick = viewModel::cancelBluprintCreation
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun ColumnScope.CreateBlueprint(
    blueprint: Blueprint.BlueprintData,
    onUpdate: (Blueprint.BlueprintData) -> Unit
) = Column(modifier = contentModifier) {
    @Composable
    fun RowField(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) = Row(
        modifier = modifier.fillMaxWidth().padding(top = 5.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )

    RowField {
        Text(StringLocale[STR_NAME_OF_ELEMENT])
        CustomTextField(value = blueprint.name, onValueChange = { onUpdate(blueprint.copy(name = it)) })
    }

    if (blueprint.type != Type.OBJECT) {
        RowField {
            Text(StringLocale[STR_MAX_HEALTH])
            IntTextField(value = blueprint.life ?: 0, onValueChange = { onUpdate(blueprint.copy(life = it)) })
        }

        RowField {
            Text(StringLocale[STR_MAX_MANA])
            IntTextField(value = blueprint.mana ?: 0, onValueChange = { onUpdate(blueprint.copy(mana = it)) })
        }
    }

    RowField(modifier = Modifier.height(200.dp).padding(top = 10.dp)) {
        Button(
            content = { Text(StringLocale[STR_IMPORT_IMG]) },
            onClick = {
                val file = JFileChooser().apply {
                    this.currentDirectory = File(System.getProperty("user.home"))
                    this.addChoosableFileFilter(
                        FileNameExtensionFilter("Images", *ImageIO.getReaderFileSuffixes())
                    )
                    this.isAcceptAllFileFilterUsed = false
                }

                if (file.showSaveDialog(AppManager.focusedWindow?.window) == JFileChooser.APPROVE_OPTION) {
                    onUpdate(blueprint.copy(img = Image(file.selectedFile.absolutePath)))
                }
            },
            modifier = Modifier.fillMaxWidth(0.30f)
        )

        val imgExist = !blueprint.img.isUnspecified() && File(blueprint.img.path).let { it.exists() && it.isFile }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.70f)
                .applyIf(condition = !imgExist, mod = { size(200.dp).clip(roundedShape).addRoundedBorder() })
        ) {
            if (imgExist) {
                Image(
                    bitmap = blueprint.img.toBitmap(),
                    contentDescription = null,
                    modifier = Modifier.sizeIn(maxHeight = 200.dp, maxWidth = 200.dp).clip(roundedShape)
                        .addRoundedBorder()
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.ScrolableContent(viewModel: ElementsEditorViewModel) {
    ScrollableColumn(modifier = contentModifier) {
        ColumnItem(viewModel.blueprints) { blueprint ->
            var editedData by (viewModel.currentEditBlueprint == blueprint).let { isEditing ->
                remember(isEditing) { mutableStateOf(blueprint.takeIf { isEditing }?.toBlueprintData()) }
            }

            ContentListRow(
                content = {
                    editedData.let { data ->
                        if (data == null) {
                            ContentText(blueprint.name)
                        } else {
                            CustomTextField(
                                value = data.name,
                                onValueChange = { editedData = data.copy(name = it) },
                                placeholder = blueprint.name,
                                modifier = Modifier.padding(horizontal = 4.dp).fillMaxWidth()
                            )
                        }
                    }
                },
                buttonBuilders = editedData.getButtons(
                    viewModel = viewModel,
                    blueprint = blueprint,
                    onUpdate = { editedData = it }
                )
            )
        }
    }
}

private fun Blueprint.BlueprintData?.getButtons(
    viewModel: ElementsEditorViewModel,
    blueprint: Blueprint,
    onUpdate: (Blueprint.BlueprintData) -> Unit
) = if (this == null) {
    if (blueprint.isCharacter()) transaction {
        listOf(
            ContentButtonBuilder(content = blueprint.HP),
            ContentButtonBuilder(content = blueprint.MP)
        )
    } else {
        emptyList()
    } + listOf(
        ImageButtonBuilder(
            content = imageFromFile(File(blueprint.sprite))
        ),
        ImageButtonBuilder(
            content = imageFromIconRes("edit_icon"),
            onClick = {
                viewModel.onEditItemSelected(blueprint)
                viewModel.cancelBluprintCreation()
            }
        ),
        ImageButtonBuilder(
            content = imageFromIconRes("delete_icon"),
            onClick = { viewModel.onRemoveBlueprint(blueprint) }
        )
    )
} else {
    if (blueprint.isCharacter()) transaction {
        listOf(
            ComposableContentBuilder(
                content = {
                    IntTextField(
                        value = life,
                        onValueChange = { onUpdate(copy(life = it)) },
                        modifier = Modifier.padding(2.dp).padding(start = 1.dp),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            ),
            ComposableContentBuilder(
                content = {
                    IntTextField(
                        value = mana,
                        onValueChange = { onUpdate(copy(mana = it)) },
                        modifier = Modifier.padding(2.dp).padding(start = 1.dp),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            )
        )
    } else {
        emptyList()
    } + listOf(
        ImageButtonBuilder(
            content = imageFromFile(File(img.path)),
            onClick = { updateImage(onUpdate) }
        ),
        ImageButtonBuilder(
            content = imageFromIconRes("confirm_icon"),
            onClick = { submitData(viewModel) }
        ),
        ImageButtonBuilder(
            content = imageFromIconRes("exit_icon"),
            onClick = { viewModel.onEditDone() }
        )
    )
}

private fun (Blueprint.BlueprintData).submitData(viewModel: ElementsEditorViewModel) {
    when (val result = viewModel.onEditConfirmed(this)) {
        is Result.Failure -> showMessage(result.message, messageType = MessageType.WARNING)
        else -> viewModel.onEditDone()
    }
}

private inline fun (Blueprint.BlueprintData).updateImage(crossinline onUpdate: (Blueprint.BlueprintData) -> Unit) {
    transaction {
        val file = JFileChooser().apply {
            this.currentDirectory = File(System.getProperty("user.home"))
            this.addChoosableFileFilter(
                FileNameExtensionFilter(StringLocale[STR_IMG], *ImageIO.getReaderFileSuffixes())
            )
            this.isAcceptAllFileFilterUsed = false
        }

        val result = file.showSaveDialog(null)

        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = file.selectedFile

            if (selectedFile.exists()) {
                onUpdate(copy(img = savePathToImage(selectedFile.absolutePath, "blueprint")))
            }
        }
    }
}