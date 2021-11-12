package jdr.exia.update

import kotlinx.serialization.Serializable

@Serializable
class Release(
    val versionId: Int,
    val versionName: String,
    val paths: List<String>,
    val isEarlyAccess: Boolean = false
) : Comparable<Release> {
    override operator fun compareTo(other: Release) = versionId.compareTo(other.versionId)
}