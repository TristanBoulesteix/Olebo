package fr.olebo.domain

import androidx.compose.ui.graphics.Color
import fr.olebo.domain.coroutine.ApplicationIoScope
import fr.olebo.domain.models.LabelVisibility
import fr.olebo.domain.models.SerializableColor
import fr.olebo.domain.serialization.ColorSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.KSerializer
import org.kodein.di.DI
import org.kodein.di.bindConstant
import org.kodein.di.bindProviderOf
import org.kodein.di.bindSingleton

internal actual fun DI.Builder.specializedInjection() {
    bindProviderOf<KSerializer<Color>>(::ColorSerializer)
    bindConstant("defaultLabelColor", creator = SerializableColor.BLACK::encode)
    bindConstant("defaultLabelVisibility", creator = LabelVisibility.OnlyForMaster::encode)
    bindSingleton<ApplicationIoScope> {
        object : ApplicationIoScope, CoroutineScope by CoroutineScope(Dispatchers.IO) {}
    }
}