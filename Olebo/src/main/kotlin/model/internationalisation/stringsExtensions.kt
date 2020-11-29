package model.internationalisation

import model.dao.Settings

fun String.localCapitalize() = this.capitalize(Settings.language)