package jdr.exia.system

enum class OS(val appDataDir: String) {
    WINDOWS(System.getenv("APPDATA")),
    MAC_OS(System.getProperty("user.home") + "/Library/"),
    GNU_LINUX(System.getProperty("user.home")),
    OTHER(System.getProperty("user.dir"));

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