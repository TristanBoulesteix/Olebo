package fr.olebo.domain.models.scenario

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
@Immutable
data class ScenarioInfo(val name: String, val sceneCount: Int = 0, val path: Path? = null)