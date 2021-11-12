package jdr.exia.view.composable.master

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WebConfig(connect: (String) -> Unit) = Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
    Row {
        var code by remember { mutableStateOf("") }

        TextField(value = code, onValueChange = { code = it }, modifier = Modifier.padding(8.dp))

        Button(onClick = { connect(code) }, modifier = Modifier.padding(8.dp), enabled = code.isNotBlank()) {
            Text("Ok")
        }
    }
}