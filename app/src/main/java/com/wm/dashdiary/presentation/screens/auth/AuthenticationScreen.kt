package com.wm.dashdiary.presentation.screens.auth


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.MessageBarState
import com.stevdzasan.onetap.BuildConfig
import com.stevdzasan.onetap.OneTapSignInState
import com.stevdzasan.onetap.OneTapSignInWithGoogle

@ExperimentalMaterial3Api
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthenticationScreen(
    loadingState: Boolean,
    oneTapState: OneTapSignInState,
    messageBarState: MessageBarState,
    onButtonClicked: () -> Unit,

) {
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .navigationBarsPadding(),
        content = {
            ContentWithMessageBar(messageBarState = messageBarState) {
                AuthenticationContent(
                    loadingState = loadingState,
                    onButtonClicked = onButtonClicked
                )
            }
        }
    )

    OneTapSignInWithGoogle(
        state = oneTapState,
        clientId = com.wm.dashdiary.BuildConfig.googlecloudWebId,
        onTokenIdReceived = { tokenId ->},
        onDialogDismissed = {message-> Log.d("Dismissed",message)}
    )

//            val credential = GoogleAuthProvider.getCredential(tokenId, null)
//            FirebaseAuth.getInstance().signInWithCredential(credential)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        onSuccessfulFirebaseSignIn(tokenId)
//                    } else {
//                        task.exception?.let { it -> onFailedFirebaseSignIn(it) }
//                    }
//                }
//        },
//        onDialogDismissed = { message ->
//            onDialogDismissed(message)
//        }
//    )

//    LaunchedEffect(key1 = authenticated) {
//        if (authenticated) {
//            navigateToHome()
//        }
//    }
}