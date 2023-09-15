package com.wm.dashdiary.presentation.screens.write

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.wm.dashdiary.model.Mood

@OptIn(ExperimentalPagerApi::class)
@Composable
fun WriteScreen(
    uiSate: UiSate,
    moodName: ()->String,
    PagerSate: PagerState,
    onBackPressed: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDeleteConfirm: () -> Unit
) {
    LaunchedEffect(key1 = uiSate.mood) {
        PagerSate.scrollToPage(Mood.valueOf(uiSate.mood.name).ordinal)
    }
    Scaffold(
        topBar = {
            WriteTopBar(
                onBackPressed = onBackPressed,
                selectDiary = uiSate.selectedDiary,
                onDeleteConfirm = onDeleteConfirm,
                moodName = moodName
            )
        },


        ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            WriteContent(
                PagerSate = PagerSate,
                title = uiSate.title,
                onTitleChange = onTitleChange,
                description = uiSate.description,
                onDescriptionChange = onDescriptionChange,
                paddingValues = it
            )

        }

    }
}