package com.wm.dashdiary.presentation.screens.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wm.dashdiary.BuildConfig
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.GoogleAuthType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthenticationViewModel:ViewModel() {
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
                        Credentials.google(tokenId,GoogleAuthType.ID_TOKEN)
                    ).loggedIn
                }
                withContext(Dispatchers.Main){
                    onSuccess(result)
                }

            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    onError(e)
                }

            }
        }
    }
}