package com.example.greenweather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.greenweather.R;
import com.example.greenweather.gson.Hourly;

import java.util.List;

/**
 * Created by Administrator on 2017/9/5.
 */

public class HourlyForecastAdapter extends RecyclerView.Adapter <HourlyForecastAdapter.ViewHolder>{
    private List<Hourly> mHourly;
    static class ViewHolder extends RecyclerView.ViewHolder
    {
        //ImageView hourlyIcon;
        TextView hourlyInfo;
        TextView hour;
        TextView hourWind;
        TextView hourWindsc;
        TextView hourTmp;
        public ViewHolder(View view){
            super(view);
            hour = (TextView) view.findViewById(R.id.hour_text);
            hourlyInfo = (TextView) view.findViewById(R.id.hourinfo_text);
            hourWind = (TextView) view.findViewById(R.id.hourwind_text);
            hourWindsc = (TextView) view.findViewById(R.id.hourwindsc_text);
            hourTmp = (TextView) view.findViewById(R.id.hourtmp_text);
        }

    }


    public HourlyForecastAdapter(List<Hourly> hourlyList){
        mHourly = hourlyList;
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Hourly hourly = mHourly.get(position);
        int date = Integer.valueOf(hourly.date.substring(8,10));
        int timeHour = Integer.valueOf(hourly.date.substring(11,13));
        if(timeHour == 0 ){
            holder.hour.setText(date + "日");
        }
        else {
            holder.hour.setText(timeHour+"时");
        }
        holder.hourTmp.setText(hourly.tmp + "°");
        holder.hourlyInfo.setText(hourly.cond.txt);
        holder.hourWind.setText(hourly.wind.dir);
        holder.hourWindsc.setText(hourly.wind.sc+"级");


    }
    @Override
    public int getItemCount(){
        return mHourly.size();
    }
}
