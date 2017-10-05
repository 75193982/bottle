package com.bottle.com.bottle.Fragment;


import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by lenovo on 2017/9/1.
 */

public class ChatFragment  extends BaseFragment {
    @Override
    public View initView() {
        TextView textView = new TextView(getActivity());
        textView.setTextSize(16);
        textView.setPadding(0,0,0,20);
        textView.setBackgroundColor(Color.parseColor("#ffffffff"));
        textView.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        textView.setText("我");
        System.err.println("initView");
        return textView ;
    }

    @Override
    public void initData() {
        super.initData();
        System.err.println("initData");
    }
}