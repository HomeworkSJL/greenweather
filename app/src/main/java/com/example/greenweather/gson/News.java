package com.example.greenweather.gson;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/9/6.
 */
public class News {
    String num;
    String channel;
    @SerializedName("list")
    public List<NewsItem> newsItemList;

public class NewsItem {
    public String src;
    public String weburl;
    public String time;
    public String pic;
    public String title;
    public String category;
    public String content;
}
}
