package com.wm.dashdiary.data.database.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "IMAGE_UPLOAD_TABLE")
data class ImageToUpload(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val remoteImagePath: String,
    val imageUri: String,
    val sessionUri: String
)