package jdr.exia.viewModel

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jdr.exia.model.act.Act
import jdr.exia.model.element.Blueprint
import org.jetbrains.exposed.sql.transactions.transaction

class MainViewModel(private val act: Act) {
    @Stable
    val items = transaction { Blueprint.all().groupBy { it.type } }

    var searchString by mutableStateOf("")
        private set

    fun search(value: String) {
        searchString = value
    }
}