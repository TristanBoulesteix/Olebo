package fr.olebo.domain

import androidx.compose.ui.graphics.Color
import fr.olebo.domain.coroutine.ApplicationIoScope
import fr.olebo.domain.models.ConfigurationItem
import fr.olebo.domain.models.appendConfiguration
import fr.olebo.domain.models.scenario.LabelVisibility
import fr.olebo.domain.models.scenario.SerializableColor
import fr.olebo.domain.serialization.ColorSerializer
import fr.olebo.domain.viewmodels.StartupViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.KSerializer
import org.kodein.di.DI
import org.kodein.di.bindProviderOf
import org.kodein.di.bindSingleton

internal actual fun DI.Builder.specializedInjection() {
    bindProviderOf<KSerializer<Color>>(::ColorSerializer)
    appendConfiguration {
        Constants(SerializableColor.BLACK.encode(), LabelVisibility.OnlyForMaster.encode())
    }
    bindSingleton<ApplicationIoScope> {
        object : ApplicationIoScope, CoroutineScope by CoroutineScope(Dispatchers.IO) {}
    }
    bindProviderOf(::StartupViewModel)
}

/**
 * Data class representing the default constants for label color and visibility
 * within the application's configuration system.
 *
 * @property defaultLabelColor Default color for labels represented as a string.
 * @property defaultLabelVisibility Default visibility setting for labels represented as a string.
 *
 * Implements the [ConfigurationItem] interface, which allows instances of this class
 * to be managed by the dependency injection framework within the application's configuration system.
 */
data class Constants(val defaultLabelColor: String, val defaultLabelVisibility: String) : ConfigurationItem