package com.wm.dashdiary.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wm.dashdiary.data.database.entity.ImageToUpload


@Database(
    entities = [ImageToUpload::class, ImageToDelete::class],
    version = 1,
    exportSchema = false
)
abstract class ImagesDatabase: RoomDatabase() {
}