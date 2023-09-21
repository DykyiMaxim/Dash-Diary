package com.wm.dashdiary.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "IMAGE_TO_DELETE_TABLE")
data class ImageToDelete(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val remoteImagePath: String
)