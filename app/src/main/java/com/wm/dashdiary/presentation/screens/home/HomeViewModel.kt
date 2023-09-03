package com.wm.dashdiary.presentation.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wm.dashdiary.data.repository.Diaries
import com.wm.dashdiary.data.repository.MongoDb
import com.wm.dashdiary.data.repository.RequestState
import kotlinx.coroutines.launch

class HomeViewModel:ViewModel() {
    var diaries:MutableState<Diaries> = mutableStateOf(RequestState.Idle)
    init{

    }

    private fun observeAllDiaries() {
        viewModelScope.launch {
            MongoDb.getAllDiaries().collect { result ->
                diaries.value = result
            }
        }
    }
}