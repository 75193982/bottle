package com.jerei.im.timchat.ui;


import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.model.UserInfo;
import com.jerei.im.timchat.utils.CustomDialog;
import com.jerei.im.timchat.utils.GetHeadImageUtil;

import com.jerei.im.ui.RoundCornerImageView;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.Collections;

public class MyAddFriendActivity extends FragmentActivity implements View.OnClickListener{

    LinearLayout search;
    LinearLayout saoysao;
    LinearLayout qr_code;
    LinearLayout add_group;
    LinearLayout search_group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_add_friend);

        search = (LinearLayout) findViewById(R.id.search);
        saoysao = (LinearLayout) findViewById(R.id.saoysao);
        qr_code = (LinearLayout) findViewById(R.id.qr_code);
        add_group = (LinearLayout) findViewById(R.id.add_group);
        search_group = (LinearLayout) findViewById(R.id.search_group);
        search.setOnClickListener(this);
        saoysao.setOnClickListener(this);
        qr_code.setOnClickListener(this);
        add_group.setOnClickListener(this);
        search_group.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==R.id.search){
            startActivity(new Intent(this,SearchFriendActivity.class));
        }
        if(id==R.id.saoysao){
            startActivity(new Intent(this,CaptureActivity.class));
        }

        if(id==R.id.search_group){
            startActivity(new Intent(this,SearchGroupActivity.class));
        }


        if(id==R.id.qr_code){


            TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onSuccess(TIMUserProfile profile) {
                    try {
                        qrCodeDialog(Create2DCode("user,"+profile.getIdentifier()+","+profile.getNickName()+",hehehedsfasdfasd"));
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            });


        }
        if(id==R.id.add_group){
            startActivity(new Intent(this,CreateGroupActivity.class));
        }
    }



    /**
     * 用字符串生成二维码
     * @param str
     * @author zhouzhe@lenovo-cw.com
     * @return
     * @throws WriterException
     */
    public Bitmap Create2DCode(String str) throws WriterException {
        //生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 300, 300);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        //二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(matrix.get(x, y)){
                    pixels[y * width + x] = 0xff000000;
                }

            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    CustomDialog.Builder customBuilder;
    private CustomDialog dialog;
    private void qrCodeDialog(Bitmap bitmap) {
        Context mContext = MyAddFriendActivity.this;
        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.qr_code_dialog, null);
        customBuilder = new CustomDialog.Builder(MyAddFriendActivity.this);

        ImageView ImageView = (ImageView) layout.findViewById(R.id.image);
        RoundCornerImageView avatar = (RoundCornerImageView) layout.findViewById(R.id.avatar);
        TextView name = (TextView) layout.findViewById(R.id.name);
        TextView area = (TextView) layout.findViewById(R.id.area);
        name.setText(UserInfo.getInstance().getNickname());
        area.setText(UserInfo.getInstance().getArea());
        GetHeadImageUtil.setImageUrl(avatar,UserInfo.getInstance().getUrl(),MyAddFriendActivity.this);
        ImageView.setImageBitmap(bitmap);
        dialog = customBuilder.create();
        dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.setContentView(layout);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();

        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.show();

    }
}
