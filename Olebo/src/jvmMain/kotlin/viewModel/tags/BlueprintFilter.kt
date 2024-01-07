package jdr.exia.viewModel.tags

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import jdr.exia.localization.*

@Immutable
enum class BlueprintFilter(private val translationKey: String) {
    ALL(STR_FILTER_BLUEPRINT_ALL), ACT(STR_FILTER_BLUEPRINT_WITH_ACT), TAG(STR_FILTER_BLUEPRINT_WITH_TAG);

    @Stable
    val value: String
        get() = StringLocale[translationKey]
}