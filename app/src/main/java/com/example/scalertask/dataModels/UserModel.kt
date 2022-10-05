package com.example.scalertask.dataModels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var name : String,
    val uid: String
):Parcelable{
    constructor():this("","")
}
