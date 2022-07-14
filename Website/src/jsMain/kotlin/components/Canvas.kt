package fr.olebo.sharescene.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import dev.petuska.kmdc.core.MDCSideEffectNew
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
) = TagElement(
    elementBuilder = canvas,
    applyAttrs = attrs,
    content = {
        if (content != null)
            content()

        DisposableEffect(js("{}")) {
            val element = scopeElement

            element.width = window.innerWidth
            element.height = window.innerHeight

            val context = element.getContext("2d") as CanvasRenderingContext2D

            context.clearRect(0.0, 0.0, element.width.toDouble(), element.height.toDouble())

            element.drawWith(context)

            onDispose { }
        }
    }
)

fun HTMLCanvasElement.relativeX(absoluteX: Int) = absoluteX * width / 1600.0

fun HTMLCanvasElement.relativeY(absoluteY: Int) = absoluteY * height / 900.0