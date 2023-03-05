package jdr.exia.view.composable.editor.act

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jdr.exia.model.tools.success
import jdr.exia.view.component.FooterRowWithCancel
import jdr.exia.view.component.HeaderRow
import jdr.exia.view.component.form.StepsIndicator
import jdr.exia.view.tools.BorderBuilder
import jdr.exia.view.tools.toBorderStroke
import jdr.exia.view.ui.roundedTopShape
import jdr.exia.viewModel.editor.ActCreatorViewModel

@Composable
fun ActCreator(onDone: () -> Unit) = Column {
    val viewModel = remember { ActCreatorViewModel() }

    Header(ActCreatorViewModel.steps, viewModel.currentStep, viewModel.headerText)

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.secondaryVariant).padding(15.dp)) {
        Card(
            modifier = Modifier.padding(top = 20.dp, end = 20.dp, start = 20.dp).fillMaxWidth(),
            border = BorderBuilder.defaultBorder.toBorderStroke(),
            shape = roundedTopShape
        ) {
            Text("test")
        }

        FooterRowWithCancel(
            onDone = onDone,
            onConfirm = { Result.success }
        )
    }
}

@Suppress("SameParameterValue")
@Composable
private fun Header(steps: Int, currentStep: Int, titleHeader: String) = HeaderRow {
    Surface(color = Color.Transparent, contentColor = MaterialTheme.colors.onSurface) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            StepsIndicator(steps, currentStep)
            Spacer(Modifier.height(12.dp))
            Text(titleHeader)
        }
    }
}