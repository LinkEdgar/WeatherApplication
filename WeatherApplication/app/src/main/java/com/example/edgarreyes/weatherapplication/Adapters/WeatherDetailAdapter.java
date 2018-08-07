package com.example.edgarreyes.weatherapplication.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.edgarreyes.weatherapplication.R;
import com.example.edgarreyes.weatherapplication.WeatherDetail;

import java.sql.Array;
import java.util.ArrayList;

public class WeatherDetailAdapter extends ArrayAdapter<WeatherDetail> {

    public WeatherDetailAdapter(Context context, ArrayList<WeatherDetail> data){
        super(context, 0, data);

    }

    private String IMAGE_BASE_URL = "https://www.metaweather.com/static/img/weather/png/";
    private String IMAGE_TYPE = ".png";

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rootView = convertView;
        if(rootView == null)
            rootView = LayoutInflater.from(getContext()).inflate(R.layout.deatil_weather_container, parent, false);
        WeatherDetail detailObject = getItem(position);

        setUI(rootView, detailObject);






        return rootView;
    }

    //Binds information from our weather object to our view, including an image which is handled
    //through glide
    private void setUI(View rootView, WeatherDetail weather){
        TextView weatherState = rootView.findViewById(R.id.detail_container_weather_state);
        weatherState.setText(weather.getWeatherState());

        TextView date = rootView.findViewById(R.id.detail_container_date);
        date.setText(weather.getDate());

        TextView minTemp = rootView.findViewById(R.id.detail_container_min_temp);
        minTemp.setText(weather.getMinTemp());

        TextView maxTemp = rootView.findViewById(R.id.detail_container_max_temp);
        maxTemp.setText(weather.getMaxTemp());

        TextView currentTemp = rootView.findViewById(R.id.detail_container_current_temp);
        currentTemp.setText(weather.getCurrentTemp());

        TextView humidity = rootView.findViewById(R.id.detail_container_humidity);
        humidity.setText(weather.getHumidity());

        TextView airPressure = rootView.findViewById(R.id.detail_container_air_pressure);
        airPressure.setText(weather.getAirPressure());

        ImageView weatherImage = rootView.findViewById(R.id.detail_container_weather_image);
        Glide.with(rootView).load(IMAGE_BASE_URL+ weather.getImageAbbreviation()+IMAGE_TYPE)
                .into(weatherImage);
    }
}
