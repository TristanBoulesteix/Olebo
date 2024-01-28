package jdr.exia.update

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jdr.exia.OLEBO_VERSION_CODE
import jdr.exia.OLEBO_VERSION_NAME
import jdr.exia.localization.STR_RELEASE_NOTES
import jdr.exia.localization.StringLocale
import jdr.exia.localization.get
import jdr.exia.main
import jdr.exia.view.component.contentListRow.ContentButtonBuilder
import jdr.exia.view.component.dialog.MessageDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

suspend fun getChangelogs(): String? = withContext(Dispatchers.IO) {
    StringLocale.getLocalizedResource("changelogs", "txt", ::main.javaClass.classLoader)?.reader()
        ?.use(InputStreamReader::readText)
}


@Composable
fun ChangelogsDialog(changelogs: String, onClose: () -> Unit = {}) {
    var changelogsVisible by remember { mutableStateOf(true) }

    LaunchedEffect(changelogsVisible) {
        if (!changelogsVisible)
            onClose()
    }

    MessageDialog(
        visible = changelogsVisible,
        title = StringLocale[STR_RELEASE_NOTES],
        buttonsBuilder = { ContentButtonBuilder(content = "Ok", onClick = { changelogsVisible = false }) },
        onCloseRequest = { changelogsVisible = false },
        height = 550.dp,
        width = 600.dp
    ) {
        Column {
            Text(
                StringLocale[STR_RELEASE_NOTES],
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                fontSize = 30.sp
            )

            Spacer(Modifier.height(10.dp))

            Text(
                "Version $OLEBO_VERSION_NAME - Code $OLEBO_VERSION_CODE",
                fontSize = 10.sp,
                fontStyle = FontStyle.Italic
            )

            Spacer(Modifier.height(10.dp))

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

