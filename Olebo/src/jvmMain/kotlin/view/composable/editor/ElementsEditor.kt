package jdr.exia.view.composable.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jdr.exia.localization.*
import jdr.exia.model.element.TypeElement
import jdr.exia.model.tools.success
import jdr.exia.model.type.Image
import jdr.exia.model.type.imageFromIconRes
import jdr.exia.view.element.*
import jdr.exia.view.element.builder.ComposableContentBuilder
import jdr.exia.view.element.builder.ContentButtonBuilder
import jdr.exia.view.element.builder.EmptyContent
import jdr.exia.view.element.builder.ImageButtonBuilder
import jdr.exia.view.element.form.IntTextField
import jdr.exia.view.tools.BorderBuilder
import jdr.exia.view.tools.MessageType
import jdr.exia.view.tools.showMessage
import jdr.exia.view.tools.toBorderStroke
import jdr.exia.view.ui.backgroundImageColor
import jdr.exia.view.ui.roundedBottomShape
import jdr.exia.view.ui.roundedTopShape
import jdr.exia.viewModel.ElementsEditorViewModel
import jdr.exia.viewModel.data.BlueprintData
import jdr.exia.viewModel.data.isCharacter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun ElementsView(onDone: () -> Unit) {
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
            FooterRowWithCancel(
                confirmText = StringLocale[STR_SUBMIT_BLUEPRINT_CHANGES],
                onConfirm = {
                    contentViewModel.saveChanges()
                    Result.success
                },
                cancelText = StringLocale[STR_CANCEL_BLUEPRINT_CHANGES],
                onDone = onDone,
                modifier = Modifier.padding(bottom = 15.dp)
            )
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

            when {
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
    val scope = rememberCoroutineScope()

    ContentListRow(
        contentText = currentType.localizedName,
        modifier = Modifier.fillMaxWidth(),
        buttonBuilders = if (viewModel.blueprints.isNotEmpty()) {
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
                onClick = {
                    viewModel.startBlueprintCreation()
                    scope.launch {
                        delay(100) // If we don't add a delay, the scroll does not always happen.
                        viewModel.itemListScrollState.scrollToItem(0)
                    }
                }
            )
        )
    )
}

@Composable
private fun ColumnScope.ScrollableContent(viewModel: ElementsEditorViewModel) = Card(
    modifier = contentModifier,
    border = BorderBuilder.defaultBorder.toBorderStroke(),
    shape = roundedBottomShape
) {
    LazyScrollableColumn(scrollState = viewModel.itemListScrollState) {
        items(viewModel.blueprints, key = { it }) { blueprint ->
            DataRow(viewModel, blueprint)
        }
    }
}

@Composable
private fun DataRow(
    viewModel: ElementsEditorViewModel,
    blueprint: BlueprintData
) {
    var editedData by (viewModel.currentEditBlueprint == blueprint).let { isEditing ->
        remember(isEditing) { mutableStateOf(blueprint.takeIf { isEditing }) }
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
                        modifier = Modifier.padding(horizontal = 4.dp).fillMaxWidth(),
                        focused = data.id == null
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

@Composable
private fun BlueprintData?.getButtons(
    viewModel: ElementsEditorViewModel,
    blueprint: BlueprintData,
    onUpdate: (BlueprintData) -> Unit
) = if (this == null) {
    if (blueprint.isCharacter()) transaction {
        listOf(
            ContentButtonBuilder(content = blueprint.life ?: 0),
            ContentButtonBuilder(content = blueprint.mana ?: 0)
        )
    } else {
        emptyList()
    } + listOf(
        ImageButtonBuilder(
            content = blueprint.img.toBitmap(),
            backgroundColor = MaterialTheme.colors.backgroundImageColor
        ),
        ImageButtonBuilder(
            content = imageFromIconRes("edit_icon"),
            onClick = { viewModel.onEditItemSelected(blueprint) }
        ),
        ImageButtonBuilder(
            content = imageFromIconRes("delete_icon"),
            onClick = { viewModel.onRemoveBlueprint(blueprint) }
        )
    )
} else {
    if (blueprint.isCharacter()) transaction {
        listOf(
            ComposableContentBuilder {
                IntTextField(
                    value = life,
                    onValueChange = { onUpdate(copy(life = it)) },
                    modifier = Modifier.padding(2.dp).padding(start = 1.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            },
            ComposableContentBuilder {
                IntTextField(
                    value = mana,
                    onValueChange = { onUpdate(copy(mana = it)) },
                    modifier = Modifier.padding(2.dp).padding(start = 1.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        )
    } else {
        emptyList()
    } + listOf(
        if (img.isUnspecified()) ComposableContentBuilder {
            Spacer(Modifier.fillMaxSize().background(Color.Gray).clickable { updateImage(onUpdate) })
        } else ImageButtonBuilder(
            content = img.toBitmap(),
            onClick = { updateImage(onUpdate) },
            tinted = false
        ),
        ImageButtonBuilder(
            content = imageFromIconRes("confirm_icon"),
            onClick = {
                viewModel.onEditConfirmed(this).onSuccess { viewModel.onEditDone() }.onFailure {
                    if (it.message != null)
                        showMessage(it.message!!, messageType = MessageType.WARNING)
                }
            }
        ),
        ImageButtonBuilder(
            content = imageFromIconRes("exit_icon"),
            onClick = { viewModel.onEditDone() }
        )
    )
}

private fun (BlueprintData).updateImage(onUpdate: (BlueprintData) -> Unit) {
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