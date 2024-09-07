package fr.olebo.application

import androidx.compose.runtime.Composable
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
 * Composable function that remembers a ViewModel instance in a Composable tree using Kodein DI container.
 * It returns [ViewModelLazy], a property delegate that lazily accesses the ViewModel instance.
 *
 * @param VM the type of the ViewModel to be remembered.
 * @param tag an optional tag to filter the bindings in the DI container.
 *
 * @return [ViewModelLazy]<VM> an instance of ViewModelLazy with the specified ViewModel ([VM]) type.
 * @throws IllegalStateException if no DI container is attached to the Composable tree.
 */
@Composable
inline fun <reified VM : ViewModel> rememberComposableViewModel(
    tag: String? = null
): ViewModelLazy<VM> = with(localDI()) {
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
        ?: error("ViewModelStoreOwner is missing for LocalViewModelStoreOwner.")

    rememberViewModel(VM::class, { viewModelStoreOwner.viewModelStore }, tag)
}

/**
 * Remembers a ViewModel instance within a Composable tree using Kodein DI container.
 *
 * This method returns a [ViewModelLazy] delegate that can be used to lazily access the ViewModel instance.
 * It leverages the Kodein DI for creating the ViewModel and ensuring the appropriate lifecycle management.
 *
 * @param tag An optional tag to filter the bindings in the Kodein DI container.
 * @param viewModelClass The KClass of the ViewModel that needs to be remembered.
 * @param storeProducer A function that produces the ViewModelStore, typically from the current LifecycleOwner.
 *
 * @return [ViewModelLazy]<VM> A property delegate to access the ViewModel instance lazily.
 * @throws DI.NotFoundException if the ViewModel class binding is not found in the Kodein DI container.
 */
@Composable
fun <VM: ViewModel> DI.rememberViewModel(
    viewModelClass: KClass<VM>,
    storeProducer: () -> ViewModelStore,
    tag: String?
): ViewModelLazy<VM> = remember {
     ViewModelLazy(
         viewModelClass = viewModelClass,
         storeProducer = storeProducer,
         factoryProducer = { KodeinViewModelScopedSingleton(di = di, tag = tag) }
     )
 }

/**
 * Factory class used to create ViewModel instances with Kodein [DI] container
 * @param di The Kodein [DI] container instance
 * @param tag An optional tag to filter the bindings
 *
 * @throws DI.NotFoundException if ViewModel class binding is not found in the DI container
 * @see ViewModelProvider.Factory
 */
private class KodeinViewModelScopedSingleton(
    private val di: DI,
    private val tag: String? = null,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
        di.direct.Instance(type = erased(modelClass), tag = tag)
}