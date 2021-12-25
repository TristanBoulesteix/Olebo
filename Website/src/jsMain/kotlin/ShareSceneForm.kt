package fr.olebo.sharescene

import androidx.compose.runtime.*
import dev.petuska.kmdc.button.MDCButton
import dev.petuska.kmdc.button.MDCButtonOpts
import fr.olebo.sharescene.components.MaterialTextField
import fr.olebo.sharescene.css.ShareSceneStyleSheet
import fr.olebo.sharescene.css.classes
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun ShareSceneForm(connect: suspend (userName: String, sessionCode: String) -> Unit) =
    Div(attrs = classes(ShareSceneStyleSheet.mainContainer)) {
        Div(attrs = classes(ShareSceneStyleSheet.formTitle)) { Text("Olebo ShareScene") }

        Div(attrs = classes(ShareSceneStyleSheet.formContent, ShareSceneStyleSheet.boxContainer)) {
            var sessionCode by remember { mutableStateOf("") }
            var userName by remember { mutableStateOf("") }

            val scope = rememberCoroutineScope()

            MaterialTextField(label = "Code de session :", value = sessionCode, onValueChange = { sessionCode = it })
            MaterialTextField(label = "Nom de joueur :", value = userName, onValueChange = { userName = it })

            MDCButton(text = "Démarrer", opts = { type = MDCButtonOpts.Type.Outlined }) {
                onClick {
                    scope.launch {
                        connect(userName, sessionCode)
                    }
                }

                if (sessionCode.isBlank() || userName.isBlank())
                    disabled()
            }
        }
    }
