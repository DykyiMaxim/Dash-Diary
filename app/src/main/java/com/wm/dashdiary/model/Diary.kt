package com.wm.dashdiary.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Diary:RealmObject {
    @PrimaryKey
    var Id: ObjectId = ObjectId.create()
    var OwnerId: String = ""
    var title: String = ""
    var mood:String=Mood.Neutral.name
    var description: String = ""
    var Images: RealmList<String> = realmListOf()
    var date: RealmInstant = RealmInstant.from(System.currentTimeMillis(), 0)
}

