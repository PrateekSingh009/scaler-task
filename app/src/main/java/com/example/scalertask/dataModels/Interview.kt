package com.example.scalertask.dataModels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Interview(
    var title : String,
    var date : String,
    var start_time : String,
    var end_time : String,
    var participants : ArrayList<UserModel>,
    var emailList: ArrayList<String>,
    var interviewID: String
    ): Parcelable{
        constructor():this("","","","",ArrayList(), ArrayList(), "")
}

