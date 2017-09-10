package com.example.greenweather.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.StrictMode;

import com.example.greenweather.MyApplication;
import com.example.greenweather.db.City;
import com.example.greenweather.db.County;
import com.example.greenweather.db.MyDataBaseHelper;
import com.example.greenweather.db.Province;
import com.example.greenweather.gson.News;
import com.example.greenweather.gson.NowCity;
import com.example.greenweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Administrator on 2017/8/8.
 */

public class Utility {


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

    public static Weather[] handleJDWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject resultObject = jsonObject.getJSONObject("result");
            JSONArray jsonArray = resultObject.getJSONArray("HeWeather5");
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

    public static News handleJDNewsResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject resultObject = jsonObject.getJSONObject("result").getJSONObject("result");
            //JSONArray jsonArray = resultObject.getJSONArray("list");
            String newsContent = resultObject .toString();

            News  news = new Gson().fromJson(newsContent, News.class);

            return news;
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
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
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

    public static void RequestWeatherCityInfo() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //  showProgressDialog();
//                Toast.makeText(MyApplication.getContext(),"Start......",Toast.LENGTH_SHORT).show();
                MyDataBaseHelper dbHelper;
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                deleteDBFile();
                dbHelper = new MyDataBaseHelper(MyApplication.getContext(), "green_weather.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    URL url = new URL("https://cdn.heweather.com/china-city-list.txt");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(1500);
                    connection.setReadTimeout(1500);
                    InputStream in = connection.getInputStream();
                    // 下面对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] info = line.split("\t");
                        String weatherId = info[0];
                        if (weatherId != null && weatherId.indexOf("CN") >= 0) {
                            String countyName = info[2];
                            String provName = info[7];
                            String superiorCity = info[9];
                            Cursor cursorCounty = db.query("County", null, "weatherid = ?",
                                    new String[]{weatherId}, null, null, null, "1");
                            if (!cursorCounty.moveToFirst()) {
                                Cursor cursorProvince = db.query("Province", null, "provincename = ?",
                                        new String[]{provName}, null, null, null, "1");
                                if (!cursorProvince.moveToFirst()) {
                                    db.execSQL("insert into Province (provincename) values(?)",
                                            new String[]{provName});
                                }
                                cursorProvince = db.query("Province", null, "provincename = ?",
                                        new String[]{provName}, null, null, null, "1");
                                cursorProvince.moveToFirst();
                                int provinceId = cursorProvince.getInt(cursorProvince.getColumnIndex("id"));
                                cursorProvince.close();
                                Cursor cursorCity = db.query("City", null, "provinceid = ? and cityname = ?",
                                        new String[]{String.valueOf(provinceId), superiorCity}, null, null, null, "1");
                                if (!cursorCity.moveToFirst()) {
                                    db.execSQL("insert into City (cityname,provinceid) values(?,?)",
                                            new String[]{superiorCity, String.valueOf(provinceId)});
                                }
                                cursorCity = db.query("City", null, "provinceid = ? and cityname = ?",
                                        new String[]{String.valueOf(provinceId), superiorCity}, null, null, null, "1");
                                cursorCity.moveToFirst();
                                int cityId = cursorCity.getInt(cursorCity.getColumnIndex("id"));
                                cursorCity.close();
                                db.execSQL("insert into County (weatherid,cityid,countyname) values(?,?,?)",
                                        new String[]{weatherId, String.valueOf(cityId), countyName});

                                cursorCounty.close();

                            }


                            /*List<County> counties = DataSupport.where("weatherId = ? ", info[0]).find(County.class);
                            if (counties.size() == 0) {
                                List<Province> provinces = DataSupport.where("provinceName = ? ", provName).find(Province.class);
                                if (provinces.size() == 0) {
                                    Province province = new Province();
                                    province.setProvinceName(provName);
                                    province.save();
                                    provinces.add(province);
                                }
                                List<City> cities = DataSupport.limit(1).where("cityName = ? and provinceId = ?",
                                        superiorCity,String.valueOf(provinces.get(0).getId()))
                                        .find(City.class);
                                if (cities.size() == 0) {
                                    City city = new City();
                                    city.setProvinceId(provinces.get(0).getId());
                                    city.setCityName(superiorCity);
                                    city.save();
                                    cities.add(city);
                                }
                                County county = new County();
                                county.setCityId(cities.get(0).getId());
                                county.setCountyName(countyName);
                                county.setWeatherId(weatherId);
                                county.save();
                            }*/

                        }
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
//                Toast.makeText(MyApplication.getContext(),"end......",Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    private static void deleteDBFile() {
        String DB_NAME = "green_weather.db"; //保存的数据库文件名
        String PACKAGE_NAME = "com.example.greenweather";
        String DB_PATH = "/data"
                + Environment.getDataDirectory().getAbsolutePath() + "/"
                + PACKAGE_NAME;  //在手机里存放数据库的位置
        String dbPath = DB_PATH + "/databases/";
        String dbfile = dbPath + DB_NAME;
        File dbFile =new File(dbfile);
        if (dbFile.exists()) {//判断数据库文件是否存在，若存在则删除
            dbFile.delete();

        }

    }

    }