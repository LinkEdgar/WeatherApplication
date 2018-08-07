package com.example.edgarreyes.weatherapplication.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.example.edgarreyes.weatherapplication.Adapters.WeatherDetailAdapter
import com.example.edgarreyes.weatherapplication.R
import com.example.edgarreyes.weatherapplication.WeatherDetail
import kotlinx.android.synthetic.main.activity_weather_detail.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/*

 */

class WeatherDetailActivity : AppCompatActivity() {

    private lateinit var woeId:String

    private var detailWeatherList: ArrayList<WeatherDetail> ?= null

    private var detailListView: ListView ?= null
    private var mAdapter: WeatherDetailAdapter ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_detail)
        detailWeatherList = ArrayList()

        initListView()

        handleIntentData()
        makeDetailWeatherRequest()
    }

    //gets the extra information passed by the parent activity
    private fun handleIntentData(){
        val intent = intent
        woeId = intent.getStringExtra(MainActivity.locationIdKey)
        Log.e("woedid", "--> $woeId")
    }

    private fun makeDetailWeatherRequest(){
        val detailInfoClient = OkHttpClient()
        val url = buildUrl()
        val request = Request.Builder()
                .url(url).build()
        detailInfoClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("Failed", "--> $e")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseData = response.body()!!.string()
                    Log.e("Response ", "--> $responseData")
                    val jsonArray = JSONObject(responseData).getJSONArray(jsonArrayKey)

                    this@WeatherDetailActivity.runOnUiThread{
                        try{
                            addDataToAdapter(jsonArray)
                        }catch(e:IOException){
                            e.printStackTrace()
                        }
                    }
                } catch (e: JSONException) {

                }

            }
        })

    }
    //helper method to build url
    private fun buildUrl():String{
        val url = MainActivity.baseUrlAddress+woeId
        return url
    }

    companion object {
        val weatherState = "weather_state_name"
        val date = "created"
        val minTemp = "min_temp"
        val maxTemp = "max_temp"
        val currentTemp = "the_temp"
        val airPressure = "air_pressure"
        val humidity = "humidity"
        val jsonArrayKey = "consolidated_weather"
    }

    private fun initListView(){
        detailWeatherList!!.add(WeatherDetail("1", "",null, null, null, null, null))
        detailWeatherList!!.add(WeatherDetail("2", "",null, null, null, null, null))
        detailWeatherList!!.add(WeatherDetail("3", "",null, null, null, null, null))
        detailWeatherList!!.add(WeatherDetail("4", "",null, null, null, null, null))

        mAdapter = WeatherDetailAdapter(this, detailWeatherList)
        detailListView = weather_detail_list_view
        detailListView!!.adapter = mAdapter
    }

    private fun addDataToAdapter(jsonArray:JSONArray){
        for(x in 0..(jsonArray.length()-1)){
            val json = jsonArray.getJSONObject(x)
            val detailWeather = WeatherDetail(json.getString(weatherState), json.getString(date),
                    json.getString(minTemp), json.getString(maxTemp),
                    json.getString(currentTemp), json.getString(airPressure),
                    json.getString(humidity))
            detailWeatherList!!.add(detailWeather)
        }
        mAdapter!!.notifyDataSetChanged()
    }
}
