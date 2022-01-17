package fr.olebo.sharescene.components

import androidx.compose.runtime.Composable
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

private val canvas = ElementBuilder.createBuilder<HTMLCanvasElement>("canvas")

@Composable
fun Canvas(
    attrs: AttrBuilderContext<HTMLCanvasElement>? = null,
    drawWith: HTMLCanvasElement.(CanvasRenderingContext2D) -> Unit,
    content: ContentBuilder<HTMLCanvasElement>? = null
) {
    TagElement(
        elementBuilder = canvas,
        applyAttrs = attrs,
        content = {
            if(content != null)
                content()

            DomSideEffect {
                it.width = window.innerWidth
                it.height = window.innerHeight

                val context = it.getContext("2d") as CanvasRenderingContext2D

                context.clearRect(0.0, 0.0, it.width.toDouble(), it.height.toDouble())

                it.drawWith(context)
            }
        }
    )
}

fun HTMLCanvasElement.relativeX(absoluteX: Int) = absoluteX * width / 1600.0

fun HTMLCanvasElement.relativeY(absoluteY: Int) = absoluteY * height / 900.0