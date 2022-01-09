package jdr.exia.view.tools

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.io.Closeable

fun <T : Closeable> mutableClosableStateOf(closableValue: T): MutableState<T> {
    val mutableState = mutableStateOf(closableValue)

    return object : MutableState<T> by mutableState {
        override var value: T
            get() = mutableState.value
            set(value) {
                mutableState.value.close()
                mutableState.value = value
            }
    }
}