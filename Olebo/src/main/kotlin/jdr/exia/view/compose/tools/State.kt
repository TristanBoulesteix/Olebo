package jdr.exia.view.compose.tools

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

infix fun <T> MutableState<T>.withSetter(setter: (newValue: T) -> Unit) = object : MutableState<T> {
    private var delegatedValue by this@withSetter

    override var value
        get() = delegatedValue
        set(valToSet) {
            setter(valToSet)
            delegatedValue = valToSet
        }

    override fun component1() = value

    override fun component2() = this::value.setter
}