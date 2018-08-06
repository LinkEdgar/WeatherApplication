package com.example.edgarreyes.weatherapplication.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.edgarreyes.weatherapplication.R;
import com.example.edgarreyes.weatherapplication.WeatherDetail;

import java.sql.Array;
import java.util.ArrayList;

public class WeatherDetailAdapter extends ArrayAdapter<WeatherDetail> {

    public WeatherDetailAdapter(Context context, ArrayList<WeatherDetail> data){
        super(context, 0, data);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rootView = convertView;
        if(rootView == null)
            rootView = LayoutInflater.from(getContext()).inflate(R.layout.deatil_weather_container, parent, false);
        WeatherDetail detailObject = getItem(position);

        TextView weatherState = rootView.findViewById(R.id.detail_container_weather_state);
        weatherState.setText(detailObject.getWeatherState());

        return rootView;
    }
}
