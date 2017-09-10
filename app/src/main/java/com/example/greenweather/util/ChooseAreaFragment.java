package com.example.greenweather.util;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.greenweather.MyApplication;
import com.example.greenweather.R;
import com.example.greenweather.WeatherActivity;
import com.example.greenweather.adapter.MyBaseAdapter;
import com.example.greenweather.db.City;
import com.example.greenweather.db.County;
import com.example.greenweather.db.MyDataBaseHelper;
import com.example.greenweather.db.Province;
import com.example.greenweather.gson.NowCity;
import com.example.greenweather.gson.RemenCities;
import com.example.greenweather.widget.MyGridView;


import com.example.greenweather.gson.LocCity;
import android.app.AlertDialog;
import android.content.DialogInterface;


import static android.support.v4.content.PermissionChecker.checkSelfPermission;
import static com.example.greenweather.R.id.headcitylistlayout;
import static com.example.greenweather.util.Utility.getAddress;
import static com.example.greenweather.util.Utility.sendRequestWithHttpURLConnection;


/**
 * Created by Administrator on 2017/8/8.
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;


    private TextView titleText;
    private Button backButton;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    private List<RemenCities> mReMenCitys;//热门城市列表

    /**
     * 省列表
     */
    private List<Province> provinceList = new ArrayList<Province>();

    /**
     * 市列表
     */
    private List<City> cityList =new ArrayList<City>();

    /**
     * 县列表
     */
    private List<County> countyList = new ArrayList<County>();


    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;
    private boolean onlyOnecity;
    private List<String> provinces;
    private ClearEditText mClearEditText;
    private MyGridView mGridView;
    private MyGridViewAdapter gvAdapter;
    private TextView setupTextView;
    private TextView queryTextView;

    public LocationClient mLocationClient;

    private TextView positionText;

    private LocCity locCity = null;
    private LinearLayout headcitylist_layout;
    private MyDataBaseHelper dbHelper = null;
    private SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);

        listView = (ListView) view.findViewById(R.id.country_lvcountry);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        mReMenCitys = new ArrayList<RemenCities>();
        locCity = new LocCity();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String concencitieString = prefs.getString("concencities", null);
        if(concencitieString != null){
            String[] cityArray = concencitieString.split(";");
            for(int i = 0 ; i<cityArray.length; i++){
                String[] cityIteminfo = cityArray[i].split(":");
                RemenCities remenCities = new RemenCities();
                remenCities.cityName = cityIteminfo[0];
                if(cityIteminfo.length == 2){
                    remenCities.weatherId = cityIteminfo[1];
                }
                else {
                    remenCities.weatherId = null;
                }
                mReMenCitys.add(remenCities);
            }
        }


        View viewhead_city = View.inflate(getActivity(), R.layout.head_city_list, null);
        headcitylist_layout = (LinearLayout) viewhead_city.findViewById(headcitylistlayout);
        positionText = (TextView) viewhead_city.findViewById(R.id.loc_text);
        setupTextView = (TextView) viewhead_city.findViewById(R.id.citynameconcen);
        queryTextView = (TextView) viewhead_city.findViewById(R.id.citynamequery);
        mClearEditText = (ClearEditText) viewhead_city.findViewById(R.id.filter_edit);

        if(mReMenCitys.size() > 0){
            headcitylist_layout.setVisibility(View.VISIBLE);
        }


        mGridView = (MyGridView) viewhead_city.findViewById(R.id.id_gv_remen);
        gvAdapter = new MyGridViewAdapter(MyApplication.getContext(),mReMenCitys);
        mGridView.setAdapter(gvAdapter);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listView.addHeaderView(viewhead_city);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<String> permissionList = new ArrayList<>();

        if (checkSelfPermission(MyApplication.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (checkSelfPermission(MyApplication.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (checkSelfPermission(MyApplication.getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (checkSelfPermission(MyApplication.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            requestPermissions(permissions, 1);
        } else {
            mLocationClient = new LocationClient(MyApplication.getContext());
            mLocationClient.registerLocationListener(new MyLocationListener());
            requestLocation();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position - 1);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position - 1);
                    queryCounties();
                }else if(currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(position - 1).getWeatherId();
                    showCountyWeatherInfo(weatherId);

                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((currentLevel == LEVEL_CITY) || onlyOnecity) {
                    queryProvinces();
                } else if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                }

            }
        });
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String weatherId = mReMenCitys.get(position).weatherId;
                if(weatherId == null){
                    String cityName = mReMenCitys.get(position).cityName;
                    showCountyWeatherInfo(cityName);
                }
                else {
                    showCountyWeatherInfo(weatherId);
                }

            }
        });
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getContext())
                        .setMessage("要删除关注"+mReMenCitys.get(position).cityName+"吗？")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                hideSoftInput(mClearEditText.getWindowToken());
                                mReMenCitys.remove(position);
                                gvAdapter.notifyDataSetChanged();
                                if(mReMenCitys.size() == 0){
                                    headcitylist_layout.setVisibility(View.GONE);
                                }
                            }
                        })
                        .setNegativeButton("查询", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                hideSoftInput(mClearEditText.getWindowToken());
                                String weatherId = mReMenCitys.get(position).weatherId;
                                if(weatherId == null){
                                    String cityName = mReMenCitys.get(position).cityName;
                                    showCountyWeatherInfo(cityName);
                                }
                                else {
                                    showCountyWeatherInfo(weatherId);
                                }
                            }
                        })
                        .show();



                return true;
            }
        });
        setupTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput(mClearEditText.getWindowToken());
                final String inputCity = mClearEditText.getText().toString().trim();
                if(inputCity.length() > 1 ) {
                    try {
                        getCityinfo(inputCity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //     gvAdapter.notifyDataSetChanged();

                    }
                }

              //  mGridView.


        });
        queryTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput(mClearEditText.getWindowToken());
                showCountyWeatherInfo(mClearEditText.getText().toString().trim());


            }
        });
        mClearEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                  //  doSomething();

                    return true;
                }
                return false;
            }

        });
        positionText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput(mClearEditText.getWindowToken());
                if(locCity != null){
                    showCountyWeatherLocInfo(locCity.getCityName(),locCity.getLat(),locCity.getLon());
                }

            }
        });

        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {
        currentLevel = LEVEL_PROVINCE;
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        if(provinceList.size() == 0) {
            dbHelper = new MyDataBaseHelper(MyApplication.getContext(), "green_weather.db", null, 1);
            if (db == null) {
                db = dbHelper.getReadableDatabase();
            }
            Cursor cursorProvince = db.query("Province", null, null, null, null, null, null);
            if (cursorProvince.moveToFirst()) {
                do {
                    Province province = new Province();
                    province.setId(cursorProvince.getInt(cursorProvince.getColumnIndex("id")));
                    province.setProvinceName(cursorProvince.getString(cursorProvince.getColumnIndex("provincename")));
                    provinceList.add(province);
                } while (cursorProvince.moveToNext());
            }
            cursorProvince.close();
        }
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCities() {
        cityList.clear();
        Cursor cursorCity = db.query("City",null,"provinceid = ? ",
                new String[]{String.valueOf(selectedProvince.getId())},null,null,null);
        if(cursorCity.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursorCity.getInt(cursorCity.getColumnIndex("id")));
                city.setCityName(cursorCity.getString(cursorCity.getColumnIndex("cityname")));
                city.setProvinceId(cursorCity.getInt(cursorCity.getColumnIndex("provinceid")));
                cityList.add(city);
            } while (cursorCity.moveToNext());
        }
        cursorCity.close();

        //cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size() == 1){
            backButton.setVisibility(View.VISIBLE);
            onlyOnecity = true;
            selectedCity = cityList.get(0);
            queryCounties();
        }
        else if (cityList.size() > 1) {
            currentLevel = LEVEL_CITY;
            titleText.setText(selectedProvince.getProvinceName());

            backButton.setVisibility(View.VISIBLE);
            onlyOnecity = false;
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(1);
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCounties() {
        countyList.clear();
        Cursor cursorCounty = db.query("County",null,"cityid = ? ",
                new String[]{String.valueOf(selectedCity.getId())},null,null,null);
        if(cursorCounty.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursorCounty.getInt(cursorCounty.getColumnIndex("id")));
                county.setCityId(cursorCounty.getInt(cursorCounty.getColumnIndex("cityid")));
                county.setWeatherId(cursorCounty.getString(cursorCounty.getColumnIndex("weatherid")));
                county.setCountyName(cursorCounty.getString(cursorCounty.getColumnIndex("countyname")));
                countyList.add(county);
            } while (cursorCounty.moveToNext());
        }
        cursorCounty.close();


        //countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size()  == 1){
            String weatherId = countyList.get(0).getWeatherId();
            showCountyWeatherInfo(weatherId);

        }else if (countyList.size() > 1) {
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
            backButton.setVisibility(View.VISIBLE);
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(1);
        }
    }



    private void showCountyWeatherInfo(String weatherId){
        WeatherActivity activity = (WeatherActivity) getActivity();
        activity.drawerLayout.closeDrawers();
        activity.requestWeather(weatherId);

    }
    private void showCountyWeatherLocInfo(String cityName,double lat,double lon){
        WeatherActivity activity = (WeatherActivity) getActivity();
        activity.drawerLayout.closeDrawers();
        activity.requestLocWeather(cityName,lat,lon);

    }

    /////fron City selector
