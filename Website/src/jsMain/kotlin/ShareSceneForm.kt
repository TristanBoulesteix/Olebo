package fr.olebo.sharescene

import androidx.compose.runtime.*
import fr.olebo.sharescene.components.MaterialButton
import fr.olebo.sharescene.components.MaterialTextField
import fr.olebo.sharescene.css.ShareSceneStyleSheet
import fr.olebo.sharescene.css.classes
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun ShareSceneForm(
    connect: suspend (userName: String, sessionCode: String) -> Unit
) = Div(attrs = classes(ShareSceneStyleSheet.mainContainer)) {
    Div(attrs = classes(ShareSceneStyleSheet.formTitle)) { Text("Olebo ShareScene") }

    Div(attrs = classes(ShareSceneStyleSheet.formContent, ShareSceneStyleSheet.boxContainer)) {
        var sessionCode by remember { mutableStateOf(sessionCodeOnURL) }
        var userName by remember { mutableStateOf("") }

        MaterialTextField(label = "Code de session :", value = sessionCode, onValueChange = { sessionCode = it })
        MaterialTextField(label = "Nom de joueur :", value = userName, onValueChange = { userName = it })

        MaterialButton(text = "Démarrer", enabled = sessionCode.isBlank() || userName.isBlank()) {
            MainScope().launch {
                connect(userName, sessionCode)
            }
        }
    }
}

private val sessionCodeOnURL
    get() = document.location?.pathname?.split('/')?.last().takeIf { it != "share-scene" }.orEmpty()