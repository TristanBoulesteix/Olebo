package jdr.exia.viewModel.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jdr.exia.localization.*
import jdr.exia.model.act.Act
import jdr.exia.model.tools.withSetter
import jdr.exia.view.HomeWindow
import jdr.exia.view.MasterWindow
import jdr.exia.view.tools.MessageType
import jdr.exia.view.tools.showConfirmMessage
import jdr.exia.view.tools.showMessage
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import org.jetbrains.exposed.sql.transactions.transaction
import javax.swing.JOptionPane

class HomeViewModel {
    private val actsAsList
        get() = transaction { Act.all().toList() }

    var acts by mutableStateOf(actsAsList)
        private set

    var content by mutableStateOf(ActsView as HomeContent) withSetter { if (it is ActsView) refreshActs() }

    /**
     * Start an act
     *
     * @param act The act to launch
     */
    fun launchAct(act: Act) = CoroutineScope(Dispatchers.Main).launch {
        val loadingString = StringLocale[STR_LOADING]

        val popup = JOptionPane(
            "$loadingString...",
            JOptionPane.INFORMATION_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            emptyArray()
        ).run {
            this.createDialog(parent, loadingString)
        }

        val job = launch(Dispatchers.Swing) {
            withTimeout(120_000) {
                try {
                    val masterWindow = MasterWindow(act).also { it.isVisible = true }
                    yield()
                    popup.dispose()
                    masterWindow.requestFocus()
                } catch (e: TimeoutCancellationException) {
                    popup.dispose()
                    showMessage("${StringLocale[STR_ERROR]}: ${e.message}", null, MessageType.ERROR)
                    HomeWindow().isVisible = true
                }
            }
        }

        if (job.isActive)
            popup.isVisible = true
    }

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