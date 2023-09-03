package com.wm.dashdiary.data.repository

import android.util.Log
import com.wm.dashdiary.BuildConfig
import com.wm.dashdiary.mapper.toInstant
import com.wm.dashdiary.model.Diary
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.ZoneId

object MongoDb:MongoRepository {
    private val app = App.Companion.create(BuildConfig.AtlasAppId)
    private val user = app.currentUser
    private lateinit var realm: Realm



    init {
        configureRealm()
    }

    override fun configureRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(user, setOf(Diary::class))
                .initialSubscriptions { sub ->
                    add(
                        query = sub.query<Diary>(query = "OwnerId == $0", user.identity),
                        name = "Diaries"
                    )
                }
                .log(LogLevel.ALL)
                .build()
            Log.d("TAG",user.identity)
            realm = Realm.open(config)
        }
    }

    override fun getAllDiaries(): Flow<Diaries> {
        return if (user != null) {
            try {
                realm.query<Diary>(query = "OwnerId == $0", user.identity)
                    .sort(property = "date", sortOrder = Sort.DESCENDING)
                    .asFlow()
                    .map { result ->
                        Log.d("TAG","HERE")

                        RequestState.Success(
                            data = result.list.groupBy {
                                it.date.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            }
                        )
                    }
            } catch (e: Exception) {
                flow { emit(RequestState.Error<Nothing>(e)) }
            }
        } else {
            flow { emit(RequestState.Error<Nothing>(UserNotAuthenticatedException())) }
        }
    }
}
private class UserNotAuthenticatedException:Exception("User is not logged in.")