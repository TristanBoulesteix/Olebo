package jdr.exia.system

enum class OS(val appDataDir: String, val executableFileTypes: Set<String> = emptySet()) {
    WINDOWS(getenv("APPDATA"), setOf("exe", "msi")),
    MAC_OS(getProperty("user.home") + "/Library/"),
    GNU_LINUX(getProperty("user.home")),
    OTHER(getProperty("user.dir"));

    companion object {
        val current
            get() = with(System.getProperty("os.name")) {
                when {
                    contains("WIN", ignoreCase = true) -> WINDOWS
                    contains("MAC", ignoreCase = true) -> MAC_OS
                    contains("NUX", ignoreCase = true) -> GNU_LINUX
                    else -> OTHER
                }
            }
    }
}