package com.wm.dashdiary.presentation.screens.write

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WriteScreen(onBackPressed: () -> Unit) {
    Scaffold(
        topBar = {
            WriteTopBar(onBackPressed)
        },


        ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

        }

    }
}