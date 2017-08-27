package com.example.greenweather.gson;

/**
 * Created by Administrator on 2017/8/21.
 */

public class LocCity {
    private String cityName = "";
    private double lat = 0;
    private double lon = 0;
    public void setCityName(String cityName){
        this.cityName = cityName;

    }
    public void setLat(double lat){
        this.lat = lat;
    }
    public void setLon(double lon)
    {
        this.lon = lon;
    }
    public String getCityName(){
        return cityName;

    }
    public double getLat(){
        return lat;
    }
    public double getLon()
    {
        return lon;
    }

}