package fr.olebo.model

import kotlinx.serialization.Serializable

@Serializable
data class Version(val versionCode: Int, val versionName: String)