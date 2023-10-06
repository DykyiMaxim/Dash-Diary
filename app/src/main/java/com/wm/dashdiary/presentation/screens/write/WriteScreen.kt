package com.wm.dashdiary.presentation.screens.write

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.wm.dashdiary.model.Diary
import com.wm.dashdiary.model.Mood
import java.time.ZonedDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WriteScreen(
    uiSate: UiSate,
    moodName: () -> String,
    PagerSate: PagerState,
    onBackPressed: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDeleteConfirm: () -> Unit,
    onSaveClicked: (Diary) -> Unit,
    onUpdatedDateTime: (ZonedDateTime) -> Unit,
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
                moodName = moodName,
                onUpdatedDateTime = onUpdatedDateTime
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
                uiSate = uiSate,
                onSaveClicked = onSaveClicked
            )

        }

    }
}