package com.wm.dashdiary.data.repository

import android.util.Log
import com.wm.dashdiary.BuildConfig
import com.wm.dashdiary.mapper.toInstant
import com.wm.dashdiary.model.Diary
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime


object MongoDB : MongoRepository {
    private var APP_ID: String = ""
    private var user: User? = null
    private lateinit var realm: Realm

    init {
        configureRealm()
    }


    override fun configureRealm() {

        APP_ID = BuildConfig.AtlasAppId
        val app = App.create(APP_ID)
        user = app.currentUser

        if (user != null) {
            val config =
                SyncConfiguration.Builder(user!!, setOf(Diary::class)).initialSubscriptions { sub ->
                    add(
                        query = sub.query<Diary>(query = "ownerId == $0", user!!.id),
                        name = "User's Diaries"
                    )
                }.build()
            realm = Realm.open(config)
        }
    }

    override fun getAllDiaries(): Flow<Diaries> {

        return if (user != null) {
            try {
                realm.query<Diary>("ownerId == $0", user!!.id)
                    .sort(property = "date", sortOrder = Sort.DESCENDING).asFlow().map { result ->
                        RequestState.Success(data = result.list.groupBy {
                            it.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        })
                    }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }
    }

    override fun getDiaryById(diaryID: ObjectId): Flow<RequestState<Diary>> {
        return if (user != null) {
            try {
                realm.query<Diary>(query = "_id == $0", diaryID).asFlow().map {
                    RequestState.Success(data = it.list.first())
                }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }

        } else {
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }
    }

    override suspend fun insertDiary(diary: Diary): RequestState<Diary> {
        return if (user != null) {
            realm.write {
                try {
                    val addedDiary = copyToRealm(diary.apply { ownerId = user!!.id })
                    RequestState.Success(data = addedDiary)

                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }

        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }

    override suspend fun updateDiary(diary: Diary): RequestState<Diary> {
        return if (user != null) {
            realm.write {
                val queriedDiary = query<Diary>(query = "_id == $0", diary._id).first().find()
                if (queriedDiary != null) {


                    queriedDiary.title = diary.title
                    queriedDiary.description = diary.description
                    queriedDiary.mood = diary.mood
                    queriedDiary.Images = diary.Images
                    queriedDiary.date = diary.date
                    RequestState.Success(data = queriedDiary)
                } else {
                    RequestState.Error(Exception("query Diary did not exist"))
                }
            }

        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }

    }

    override suspend fun deleteDiary(id: ObjectId): RequestState<Diary> {
        return if (user != null) {
            realm.write {
                val diary = query<Diary>(
                    query = "_id == $0 AND ownerId == $1", id, user!!.id
                ).first().find()
                if (diary != null) {
                    try {
                        delete(diary)
                        RequestState.Success(data = diary)
                    } catch (e: Exception) {
                        RequestState.Error(e)

                    }
                } else {
                    RequestState.Error(Exception("Diary didn't exist"))
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }

    override fun getFilteredDiaries(zonedDateTime: ZonedDateTime): Flow<Diaries> {
        return if (user != null) {
            try {
                realm.query<Diary>(
                    "ownerId == $0 AND date < $1 AND date > $2",
                    user!!.id,
                    RealmInstant.from(
                        LocalDateTime.of(
                            zonedDateTime.toLocalDate().plusDays(1),
                            LocalTime.MIDNIGHT
                        ).toEpochSecond(zonedDateTime.offset), 0
                    ),
                    RealmInstant.from(
                        LocalDateTime.of(
                            zonedDateTime.toLocalDate(),
                            LocalTime.MIDNIGHT
                        ).toEpochSecond(zonedDateTime.offset), 0
                    ),
                ).asFlow().map { result ->
                    RequestState.Success(
                        data = result.list.groupBy {
                            it.date.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                    )
                }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }
    }

    override suspend fun deleteAllDiaries(): RequestState<Boolean> {
        return if (user != null) {
            realm.write {
                val diaries = this.query<Diary>("ownerId == $0", user!!.id).find()
                try {
                    delete(diaries)
                    RequestState.Success(data = true)
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }
}


private class UserNotAuthenticatedException : Exception("User is not logged in.")