@file:Suppress("FunctionName")

package jdr.exia.view.composable.master

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import jdr.exia.model.element.Priority
import jdr.exia.model.element.Size
import jdr.exia.model.tools.isCharacter
import jdr.exia.model.tools.withSetter
import jdr.exia.view.element.CustomTextField
import jdr.exia.view.element.IntTextField
import jdr.exia.view.element.TitledDropdownMenu
import jdr.exia.view.tools.DefaultFunction
import jdr.exia.view.tools.withFocusCursor
import org.jetbrains.exposed.sql.transactions.transaction

@Composable
fun SelectedEditor(
    modifier: Modifier,
    commandManager: CommandManager,
    selectedElements: List<Element>,
    deleteSelectedElement: DefaultFunction,
    repaint: DefaultFunction
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    ImagePreview(selectedElements)

    ColumnEditor {
        val size = 250.dp

        NameElement(selectedElements = selectedElements, modifier = Modifier.width(size))
        LabelField(selectedElements = selectedElements, repaint = repaint, modifier = Modifier.width(size))
    }

    ColumnEditor {
        SizeSelector(selectedElements = selectedElements, repaint = repaint, commandManager = commandManager)
        LayerSelector(selectedElements = selectedElements, repaint = repaint)
    }

    OrientationButtons(selectedElements = selectedElements, repaint = repaint, commandManager = commandManager)

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
private fun ImagePreview(selectedElements: List<Element>) {
    val borderColor = if (selectedElements.all { it.isVisible }) Color.Black else Color.Blue

    val modifier = Modifier.padding(15.dp).size(150.dp).border(BorderStroke(10.dp, borderColor))

    if (selectedElements.size == 1) {
        Image(
            bitmap = selectedElements[0].spriteBitmap,
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
private fun LabelField(selectedElements: List<Element>, repaint: DefaultFunction, modifier: Modifier) {
    if (selectedElements.size == 1) {
        var value by remember(selectedElements.size, selectedElements.firstOrNull()) {
            if (selectedElements.size == 1) {
                val element = selectedElements.first()

                mutableStateOf(element.alias) withSetter {
                    transaction { element.alias = it }
                    repaint()
                }
            } else mutableStateOf("")
        }

        CustomTextField(
            value = value,
            onValueChange = {
                if (selectedElements.size == 1 && it.length <= 20)
                    value = it
            },
            placeholder = StringLocale[STR_LABEL],
            modifier = modifier.withFocusCursor()
        )
    } else {
        Text(StringLocale[STR_LABEL], modifier = modifier)
    }
}

private fun <T> List<Element>.getElementProperty(elementPropertyGetter: Element.() -> T, defaultValue: T) = when {
    this.isEmpty() -> defaultValue
    this.size == 1 -> this.first().elementPropertyGetter()
    else -> this.groupingBy(elementPropertyGetter).eachCount().maxByOrNull { it.value }?.key ?: defaultValue
}

@Composable
private fun SizeSelector(selectedElements: List<Element>, repaint: DefaultFunction, commandManager: CommandManager) {
    var selectedSize by remember(selectedElements) {
        mutableStateOf(
            selectedElements.getElementProperty(
                elementPropertyGetter = Element::size,
                defaultValue = Size.DEFAULT
            )
        ) withSetter { newSize ->
            Element.cmdDimension(newSize, commandManager, selectedElements)
            repaint()
        }
    }

    val isEnabled = selectedElements.isNotEmpty()

    TitledDropdownMenu(
        title = StringLocale[STR_SIZE],
        items = Size.values(),
        onValueChanged = { selectedSize = it },
        selectedItem = selectedSize,
        isEnabled = isEnabled
    )
}

@Composable
private fun LayerSelector(selectedElements: List<Element>, repaint: DefaultFunction) {
    var selectedLayer by remember(selectedElements) {
        mutableStateOf(
            selectedElements.getElementProperty(
                elementPropertyGetter = Element::priority,
                defaultValue = Priority.REGULAR
            )
        ) withSetter { newPriority ->
            selectedElements.forEach { it.priority = newPriority }
            repaint()
        }
    }

    val isEnabled = selectedElements.isNotEmpty()

    TitledDropdownMenu(
        title = StringLocale[STR_PRIORITY],
        items = Priority.values(),
        onValueChanged = { selectedLayer = it },
        selectedItem = selectedLayer,
        isEnabled = isEnabled
    )
}

@Composable
private fun OrientationButtons(
    selectedElements: List<Element>,
    repaint: DefaultFunction,
    commandManager: CommandManager
) = ColumnEditor {
    OutlinedButton(
        onClick = {
            Element.cmdOrientationToRight(commandManager, selectedElements)
            repaint()
        },
        modifier = Modifier.width(buttonsWidth),
        enabled = selectedElements.isNotEmpty()
    ) { Text(StringLocale[STR_ROTATE_TO_RIGHT]) }

    OutlinedButton(
        onClick = {
            Element.cmdOrientationToRight(commandManager, selectedElements)
            repaint()
        },
        modifier = Modifier.width(buttonsWidth),
        enabled = selectedElements.isNotEmpty()
    ) { Text(StringLocale[STR_ROTATE_TO_LEFT]) }
}

@Composable
private fun VisibilityButtons(
    selectedElements: List<Element>,
    deleteSelectedElement: DefaultFunction,
    repaint: DefaultFunction,
    commandManager: CommandManager
) = ColumnEditor {
    var isVisible by remember(selectedElements) {
        mutableStateOf(
            selectedElements.getElementProperty(
                elementPropertyGetter = Element::isVisible,
                defaultValue = true
            )
        ) withSetter { newVisiblity ->
            Element.cmdVisiblity(newVisiblity, commandManager, selectedElements)
            repaint()
        }
    }

    val visibilityText = if (selectedElements.isEmpty()) {
        StringLocale[STR_VISIBILITY]
    } else {
        StringLocale[if (isVisible) STR_HIDE else STR_SHOW]
    }

    OutlinedButton(
        onClick = { isVisible = !isVisible },
        modifier = Modifier.width(buttonsWidth),
        enabled = selectedElements.isNotEmpty()
    ) { Text(visibilityText) }

    OutlinedButton(
        onClick = deleteSelectedElement,
        modifier = Modifier.width(buttonsWidth),
        enabled = selectedElements.isNotEmpty()
    ) { Text(StringLocale[STR_DELETE]) }
}

@Composable
private fun LifeField(selectedElements: List<Element>) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.width(200.dp)
) {
    if (selectedElements.size == 1 && selectedElements.first().isCharacter()) {
        val element = selectedElements.first()

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
    } else {
        Text("0 / 0 ${StringLocale[STR_MP]}", modifier = Modifier.fillMaxWidth())
    }
}
