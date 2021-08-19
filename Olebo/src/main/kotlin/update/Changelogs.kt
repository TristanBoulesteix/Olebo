package jdr.exia.update

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import jdr.exia.main
import jdr.exia.view.element.builder.ContentButtonBuilder
import jdr.exia.view.element.dialog.PromptDialog
import java.io.InputStreamReader

fun getChangelogs(): String? =
    ::main.javaClass.classLoader.getResourceAsStream("changelogs.txt")?.reader()?.use(InputStreamReader::readText)

@Composable
fun Changelogs(changelogs: String) {
    var changelogsVisible by remember { mutableStateOf(true) }

    PromptDialog(
        visible = changelogsVisible,
        title = "Changelogs",
        buttonBuilders = listOf(ContentButtonBuilder(content = "Ok", onClick = { changelogsVisible = false })),
        onCloseRequest = { changelogsVisible = false },
        height = 300.dp,
        width = 500.dp
    ) {
        Column {
            Text("Changelogs :", fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)

            val scrollState = rememberScrollState()

            Box {
                Text(changelogs, modifier = Modifier.fillMaxWidth().verticalScroll(state = scrollState))
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(scrollState)
                )
            }
        }
    }
}

