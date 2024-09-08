package fr.olebo.domain.navigation

import androidx.compose.runtime.Stable

/**
 * Interface representing a handler for managing screen navigation in an application.
 */
@Stable
interface NavigationHandler {
    /**
     * Represents the currently active screen in the application.
     *
     * Utilized within the navigation logic to keep track of and respond to the
     * current screen being displayed. The value of `currentScreen` dictates the
     * UI content being rendered at any given moment.
     */
    val currentScreen: Route

    /**
     * Navigates to the specified screen.
     *
     * @param screen the target [Route] to navigate to.
     */
    fun navigate(screen: Route)
}
