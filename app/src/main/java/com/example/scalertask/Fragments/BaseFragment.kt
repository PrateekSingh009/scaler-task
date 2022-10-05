package com.example.scalertask.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.scalertask.R
import com.example.scalertask.databinding.FragmentListBinding


import com.example.scalertask.utils.Constants



open class BaseFragment: Fragment() {

  fun addFragment(id: String, containerID: Int, args: Bundle?) {
      getFragSupportManager().beginTransaction().setReorderingAllowed(true).addToBackStack(id).add(containerID,
      Constants.getFragmentClass(id), args)
  }

    fun replaceFragment(id: String, containerID: Int, args: Bundle?) {
        getFragSupportManager().beginTransaction().setReorderingAllowed(true).replace(containerID,
            Constants.getFragmentClass(id), args).commit()
    }

//    fun getBindingObject(inflater: LayoutInflater, container: ViewGroup?, layout : Int) : FragmentListBinding {
//        return DataBindingUtil.inflate(inflater, layout ,container,false)
//    }

    fun popBackStack() = getFragSupportManager().popBackStack()


    private fun getFragSupportManager(): FragmentManager = requireActivity().supportFragmentManager

}