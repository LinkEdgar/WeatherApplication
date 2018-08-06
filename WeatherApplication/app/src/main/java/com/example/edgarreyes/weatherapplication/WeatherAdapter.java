package com.example.edgarreyes.weatherapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder>{

    public WeatherAdapter(ArrayList<Location> data, Context context){
        mLocationList = data;
        this.context = context;
    }

    private ArrayList<Location> mLocationList;
    private Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.location_container, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Location location = mLocationList.get(position);
        //sets the type
        holder.mTypeTV.setText(location.getType());
        //sets the location
        holder.mLocationIdTV.setText(location.getLocation());
        //sets the title
        holder.mTitleTV.setText(location.getTitle());

    }

    @Override
    public int getItemCount() {
        return mLocationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTV;
        private TextView mLocationIdTV;
        private TextView mTypeTV;
        public ViewHolder(View itemView) {
            super(itemView);
            mTitleTV = itemView.findViewById(R.id.location_container_title);
            mLocationIdTV = itemView.findViewById(R.id.location_container_location);
            mTypeTV = itemView.findViewById(R.id.location_container_type);

        }

        @Override
        public void onClick(View view) {

        }
    }
}
