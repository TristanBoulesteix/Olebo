package jdr.exia.view.composable.editor.act

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddLink
import androidx.compose.material.icons.outlined.LibraryAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jdr.exia.localization.STR_ASSOCIATE_BLUEPRINT_AND_SCENARIO
import jdr.exia.localization.STR_ASSOCIATE_TAGS
import jdr.exia.localization.StringLocale
import jdr.exia.localization.get
import jdr.exia.model.act.Act
import jdr.exia.view.component.form.TextTrailingIcon
import jdr.exia.view.composable.editor.TagsAssociation
import jdr.exia.view.composable.editor.element.ElementsView
import jdr.exia.view.tools.addRoundedBorder
import jdr.exia.view.ui.roundedShape
import jdr.exia.view.window.LocalPopup
import jdr.exia.viewModel.ActEditorViewModel

@Composable
fun IconEditTags(viewModel: ActEditorViewModel) {
    val popup = LocalPopup.current ?: return

    TextTrailingIcon(
        icon = Icons.Outlined.LibraryAdd,
        tooltipMessage = StringLocale[STR_ASSOCIATE_TAGS],
        onClick = {
            popup.content = {
                TagsAssociation(
                    nameOfAssociated = viewModel.actName,
                    selection = viewModel.tags,
                    tags = viewModel.tagsAsString,
                    onConfirm = { newTags, tagsToDelete, selectedTags ->
                        viewModel.createTags(newTags)
                        viewModel.deleteTags(tagsToDelete)
                        viewModel.tags = selectedTags
                        popup.close()
                    }
                )
            }
        }
    )
}

@Composable
fun IconEditAssociatedBlueprints(act: Act?) {
    val popup = LocalPopup.current ?: return

    TextTrailingIcon(
        icon = Icons.Outlined.AddLink,
        tooltipMessage = StringLocale[STR_ASSOCIATE_BLUEPRINT_AND_SCENARIO],
        onClick = {
            popup.content = {
                Surface(
                    Modifier.fillMaxSize(.9f).addRoundedBorder(MaterialTheme.colors.primary),
                    shape = roundedShape
                ) {
                    ElementsView(
                        title = StringLocale[STR_ASSOCIATE_BLUEPRINT_AND_SCENARIO],
                        initialAct = act
                    ) {
                        popup.close()
                    }
                }
            }
        }
    )
}