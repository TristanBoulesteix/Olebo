package jdr.exia.updater

import kotlinx.serialization.Serializable

@Serializable
data class UpdateOptions(val restart: Boolean, val localeCode: String)