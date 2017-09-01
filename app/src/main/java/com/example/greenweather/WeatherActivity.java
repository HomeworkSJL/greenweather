package com.example.greenweather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.greenweather.gson.Forecast;
import com.example.greenweather.gson.Weather;
import com.example.greenweather.service.AutoUpdateService;
import com.example.greenweather.util.ChooseAreaFragment;
import com.example.greenweather.util.HttpUtil;
import com.example.greenweather.util.Utility;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.GsonBuilder;

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
    private TextView aqiText;
    private TextView pm25Text;
    private TextView pm10Text;
    private TextView airqualityText;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private TextView drsgText;
    private TextView fluText;
    private TextView travText;
    private TextView uvText;

    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;
    public DrawerLayout drawerLayout;
    private Button navButton;
    private LinearLayout airLinearLayout;
    private LinearLayout noinfoLayout;
    private Button noinfoButton;

    private Weather[] oldWeather;
    private String oldBingPic;
    private boolean isNotShowed;


    private boolean glided ;


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
        airLinearLayout = (LinearLayout) findViewById(R.id.air);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        pm10Text = (TextView) findViewById(R.id.pm10_text);
        airqualityText = (TextView) findViewById(R.id.air_quality);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        drsgText = (TextView) findViewById(R.id.drsg);
        fluText = (TextView) findViewById(R.id.flu);
        travText = (TextView) findViewById(R.id.trav);
        uvText = (TextView) findViewById(R.id.uv);

        noinfoLayout = (LinearLayout) findViewById(R.id.noinfoshow);
        noinfoButton = (Button) findViewById(R.id.navnoifo_button);


        glided = false;
        isNotShowed = true;
        mWeatherId = null;



        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) findViewById(R.id.dwawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);

        showinfo();


        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });
        noinfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mWeatherId != null){
                    requestWeather(mWeatherId);
                }
            }
        });
        if (oldBingPic != null) {
            Glide.with(this).load(oldBingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }





    }


    public void requestWeather(final String requestWeatherId) {
        String wertherUrl = "https://free-api.heweather.com/v5/weather?city=" +
                requestWeatherId + "&key=532bf4f8455c45bcb714dfb9ff70d49e";
        HttpUtil.sendOkHttpRequest(wertherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气数据失败，显示原有数据",
                                Toast.LENGTH_SHORT).show();
                        if (oldWeather != null && oldWeather.length > 1) {
                            ShowChoise();
                        }
                        else {
                            if (oldWeather != null && oldWeather[0] != null) {
                                showWeatherInfo(oldWeather[0]);
                            }
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather[] weather;
                if (responseText.indexOf("\"basic\"") > 0) {
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
                                editor.putString("weather", responseText);
                                editor.apply();
                                showWeatherInfo(oldWeather[0]);
                            }
                            mWeatherId = requestWeatherId;
                        } else {
                            if (responseText.indexOf("{\"status\":\"unknown city\"}") > 0) {
                                Toast.makeText(WeatherActivity.this, "城市名不对哦，显示原有数据",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(WeatherActivity.this, "获取天气信息异常，显示原有数据",
                                        Toast.LENGTH_SHORT).show();
                            }
                            if (oldWeather != null && oldWeather[0] != null) {
                                showWeatherInfo(oldWeather[0]);
                            }
                        }
                        swipeRefresh.setRefreshing(false);

                    }
                });

            }
        });
        loadBingPic();
    }


