@file:Suppress("FunctionName")

package jdr.exia.view.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jdr.exia.localization.STR_CANCEL
import jdr.exia.localization.STR_CONFIRM
import jdr.exia.localization.STR_SCENES
import jdr.exia.localization.Strings
import jdr.exia.model.act.Act
import jdr.exia.model.act.Act.Companion.isEqualTo
import jdr.exia.view.compose.components.ButtonBuilder
import jdr.exia.view.compose.components.ContentRow
import jdr.exia.view.compose.ui.BorderInlined
import jdr.exia.view.compose.ui.blue
import jdr.exia.view.compose.ui.border
import jdr.exia.view.compose.ui.lightOrange
import jdr.exia.viewModel.ActEditorViewModel

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
        Column(modifier = Modifier.padding(20.dp).weight(1f).fillMaxSize()) {

            ContentRow(
                contentText = Strings[STR_SCENES],
                modifier = Modifier.background(Color.White).border(BorderInlined.defaultBorder),
                buttonBuilders = listOf(ButtonBuilder(imageFromIcon("create_icon")) { })
            )

            LazyColumn(
                modifier = Modifier.background(Color.White)
                    .weight(3f)
                    .fillMaxSize()
                    .border(BorderInlined.defaultBorder)
            ) {
                items(items = viewModel.scenes) {
                    if (viewModel.currentEditScene isEqualTo it) {
                        EditSceneRow(it)
                    } else {
                        ContentRow(
                            contentText = it.name,
                            buttonBuilders = listOf(
                                ButtonBuilder(
                                    imageFromIcon("edit_icon"),
                                    onClick = { viewModel.onEditItemSelected(it) }),
                                ButtonBuilder(imageFromIcon("delete_icon"), onClick = {})
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
                Text(text = Strings[STR_CONFIRM])
            }
            OutlinedButton(onClick = onDone) {
                Text(text = Strings[STR_CANCEL])
            }
        }
    }
}

@Composable
private fun EditSceneRow(sceneData: Act.SceneData? = null) {

}