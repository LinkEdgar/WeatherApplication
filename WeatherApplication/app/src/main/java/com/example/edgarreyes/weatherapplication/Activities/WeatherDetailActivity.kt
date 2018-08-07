package com.example.edgarreyes.weatherapplication.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
    }

    /*
    Http request for the detailed forecast through OkHttpClient.
    Since the UI must be updated on the main thread we call runOnUiThread to update the listview
     */
    private fun makeDetailWeatherRequest(){
        val detailInfoClient = OkHttpClient()
        val url = buildUrl()
        val request = Request.Builder()
                .url(url).build()
        setProgressbarVisibility(true)
        detailInfoClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("HttpRequest:", "failed")
                this@WeatherDetailActivity.runOnUiThread{
                    try{
                        setProgressbarVisibility(false)
                    }catch(e:IOException){
                        e.printStackTrace()
                    }
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseData = response.body()!!.string()
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
        val weatherAbrreviation = "weather_state_abbr"
    }

    //sets up the List view
    private fun initListView(){
        mAdapter = WeatherDetailAdapter(this, detailWeatherList)
        detailListView = weather_detail_list_view
        detailListView!!.adapter = mAdapter
    }

    //This method takes in a json array and extracts the necessary data into a custom weather object that
    //populates our listview
    private fun addDataToAdapter(jsonArray:JSONArray){
        for(x in 0..(jsonArray.length()-1)){
            val json = jsonArray.getJSONObject(x)
            val detailWeather = WeatherDetail(json.getString(weatherState), json.getString(date),
                    json.getString(minTemp), json.getString(maxTemp),
                    json.getString(currentTemp), json.getString(airPressure),
                    json.getString(humidity), json.getString(weatherAbrreviation))
            detailWeatherList!!.add(detailWeather)
        }
        setProgressbarVisibility(false)
        mAdapter!!.notifyDataSetChanged()
    }

    //takes a boolean and sets the progressbar base on its value
    private fun setProgressbarVisibility(setVisible: Boolean){
        if(setVisible)
            weather_detail_progressbar.visibility = View.VISIBLE
        else weather_detail_progressbar.visibility = View.GONE
    }
}
