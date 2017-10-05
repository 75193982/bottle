package com.bottle.com.bottle.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.bottle.com.bottle.R;
import com.example.swipebackactivity.SystemBarTintManager;
import com.example.swipebackactivity.app.SwipeBackActivity;

/**
 * Created by lenovo on 2017/8/29.
 */

public class BaseActivity extends SwipeBackActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            /*setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);*/
            setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        super.onCreate(savedInstanceState);
        //setStatusBarColor(getResources().getColor(R.color.colorPrimary));
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
