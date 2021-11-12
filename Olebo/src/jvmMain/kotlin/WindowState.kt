package jdr.exia

import jdr.exia.model.act.Act

sealed class WindowState {
    object HomeWindow : WindowState()

    class MasterWindow(val act: Act) : WindowState()
}