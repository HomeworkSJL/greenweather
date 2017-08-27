package com.example.greenweather.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.greenweather.MainActivity;
import com.example.greenweather.MyApplication;
import com.example.greenweather.WeatherActivity;
import com.example.greenweather.db.City;
import com.example.greenweather.db.County;
import com.example.greenweather.db.Province;
import com.example.greenweather.gson.NowCity;
import com.example.greenweather.gson.Weather;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/8/8.
 */

public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather[] handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
            Weather[] weather = new Weather[jsonArray.length()];
            for(int i = 0;i < jsonArray.length(); i++){
                String weatherContent = jsonArray.getJSONObject(i).toString();
                weather[i]= new Gson().fromJson(weatherContent, Weather.class);
            }

            return weather;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static NowCity[] handleNowCityResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
            NowCity[] nowCities = new NowCity[jsonArray.length()];
            for(int i = 0;i < jsonArray.length(); i++){
                String weatherContent = jsonArray.getJSONObject(i).toString();
                nowCities[i]= new Gson().fromJson(weatherContent, NowCity.class);
            }

            return nowCities;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 逆地理编码专属请求
     */
    public static String getAddress(String lat, String lng) {
        StringBuilder location = new StringBuilder();
        location.append(lat).append(',').append(lng);
        final String urlStr = "http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&location=" +
                location.toString() +
                "&output=json&pois=1&ak=LXv7vNPGNPx5t0lGib0Z0rlH3jFDu11R&coordtype=" +
                "wgs84ll&mcode=F7:07:63:A2:1E:DF:A1:F5:04:51:F7:5B:28:0A:AE:9B:D1:94:7E:9F;com.example.greenweather";

        String data = sendRequestWithHttpURLConnection(urlStr);
        String provStr,cityStr;
        if (data.indexOf("{\"status\":0,\"") > 0) {
            int posProv = data.indexOf("\"province\":\"");
            int posProv2 = data.indexOf('"', posProv + 13);
            provStr =  data.substring(posProv + 12, posProv2 - 1);
            int posCity = data.indexOf("\"district\":\"");
          int posCity2 = data.indexOf('"',posCity + 14);
          cityStr = data.substring(posCity+12,posCity2-1);
          return provStr +" " + cityStr;
        }
        else {
            return null;
        }
    }

    public static String sendRequestWithHttpURLConnection(final String urlString) {

        final StringBuilder response = new StringBuilder();

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            InputStream in = connection.getInputStream();
            // 下面对获取到的输入流进行读取
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response.toString();
    }




}