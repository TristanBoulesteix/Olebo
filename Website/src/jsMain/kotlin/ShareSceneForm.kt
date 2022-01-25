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

        MaterialTextField(
            label = StringLocale[STR_SESSION_CODE],
            value = sessionCode,
            onValueChange = { sessionCode = it })
        MaterialTextField(label = StringLocale[STR_PLAYER_NAME], value = userName, onValueChange = { userName = it })

        MaterialButton(
            text = StringLocale[if (connectionState is Login) STR_LOGIN else STR_START],
            enabled = sessionCode.isNotBlank() && userName.isNotBlank() && connectionState !is Login
        ) {
            setConnectionState(Login)

            MainScope().launch {
                connect(userName, sessionCode)
            }
        }

        if (connectionState is Disconnected.ConnectionFailed)
            Span(attrs = classes(ShareSceneStyleSheet.validationErrorText)) {
                Text(StringLocale[ST_INVALID_SESSION_PARAM])
            }
    }
}

private val sessionCodeOnURL
    get() = document.location?.pathname?.split('/')?.getOrNull(2).orEmpty()