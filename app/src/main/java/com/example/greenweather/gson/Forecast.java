package com.example.greenweather.gson;

import com.google.gson.annotations.SerializedName;



/**
 * Created by Administrator on 2017/8/11.
 */

public class Forecast {
    public Astro astro;
    public Cond cond;
    public String date;
    public String hum;
    public String pcpn;
    public String pop;
    public String pres;

    @SerializedName("tmp")
    public Temperature temperature;
    public String uv;
    public String vis;
    public Wind wind;


    public class Astro { //"mr":"21:39","ms":"09:24","sr":"05:20","ss":"18:46"
        public String mr;
        public String ms;
        public String sr;
        public String ss;
    }
    public class Cond{//"code_d":"306","code_n":"306","txt_d":"中雨","txt_n":"中雨"
        public String code_d;
        public String code_n;
        public String txt_d;
        public String txt_n;
    }

    public class Temperature{
        public String max;
        public String min;

    }
    public class Wind{
        public String deg;
        public String dir;
        public String sc;
        public String spd;

    }
}

/*{"astro":{"mr":"21:39","ms":"09:24","sr":"05:20","ss":"18:46"},
        "cond":{"code_d":"306","code_n":"306","txt_d":"中雨","txt_n":"中雨"},
        "date":"2017-08-12",
        "hum":"87",
        "pcpn":"24.4",
        "pop":"100",
        "pres":"1006",
        "tmp":{"max":"29","min":"25"},
        "uv":"1",
        "vis":"11",
        "wind":{"deg":"115","dir":"东南风","sc":"微风","spd":"15"}},*/