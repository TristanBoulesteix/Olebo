package fr.olebo.sharescene.html

import kotlinx.html.*

fun HTML.shareSceneUi() {
    val contentStyle = "height: 100%; width: 100%; margin: 0"

    style = contentStyle

    head {
        title("ShareScene login")
    }
    body {
        style = contentStyle

        script(src = "/static/Website.js") { }
    }
}