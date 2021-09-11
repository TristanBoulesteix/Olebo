package jdr.exia.model.tools

import androidx.compose.runtime.MutableState

inline infix fun <T> MutableState<T>.withSetter(crossinline setter: (newValue: T) -> Unit) =
    object : MutableState<T> by this {
        override var value
            get() = this@withSetter.value
            set(valToSet) {
                setter(valToSet)
                this@withSetter.value = valToSet
            }
    }