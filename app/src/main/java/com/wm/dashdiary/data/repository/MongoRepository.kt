package com.wm.dashdiary.data.repository

import com.wm.dashdiary.model.Diary
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId
import java.time.LocalDate
import java.time.ZonedDateTime

typealias Diaries = RequestState<Map<LocalDate, List<Diary>>>

interface MongoRepository {
    fun configureRealm()
    fun getAllDiaries(): Flow<Diaries>
    fun getDiaryById(diaryID: ObjectId): Flow<RequestState<Diary>>
    fun getFilteredDiaries(zonedDateTime: ZonedDateTime): Flow<Diaries>
    suspend fun insertDiary(diary: Diary): RequestState<Diary>
    suspend fun updateDiary(diary: Diary): RequestState<Diary>
    suspend fun deleteDiary(id: ObjectId): RequestState<Diary>
    suspend fun deleteAllDiaries(): RequestState<Boolean>
}