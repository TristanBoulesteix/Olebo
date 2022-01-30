package fr.olebo.sharescene

import androidx.compose.runtime.*
import fr.olebo.sharescene.components.MaterialButton
import fr.olebo.sharescene.components.MaterialTextField
import fr.olebo.sharescene.connection.ConnectionState
import fr.olebo.sharescene.connection.Disconnected
import fr.olebo.sharescene.connection.Login
import fr.olebo.sharescene.css.ShareSceneStyleSheet
import fr.olebo.sharescene.css.classes
import jdr.exia.localization.*
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement

private const val PLAYER_NAME_FIELD_ID = "player_name_field_id"
private const val CONNECTION_BUTTON_ID = "connection_button_id"

@Composable
fun ShareSceneForm(
    connectionState: ConnectionState,
    setConnectionState: (ConnectionState) -> Unit,
    connect: suspend (userName: String, sessionCode: String) -> Unit
) = Div(attrs = classes(ShareSceneStyleSheet.mainContainer)) {
    Div(attrs = classes(ShareSceneStyleSheet.formTitle)) { Text("Olebo ShareScene") }

    Div(attrs = classes(ShareSceneStyleSheet.formContent, ShareSceneStyleSheet.boxContainer)) {
        var sessionCode by remember { mutableStateOf(sessionCodeOnURL) }
        var userName by remember { mutableStateOf("") }

        val startConnection = remember {
            fun() {
                setConnectionState(Login)

                MainScope().launch {
                    connect(userName, sessionCode)
                }
            }
        }

        MaterialTextField(
            label = StringLocale[STR_SESSION_CODE],
            value = sessionCode,
            onValidation = {
                val nameTextField = document.getElementById(PLAYER_NAME_FIELD_ID) as? HTMLInputElement

                if (nameTextField?.value.isNullOrBlank()) {
                    nameTextField?.focus()
                }
            },
            onValueChange = { sessionCode = it }
        )

        MaterialTextField(
            id = PLAYER_NAME_FIELD_ID,
            label = StringLocale[STR_PLAYER_NAME],
            value = userName,
            onValidation = {
                val connectionButton = document.getElementById(CONNECTION_BUTTON_ID) as? HTMLButtonElement

                if (connectionButton?.disabled == false)
                    startConnection()
            },
            onValueChange = { userName = it }
        )

        MaterialButton(
            id = CONNECTION_BUTTON_ID,
            text = StringLocale[if (connectionState is Login) STR_LOGIN else STR_START],
            enabled = sessionCode.isNotBlank() && userName.isNotBlank() && connectionState !is Login,
            onclick = startConnection
        )

        if (connectionState is Disconnected.ConnectionFailed)
            Span(attrs = classes(ShareSceneStyleSheet.validationErrorText)) {
                Text(StringLocale[ST_INVALID_SESSION_PARAM])
            }

        LaunchedEffect(Unit) {
            if(sessionCode.isNotBlank()) {
                val nameTextField = document.getElementById(PLAYER_NAME_FIELD_ID) as? HTMLInputElement
                nameTextField?.focus()
            }
        }
    }
}

private val sessionCodeOnURL
    get() = document.location?.pathname?.split('/')?.getOrNull(2).orEmpty()