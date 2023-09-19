package com.wm.dashdiary.presentation.screens.write

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wm.dashdiary.data.repository.MongoDB
import com.wm.dashdiary.data.repository.RequestState
import com.wm.dashdiary.mapper.toRealmInstant
import com.wm.dashdiary.model.Diary
import com.wm.dashdiary.model.Mood
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.time.ZonedDateTime

class WriteViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var uiSate by mutableStateOf(UiSate())
        private set

    init {
        getDiaryIdArgument()
        getSelectedDiary()
    }

    private fun getDiaryIdArgument() {
        uiSate = uiSate.copy(
            selectDiaryId = savedStateHandle.get<String>(
                key = "diaryId"
            )
        )

    }

    private fun getSelectedDiary() {
        if (uiSate.selectDiaryId != null) {
            viewModelScope.launch {
                MongoDB.getDiaryById(ObjectId.invoke(uiSate.selectDiaryId!!)).catch {
                    emit(RequestState.Error(Exception("Diary is already deleted.")))
                }.collect { diary ->

                    if (diary is RequestState.Success) {
                        setSelectedDiary(diary.data)
                        setTitle(title = diary.data.title)
                        setDescription(description = diary.data.description)
                        setMood(mood = Mood.valueOf(diary.data.mood))

                    }

                }


            }
        }
    }

    private suspend fun insertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {

        val result = MongoDB.insertDiary(diary = diary.apply {
            if (uiSate.updatedDateTime != null) {
                date = uiSate.updatedDateTime!!
            }
        })
        if (result is RequestState.Success) {

            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (result is RequestState.Error) {
            withContext(Dispatchers.Main) {
                onError(result.error.message.toString())
            }
        }
    }

    private suspend fun updateDiary(
        diary: Diary, onSuccess: () -> Unit, onError: (String) -> Unit
    ) {
        val result = MongoDB.updateDiary(diary.apply {
            _id = ObjectId.invoke(uiSate.selectDiaryId!!)
            date = if (uiSate.updatedDateTime != null) {
                uiSate.updatedDateTime!!
            } else {
                uiSate.selectedDiary!!.date
            }
        })
        if (result is RequestState.Success) {
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (result is RequestState.Error) {
            withContext(Dispatchers.Main) {
                onError(result.error.message.toString())
            }
        }

    }

    fun upsertDiary(
        diary: Diary, onSuccess: () -> Unit, onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiSate.selectDiaryId != null) {
                updateDiary(diary = diary, onSuccess = onSuccess, onError = onError)
            } else {
                insertDiary(diary = diary, onSuccess = onSuccess, onError = onError)
            }
        }

    }

    fun deleteDiary(
        onSuccess: () -> Unit, onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiSate.selectDiaryId != null) {
                val result = MongoDB.deleteDiary(ObjectId.invoke(uiSate.selectDiaryId!!))
                if (result is RequestState.Success) {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else if (result is RequestState.Error) {
                    withContext(Dispatchers.Main) {
                        onError(result.error.message.toString())
                    }
                }
            }
        }


    }

    fun setSelectedDiary(diary: Diary) {
        uiSate = uiSate.copy(selectedDiary = diary)
    }

    fun setTitle(title: String) {
        uiSate = uiSate.copy(title = title)
    }

    fun setDescription(description: String) {
        uiSate = uiSate.copy(description = description)
    }

    fun setMood(mood: Mood) {
        uiSate = uiSate.copy(mood = mood)
    }

    fun setDateTime(zonedDateTime: ZonedDateTime) {
        uiSate = uiSate.copy(updatedDateTime = zonedDateTime.toInstant().toRealmInstant())
    }
}

data class UiSate(
    val selectDiaryId: String? = null,
    val selectedDiary: Diary? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral,
    val updatedDateTime: RealmInstant? = null
)