package jdr.exia.localization

expect abstract class ResourceBundle {
    fun getString(key: String) : String
}