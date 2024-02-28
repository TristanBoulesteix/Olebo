package fr.olebo.domain.models

enum class OS(val appDataDir: String, val executableFileTypes: Set<String> = emptySet()) {
    WINDOWS(System.getenv("APPDATA"), setOf("exe", "msi")),
    MAC_OS(System.getProperty("user.home") + "/Library/"),
    GNU_LINUX(System.getProperty("user.home"))
}