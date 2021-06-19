import jdr.exia.system.OS

class Updater private constructor(private val assetsForOs: List<Asset>, val versionName: String) {
    internal companion object {
        operator fun invoke(release: Release?, os: OS) = when {
            release != null -> release.assets.filter { it.name.substringAfterLast('.', "") in os.executableFileTypes }
                .takeIf { it.isNotEmpty() }?.let { Updater(it, release.tag) }
            else -> null
        }
    }

    fun start(auto: Boolean = true) {
        Runtime.getRuntime().addShutdownHook(Thread {
            val url = assetsForOs.firstOrNull()?.url
        })
    }
}