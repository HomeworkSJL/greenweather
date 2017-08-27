package com.example.greenweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/8/11.
 */

public class Now {
    public Cond cond;
    public String fl;
    public String hum;
    public String pcpn;
    public String pres;
    @SerializedName("tmp")
    public String temperature;
    public String vis;
    public Wind wind;



    public class Cond {
        public String code;
        public String txt;
    }
    public class Wind {
        public String deg;
        public String dir;
        public String sc;
        public String spd;
    }
}