///自动定位的天气

    public void requestLocWeather(final String requestWeatherId, final double lat, final double lon) {
        String wertherUrl = "https://free-api.heweather.com/v5/weather?city=" +
                requestWeatherId + "&key=532bf4f8455c45bcb714dfb9ff70d49e";
        HttpUtil.sendOkHttpRequest(wertherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气数据失败，显示原有数据",
                                Toast.LENGTH_SHORT).show();
                        if (oldWeather != null && oldWeather[0] != null) {
                            showWeatherInfo(oldWeather[0]);
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather[] weather;
                if (responseText.indexOf("\"basic\"") > 0) {
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
                                Gson gson;
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
                                    SharedPreferences prefs ;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


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
                                editor.putString("weather", responseText);

                                editor.apply();
                                showWeatherInfo(oldWeather[0]);
                            }
                            mWeatherId = requestWeatherId;
                        } else {
                            if (responseText.indexOf("{\"status\":\"unknown city\"}") > 0) {
                                Toast.makeText(WeatherActivity.this, "城市名不对哦，显示原有数据",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(WeatherActivity.this, "获取天气信息异常，显示原有数据",
                                        Toast.LENGTH_SHORT).show();
                            }
                            if (oldWeather != null && oldWeather[0] != null) {
                                showWeatherInfo(oldWeather[0]);
                            }
                        }
                        swipeRefresh.setRefreshing(false);

                    }
                });

            }
        });
        loadBingPic();
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
        weatherInfoText.setText(weatherInfo);
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
        String comfort = "舒适度：" + weather.suggestion.comf.brf + "，" + weather.suggestion.comf.txt;
        String carWash = "洗车建议： " + weather.suggestion.cw.txt;
        String sport = "运动建议： " + weather.suggestion.sport.brf + "，" + weather.suggestion.sport.txt;
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
        weatherLayout.setVisibility(View.VISIBLE);
        if(isNotShowed) {
            isNotShowed = false;
            noinfoLayout.setVisibility(View.GONE);
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        }
    }


    private void loadBingPic() {
        /////
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        String weatherString = prefs.getString("weather", null);
        boolean past = false;
        oldBingPic = prefs.getString("bing_pic", null);
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            oldWeather = Utility.handleWeatherResponse(weatherString);
            if(oldWeather != null){
                String prefDate = oldWeather[0].basic.update.updateTime.split(" ")[0];
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date curDate = new Date(System.currentTimeMillis() + 8 * 3600 * 1000);//获取当前中国时间
                String todayTime = formatter.format(curDate);
                String today = todayTime;
                if (!prefDate.equals(today)) {
                    past = true;
                }
            }
        }
        if ((!past) && (oldBingPic != null)) {
            if(!glided){
                Glide.with(WeatherActivity.this).load(oldBingPic).listener(requestListener).into(bingPicImg);
            }
        } else {
            ///
            String requestBingPic = "http://www.bing.com/HPImageArchive.aspx?" +
                    "format=js&idx=0&n=1";
            HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String bingPicBodyOrigin = response.body().string();
                    String bingPicBody = bingPicBodyOrigin;
                    int pos1 = bingPicBody.indexOf("\"url\":\"") + 7;
                    if (pos1 > 20) {
                        int pos2 = bingPicBody.indexOf('"', pos1);
                        final String bingPic = "http://s.cn.bing.net" + bingPicBody.substring(pos1, pos2);
                        oldBingPic = bingPic;
                        SharedPreferences.Editor editor ;
                        editor = PreferenceManager.
                                getDefaultSharedPreferences(WeatherActivity.this).edit();


                        editor.putString("bing_pic", bingPic);
                        editor.apply();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (oldBingPic != null) {
                                Glide.with(WeatherActivity.this).load(oldBingPic).listener(requestListener).into(bingPicImg);
                            }
                        }
                    });

                }
            });
        }
    }
    ////监听glide加载情况
    private RequestListener<String, GlideDrawable> requestListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            // todo log exception
            glided = false;
            Glide.with(WeatherActivity.this).load(R.drawable.bg).into(bingPicImg);

            // important to return false so the error placeholder can be placed
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            glided = true;
            return false;
        }
    };

    private void showinfo() {
        weatherLayout.setVisibility(View.INVISIBLE);
        SharedPreferences prefs ;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        oldBingPic = prefs.getString("bing_pic", null);
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            oldWeather = Utility.handleWeatherResponse(weatherString);
            if (oldWeather != null) {

                mWeatherId = oldWeather[0].basic.weatherId;

                weatherLayout.setVisibility(View.INVISIBLE);
                requestWeather(mWeatherId);
            }
        }
        if (oldWeather == null) {
            Thread thread=new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    addFregment();
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
            thread.start();

        }
        else {
            Thread thread=new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    addFregment();
                }
            });
            thread.start();
        }

        //if(oldBingPic != null)   loadBingPic();


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
                        Gson gson;
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
                        }


                        showWeatherInfo(oldWeather[which]);
                    };
                }

        );
        builder.show();
    }
    private void addFregment(){
        //动态加载碎片，缩短启动速度，这样能快些显示天气信息。
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.choose_area_fragment, new ChooseAreaFragment());
        transaction.commit();
    }


}
