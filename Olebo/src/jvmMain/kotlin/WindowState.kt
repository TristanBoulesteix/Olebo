package jdr.exia

import androidx.compose.runtime.Immutable
import jdr.exia.model.act.Act

@Immutable
sealed class WindowState

object HomeWindow : WindowState()

class MasterWindow(val act: Act) : WindowState()