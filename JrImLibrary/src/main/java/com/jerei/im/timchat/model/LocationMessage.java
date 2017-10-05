package com.jerei.im.timchat.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jerei.im.IMContext;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.adapters.ChatAdapter;
import com.jerei.im.timchat.utils.BitmapCompressTools;
import com.jerei.im.timchat.utils.CustomDialog;
import com.jerei.im.timchat.utils.EmoticonUtil;
import com.jerei.im.timchat.utils.PackageInfosUtils;
import com.tencent.TIMFaceElem;
import com.tencent.TIMLocationElem;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

/**
 * 文本消息数据
 */
public class LocationMessage extends Message {

    public LocationMessage(TIMMessage message) {
        this.message = message;
    }

    public Context context;

    public LocationMessage(double latitude, double longitude, String s) {
        message = new TIMMessage();
        TIMLocationElem elem = new TIMLocationElem();
        elem.setLatitude(latitude);   //设置纬度
        elem.setLongitude(longitude);   //设置经度
        elem.setDesc(s);
        message.addElement(elem);
    }


    /**
     * 在聊天界面显示消息
     *
     * @param viewHolder 界面样式
     * @param context 显示消息的上下文
     */
    TIMLocationElem tiMLocationElem;

    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, final Context context) {
        this.context = context;
        clearView(viewHolder);
        boolean hasText = false;
        View view = LayoutInflater.from(IMContext.getContext()).inflate(R.layout.location_view, null);

        TextView tv = (TextView) view.findViewById(R.id.location_info);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setTextColor(IMContext.getContext().getResources().getColor(isSelf() ? R.color.black : R.color.black));
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (int i = 0; i < message.getElementCount(); ++i) {
            switch (message.getElement(i).getType()) {

                case Location:
                    tiMLocationElem = (TIMLocationElem) message.getElement(i);
                    stringBuilder.append(tiMLocationElem.getDesc());
                    hasText = true;
                    break;
            }

        }
        if (!hasText) {
            stringBuilder.insert(0, " ");
        }
        tv.setText(stringBuilder);
        getBubbleView(viewHolder).removeView(view);
        getBubbleView(viewHolder).addView(view);
        showStatus(viewHolder);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bankDialog();

            }
        });
    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < message.getElementCount(); ++i) {
            switch (message.getElement(i).getType()) {

                case Location:
                    TIMLocationElem locationElem = (TIMLocationElem) message.getElement(i);
                    result.append("[位置信息]：" + locationElem.getDesc());
                    break;
            }

        }
        return result.toString();
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    private int getNumLength(int n) {
        return String.valueOf(n).length();
    }


    CustomDialog.Builder customBuilder;
    private CustomDialog dialog;

    private void bankDialog() {

        LayoutInflater inflater = (LayoutInflater)
                IMContext.getContext().getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.mapdialog, null);
        customBuilder = new CustomDialog.Builder(context);
        LinearLayout baidulaylout = (LinearLayout) layout.findViewById(R.id.baidulaylout);
        LinearLayout gaodelaylout = (LinearLayout) layout.findViewById(R.id.gaodelaylout);
        LinearLayout tencentlaylout = (LinearLayout) layout.findViewById(R.id.tencentlaylout);
        TextView baidu = (TextView) layout.findViewById(R.id.baidu);
        TextView gaode = (TextView) layout.findViewById(R.id.gaode);

        if (PackageInfosUtils.isAvilible(IMContext.getContext(), "com.baidu.BaiduMap")) {
            baidu.setVisibility(View.GONE);
        }
        if (PackageInfosUtils.isAvilible(IMContext.getContext(), "com.autonavi.minimap")) {
            gaode.setVisibility(View.GONE);
        }

        dialog = customBuilder.create();
        dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.setContentView(layout);
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth() - 120; //设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.show();
        btnClick(baidulaylout, gaodelaylout, tencentlaylout);
    }

    private void btnClick(LinearLayout btn1, LinearLayout btn2, LinearLayout btn3) {
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (PackageInfosUtils.isAvilible(IMContext.getContext(), "com.baidu.BaiduMap")) {//传入指定应用包名
                    Intent intent = null;
                    try {

                        intent = Intent.getIntent("intent://map/marker?location=" + tiMLocationElem.getLongitude() + "," + tiMLocationElem.getLatitude() + "&title=位置&content=" + tiMLocationElem.getDesc() +"&src=thirdapp.marker.yourCompanyName.yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");

                        ((Activity) context).startActivity(intent); //启动调用
                    } catch (URISyntaxException e) {
                        Log.e("intent", e.getMessage());
                    }
                } else {//未安装
                    //market为路径，id为包名
                    //显示手机上所有的market商店
                    Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    ((Activity) context).startActivity(intent);
                }

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (PackageInfosUtils.isAvilible(IMContext.getContext(), "com.autonavi.minimap")) {//传入指定应用包名
                    Intent intent = null;


                    intent = new Intent("android.intent.action.VIEW",
                            android.net.Uri.parse("androidamap://showTraffic?sourceApplication=softname&poiid=BGVIS1&lat=" + tiMLocationElem.getLongitude() + "&lon=" + tiMLocationElem.getLatitude() + "&level=10&dev=0"));
                    intent.setPackage("com.autonavi.minimap");

                    ((Activity) context).startActivity(intent); //启动调用

                } else {//未安装
                    //market为路径，id为包名
                    //显示手机上所有的market商店
                    Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    ((Activity) context).startActivity(intent);
                }
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent= new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://apis.map.qq.com/uri/v1/marker?marker=coord:"+ tiMLocationElem.getLongitude() +","+ tiMLocationElem.getLatitude() +";title:;addr:" + tiMLocationElem.getLatitude() +"&referer=myapp");
                intent.setData(content_url);
                intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
                ((Activity) context).startActivity(intent); //启动调用

            }
        });

    }
}
