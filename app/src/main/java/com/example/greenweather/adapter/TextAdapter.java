package com.example.greenweather.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/9/10.
 */

public class TextAdapter extends BaseAdapter {
        private Context mContext;
        String[] newsChannel ;
    // = {"头条","新闻","财经","体育","娱乐","军事",
       // "教育","科技","NBA","股票","星座","女性","健康","育儿"};
        public TextAdapter(Context context,String[] newsChannel ) {
            this.mContext=context;
            this.newsChannel = newsChannel;
        }

        @Override
        public int getCount() {
            return newsChannel.length;
        }

        @Override
        public Object getItem(int position) {
            return newsChannel[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //定义一个TextView,显示在GridView里
            TextView textView;
            if(convertView==null){
                textView=new TextView(mContext);
              //  textView.setLayoutParams(new GridView.LayoutParams(85, 85));
                textView.setWidth(40);
                textView.setPadding(8, 8, 8, 8);
            }else{
                textView = (TextView) convertView;
            }
            textView.setText(newsChannel[position]);
            return textView;
        }

    }
