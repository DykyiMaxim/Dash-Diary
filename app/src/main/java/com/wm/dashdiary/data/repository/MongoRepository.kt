package com.wm.dashdiary.data.repository

import com.wm.dashdiary.model.Diary
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId
import java.time.LocalDate

typealias Diaries = RequestState<Map<LocalDate, List<Diary>>>

interface MongoRepository {
    fun configureRealm()
    fun getAllDiaries(): Flow<Diaries>
    fun GetDiaryById(diaryID:ObjectId):Flow<RequestState<Diary>>
}