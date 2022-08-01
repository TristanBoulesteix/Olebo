package jdr.exia.service

import androidx.compose.ui.platform.UriHandler
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private fun encodeStringToSend(string: String) = URLEncoder.encode(string, StandardCharsets.UTF_8).replace("+", "%20")

fun UriHandler.sendMailToDevelopers(subject: String, body: String = "") =
    openUri("mailto:contact.olebo@tb-lab.fr?subject=${encodeStringToSend(subject)}&body=${encodeStringToSend(body)}")