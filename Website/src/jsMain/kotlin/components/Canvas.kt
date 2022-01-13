package fr.olebo.sharescene.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

private val canvas = ElementBuilder.createBuilder<HTMLCanvasElement>("canvas")

@Composable
fun Canvas(
    attrs: AttrBuilderContext<HTMLCanvasElement>? = null,
    draw: CanvasRenderingContext2D.(canvasWidth: Double, canvasHeight: Double) -> Unit
) {
    TagElement(
        elementBuilder = canvas,
        applyAttrs = attrs,
        content = {
            DomSideEffect {
                val context = it.getContext("2d") as CanvasRenderingContext2D

                context.clearRect(0.0, 0.0, it.width.toDouble(), it.height.toDouble())

                context.draw(it.width.toDouble(), it.height.toDouble())
            }
        }
    )
}