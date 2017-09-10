package com.example.greenweather.gson;

/**
 * Created by Administrator on 2017/9/4.
 */

public class Hourly {
    public Cond cond;
    public String date;
    public String hum;
    public String pop;
    public String pres;
    public String tmp;
    public Wind wind;


    public class Cond{//"cond":{"code":"300","txt":"阵雨"},
        public String code;
        public String txt;
    }
    public class Wind {
        public String deg;
        public String dir;
        public String sc;
        public String spd;
    }

/*
    "hourly_forecast":
    [
    {"cond":{"code":"300","txt":"阵雨"},
    "date":"2017-09-04 19:00",
    "hum":"83",
    "pop":"60",
    "pres":"1010",
    "tmp":"24",
    "wind":{"deg":"126","dir":"东南风","sc":"3-4","spd":"18"}},
    {"cond":{"code":"305","txt":"小雨"},"
    date":"2017-09-04 22:00",
    "hum":"89","
    pop":"27",
    "pres":"1010",
    "tmp":"23",
    "wind":{"deg":"135","dir":"东南风","sc":"3-4","spd":"17"}}]
     */
}
