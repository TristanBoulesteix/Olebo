package jdr.exia.view.composable.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
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
import jdr.exia.model.element.TypeElement
import jdr.exia.model.tools.SimpleResult
import jdr.exia.model.tools.isCharacter
import jdr.exia.model.type.Image
import jdr.exia.model.type.imageFromIconRes
import jdr.exia.model.type.imageFromPath
import jdr.exia.view.WindowStateManager
import jdr.exia.view.element.*
import jdr.exia.view.element.builder.ComposableContentBuilder
import jdr.exia.view.element.builder.ContentButtonBuilder
import jdr.exia.view.element.builder.EmptyContent
import jdr.exia.view.element.builder.ImageButtonBuilder
import jdr.exia.view.element.form.IntTextField
import jdr.exia.view.tools.*
import jdr.exia.view.ui.roundedBottomShape
import jdr.exia.view.ui.roundedShape
import jdr.exia.view.ui.roundedTopShape
import jdr.exia.viewModel.ElementsEditorViewModel
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun ElementsView(onDone: () -> Unit, closeText: String = StringLocale[STR_BACK]) {
    val tabs = remember { listOf(TypeElement.Object, TypeElement.PJ, TypeElement.PNJ) }

    val contentViewModel = remember { ElementsEditorViewModel(tabs.first()) }

    TabPanel(
        backgroundColor = MaterialTheme.colors.secondaryVariant,
        tabs = tabs,
        onTabChanged = { contentViewModel.currentType = it },
        headerTabOption = HeaderTabOptions(backgroundColor = MaterialTheme.colors.secondary),
        content = { currentTab, padding ->
            Content(
                viewModel = contentViewModel,
                innerPadding = padding,
                currentType = currentTab
            )
        },
        footer = {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp).padding(bottom = 20.dp)
            ) {
                OutlinedButton(
                    onClick = when {
                        contentViewModel.currentEditBlueprint != null -> contentViewModel::onEditDone
                        contentViewModel.hasBlueprintInCreationForCurrentType -> contentViewModel::cancelBlueprintCreation
                        else -> onDone
                    },
                    content = {
                        Text(text = if (contentViewModel.currentEditBlueprint == null && contentViewModel.hasBlueprintInCreationForCurrentType) closeText else StringLocale[STR_CANCEL])
                    }
                )
            }
        },
        tabNameProvider = TypeElement::localizedName
    )
}

@Stable
private val ColumnScope.contentModifier
    get() = Modifier.padding(bottom = 20.dp, end = 20.dp, start = 20.dp)
        .weight(1f)
        .fillMaxSize()

@Composable
private fun Content(viewModel: ElementsEditorViewModel, innerPadding: PaddingValues, currentType: TypeElement) =
    Box(modifier = Modifier.padding(innerPadding)) {
        Column(modifier = Modifier.fillMaxSize().padding(15.dp)) {
            HeaderContent(currentType, viewModel)

            val blueprintInCreation = viewModel.blueprintsInCreation[currentType]

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
                else -> ScrollableContent(viewModel)
            }
        }
    }

@Composable
private fun HeaderContent(
    currentType: TypeElement,
    viewModel: ElementsEditorViewModel
) = Card(
    modifier = Modifier.padding(top = 20.dp, end = 20.dp, start = 20.dp)
        .fillMaxWidth(),
    border = BorderBuilder.defaultBorder.toBorderStroke(),
    shape = roundedTopShape
) {
    ContentListRow(
        contentText = currentType.localizedName,
        modifier = Modifier.fillMaxWidth(),
        buttonBuilders =
        if (!viewModel.hasBlueprintInCreationForCurrentType) {
            if (viewModel.blueprints.isNotEmpty()) {
                if (currentType != TypeElement.Object) listOf(
                    ContentButtonBuilder(
                        content = StringLocale[STR_HP],
                        enabled = false
                    ),
                    ContentButtonBuilder(
                        content = StringLocale[STR_MP],
                        enabled = false
                    )
                ) else {
                    emptyList()
                } + listOf(
                    ContentButtonBuilder(
                        content = StringLocale[STR_IMG],
                        enabled = false
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
                        viewModel.onSubmitBlueprint().onSuccess { viewModel.cancelBlueprintCreation() }.onFailure {
                            showMessage(
                                StringLocale[ST_SCENE_ALREADY_EXISTS_OR_INVALID],
                                messageType = MessageType.WARNING
                            )
                        }
                    }
                ),
                ImageButtonBuilder(
                    content = imageFromIconRes("exit_icon"),
                    onClick = viewModel::cancelBlueprintCreation
                )
            )
        }
    )
}

@Composable
private fun ColumnScope.CreateBlueprint(
    blueprint: Blueprint.BlueprintData,
    onUpdate: (Blueprint.BlueprintData) -> Unit
) = Card(
    modifier = contentModifier,
    border = BorderBuilder.defaultBorder.toBorderStroke(),
    shape = roundedBottomShape
) {
    Column {
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

        if (blueprint.type != TypeElement.Object) {
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

                    if (file.showSaveDialog(WindowStateManager.currentFocusedWindowScope?.window) == JFileChooser.APPROVE_OPTION) {
                        onUpdate(blueprint.copy(img = Image(file.selectedFile.absolutePath)))
                    }
                },
                modifier = Modifier.fillMaxWidth(0.30f)
            )

            val imgExist = !blueprint.img.isUnspecified() && File(blueprint.img.path).let { it.exists() && it.isFile }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.70f)
                    .applyIf(condition = !imgExist, modifier = { size(200.dp).clip(roundedShape).addRoundedBorder() })
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
}

@Composable
private fun ColumnScope.ScrollableContent(viewModel: ElementsEditorViewModel) = Card(
    modifier = contentModifier,
    border = BorderBuilder.defaultBorder.toBorderStroke(),
    shape = roundedBottomShape
) {
    LazyScrollableColumn {
        items(viewModel.blueprints, key = { it.id }) { blueprint ->
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
            content = imageFromPath(blueprint.sprite)
        ),
        ImageButtonBuilder(
            content = imageFromIconRes("edit_icon"),
            onClick = {
                viewModel.onEditItemSelected(blueprint)
                viewModel.cancelBlueprintCreation()
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
            content = imageFromPath(img.path),
            onClick = { updateImage(onUpdate) }
        ),
        ImageButtonBuilder(
            content = imageFromIconRes("confirm_icon"),
            onClick = { submitData(onEditConfirmed = viewModel::onEditConfirmed, onEditDone = viewModel::onEditDone) }
        ),
        ImageButtonBuilder(
            content = imageFromIconRes("exit_icon"),
            onClick = { viewModel.onEditDone() }
        )
    )
}

private inline fun (Blueprint.BlueprintData).submitData(
    onEditConfirmed: (Blueprint.BlueprintData) -> SimpleResult,
    onEditDone: () -> Unit
) {
    onEditConfirmed(this).onSuccess { onEditDone() }.onFailure {
        if (it.message != null)
            showMessage(it.message!!, messageType = MessageType.WARNING)
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
                onUpdate(copy(img = Image(selectedFile.absolutePath)))
            }
        }
    }
}