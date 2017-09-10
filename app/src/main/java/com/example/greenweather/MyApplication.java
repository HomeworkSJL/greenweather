package com.example.greenweather;

import android.app.Application;
import android.content.Context;
import android.os.Environment;




import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/8/16.
 */

public class MyApplication extends Application {
    private static Context context;
    private static MyApplication instance;
    @Override
    public void onCreate(){
        context = getApplicationContext();
        createDatabase();
        instance = this;


    }

    private void createDatabase() {
        final int BUFFER_SIZE = 200000;
        final String DB_NAME = "green_weather.db"; //保存的数据库文件名
        final String PACKAGE_NAME = "com.example.greenweather";
        final String DB_PATH = "/data"
                + Environment.getDataDirectory().getAbsolutePath() + "/"
                + PACKAGE_NAME;  //在手机里存放数据库的位置
        final String dbPath = DB_PATH + "/databases/";
        final String dbfile = dbPath + DB_NAME;
        try {
            if (!(new File(dbfile).exists())) {//判断数据库文件是否存在，若不存在则执行导入
                ///
                File filepath = new File(dbPath);
                if (!filepath.exists()) {
                    filepath.mkdirs();
                }
                ///
                if (filepath.exists()) {
                    InputStream is = context.getResources().openRawResource(
                            R.raw.he_weather); //欲导入的数据库
                    FileOutputStream fos = new FileOutputStream(dbfile);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int count = 0;
                    while ((count = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                    fos.close();
                    is.close();
                }
            }
        } catch (IOException e) {
        }
    }

    public static MyApplication getInstance() {
         return instance;
    }

    public static Context getContext(){
        return context;
    }
}
