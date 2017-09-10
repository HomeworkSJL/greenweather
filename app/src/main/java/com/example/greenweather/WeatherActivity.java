package com.example.greenweather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.greenweather.adapter.HourlyForecastAdapter;
import com.example.greenweather.gson.Forecast;
import com.example.greenweather.gson.Hourly;
import com.example.greenweather.gson.News;
import com.example.greenweather.gson.Weather;
import com.example.greenweather.util.ChooseAreaFragment;
import com.example.greenweather.util.HttpUtil;
import com.example.greenweather.util.Utility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


import static android.R.attr.bitmap;
import static com.example.greenweather.util.Utility.getAddress;

/**
 * Created by Administrator on 2017/8/11.
 */

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private LinearLayout newsLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView pm10Text;
    private TextView airqualityText;
    private TextView airSuggest;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private TextView drsgText;
    private TextView fluText;
    private TextView travText;
    private TextView uvText;

    private ImageView bingPicImg;
    private String mWeatherId;
    public DrawerLayout drawerLayout;
    private Button navButton;
    private LinearLayout airLinearLayout;
    private LinearLayout noinfoLayout;
    private Button noinfoButton;

    private Weather[] oldWeather;
    private boolean isNotShowed;
    private boolean fragNotAdded;
    private RecyclerView recyclerView;
    private HourlyForecastAdapter adapter;
    private Handler mHandler ;
    private Runnable runnableRefresh;

    private Runnable runnableNews;
    private TextView updatetime;
    private int newsStart;
    private int newsSize;
    private ImageView newsMore;
    private ImageView loadingImage;
    private TextView channelText;
    private String channelNews;



        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            // getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //初始化各条件
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        newsLayout = (LinearLayout) findViewById(R.id.news_layout);
        airLinearLayout = (LinearLayout) findViewById(R.id.air);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        pm10Text = (TextView) findViewById(R.id.pm10_text);
        airqualityText = (TextView) findViewById(R.id.air_quality);
        airSuggest = (TextView) findViewById(R.id.airsuggest);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        drsgText = (TextView) findViewById(R.id.drsg);
        fluText = (TextView) findViewById(R.id.flu);
        travText = (TextView) findViewById(R.id.trav);
        uvText = (TextView) findViewById(R.id.uv);
        updatetime = (TextView) findViewById(R.id.updatetime_text);
            channelText =(TextView) findViewById(R.id.channel);

        noinfoLayout = (LinearLayout) findViewById(R.id.noinfoshow);
        noinfoButton = (Button) findViewById(R.id.navnoifo_button);
        newsMore = (ImageView) findViewById(R.id.newsmore);
            loadingImage = (ImageView) findViewById(R.id.loading);


        isNotShowed = true;
        mWeatherId = null;
        fragNotAdded = true;
            channelNews = "健康";
        newsStart = 0;
        newsSize = 20;

            StringBuilder sbBuilder = new StringBuilder();
            sbBuilder.append("头条 ");
            sbBuilder.append("新闻 ");
            sbBuilder.append("财经 ");
            sbBuilder.append("体育 ");
            sbBuilder.append("娱乐 ");
            sbBuilder.append("军事 ");
            sbBuilder.append("教育 ");
            sbBuilder.append("科技 ");
            sbBuilder.append("NBA ");
            sbBuilder.append("股票 ");
            sbBuilder.append("星座 ");
            sbBuilder.append("女性 ");
            sbBuilder.append("健康 ");
            sbBuilder.append("育儿 ");
            String likeUsers = sbBuilder.substring(0, sbBuilder.lastIndexOf(" ")).toString();
            channelText.setMovementMethod(LinkMovementMethod.getInstance());
            channelText.setText(addClickablePart(likeUsers), TextView.BufferType.SPANNABLE);

            recyclerView = (RecyclerView) findViewById(R.id.hourlycast_layout);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);



            drawerLayout = (DrawerLayout) findViewById(R.id.dwawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);

        showinfo();

            mHandler = new Handler();
            runnableRefresh = new Runnable() {
                public void run() {
                    this.update();
                    mHandler.postDelayed(this, 1000 * 60 * 60);// 间隔一个小时
                }
                void update() {
                    if(mWeatherId != null)
                    {
                        requestWeather(mWeatherId);
                    }
                }
            };
            //延时加载新闻
            runnableNews = new Runnable() {
                public void run() {
                    requestNews();


                }
            };


            navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragNotAdded) {addFregment();}
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });
        noinfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });
       updatetime.setOnClickListener(new View.OnClickListener() {
          @Override
            public void onClick(View v) {
              if(mWeatherId != null)
                 {
                    Toast.makeText(WeatherActivity.this,"天气更新中...",Toast.LENGTH_SHORT).show();
                    requestWeather(mWeatherId);
                 }
                }
            });
            newsMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestNews();
                }
            });




    }


    public void requestWeather(final String requestWeatherId) {
        String wertherUrl = "https://free-api.heweather.com/v5/weather?city=" +
                requestWeatherId + "&key=532bf4f8455c45bcb714dfb9ff70d49e";
        //String wertherUrl = "https://way.jd.com/he/full_weather?city=" + requestWeatherId+ "&appkey=470cab9b513497235fa0b2cce52d37e5";
        HttpUtil.sendOkHttpRequest(wertherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气数据失败",
                                Toast.LENGTH_SHORT).show();

                        if(fragNotAdded) {addFregment();}
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather[] weather;
                mHandler.postDelayed(runnableNews,5000);

                if (responseText.contains("\"basic\"") ) {
                    weather = Utility.handleWeatherResponse(responseText);
                    oldWeather = weather;
                } else {
                    weather = null;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && weather[0] != null && "ok".equals(weather[0].status)) {

                            if (oldWeather != null && oldWeather.length > 1) {
                                ShowChoise();
                            } else {
                                if(requestWeatherId.indexOf("CN") == -1){
                                    oldWeather[0].basic.cityName = requestWeatherId;
                                }
                                SharedPreferences.Editor editor;
                                editor = PreferenceManager.
                                        getDefaultSharedPreferences(WeatherActivity.this).
                                        edit();
                                editor.putString("weatherid", requestWeatherId);
                                editor.apply();
                                showWeatherInfo(oldWeather[0]);
                            }
                            mWeatherId = requestWeatherId;
                        } else {
                            if (responseText.indexOf("{\"status\":\"unknown city\"}") > 0) {
                                Toast.makeText(WeatherActivity.this, "城市名不对哦",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(WeatherActivity.this, "获取天气信息异常",
                                        Toast.LENGTH_SHORT).show();
                            }
                            if (oldWeather != null && oldWeather[0] != null) {
                                showWeatherInfo(oldWeather[0]);
                            }
                        }
                        if(fragNotAdded) {addFregment();}

                    }
                });

            }
        });
       // loadBingPic();
    }


