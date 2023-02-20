package com.wm.dashdiary.presentation.screens.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wm.dashdiary.BuildConfig
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.GoogleAuthType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthenticationViewModel:ViewModel() {
    var Authenticated = mutableStateOf(false)
        private set
    var LoadingState = mutableStateOf(false)
    private set
    fun setLoading(loading:Boolean){
        LoadingState.value = loading
    }
    fun SignInWithAtlas(
        tokenId:String,
        onSuccess:(Boolean)->Unit,
        onError:(Exception)->Unit){
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO){
                    App.Companion.create(BuildConfig.AtlasAppId).login(
                        Credentials.jwt(tokenId)
                    ).loggedIn
                }
                withContext(Dispatchers.Main){
                    onSuccess(result)
                    delay(600)
                    Authenticated.value=true
                }

            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    onError(e)
                }

            }
        }
    }
}