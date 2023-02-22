package com.wm.dashdiary.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import com.wm.dashdiary.BuildConfig
import com.wm.dashdiary.presentation.screens.auth.AuthenticationScreen
import com.wm.dashdiary.presentation.screens.auth.AuthenticationViewModel
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

@Composable
fun SetupNavGraph(startDestinatio:String,navController:NavHostController){
    NavHost( navController = navController,startDestinatio){
        authenticationRout(NavigateToHome = {
            navController.popBackStack()
            navController.navigate(Screen.Home.route)
        })
        HomeScreenRout()
        WriteRout()
    }
}

 @OptIn(ExperimentalMaterial3Api::class)
 fun NavGraphBuilder.authenticationRout(NavigateToHome:()->Unit) {
     composable(route = Screen.Authentication.route) {
         val viewModel:AuthenticationViewModel = viewModel()
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
             onTokenIdReceives ={tokenId->
                 viewModel.SignInWithAtlas(
                     tokenId = tokenId,
                     onSuccess = {
                         messageBarState.addSuccess("Successfully authenticated")
                         viewModel.setLoading(false)
                     },

                     onError = {messageBarState.addError(it)})
                      messageBarState.addSuccess("Successfully authenticated")
                                },
             onDialogDismast ={message->
                 messageBarState.addError(Exception(message))
                 viewModel.setLoading(false) },
             navigateToHome = NavigateToHome

             )
     }
 }


fun NavGraphBuilder.HomeScreenRout(){
    composable(route = Screen.Home.route){
        val scope = rememberCoroutineScope() /*TODO:Make it normal*/
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { scope.launch(Dispatchers.IO) { App.Companion.create(BuildConfig.AtlasAppId).currentUser?.logOut()  } }){
                Text(text = "Logout")
            }

        }
    }
}
fun NavGraphBuilder.WriteRout(){
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = "WritingScreenIdNav"){
            type= NavType.StringType
            nullable=true
            defaultValue=null
        })

        ){
    }
}
