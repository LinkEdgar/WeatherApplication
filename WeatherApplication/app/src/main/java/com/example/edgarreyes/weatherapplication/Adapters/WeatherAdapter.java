package com.example.edgarreyes.weatherapplication.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.edgarreyes.weatherapplication.Location;
import com.example.edgarreyes.weatherapplication.R;

import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder>{

    public WeatherAdapter(ArrayList<Location> data, Context context, onClicked mClick){
        mLocationList = data;
        this.context = context;
        mClicked = mClick;
    }
    public interface onClicked{
        void onClick(int position);
    }

    private onClicked mClicked;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Location location = mLocationList.get(position);
        //sets the type
        holder.mTypeTV.setText(location.getType());
        //sets the location
        holder.mLocationIdTV.setText(location.getLocation());
        //sets the title
        holder.mTitleTV.setText(location.getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClicked.onClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mLocationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTitleTV;
        private TextView mLocationIdTV;
        private TextView mTypeTV;
        private View mView;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mTitleTV = itemView.findViewById(R.id.location_container_title);
            mLocationIdTV = itemView.findViewById(R.id.location_container_location);
            mTypeTV = itemView.findViewById(R.id.location_container_type);

        }
    }
}