private class MyGridViewAdapter extends MyBaseAdapter<RemenCities, GridView> {
    private LayoutInflater inflater;
    public MyGridViewAdapter(Context ct, List<RemenCities> list) {
        super(ct, list);
        inflater = LayoutInflater.from(ct);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_remen_city, null);
            holder.id_tv_cityname = (TextView) convertView.findViewById(R.id.id_tv_cityname);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        String info = mReMenCitys.get(position).cityName;
        holder.id_tv_cityname.setText(info);
        return convertView;
    }
    class ViewHolder{
        TextView id_tv_cityname;
    }
}
    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    protected void hideSoftInput(IBinder token) {
        if (token != null) {
           // InputMethodManager im = (InputMethodManager) MyApplication.getInstance().
             //       getSystemService(Context.INPUT_METHOD_SERVICE);
            InputMethodManager im = (InputMethodManager)mClearEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(mClearEditText.getWindowToken(), 0);

            if (im.isActive()) {
                ;//isOpen若返回true，则表示输入法打开
                im.hideSoftInputFromWindow(token,
                        InputMethodManager.HIDE_NOT_ALWAYS);


            }
        }
    }


    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if(location != null && location.getDistrict() !=null) {
                //StringBuilder currentPosition = new StringBuilder();

                locCity.setCityName(location.getDistrict());
                locCity.setLat(location.getLatitude());
                locCity.setLon(location.getLongitude());
                //currentPosition.append(locCity.getCityName());
                positionText.setText(locCity.getCityName());
                positionText.setVisibility(View.VISIBLE);
                mLocationClient.stop();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean granted = true;
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            granted = false;
                            Toast.makeText(MyApplication.getContext(), "必须同意所有权限才能使用自动定位", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(granted){
                        mLocationClient = new LocationClient(MyApplication.getContext());
                        mLocationClient.registerLocationListener(new MyLocationListener());
                        requestLocation();
                    }
                } else {
                    Toast.makeText(MyApplication.getContext(), "发生未知错误", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }



    public void getCityinfo(final String requestWeatherId) {
        String wertherUrl = "https://free-api.heweather.com/v5/now?city=" +
                requestWeatherId + "&key=532bf4f8455c45bcb714dfb9ff70d49e";
        String info = sendRequestWithHttpURLConnection(wertherUrl);
        NowCity[] nowCity ;
        if (info.indexOf("\"basic\"") > 0) {
            nowCity = Utility.handleNowCityResponse(info);
            if(nowCity.length > 1){
                ShowChoise(nowCity);
            }
            else
            {
                addRemenCity(nowCity[0]);
            }
         }
    }






    private void addRemenCity(NowCity nowCity) {
        if(nowCity == null) return;
        boolean isChanged = true;
        for (int j = 0; j < mReMenCitys.size(); j++) {
                if(mReMenCitys.get(j).weatherId != null ){
                    if (mReMenCitys.get(j).weatherId.equals(nowCity.basic.weatherId)){
                        isChanged = false;
                        break;

                    }
                }
                else if  ((nowCity.basic.cityName.equals(mReMenCitys.get(j).cityName))) {
                    isChanged = false;
                    break;
                }
            }
        if (isChanged) {
            RemenCities remenCities = new RemenCities();
            remenCities.weatherId = nowCity.basic.weatherId;
            remenCities.cityName = nowCity.basic.cityName;
            mReMenCitys.add(remenCities);
            if(mReMenCitys.size() == 1){
                headcitylist_layout.setVisibility(View.VISIBLE);
            }
            StringBuilder strCities = new StringBuilder();
            for (int j = 0; j < mReMenCitys.size(); j++) {
                strCities.append(mReMenCitys.get(j).cityName + ":" + mReMenCitys.get(j).weatherId + ";");
            }
            SharedPreferences.Editor editor = PreferenceManager.
                    getDefaultSharedPreferences(MyApplication.getContext()).edit();
            editor.putString("concencities", strCities.toString());
            if (!(editor.commit())) {
                Toast.makeText(MyApplication.getContext(), "保存不了数据啦", Toast.LENGTH_LONG).show();
            }
            gvAdapter.notifyDataSetChanged();
        }
        else {
            Toast.makeText(mClearEditText.getContext(), "您已经添加了", Toast.LENGTH_LONG).show();

        }
    }


    public void ShowChoise(final NowCity[] nowCities) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Holo_Light_Dialog);
                //builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("请选择一个城市");
                //    指定下拉列表的显示数据
                StringBuilder cityItems = new StringBuilder();
                for (int i = 0; i < nowCities.length; i++) {
                    String prov = getAddress(nowCities[i].basic.lat, nowCities[i].basic.lon);
                    if (prov != null) {
                        nowCities[i].basic.cityName = prov;
                    }
                    cityItems.append(nowCities[i].basic.cityName + ";");
                }
                String[] citiestmp = cityItems.toString().split(";");
                final String[] cities = citiestmp;
                //    设置一个下拉的列表选择项
                AlertDialog.Builder builder1 = builder.setItems(cities, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addRemenCity(nowCities[which]);
                            }
                        }

                );
                builder.show();
            }
    @Override
    public void onDestroy(){
        super.onDestroy();
        db.close();
        dbHelper.close();
    }


}

