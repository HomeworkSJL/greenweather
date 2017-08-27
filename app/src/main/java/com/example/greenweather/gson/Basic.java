package com.example.greenweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/8/11.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("cnty")
    public String countryName;

    @SerializedName("id")
    public String weatherId;
    public String lat;
    public String lon;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
        public String utc;
    }

}
