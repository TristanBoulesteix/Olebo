package jdr.exia.model.tools

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.structuralEqualityPolicy
import kotlin.experimental.ExperimentalTypeInference

@JvmName("settableMutableStateWithSetter")
fun <T> settableMutableState(
    value: T,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
    setter: (newValue: T) -> T
): MutableState<T> = SettableMutableState(mutableStateOf(value, policy), setter)

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
fun <T> settableMutableState(
    value: T,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
    setter: (newValue: T) -> Unit
): MutableState<T> = settableMutableState(value, policy) {
    setter(it)
    it
}

private class SettableMutableState<T>(val mutableState: MutableState<T>, val setter: (newValue: T) -> T) :
    MutableState<T> by mutableState {
    override var value
        get() = mutableState.value
        set(valToSet) {
            mutableState.value = setter(valToSet)
        }
}