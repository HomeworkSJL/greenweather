package com.example.greenweather.gson;

/**
 * Created by Administrator on 2017/8/11.
 */

public class AQI {
    public AQICity city;
    public class AQICity {
        public String aqi;
        public String pm10;
        public String pm25;
        public String qlty; //共六个级别，分别：优，良，轻度污染，中度污染，重度污染，严重污染
    }
}
