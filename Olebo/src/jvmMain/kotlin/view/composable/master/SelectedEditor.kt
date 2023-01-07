package jdr.exia.view.composable.master

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jdr.exia.localization.*
import jdr.exia.model.command.CommandManager
import jdr.exia.model.element.Element
import jdr.exia.model.element.Layer
import jdr.exia.model.element.SizeElement
import jdr.exia.model.tools.isCharacter
import jdr.exia.model.tools.settableMutableState
import jdr.exia.view.component.CustomTextField
import jdr.exia.view.component.form.IntTextField
import jdr.exia.view.tools.applyIf
import org.jetbrains.exposed.sql.transactions.transaction

@Composable
fun SelectedEditor(
    commandManager: CommandManager,
    selectedElements: List<Element>,
    deleteSelectedElement: () -> Unit,
    setPriority: (Layer) -> Unit,
    repaint: () -> Unit
) = Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    ImagePreview(selectedElements = selectedElements, commandManager = commandManager)

    ColumnEditor {
        val size = 225.dp

        NameElement(selectedElements = selectedElements, modifier = Modifier.width(size))
        LabelField(selectedElements = selectedElements, repaint = repaint, modifier = Modifier.width(size))
    }

    ColumnEditor {
        SizeSelector(selectedElements = selectedElements, repaint = repaint, commandManager = commandManager)
        LayerSelector(selectedElements = selectedElements, setPriority = setPriority)
    }

    VisibilityButtons(
        selectedElements = selectedElements,
        repaint = repaint,
        commandManager = commandManager,
        deleteSelectedElement = deleteSelectedElement
    )

    ColumnEditor {
        LifeField(selectedElements)
        ManaField(selectedElements)
    }
}

private val buttonsWidth = 200.dp

@Composable
inline fun ColumnEditor(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) = Column(
    modifier = modifier.padding(start = 30.dp).fillMaxHeight(),
    verticalArrangement = Arrangement.SpaceAround,
    content = content
)

@Composable
private fun ImagePreview(selectedElements: List<Element>, commandManager: CommandManager) {
    val allElementsAreHidden = remember(selectedElements, commandManager.composeKey) {
        selectedElements.isNotEmpty() && selectedElements.none { it.isVisible }
    }

    val modifier = Modifier.padding(15.dp).size(150.dp).background(Color.LightGray).applyIf(allElementsAreHidden) {
        Modifier.border(BorderStroke(3.dp, Color.Blue))
    }

    if (selectedElements.size == 1) {
        Image(
            bitmap = selectedElements.first().sprite,
            contentDescription = null,
            modifier = modifier
        )
    } else {
        Spacer(modifier = modifier)
    }
}

@Composable
private fun NameElement(selectedElements: List<Element>, modifier: Modifier) {
    val text = when {
        selectedElements.isEmpty() -> StringLocale[STR_NO_SELECTED_ELEMENT]
        selectedElements.size == 1 -> selectedElements.first().name
        else -> "${selectedElements.size} ${StringLocale[STR_SELECTED_ELEMENTS, StringStates.NORMAL]}"
    }

    Text(text, modifier)
}

@Composable
private fun LabelField(selectedElements: List<Element>, repaint: () -> Unit, modifier: Modifier) {
    if (selectedElements.size == 1) {
        var value by remember(selectedElements) { mutableStateOf(selectedElements.first().alias) }

        CustomTextField(
            value = value,
            onValueChange = {
                if (selectedElements.size == 1 && it.length <= 20) {
                    value = it

                    val element = selectedElements.first()

                    transaction { element.alias = it }
                    repaint()
                }
            },
            placeholder = StringLocale[STR_LABEL],
            modifier = modifier
        )
    } else {
        Text(StringLocale[STR_LABEL], modifier = modifier)
    }
}

private fun <T> List<Element>.getElementProperty(elementPropertyGetter: Element.() -> T, defaultValue: T) = when {
    isEmpty() -> defaultValue
    size == 1 -> first().elementPropertyGetter()
    else -> groupingBy(elementPropertyGetter).eachCount().maxByOrNull { it.value }?.key ?: defaultValue
}

