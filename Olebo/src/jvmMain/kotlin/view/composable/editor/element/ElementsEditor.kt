package jdr.exia.view.composable.editor.element

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jdr.exia.localization.*
import jdr.exia.model.act.Act
import jdr.exia.model.element.TypeElement
import jdr.exia.model.tools.success
import jdr.exia.model.type.Image
import jdr.exia.view.component.*
import jdr.exia.view.component.contentListRow.*
import jdr.exia.view.component.form.IntTextField
import jdr.exia.view.component.form.TextTrailingIcon
import jdr.exia.view.composable.editor.TagsAssociation
import jdr.exia.view.tools.BorderBuilder
import jdr.exia.view.tools.MessageType
import jdr.exia.view.tools.showMessage
import jdr.exia.view.tools.toBorderStroke
import jdr.exia.view.ui.backgroundImageColor
import jdr.exia.view.ui.roundedBottomShape
import jdr.exia.view.ui.roundedTopShape
import jdr.exia.view.window.LocalPopup
import jdr.exia.viewModel.holder.BlueprintData
import jdr.exia.viewModel.holder.isCharacter
import jdr.exia.viewModel.elements.ElementsEditorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * Panel to view, create and edit elements
 */
@Composable
fun ElementsView(initialAct: Act? = null, title: String? = null, onDone: () -> Unit) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.background(MaterialTheme.colors.secondary)
) {
    val tabs = remember { listOf(TypeElement.Object, TypeElement.PJ, TypeElement.PNJ) }

    val contentViewModel = remember(initialAct) { ElementsEditorViewModel(initialAct, tabs.first()) }

    if (title != null) {
        Title(title)
    }

    ActDropDown(contentViewModel.selectedAct, contentViewModel::selectedAct::set)

    TabPanel(
        backgroundColor = MaterialTheme.colors.secondaryVariant,
        tabs = tabs,
        onTabChanged = { contentViewModel.currentType = it },
        headerTabOption = HeaderTabOptions(backgroundColor = MaterialTheme.colors.secondary, paddingHeight = 8.dp),
        content = { currentTab, padding ->
            Content(
                viewModel = contentViewModel,
                innerPadding = padding,
                currentType = currentTab
            )
        },
        footer = {
            FooterRowWithCancel(
                confirmText = StringLocale[STR_SUBMIT],
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
                    Card(
                        modifier = Modifier.padding(end = 20.dp, start = 20.dp, bottom = 20.dp)
                            .fillMaxSize(),
                        border = BorderBuilder.defaultBorder.toBorderStroke(),
                        shape = roundedBottomShape
                    ) {
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
                }

                else -> ItemList(viewModel)
            }
        }
    }

@Composable
private fun Title(title: String) = Surface(
    Modifier.fillMaxWidth().padding(vertical = 10.dp),
    color = MaterialTheme.colors.secondary
) {
    Box(contentAlignment = Alignment.Center) {
        Text(title, fontWeight = FontWeight.Bold)
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
        buttonBuilders = {
            if (viewModel.blueprints.isNotEmpty()) {
                if (currentType != TypeElement.Object) {
                    ContentButtonBuilder(
                        content = StringLocale[STR_HP],
                        enabled = false
                    )

                    ContentButtonBuilder(
                        content = StringLocale[STR_MP],
                        enabled = false
                    )
                }
                ContentButtonBuilder(
                    content = StringLocale[STR_IMG],
                    enabled = false
                )

                EmptyContent()
            }

            IconButtonBuilder(
                content = Icons.Outlined.Add,
                onClick = {
                    viewModel.startBlueprintCreation()
                    scope.launch {
                        delay(100) // If we don't add a delay, the scroll does not always happen.
                        viewModel.itemListScrollState.scrollToItem(0)
                    }
                }
            )

        }
    )
}

@Composable
private fun ColumnScope.ItemList(viewModel: ElementsEditorViewModel) = Card(
    modifier = contentModifier,
    border = BorderBuilder.defaultBorder.toBorderStroke(),
    shape = roundedBottomShape
) {
    LazyScrollableColumn(scrollState = viewModel.itemListScrollState) {
        items(viewModel.blueprints, key = { it }) { blueprint ->
            ItemDescription(viewModel, blueprint)
        }
    }
}

@Composable
private fun ItemDescription(
    viewModel: ElementsEditorViewModel,
    blueprint: BlueprintData
) = Column {
    var editedData by (viewModel.currentEditBlueprint == blueprint).let { isEditing ->
        remember(isEditing) { mutableStateOf(blueprint.takeIf { isEditing }) }
    }

    ContentListRow(
        content = {
            editedData.let { data ->
                if (data == null) {
                    ContentText(blueprint.name)
                } else {
                    val popup = LocalPopup.current!!

                    CustomTextField(
                        value = data.name,
                        onValueChange = { editedData = data.copy(name = it) },
                        placeholder = blueprint.name,
                        modifier = Modifier.padding(horizontal = 4.dp).fillMaxWidth(),
                        focused = data.id == null,
                        trailingIcon = {
                            TextTrailingIcon(
                                icon = Icons.Outlined.LibraryAdd,
                                tooltipMessage = StringLocale[STR_ASSOCIATE_TAGS],
                                onClick = {
                                    popup.content = {
                                        TagsAssociation(
                                            nameOfAssociated = data.name,
                                            selection = data.tags,
                                            tags = viewModel.tagsAsString,
                                            onConfirm = { newTags, tagsToDelete, selectedTags ->
                                                viewModel.createTags(newTags)
                                                viewModel.deleteTags(tagsToDelete)
                                                editedData = editedData?.copy(tags = selectedTags)
                                                popup.close()
                                            }
                                        )
                                    }
                                }
                            )
                        }
                    )
                }
            }
        },
        buttonBuilders = {
            Buttons(
                from = editedData,
                viewModel = viewModel,
                blueprint = blueprint,
                onUpdate = { editedData = it }
            )
        }
    )
}

@Composable
private fun RowButtonScope.Buttons(
    from: BlueprintData?,
    viewModel: ElementsEditorViewModel,
    blueprint: BlueprintData,
    onUpdate: (BlueprintData) -> Unit
) {
    if (from == null) {
        if (blueprint.isCharacter()) {
            ContentButtonBuilder(content = blueprint.life ?: 0)
            ContentButtonBuilder(content = blueprint.mana ?: 0)
        }

        ImageButtonBuilder(
            content = blueprint.img.toBitmap(),
            backgroundColor = MaterialTheme.colors.backgroundImageColor
        )

        IconButtonBuilder(
            content = Icons.Outlined.Edit,
            onClick = { viewModel.onEditItemSelected(blueprint) }
        )

        IconButtonBuilder(
            content = Icons.Outlined.Delete,
            onClick = { viewModel.onRemoveBlueprint(blueprint) }
        )

    } else {
        if (blueprint.isCharacter()) {
            ComposableContentBuilder {
                IntTextField(
                    value = from.life,
                    onValueChange = { onUpdate(from.copy(life = it)) },
                    modifier = Modifier.padding(2.dp).padding(start = 1.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }

            ComposableContentBuilder {
                IntTextField(
                    value = from.mana,
                    onValueChange = { onUpdate(from.copy(mana = it)) },
                    modifier = Modifier.padding(2.dp).padding(start = 1.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }

        }

        if (from.img.isUnspecified()) {
            ComposableContentBuilder {
                Spacer(Modifier.fillMaxSize().background(Color.Gray).clickable { from.updateImage(onUpdate) })
            }
        } else {
            ImageButtonBuilder(
                content = from.img.toBitmap(),
                onClick = { from.updateImage(onUpdate) },
                tinted = false
            )

            IconButtonBuilder(
                content = Icons.Outlined.Done,
                onClick = {
                    viewModel.onEditConfirmed(from).onSuccess { viewModel.onEditDone() }.onFailure {
                        if (it.message != null)
                            showMessage(it.message!!, messageType = MessageType.WARNING)
                    }
                }
            )

            IconButtonBuilder(
                content = Icons.Outlined.Close,
                onClick = { viewModel.onEditDone() }
            )
        }

    }
}

private fun BlueprintData.updateImage(onUpdate: (BlueprintData) -> Unit) {
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