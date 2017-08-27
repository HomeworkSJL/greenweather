package com.example.greenweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/8/11.
 */

public class Suggestion {
    public Info air;
    public Info comf;
    public Info cw;
    public Info drsg;
    public Info flu;
    public Info sport;
    public Info trav;
    public Info uv;


    public class Info {
        public String brf;
        public String txt;
    }
}
