package fr.olebo.domain

import androidx.compose.ui.graphics.Color
import fr.olebo.domain.models.LabelVisibility
import fr.olebo.domain.models.SerializableColor
import fr.olebo.domain.serialization.ColorSerializer
import kotlinx.serialization.KSerializer
import org.kodein.di.DI
import org.kodein.di.bindConstant
import org.kodein.di.bindProviderOf

internal actual fun DI.Builder.specializedInjection() {
    bindProviderOf<KSerializer<Color>>(::ColorSerializer)
    bindConstant("defaultLabelColor", creator = SerializableColor.BLACK::encode)
    bindConstant("defaultLabelVisibility", creator = LabelVisibility.OnlyForMaster::encode)
}