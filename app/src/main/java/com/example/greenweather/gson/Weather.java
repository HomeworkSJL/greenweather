package com.example.greenweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */

public class Weather {
    public AQI aqi;
    public Basic basic;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
    @SerializedName("hourly_forecast")
    public List<Hourly> hourlyList;
    public Now now;
    public String status;
    public Suggestion suggestion;
}
