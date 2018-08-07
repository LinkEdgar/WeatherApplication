package com.example.edgarreyes.weatherapplication.ViewModel

import android.arch.lifecycle.ViewModel
import com.example.edgarreyes.weatherapplication.WeatherDetail

class WeatherViewModel: ViewModel(){
    private var mData: ArrayList<WeatherDetail> ?= null

    fun getData(): ArrayList<WeatherDetail>{
        if(mData == null)
            mData = ArrayList()
        return mData!!
    }

    fun setData(list: ArrayList<WeatherDetail>){
        mData = list
    }

    fun isDataEmpty(): Boolean{
        return mData!!.isEmpty()
    }
}