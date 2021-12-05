package fr.olebo.sharescene

import androidx.compose.runtime.*
import fr.olebo.sharescene.components.MaterialTextField
import fr.olebo.sharescene.components.TitledInputText
import fr.olebo.sharescene.css.ShareSceneStyleSheet
import fr.olebo.sharescene.css.classes
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun ShareSceneForm() = Div(attrs = classes(ShareSceneStyleSheet.mainContainer)) {
    Div(attrs = classes(ShareSceneStyleSheet.formTitle)) { Text("Olebo ShareScene") }

    Div(attrs = classes(ShareSceneStyleSheet.formContent, ShareSceneStyleSheet.boxContainer)) {
        var sessionCode by remember { mutableStateOf("") }
        var userName by remember { mutableStateOf("") }

        TitledInputText(label = "Code de session :", input = sessionCode, onInputChange = { sessionCode = it })
        TitledInputText(label = "Nom de joueur :", input = userName, onInputChange = { userName = it })
        MaterialTextField(label = "label", value = sessionCode, onValueChange = { sessionCode = it })
    }
}

