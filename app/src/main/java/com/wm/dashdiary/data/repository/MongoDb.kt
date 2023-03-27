package com.wm.dashdiary.data.repository

import com.wm.dashdiary.BuildConfig
import com.wm.dashdiary.model.Diary
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration

object MongoDb:MongoRepository {
    private val app = App.Companion.create(BuildConfig.AtlasAppId)
    private val user = app.currentUser
    private lateinit var realm:Realm

    override fun configureRealm() {
        if(user!=null){
            val config = SyncConfiguration.Builder(user, setOf(Diary::class))
                .initialSubscriptions{sub ->
                    add(
                        query = sub.query(query = "OwnerId==$0", args = arrayOf(user.identity)), //hz-hz
                        name = "User's Diaries"
                    )

                }
                .log(LogLevel.ALL)
                .build()
            realm=Realm.open(config)
        }

    }
}