package com.jerei.im.timchat.ui.customview;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.jerei.im.presentation.business.LoginBusiness;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.model.FriendshipInfo;
import com.jerei.im.timchat.model.GroupInfo;
import com.jerei.im.timchat.model.UserInfo;




import java.util.List;

public class DialogActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog);
        setFinishOnTouchOutside(false);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {


        int viewid = v.getId();
        if (viewid == R.id.btnOk) {
            LoginBusiness.loginIm(UserInfo.getInstance().getId(), UserInfo.getInstance().getUserSig(), new TIMCallBack() {
                @Override
                public void onError(int i, String s) {
                    Toast.makeText(DialogActivity.this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                    logout();
                }

                @Override
                public void onSuccess() {
                    Toast.makeText(DialogActivity.this, getString(R.string.login_succ), Toast.LENGTH_SHORT).show();
                    String deviceMan = android.os.Build.MANUFACTURER;
                    //注册小米和华为推送
//                        if (deviceMan.equals("Xiaomi") && shouldMiInit()){
//                            MiPushClient.registerPush(getApplicationContext(), "2882303761517480335", "5411748055335");
//                        }else if (deviceMan.equals("HUAWEI")){
//                            PushManager.requestToken(getApplicationContext());
//                        }
                    finish();
                }
            });
        } else if (viewid == R.id.btnCancel) {
            logout();
        }
    }

    private void logout(){
//        TlsBusiness.logout(UserInfo.getInstance().getId());
//        UserInfo.getInstance().setId(null);
//        FriendshipInfo.getInstance().clear();
//        GroupInfo.getInstance().clear();
//        Intent intent = new Intent(this,SplashActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
    }

    /**
     * 判断小米推送是否已经初始化
     */
    private boolean shouldMiInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
