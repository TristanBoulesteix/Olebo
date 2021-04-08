@file:Suppress("DuplicatedCode")

package jdr.exia.viewModel

import androidx.compose.desktop.AppFrame
import androidx.compose.desktop.AppManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jdr.exia.localization.*
import jdr.exia.model.act.Act
import jdr.exia.view.frames.home.HomeFrame
import jdr.exia.view.frames.rpg.MasterFrame
import jdr.exia.view.utils.MessageType
import jdr.exia.view.utils.showConfirmMessage
import jdr.exia.view.utils.showMessage
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import org.jetbrains.exposed.sql.transactions.transaction
import javax.swing.JOptionPane

class HomeViewModel {
    private val actsAsList
        get() = transaction { Act.all().toList() }

    var acts by mutableStateOf(actsAsList)
        private set

    /**
     * Start an act
     *
     * @param act The act to launch
     */
    fun launchAct(act: Act) = GlobalScope.launch {
        val popup = withContext(Dispatchers.Main) {
            val loadingString = StringLocale[STR_LOADING]

            JOptionPane(
                "$loadingString...",
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                emptyArray()
            ).run {
                this.createDialog(parent, loadingString)
            }
        }

        val job = launch(Dispatchers.Swing) {
            withTimeout(120_000) {
                try {
                    ViewManager.initializeAct(act)
                    yield()
                    popup.dispose()
                    AppManager.windows.forEach(AppFrame::close)
                    MasterFrame.requestFocus()
                } catch (e: TimeoutCancellationException) {
                    popup.dispose()
                    showMessage("${StringLocale[STR_ERROR]}: ${e.message}", null, MessageType.ERROR)
                    HomeFrame().isVisible = true
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

    fun refreshActs() {
        acts = actsAsList
    }
}