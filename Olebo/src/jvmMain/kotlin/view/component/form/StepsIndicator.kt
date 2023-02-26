package jdr.exia.view.component.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun StepsIndicator(numberOfSteps: Int, currentStep: Int, modifier: Modifier = Modifier) = Row(
    horizontalArrangement = Arrangement.Center,
    modifier = modifier then Modifier.fillMaxWidth()
) {
    for (i in 1..numberOfSteps) {
        Step(i, i == currentStep)
        Spacer(Modifier.width(16.dp))
    }
}

@Composable
private fun Step(index: Int, isSelected: Boolean) = Box(
    modifier = Modifier.size(24.dp).clip(CircleShape)
        .background(if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = .12f)),
    contentAlignment = Alignment.Center
) {
    Text(index.toString())
}