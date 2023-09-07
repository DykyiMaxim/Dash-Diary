package com.wm.dashdiary.presentation.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wm.dashdiary.data.repository.Diaries
import com.wm.dashdiary.data.repository.MongoDB
import com.wm.dashdiary.data.repository.RequestState
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var diaries: MutableState<Diaries> = mutableStateOf(RequestState.Idle)

    init {
        observeAllDiaries()
    }

    private fun observeAllDiaries() {
        viewModelScope.launch {
            MongoDB.getAllDiaries()
                .collect { result ->
                    diaries.value = result

                }
        }
    }
}