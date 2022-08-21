package fr.olebo.sharescene.utils

import kotlinx.browser.window

val isPortrait
    get() = window.matchMedia("(orientation: portrait)").matches