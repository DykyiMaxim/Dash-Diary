package com.wm.dashdiary.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage

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
}