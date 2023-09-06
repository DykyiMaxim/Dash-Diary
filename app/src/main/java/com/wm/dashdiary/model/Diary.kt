package com.wm.dashdiary.model

import com.wm.dashdiary.mapper.toRealmInstant
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.time.Instant

open class Diary:RealmObject {
    @PrimaryKey
    
    var _id: ObjectId = ObjectId()
    var date: RealmInstant = Instant.now().toRealmInstant()
    var description: String = ""
    var mood:String=Mood.Neutral.name
    var ownerId: String = ""
    var title: String = ""
    var Images: RealmList<String> = realmListOf()
}
