package com.example.scalertask.utils

import androidx.fragment.app.Fragment

import com.example.scalertask.Fragments.CreateFragment
import com.example.scalertask.Fragments.EditFragment
import com.example.scalertask.Fragments.ListFragment
import com.example.scalertask.R

class Constants {
    companion object {
        const val CREATE = "CREATE"
        const val LIST = "LIST"
        const val MAIN_CONTAINER = R.id.fragment
        const val INTERVIEW = "INTERVIEW"
        const val EDIT = "EDIT"
        const val CREATE_LAYOUT = R.layout.fragment_create
        const val LIST_LAYOUT = R.layout.fragment_list
         fun getFragmentClass(id: String): Class<Fragment> {
             return when(id) {
                 CREATE -> CreateFragment::class.java as Class<Fragment>
                 LIST -> ListFragment::class.java as Class<Fragment>
                 EDIT -> EditFragment::class.java as Class<Fragment>
                 else -> ListFragment::class.java as Class<Fragment>
             }
         }
    }
}