package com.example.edgarreyes.weatherapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    //used to get last known location
    private lateinit var fusedLocation: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //checkLocationPermissions()

        getUserLocation()
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
                //val url = baseUrlAddress+"?lattlong=36.96,-122.02"
                loadWeatherInformation(url)
            }else Toast.makeText(this, R.string.location_null_label,Toast.LENGTH_LONG).show()
        }
    }
    //This method takes a url to the weather api and makes an http request
    private fun loadWeatherInformation(url:String){
        val weatherClient = OkHttpClient()
        val request= Request.Builder().url(url).build()
        weatherClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("Failed", "--> $e")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseData = response.body()!!.string()
                    val json = JSONObject(responseData)
                    Log.e("result", "--> $json")
                    /*
                    val jsonArray = json.getJSONArray("zip_codes")
                    // Log.e("jsonarray", "$jsonArray")
                    for(x in 0..jsonArray.length()){ //iterates through the json array and extracts all unique zip codes
                        val jObject = jsonArray.getJSONObject(x)
                        val zipCode = jObject.getString("zip_code")
                    }
                    */
                } catch (e: JSONException) {

                }

            }
        })
    }

    companion object {
        val baseUrlAddress = "https://www.metaweather.com/api/location/search/?lattlong="
        val requestLocationCode  = 24
    }
    private fun buildUrl(lat: Double, long: Double):String{
        val stringBuilder = StringBuilder()
        stringBuilder.append(baseUrlAddress).append(lat).append(",$long")
        Log.d("buildUrl()", "url --> $stringBuilder")
        return stringBuilder.toString()
    }
}
