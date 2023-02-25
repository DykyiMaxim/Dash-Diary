package com.wm.dashdiary.navigation

import DisplayAlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stevdzasan.messagebar.rememberMessageBarState
import com.wm.dashdiary.BuildConfig
import com.stevdzasan.onetap.rememberOneTapSignInState
import com.wm.dashdiary.presentation.screens.auth.AuthenticationScreen
import com.wm.dashdiary.presentation.screens.auth.AuthenticationViewModel
import com.wm.dashdiary.presentation.screens.home.HomeScreen
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

@Composable
fun SetupNavGraph(startDestinatio: String, navController: NavHostController) {
    NavHost(navController = navController, startDestinatio) {
        authenticationRout(NavigateToHome = {
            navController.popBackStack()
            navController.navigate(Screen.Home.route)
        }
        )
        HomeScreenRout(
            NavigateToWrite = {
                navController.navigate(Screen.Write.route)
            },
            NavigateToAuth = {
                navController.popBackStack()
                navController.navigate(Screen.Authentication.route)
            }
        )
        WriteRout()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.authenticationRout(NavigateToHome: () -> Unit) {
    composable(route = Screen.Authentication.route) {
        val viewModel: AuthenticationViewModel = viewModel()
        val authenticated by viewModel.Authenticated
        val loadingState by viewModel.LoadingState
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()
        AuthenticationScreen(
            authenticated = authenticated,
            loadingState = loadingState,
            oneTapState = oneTapState,
            messageBarState = messageBarState,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            onTokenIdReceives = { tokenId ->
                viewModel.SignInWithAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess("Successfully authenticated")
                        viewModel.setLoading(false)
                    },

                    onError = { messageBarState.addError(it) })
                messageBarState.addSuccess("Successfully authenticated")
            },
            onDialogDismast = { message ->
                messageBarState.addError(Exception(message))
                viewModel.setLoading(false)
            },
            navigateToHome = NavigateToHome

        )
    }
}


fun NavGraphBuilder.HomeScreenRout(
    NavigateToWrite: () -> Unit,
    NavigateToAuth: () -> Unit
) {
    composable(route = Screen.Home.route) {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var signOutDialogOpen by remember { mutableStateOf(false) }
        HomeScreen(
            drawerState = drawerState,
            onMenuClicked = { scope.launch { drawerState.open() } },
            onSignOutClicked = { signOutDialogOpen = true },
            navigateToWrite = NavigateToWrite
        )
        DisplayAlertDialog(
            title = "Sign Out",
            message = "Are you sure you want to Sign Out from your Google Account?",
            dialogOpened = signOutDialogOpen,
            onDialogClosed = { signOutDialogOpen = false },
            onYesClicked = {
                scope.launch(Dispatchers.IO) {
                    val user = App.create(BuildConfig.AtlasAppId).currentUser
                    if (user != null) {
                        user.logOut()
                        withContext(Dispatchers.Main) {
                            NavigateToAuth()
                        }
                    }
                }
            }
        )

    }
}

fun NavGraphBuilder.WriteRout() {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = "WritingScreenIdNav") {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })

    ) {
    }
}
