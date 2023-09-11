package com.wm.dashdiary.presentation.screens.write

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wm.dashdiary.model.Diary

@Composable
fun WriteScreen(
    onBackPressed: () -> Unit,
    selectDiary: Diary?,
    onDeleteConfirm: () -> Unit
) {
    Scaffold(
        topBar = {
            WriteTopBar(
                onBackPressed = onBackPressed,
                selectDiary = selectDiary,
                onDeleteConfirm = onDeleteConfirm
            )
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