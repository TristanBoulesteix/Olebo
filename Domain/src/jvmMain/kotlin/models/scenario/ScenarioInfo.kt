package fr.olebo.domain.models.scenario

import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
data class ScenarioInfo(val path: Path, val name: String, val sceneCount: Int)
