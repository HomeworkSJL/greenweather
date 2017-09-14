package com.example.greenweather.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.greenweather.R;

/**
 * Created by Administrator on 2017/9/13.
 */

public class CSOKCancelDialog extends Dialog {
    private Button positiveButton, negativeButton;
    private TextView contenttv;

    public CSOKCancelDialog(Context context) {
        super(context,R.style.mydialog);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.showdialog_ok_cancel, null);  //通过LayoutInflater获取布局
        contenttv = (TextView) view.findViewById(R.id.title);
        positiveButton = (Button) view.findViewById(R.id.acceptbtn);
        negativeButton = (Button) view.findViewById(R.id.refusebtn);
        setContentView(view);  //设置view
    }
    //设置内容
    public void setContent(String content) {
        contenttv.setText(content);
    }
    //确定按钮监听
    public void setOnPositiveListener(View.OnClickListener listener){
        positiveButton.setOnClickListener(listener);
    }

    //否定按钮监听
    public void setOnNegativeListener(View.OnClickListener listener){
        negativeButton.setOnClickListener(listener);
    }
}
