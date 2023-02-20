package com.wm.dashdiary.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import com.wm.dashdiary.presentation.screens.auth.AuthenticationScreen
import com.wm.dashdiary.presentation.screens.auth.AuthenticationViewModel
import java.lang.Exception

@Composable
fun SetupNavGraph(startDestinatio:String,navController:NavHostController){
    NavHost( navController = navController,startDestinatio){
        authenticationRout()
        HomeScreenRout()
        WriteRout()
    }
}

 @OptIn(ExperimentalMaterial3Api::class)
 fun NavGraphBuilder.authenticationRout() {
     composable(route = Screen.Authentication.route) {
         val viewModel:AuthenticationViewModel = viewModel()
         val loadingState by viewModel.LoadingState
         val oneTapState = rememberOneTapSignInState()
         val messageBarState = rememberMessageBarState()
         AuthenticationScreen(
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
                     onSuccess = {if(it){
                         messageBarState.addSuccess("Successfully authenticated")}
                         viewModel.setLoading(false)
                                 },
                     onError = {messageBarState.addError(it)}
                 )

                 messageBarState.addSuccess("Successfully authenticated")
                                },
             onDialogDismast ={message-> messageBarState.addError(Exception(message))}

             )

     }
 }


fun NavGraphBuilder.HomeScreenRout(){
    composable(route = Screen.Home.route){
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
