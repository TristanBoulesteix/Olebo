package fr.olebo.application

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import org.kodein.di.DI
import org.kodein.di.compose.localDI
import org.kodein.di.direct
import org.kodein.type.erased
import kotlin.reflect.KClass

/**
 * A composition-local to provide instances of ViewModelStoreOwner in a composable function.
 * This is used to scope ViewModel instances to the composition.
 *
 * **See Also:** [GitHub](https://gist.github.com/manuelvicnt/a2e4c4812243ac1b218b24d0ac8d22bb)
 */
class CompositionScopedViewModelStoreOwner : ViewModelStoreOwner, RememberObserver {

    override val viewModelStore = ViewModelStore()

    override fun onAbandoned() {
        viewModelStore.clear()
    }

    override fun onForgotten() {
        viewModelStore.clear()
    }

    override fun onRemembered() = Unit
}

/**
 * Composable function that remembers a ViewModel instance in a Composable tree using Kodein DI container.
 * It returns ViewModelLazy, a property delegate that lazily accesses the ViewModel instance.
 *
 * @param VM the type of the ViewModel to be remembered.
 * @param tag an optional tag to filter the bindings in the DI container.
 *
 * @return ViewModelLazy<VM> an instance of ViewModelLazy with the specified ViewModel type.
 * @throws IllegalStateException if no DI container is attached to the Composable tree.
 */
@Composable
inline fun <reified VM : ViewModel> rememberViewModel(
    tag: String? = null
): ViewModelLazy<VM> = with(localDI()) {
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
        ?: error("ViewModelStoreOwner is missing for LocalViewModelStoreOwner.")

    remember {
        ViewModelLazy(
            viewModelClass = VM::class,
            storeProducer = { viewModelStoreOwner.viewModelStore },
            factoryProducer = { KodeinViewModelScopedSingleton(di = di, tag = tag) }
        )
    }
}

/**
 * Factory class used to create ViewModel instances with Kodein DI container
 * @param di The Kodein DI container instance
 * @param tag An optional tag to filter the bindings
 *
 * @throws DI.NotFoundException if ViewModel class binding is not found in the DI container
 * @see ViewModelProvider.Factory
 */
class KodeinViewModelScopedSingleton(
    private val di: DI,
    private val tag: String? = null,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
        di.direct.Instance(type = erased(modelClass), tag = tag)
}