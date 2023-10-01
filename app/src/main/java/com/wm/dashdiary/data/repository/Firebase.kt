package com.wm.dashdiary.data.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import com.wm.dashdiary.data.database.entity.ImageToUpload

class Firebase {
    fun fetchImagesFromFirebase(
        remoteImagePaths: List<String>,
        onImageDownload: (Uri) -> Unit,
        onImageDownloadFailed: (Exception) -> Unit = {},
        onReadyToDisplay: () -> Unit = {}
    ) {
        if (remoteImagePaths.isNotEmpty()) {
            remoteImagePaths.forEachIndexed { index, remoteImagePath ->
                if (remoteImagePath.trim().isNotEmpty()) {
                    FirebaseStorage.getInstance().reference.child(remoteImagePath.trim()).downloadUrl
                        .addOnSuccessListener {
                            Log.d("DownloadURL", "$it")
                            onImageDownload(it)
                            if (remoteImagePaths.lastIndexOf(remoteImagePaths.last()) == index) {
                                onReadyToDisplay()
                            }
                        }.addOnFailureListener {
                            onImageDownloadFailed(it)
                        }
                }
            }
        }
    }
    fun retryUploadingImageToFirebase(
        imageToUpload: ImageToUpload,
        onSuccess: () -> Unit
    ) {
        val storage = FirebaseStorage.getInstance().reference
        storage.child(imageToUpload.remoteImagePath).putFile(
            imageToUpload.imageUri.toUri(),
            storageMetadata { },
            imageToUpload.sessionUri.toUri()
        ).addOnSuccessListener { onSuccess() }
    }

}