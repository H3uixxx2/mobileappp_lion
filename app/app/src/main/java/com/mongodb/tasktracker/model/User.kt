package com.mongodb.tasktracker.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId

open class User(
    @PrimaryKey var _id: ObjectId = ObjectId(),
    //var username: String = "",
    var password: String = "",
    var role: String = "",
    var details: UserDetails? = null
) : RealmObject()

open class UserDetails(
    var name: String = "",
    var email: String = ""
) : RealmObject()
