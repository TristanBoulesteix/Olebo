package jdr.exia

import androidx.compose.runtime.Immutable
import jdr.exia.model.act.Act

@Immutable
sealed class WindowState

data object HomeWindow : WindowState()

class MasterWindow(val act: Act) : WindowState()