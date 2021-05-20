package jdr.exia.model.tools

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jdr.exia.view.tools.DefaultFunction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.reflect.KMutableProperty0

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

fun <T> KMutableProperty0<T>.toMutableState(repaint: DefaultFunction): MutableState<T> {
    var property by this

    val mutableState = mutableStateOf(property) withSetter {
        CoroutineScope(Dispatchers.IO).launch {
            transaction { property = it }

            repaint()
        }
    }

    val (_, setter) = mutableState

    return object : MutableState<T> {
        override var value by mutableState

        override fun component1(): T {
            setter(property)

            return value
        }

        override fun component2(): (T) -> Unit = setter
    }
}