///自动定位的天气

    public void requestLocWeather(final String requestWeatherId, final double lat, final double lon) {
        //String wertherUrl = "https://free-api.heweather.com/v5/weather?city=" +
         //       requestWeatherId + "&key=532bf4f8455c45bcb714dfb9ff70d49e";
        String wertherUrl = "https://way.jd.com/he/full_weather?city=" + requestWeatherId+ "&appkey=470cab9b513497235fa0b2cce52d37e5";

        HttpUtil.sendOkHttpRequest(wertherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气数据失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather[] weather;
                if (responseText.contains("\"basic\"") ) {
                    weather = Utility.handleJDWeatherResponse(responseText);
                   // weather = Utility.handleWeatherResponse(responseText);
                    oldWeather = weather;
                } else {
                    weather = null;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && weather[0] != null && "ok".equals(weather[0].status)) {
                            //百度定位误差，要选距离小的
                            if (oldWeather != null && oldWeather.length > 1) {
                                int k = 0;
                                double compare = -1;
                                double itempower;
                                double latdtmp,londtmp;
                                double latdif,londif;
                                for(int i = 0; i < oldWeather.length; i++){
                                    latdtmp = Double.valueOf(oldWeather[i].basic.lat);
                                    londtmp =Double.valueOf(oldWeather[i].basic.lon);
                                    latdif = latdtmp - lat;
                                    londif = londtmp - lon;
                                    itempower =  latdif * latdif + londif*londif;
                                    if(compare < 0){
                                        compare = itempower;
                                        k = i;
                                    }
                                    else if(itempower < compare){
                                        compare =itempower;
                                        k = i;
                                    }
                                }
                                /////////
                           /*     Gson gson;
                                GsonBuilder builder = new GsonBuilder();
                                gson = builder.create();
                                String weatherContent = gson.toJson(oldWeather[k], Weather.class);
                                try {
                                    JSONArray ja0 = new JSONArray();
                                    ja0.put(new JSONObject(weatherContent));
                                    JSONObject h5 = new JSONObject();
                                    h5.put("HeWeather5", ja0);
                                    String output = h5.toString();
                                    SharedPreferences.Editor editor ;
                                    editor = PreferenceManager.
                                            getDefaultSharedPreferences(WeatherActivity.this).edit();
                                    editor.putString("weather", output);
                                    editor.apply();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }*/


                                showWeatherInfo(oldWeather[k]);



                            }
                            else {
                                if(requestWeatherId.indexOf("CN") == -1){
                                    oldWeather[0].basic.cityName = requestWeatherId;
                                }
                                SharedPreferences.Editor editor;
                                editor = PreferenceManager.
                                        getDefaultSharedPreferences(WeatherActivity.this).
                                        edit();
                                editor.putString("weatherid", requestWeatherId);

                                editor.apply();
                                showWeatherInfo(oldWeather[0]);
                            }
                            mWeatherId = requestWeatherId;
                        } else {
                            if (responseText.indexOf("{\"status\":\"unknown city\"}") > 0) {
                                Toast.makeText(WeatherActivity.this, "城市名不对哦",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(WeatherActivity.this, "获取天气信息异常",
                                        Toast.LENGTH_SHORT).show();
                            }
                            if (oldWeather != null && oldWeather[0] != null) {
                                showWeatherInfo(oldWeather[0]);
                            }
                        }

                    }
                });

            }
        });
       // loadBingPic();
    }


    //////////
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        //  StringBuilder requestAddress= getAddress(weather.basic.lat,weather.basic.lon);
        //String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.cond.txt + " " + weather.now.wind.dir + " "
                + weather.now.wind.sc;
        titleCity.setText(cityName);
        // titleUpdateTime.setText("天气数据时间："+updateTime);

        degreeText.setText(degree);
        Resources res=getResources();
        int resIdentifier=res.getIdentifier("h"+weather.now.cond.code,"drawable",getPackageName());
        Drawable drawable= getResources().getDrawable(resIdentifier);
/// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        degreeText.setCompoundDrawables(drawable,null,null,null);
        weatherInfoText.setText(weatherInfo);
        updatetime.setText("更新：" + weather.basic.update.updateTime.substring(11,16));
        List<Hourly> hourlyList = weather.hourlyList;
        adapter = new HourlyForecastAdapter(hourlyList);
        recyclerView.setAdapter(adapter);


        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                    forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            // TextView minText = (TextView) view.findViewById(R.id.min_text);
            TextView windText = (TextView) view.findViewById(R.id.wind_text);

            dateText.setText(forecast.date.substring(5));
            infoText.setText(forecast.cond.txt_d + "/" + forecast.cond.txt_n);
            windText.setText(forecast.wind.dir + forecast.wind.sc);
            maxText.setText(forecast.temperature.max + "º/" + forecast.temperature.min + "º");
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
            airqualityText.setText(weather.aqi.city.qlty);
            pm10Text.setText(weather.aqi.city.pm10);
            airLinearLayout.setVisibility(View.VISIBLE);
        } else {
            airLinearLayout.setVisibility(View.GONE);
        }
        String airSuggestString = "空气："+weather.suggestion.air.brf+"，"+ weather.suggestion.air.txt;
        String comfort = "舒适度：" + weather.suggestion.comf.brf + "，" + weather.suggestion.comf.txt;
        String carWash = "洗车建议： " + weather.suggestion.cw.txt;
        String sport = "运动建议： " + weather.suggestion.sport.brf + "，" + weather.suggestion.sport.txt;
        airSuggest.setText(airSuggestString);
        comfortText.setText(comfort);
        sportText.setText(sport);
        carWashText.setText(carWash);
        drsgText.setText("穿衣建议：" +
                weather.suggestion.drsg.txt);
        fluText.setText("感冒指数：" + weather.suggestion.flu.brf + "，" +
                weather.suggestion.flu.txt);
        travText.setText("游玩建议：" + weather.suggestion.trav.brf + "，" +
                weather.suggestion.trav.txt);
        uvText.setText("UV：" + weather.suggestion.uv.brf + "，" +
                weather.suggestion.uv.txt);
        if(weatherInfo.contains("冰")){
            Glide.with(WeatherActivity.this).load(R.drawable.ice).into(bingPicImg);
            bingPicImg.setVisibility(View.VISIBLE);
        }
        else if(weatherInfo.contains("雪")){
            Glide.with(WeatherActivity.this).load(R.drawable.snow).into(bingPicImg);
            bingPicImg.setVisibility(View.VISIBLE);
        }
        else if(weatherInfo.contains("雨")){
            Glide.with(WeatherActivity.this).load(R.drawable.rain).into(bingPicImg);
            bingPicImg.setVisibility(View.VISIBLE);
        }
        else if(weatherInfo.contains("云")){
            Glide.with(WeatherActivity.this).load(R.drawable.cloudy).into(bingPicImg);
            bingPicImg.setVisibility(View.VISIBLE);
        }
        else if(weatherInfo.contains("晴")){
            Glide.with(WeatherActivity.this).load(R.drawable.sunny).into(bingPicImg);
            bingPicImg.setVisibility(View.VISIBLE);
        }
        else if(weatherInfo.contains("阴")){
            Glide.with(WeatherActivity.this).load(R.drawable.overcast).into(bingPicImg);
            bingPicImg.setVisibility(View.VISIBLE);
        }
        else if(weatherInfo.contains("霾")){
            Glide.with(WeatherActivity.this).load(R.drawable.haze).into(bingPicImg);
            bingPicImg.setVisibility(View.VISIBLE);
        }
        else if(weatherInfo.contains("雾")){
            Glide.with(WeatherActivity.this).load(R.drawable.frog).into(bingPicImg);
            bingPicImg.setVisibility(View.VISIBLE);
        }
        else {
            bingPicImg.setVisibility(View.GONE);
        }
        if(isNotShowed) {
            weatherLayout.setVisibility(View.VISIBLE);
            isNotShowed = false;
            noinfoLayout.setVisibility(View.GONE);
             mHandler.postDelayed(runnableRefresh,1000 * 60 * 60);
            mHandler.postDelayed(runnableNews,5000);
           // requestNews();
        }


    }


    private void showinfo() {
        weatherLayout.setVisibility(View.INVISIBLE);
        SharedPreferences prefs ;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherId = prefs.getString("weatherid", null);
        if (weatherId != null) {
            //有缓存时直接解析天气数据
                mWeatherId = weatherId;
                requestWeather(mWeatherId);
        }
        else  {
                    addFregment();
                    drawerLayout.openDrawer(GravityCompat.START);

        }
    }


    public void ShowChoise() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("请选择一个城市");
        //    指定下拉列表的显示数据
        StringBuilder cityItems = new StringBuilder();
        for (int i = 0; i < oldWeather.length; i++) {
            String prov = getAddress(oldWeather[i].basic.lat, oldWeather[i].basic.lon);
            if (prov != null) {
                oldWeather[i].basic.cityName = prov;
            }
            cityItems.append(oldWeather[i].basic.cityName + ";");
        }
        String[] citiestmp = cityItems.toString().split(";");
        final String[] cities = citiestmp;
        //    设置一个下拉的列表选择项
        AlertDialog.Builder builder1 = builder.setItems(cities, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /////////
/*                        Gson gson;
                        GsonBuilder builder = new GsonBuilder();
                        gson = builder.create();
                        String weatherContent = gson.toJson(oldWeather[which], Weather.class);
                        try {
                            JSONArray ja0 = new JSONArray();
                            ja0.put(new JSONObject(weatherContent));
                            JSONObject h5 = new JSONObject();
                            h5.put("HeWeather5", ja0);
                            String output = h5.toString();
                            SharedPreferences.Editor editor ;
                            editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", output);
                            editor.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                        showWeatherInfo(oldWeather[which]);
                    };
                }

        );
        builder.show();
    }
    private void addFregment(){
        //动态加载碎片，缩短启动速度，这样能快些显示天气信息。
        if(fragNotAdded) {
            fragNotAdded = false;
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.choose_area_fragment, new ChooseAreaFragment());
            transaction.commit();
        }
    }
    public void requestNews(){
        newsMore.setVisibility(View.GONE);
        loadingImage.setVisibility(View.VISIBLE);
        String newsUrl = "https://way.jd.com/jisuapi/get?channel="+ channelNews +"&num="
                + newsSize +"&start="+ newsStart +"&appkey=470cab9b513497235fa0b2cce52d37e5";
        HttpUtil.sendOkHttpRequest(newsUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newsMore.setVisibility(View.VISIBLE);
                        loadingImage.setVisibility(View.GONE);

                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                if (responseText.contains("\"msg\":\"查询成功\"")) {
                    newsStart += newsSize;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           News newsGot = Utility.handleJDNewsResponse(responseText);

                            if(newsGot.newsItemList.size() == newsSize){
                                newsMore.setVisibility(View.VISIBLE);
                            }
                            loadingImage.setVisibility(View.GONE);


                            //  newsLayout.removeAllViews();
                            for (News.NewsItem newsItem : newsGot.newsItemList) {
                                View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.news_item,
                                        newsLayout, false);
                                TextView titleText = (TextView) view.findViewById(R.id.newstitle);
                                ImageView newsImg = (ImageView) view.findViewById(R.id.newsimage);
                                final ImageView newsimageShowed = (ImageView) view.findViewById(R.id.newsimageshowed);
                                TextView newsFrom = (TextView) view.findViewById(R.id.newsfrom);
                                final LinearLayout newContentLayout = (LinearLayout) view.findViewById(R.id.newscontent);
                                final String picUrl = newsItem.pic;

                                titleText.setText(newsItem.title);
                                newsFrom.setText(newsItem.src);
                                if(!("".equals(picUrl))){
                                    Glide.with(WeatherActivity.this).load(picUrl).into(newsImg);
                                }
                                Element webtag = Jsoup.parse(newsItem.content).body();
                                SpannableStringBuilder ssb = new SpannableStringBuilder();
                                List<Node> mChildNode = webtag.childNodes();
                                boolean isFirstChild;
                                for(Node firstchildNode : mChildNode){
                               //     Log.d("Test"+firstchildNode.nodeName(),firstchildNode.attributes().toString()+"::"+firstchildNode.toString());
                                    isFirstChild = true;
                                    String nodenameChild = firstchildNode.nodeName();
                                    if(nodenameChild != null) {
                                        switch (nodenameChild) {
                                            case "p":
                                                List<Node> secondChildnodeList = firstchildNode.childNodes();
                                                for (Node node : secondChildnodeList) {
                                                    switch (node.nodeName()) {
                                                        case "img":
                                                            if(ssb.length() > 0) {
                                                                TextView text1 = new TextView(WeatherActivity.this);
                                                                text1.setText(ssb); //动态添加
                                                                newContentLayout.addView(text1);
                                                                ssb.clear();
                                                            }
                                                            String imgSrc = node.attr("src");
                                                            String innerpicUrl ;
                                                            if(imgSrc.contains("http")) {
                                                                innerpicUrl =  imgSrc;
                                                            }
                                                            else {
                                                                innerpicUrl = "http:" + imgSrc;
                                                            }
                                                            if(innerpicUrl != null ) {
                                                                ImageView img1 = new ImageView(WeatherActivity.this);
                                                                Glide.with(WeatherActivity.this).load(picUrl).into(img1);
                                                                newContentLayout.addView(img1);
                                                            }
                                                            break;
                                                        case "figure":
                                                            if(ssb.length() > 0) {
                                                                TextView text1 = new TextView(WeatherActivity.this);
                                                                text1.setText(ssb); //动态添加
                                                                newContentLayout.addView(text1);
                                                                ssb.clear();

                                                            }
                                                            List<Node> figureNode = node.childNodes();
                                                            for(Node figure:figureNode){
                                                                if("img".equals(figure.nodeName())){
                                                            imgSrc = figure.attr("src");
                                                            if(imgSrc.contains("http")) {
                                                                innerpicUrl =  imgSrc;
                                                            }
                                                            else {
                                                                innerpicUrl = "http:" + imgSrc;
                                                            }
                                                            if(innerpicUrl != null ) {
                                                                ImageView img1 = new ImageView(WeatherActivity.this);
                                                                Glide.with(WeatherActivity.this).load(picUrl).into(img1);
                                                                newContentLayout.addView(img1);
                                                            }
                                                            }
                                                                else {
                                                                    {
                                                                        Element otherElement = Jsoup.parse(figure.toString()).body();
                                                                        ssb.append(otherElement.text().replace("&nbsp;", " "));
                                                                    }
                                                                }
                                                            }

                                                            break;
                                                        case "strong":
                                                            SpannableString spanString = new SpannableString(node.childNode(0)
                                                                    .toString().trim().replace("　",""));
                                                            StyleSpan span = new StyleSpan(Typeface.BOLD);
                                                            spanString.setSpan(span, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                            if(isFirstChild){
                                                                ssb.append("　");//加中文空格
                                                                isFirstChild = false;
                                                            }
                                                            ssb.append(spanString);
                                                            break;
                                                        default:
                                                            if(isFirstChild){
                                                                ssb.append("　");//加中文空格
                                                                isFirstChild = false;
                                                            }
                                                            Element otherElement = Jsoup.parse(node.toString()).body();
                                                            ssb.append(otherElement.text().replace("&nbsp;", " "));
                                                            break;
                                                    }
                                                }
                                                ssb.append("\n");
                                                break;
                                            case "img":
                                                if(ssb.length() > 0) {
                                                    TextView text1 = new TextView(WeatherActivity.this);
                                                    text1.setText(ssb); //动态添加
                                                    newContentLayout.addView(text1);
                                                    ssb.clear();
                                                }
                                                String imgSrc = firstchildNode.attr("src");
                                                String innerpicUrl ;
                                                if(imgSrc.contains("http")) {
                                                    innerpicUrl =  imgSrc;
                                                }
                                                else {
                                                    innerpicUrl = "http:" + imgSrc;
                                                }
                                                if(innerpicUrl != null ) {
                                                    ImageView img1 = new ImageView(WeatherActivity.this);
                                                    Glide.with(WeatherActivity.this).load(picUrl).into(img1);
                                                    newContentLayout.addView(img1);
                                                }
                                                break;
                                            case "figure":
                                                if(ssb.length() > 0) {
                                                    TextView text1 = new TextView(WeatherActivity.this);
                                                    text1.setText(ssb); //动态添加
                                                    newContentLayout.addView(text1);
                                                    ssb.clear();

                                                }
                                                List<Node> figureNode = firstchildNode.childNodes();
                                                for(Node figure:figureNode){
                                                    if("img".equals(figure.nodeName())){
                                                        imgSrc = figure.attr("src");
                                                        if(imgSrc.contains("http")) {
                                                            innerpicUrl =  imgSrc;
                                                        }
                                                        else {
                                                            innerpicUrl = "http:" + imgSrc;
                                                        }
                                                        if(innerpicUrl != null ) {
                                                            ImageView img1 = new ImageView(WeatherActivity.this);
                                                            Glide.with(WeatherActivity.this).load(picUrl).into(img1);
                                                            newContentLayout.addView(img1);
                                                        }
                                                    }
                                                    else {
                                                        Element otherElement = Jsoup.parse(figure.toString()).body();
                                                        ssb.append(otherElement.text().replace("&nbsp;", " "));
                                                    }
                                                }

                                                break;

                                            case "strong":
                                                SpannableString spanString = new SpannableString(firstchildNode.toString());
                                                StyleSpan span = new StyleSpan(Typeface.BOLD);
                                                spanString.setSpan(span, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                                ssb.append(spanString);
                                                break;
                                            default:
                                                Element otherElement = Jsoup.parse(firstchildNode.toString()).body();
                                                ssb.append(otherElement.text().replace("&nbsp;", " "));

                                        }
                                    }
                                    else {
                                        Element otherElement = Jsoup.parse(firstchildNode.toString()).body();
                                        ssb.append(otherElement.text().replace("&nbsp;", " "));

                                    }
                                }
                                if(ssb.length() > 0) {
                                    TextView text1 = new TextView(WeatherActivity.this);
                                    text1.setText(ssb); //动态添加
                                    newContentLayout.addView(text1);
                                    ssb.clear();
                                }

                                // newsInfo.setText(ssb);
                                Elements pageList = Jsoup.parse(newsItem.content).body().getElementsByTag("p");
                                StringBuilder pageText = new StringBuilder();
                                for(int i = 0; i < pageList.size(); i++){
                                    pageText.append("    ");
                                    pageText.append(pageList.get(i).text().replace("　",""));
                                    pageText.append("\n");
                                }
                              //  newsInfo.setText(pageText);
                                LinearLayout newstitleandfrom = (LinearLayout)view.findViewById(R.id.newstitleandfrom);
                                newstitleandfrom.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(newContentLayout.getVisibility() == View.VISIBLE){
                                            newContentLayout.setVisibility(View.GONE);
                                        }
                                        else {
                                            newContentLayout.setVisibility(View.VISIBLE);
                                        }

                                    }
                                });
                                newsImg.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        if(newsimageShowed.getVisibility() == View.VISIBLE)
                                        {
                                            newsimageShowed.setVisibility(View.GONE);
                                        }
                                        else {
                                            if(!("".equals(picUrl))){
                                                Glide.with(WeatherActivity.this).load(picUrl).into(newsimageShowed);
                                            }

                                            newsimageShowed.setVisibility(View.VISIBLE);
                                        }
                                        return true;
                                    }
                                });
                                newsImg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(newContentLayout.getVisibility() == View.VISIBLE){
                                            newContentLayout.setVisibility(View.GONE);
                                            newsimageShowed.setVisibility(View.GONE);
                                        }
                                        else {
                                            newContentLayout.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                                newsLayout.addView(view);
                            }
                        }
                    });

                }
            }
        });
    }
    /**
     * @param str
     * @return
     */
    private SpannableStringBuilder addClickablePart(String str) {
//        SpannableString spanStr = new SpannableString(str);

        SpannableStringBuilder ssb = new SpannableStringBuilder(str);

        String[] likeUsers = str.split(" ");

        if (likeUsers.length > 0) {
            // 最后一个
            for (int i = 0; i < likeUsers.length; i++) {
                final String name = likeUsers[i];
                final int start = str.indexOf(name);
                ssb.setSpan(new ClickableSpan() {

                    @Override
                    public void onClick(View widget) {
                        channelNews = name;
                        newsLayout.removeAllViews();
                        requestNews();
                        /*Toast.makeText(MyApplication.getContext(), name,
                                Toast.LENGTH_SHORT).show();*/

                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                         ds.setColor(Color.BLACK); // 设置文本颜色
                        // 去掉下划线
                        ds.setUnderlineText(false);
                    }

                }, start, start + name.length(), 0);
            }
        }
        return ssb;
    } // end of addClickablePart
    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(runnableRefresh); //停止刷新
        mHandler.removeCallbacks(runnableNews);
        super.onDestroy();
    }

}
