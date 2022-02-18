package jdr.exia.model.dao.option

import jdr.exia.localization.*
import kotlinx.serialization.Serializable

@Serializable
enum class ThemeMode(private val stringKey: String) {
    Dark(STR_DARK_THEME), Light(STR_LIGHT_THEME), Auto(STR_AUTO_THEME);

    override fun toString() = StringLocale[stringKey]
}