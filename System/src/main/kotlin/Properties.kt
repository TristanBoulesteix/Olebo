package jdr.exia.system

internal fun getProperty(key: String, defaultValue: String = "") = System.getProperty(key) ?: defaultValue

internal fun getenv(key: String, defaultValue: String = "") = System.getenv(key) ?: defaultValue