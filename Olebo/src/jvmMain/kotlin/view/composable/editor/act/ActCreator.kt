package jdr.exia.view.composable.editor.act

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jdr.exia.localization.STR_NEW_ACT
import jdr.exia.localization.StringLocale
import jdr.exia.localization.get
import jdr.exia.view.component.HeaderRow

@Composable
fun ActCreator(onDone: () -> Unit) = Column {
    Header()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.secondaryVariant).padding(15.dp)) {
        Text("test")
    }
}

@Composable
private fun Header() {
    HeaderRow {
        Surface(color = Color.Transparent, contentColor = MaterialTheme.colors.onSurface) {
            Text(StringLocale[STR_NEW_ACT])
        }
    }
}