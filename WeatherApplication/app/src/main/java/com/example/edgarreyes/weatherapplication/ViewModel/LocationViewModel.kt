package com.example.edgarreyes.weatherapplication.ViewModel

import android.arch.lifecycle.ViewModel
import com.example.edgarreyes.weatherapplication.Location

class LocationViewModel: ViewModel(){

    private var mData: ArrayList<Location> ?= null

    fun getData():ArrayList<Location>{
        if(mData == null){
            mData = ArrayList()
        }
        return mData!!
    }

    fun setData(list: ArrayList<Location>){
        mData =list
    }
}