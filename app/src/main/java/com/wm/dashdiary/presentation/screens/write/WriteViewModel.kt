package com.wm.dashdiary.presentation.screens.write

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.wm.dashdiary.data.database.ImageToUploadDao
import com.wm.dashdiary.data.database.entity.ImageToUpload
import com.wm.dashdiary.data.repository.MongoDB
import com.wm.dashdiary.data.repository.RequestState
import com.wm.dashdiary.data.repository.fetchImagesFromFirebase
import com.wm.dashdiary.mapper.toRealmInstant
import com.wm.dashdiary.model.Diary
import com.wm.dashdiary.model.GalleryImage
import com.wm.dashdiary.model.GalleryState
import com.wm.dashdiary.model.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, private val imagesToUploadDao: ImageToUploadDao
) : ViewModel() {
    val galleryState = GalleryState()
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
                        fetchImagesFromFirebase(remoteImagePaths = diary.data.Images,
                            onImageDownload = { downloadImage ->
                                galleryState.addImage(
                                    galleryImage = GalleryImage(
                                        image = downloadImage,
                                        remoteImagePath = extractRemoteImagePath(fullImageUrl = downloadImage.toString())
                                    )
                                )
                            })

                    }

                }


            }
        }
    }

    private fun extractRemoteImagePath(fullImageUrl: String): String {
        val chunk = fullImageUrl.split("%2F")
        val imageName = chunk[2].split("?").first()
        return "images/${Firebase.auth.currentUser?.uid}/$imageName"

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
            uploadImagesToFirebase()
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
            uploadImagesToFirebase()
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

    fun addImage(image: Uri, imageType: String) {
        val remoteImagePath =
            "images/${FirebaseAuth.getInstance().currentUser?.uid}/" + "${image.lastPathSegment}-${System.currentTimeMillis()}.$imageType"
        galleryState.addImage(
            GalleryImage(
                image = image, remoteImagePath = remoteImagePath
            )
        )
    }

    private fun uploadImagesToFirebase() {
        val storage = FirebaseStorage.getInstance().reference
        galleryState.images.forEach { galleryImage ->
            val imagePath = storage.child(galleryImage.remoteImagePath)
            imagePath.putFile(galleryImage.image).addOnPausedListener {
                val sessionUri = it.uploadSessionUri
                if (sessionUri != null) {
                    viewModelScope.launch(Dispatchers.IO) {
                        imagesToUploadDao.addImageToUpload(
                            ImageToUpload(
                                remoteImagePath = galleryImage.remoteImagePath,
                                imageUri = galleryImage.image.toString(),
                                sessionUri = sessionUri.toString()
                            )
                        )
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