@Composable
private fun SizeSelector(selectedElements: List<Element>, repaint: () -> Unit, commandManager: CommandManager) = Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.width(180.dp)
) {
    Text(StringLocale[STR_SIZE])

    val width = 120.dp

    var expanded by remember { mutableStateOf(false) }

    var size by remember(selectedElements) {
        mutableStateOf(
            selectedElements.getElementProperty(
                elementPropertyGetter = Element::size,
                defaultValue = SizeElement.DEFAULT
            )
        )
    }

    Box(
        Modifier.size(width = width, height = 30.dp).wrapContentSize(Alignment.CenterStart)
            .background(Color.DarkGray).clickable(onClick = { expanded = true })
    ) {
        Text(size.toString(), Modifier.fillMaxWidth().padding(5.dp))

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(width)
        ) {
            val sizes = remember(SizeElement::values)

            sizes.forEach {
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        size = it
                        Element.cmdDimension(it, commandManager, selectedElements)
                        repaint()
                    },
                    content = {
                        Text(it.toString())
                    }
                )
            }
        }
    }
}

@Composable
private fun LayerSelector(selectedElements: List<Element>, setPriority: (Layer) -> Unit) = Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.width(180.dp)
) {
    Text(StringLocale[STR_PRIORITY])

    val width = 120.dp

    var expanded by remember { mutableStateOf(false) }

    var layer by remember(selectedElements) {
        mutableStateOf(
            selectedElements.getElementProperty(
                elementPropertyGetter = Element::priority,
                defaultValue = Layer.REGULAR
            )
        )
    }

    Box(
        Modifier.size(width = width, height = 30.dp).wrapContentSize(Alignment.CenterStart)
            .background(Color.DarkGray).clickable(onClick = { expanded = true })
    ) {
        Text(layer.toString(), Modifier.fillMaxWidth().padding(5.dp))

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(width)
        ) {
            val layers = remember(Layer::values)

            layers.forEach {
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        layer = it
                        setPriority(it)
                    },
                    content = {
                        Text(it.toString())
                    }
                )
            }
        }
    }
}

@Composable
private fun VisibilityButtons(
    selectedElements: List<Element>,
    deleteSelectedElement: () -> Unit,
    repaint: () -> Unit,
    commandManager: CommandManager
) = ColumnEditor {
    var isVisible by remember(selectedElements, commandManager.composeKey) {
        settableMutableState(
            selectedElements.getElementProperty(
                elementPropertyGetter = Element::isVisible,
                defaultValue = true
            )
        ) { newVisibility ->
            Element.cmdVisibility(newVisibility, commandManager, selectedElements)
            repaint()
        }
    }

    val visibilityText = if (selectedElements.isEmpty()) {
        StringLocale[STR_VISIBILITY]
    } else {
        StringLocale[if (isVisible) STR_HIDE else STR_SHOW]
    }

    val isEnabled = selectedElements.isNotEmpty()

    OutlinedButton(
        onClick = { isVisible = !isVisible },
        modifier = Modifier.width(buttonsWidth),
        enabled = isEnabled
    ) { Text(visibilityText) }

    OutlinedButton(
        onClick = deleteSelectedElement,
        modifier = Modifier.width(buttonsWidth),
        enabled = isEnabled
    ) { Text(StringLocale[STR_DELETE]) }
}

@Composable
private fun LifeField(selectedElements: List<Element>) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.width(200.dp)
) {
    if (selectedElements.size == 1 && selectedElements.first().isCharacter()) {
        val element = selectedElements.first()

        key(element) {
            IntTextField(
                value = element.currentHealth,
                onValueChange = {
                    if (it != null) {
                        transaction { element.currentHealth = it }
                    }
                },
                maxSize = 3,
                modifier = Modifier.width(80.dp)
            )

            Text(" / ${element.maxHP} ${StringLocale[STR_HP]}", modifier = Modifier)
        }
    } else {
        Text("0 / 0 ${StringLocale[STR_HP]}", modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun ManaField(selectedElements: List<Element>) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.width(200.dp)
) {
    if (selectedElements.size == 1 && selectedElements.first().isCharacter()) {
        val element = selectedElements.first()

        key(element) {
            IntTextField(
                value = element.currentMana,
                onValueChange = {
                    if (it != null) {
                        transaction { element.currentMana = it }
                    }
                },
                maxSize = 3,
                modifier = Modifier.width(80.dp)
            )

            Text(" / ${element.maxMana} ${StringLocale[STR_MP]}", modifier = Modifier)
        }
    } else {
        Text("0 / 0 ${StringLocale[STR_MP]}", modifier = Modifier.fillMaxWidth())
    }
}
