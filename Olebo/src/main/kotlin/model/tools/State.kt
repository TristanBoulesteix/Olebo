package jdr.exia.model.tools

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

inline infix fun <T> MutableState<T>.withSetter(crossinline setter: (newValue: T) -> Unit) = object : MutableState<T> {
    private var delegatedValue by this@withSetter

    override var value
        get() = delegatedValue
        set(valToSet) {
            setter(valToSet)
            delegatedValue = valToSet
        }

    override fun component1() = value

    override fun component2(): (T) -> Unit = this::value.setter
}