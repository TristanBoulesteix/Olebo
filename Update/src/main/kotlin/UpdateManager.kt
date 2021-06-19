class UpdateManager internal constructor(private val release: Release?) {
    val hasUpdateAvailable
        get() = release != null
}