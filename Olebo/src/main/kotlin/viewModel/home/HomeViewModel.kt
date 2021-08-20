package jdr.exia.viewModel.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jdr.exia.localization.STR_DELETE_ACT
import jdr.exia.localization.ST_CONFIRM_DELETE_ACT
import jdr.exia.localization.StringLocale
import jdr.exia.model.act.Act
import jdr.exia.model.tools.withSetter
import jdr.exia.view.tools.showConfirmMessage
import org.jetbrains.exposed.sql.transactions.transaction

class HomeViewModel {
    private val actsAsList
        get() = transaction { Act.all().toList() }

    var acts by mutableStateOf(actsAsList)
        private set

    var content by mutableStateOf(ActsView as HomeContent) withSetter { if (it is ActsView) refreshActs() }

    fun deleteAct(act: Act) = transaction {
        showConfirmMessage(message = StringLocale[ST_CONFIRM_DELETE_ACT], title = StringLocale[STR_DELETE_ACT]) {
            act.delete()
            refreshActs()
        }
    }

    private fun refreshActs() {
        acts = actsAsList
    }
}