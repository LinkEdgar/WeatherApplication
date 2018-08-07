package com.example.edgarreyes.weatherapplication.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.edgarreyes.weatherapplication.Adapters.WeatherAdapter
import com.example.edgarreyes.weatherapplication.Location
import com.example.edgarreyes.weatherapplication.ViewModel.LocationViewModel
import com.example.edgarreyes.weatherapplication.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class MainActivity : AppCompatActivity(), WeatherAdapter.onClicked {


    //used to get last known location
    private lateinit var fusedLocation: FusedLocationProviderClient

    private var mRecyclerView: RecyclerView ?= null

    private var mAdapter: WeatherAdapter?= null

    private var mLocationList: ArrayList<Location> ? = null

    private var mViewModel: LocationViewModel?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)

        mLocationList = mViewModel!!.getData()

        initRecyclerView()

        //if the list of locations has a size < 0 then we reload the data
        if(mLocationList!!.size < 1) {
            checkLocationPermissions()
        }
        setupSearchView()


    }

    //Checks if we have the user's permission to access their location and if we don't we ask for the permission
    private fun checkLocationPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            //We have user permission
            getUserLocation()
        }else{
            //We ask the user for permission to access their location
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), requestLocationCode)
        }


    }

    /*
    Attempts to get the user's location via the fusedLocation client. If the location is null a toast
    is displayed letting the user know we could not get their location . If we are successful then we
    call loadWeatherInformation()
     */

    @SuppressLint("MissingPermission")
    private fun getUserLocation(){
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        fusedLocation.lastLocation.addOnSuccessListener { location ->
            if(location != null) {
                val latitude= location.latitude
                val longitude = location.longitude
                val url =buildUrl(latitude, longitude)
                loadWeatherInformation(url)
            }else Toast.makeText(this, R.string.location_null_label,Toast.LENGTH_LONG).show()
        }
    }
    //This method takes a url to the weather api and makes an http request
    private fun loadWeatherInformation(url:String){
        val weatherClient = OkHttpClient()
        val request= Request.Builder().url(url).build()
        setProgressbarVisibility(true)
        weatherClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("Failed", "--> $e")
                this@MainActivity.runOnUiThread {
                    try {
                        setProgressbarVisibility(false)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseData = response.body()!!.string()
                    val jsonArray = JSONArray(responseData)

                    //Data must be processes on main thread to  update UI
                    this@MainActivity.runOnUiThread {
                        try {
                            addDataToAdapter(jsonArray)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: JSONException) {

                }

            }
        })
    }

    companion object {
        val baseUrlAddress = "https://www.metaweather.com/api/location/"
        val latAndLongSearch = "search/?lattlong="
        val requestLocationCode  = 24
        val titleKey = "title"
        val locationIdKey = "woeid"
        val typeKey = "location_type"
        val locationListKey = "list"
        val queryUrlAddress = "search/?query="
    }

    //helper method to correctly create our request url
    private fun buildUrl(lat: Double, long: Double):String{
        val stringBuilder = StringBuilder()
        stringBuilder.append(baseUrlAddress).append(latAndLongSearch).append(lat).append(",$long")
        Log.d("buildUrl()", "url --> $stringBuilder")
        return stringBuilder.toString()
    }

    //sets up the recyclerview
    private fun initRecyclerView(){
        mRecyclerView = main_activity_recycler_view
        val manager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        mRecyclerView!!.layoutManager = manager
        mAdapter = WeatherAdapter(mLocationList, this, this)
        mRecyclerView!!.adapter = mAdapter

    }
    //takes a boolean and sets the progressbar base on its value
    private fun setProgressbarVisibility(setVisible: Boolean){
        if(setVisible)
            main_activity_progressbar.visibility = View.VISIBLE
        else main_activity_progressbar.visibility = View.GONE
    }

    //Call back for recyclerview children to handle click events
    override fun onClick(position: Int) {
        val detailActivityIntent = Intent(this, WeatherDetailActivity::class.java)
        detailActivityIntent.putExtra(locationIdKey, mLocationList!!.get(position).location)
        startActivity(detailActivityIntent)
    }

    /*
    Takes a json array and extracts the information for our location object
     */
    fun addDataToAdapter(jsonArray: JSONArray){
        for(x in 0..(jsonArray.length()-1)){
            val json = jsonArray.getJSONObject(x)
            val location = Location(json.getString(titleKey), json.getString(locationIdKey), json.getString(typeKey))
            mLocationList!!.add(location)
            mAdapter!!.notifyItemInserted(x)
        }
        //sets the data
        mViewModel!!.setData(mLocationList!!)
        setProgressbarVisibility(false)
    }

    private fun setupSearchView(){
        main_activity_searchview!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                submitSearch(s)
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {

                return false
            }
        })
    }
    /*
    Takes in the user's text and attempts to query via the api
     */
    private fun submitSearch(userInput: String){
        //url address to perform queries
        val url = "$baseUrlAddress$queryUrlAddress$userInput"
        if(mLocationList!!.size> 0)
            mLocationList!!.clear()
        loadWeatherInformation(url)
    }
}
