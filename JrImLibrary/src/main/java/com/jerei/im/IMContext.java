package com.jerei.im;

import android.app.Application;
import android.content.Context;

import com.jerei.im.timchat.model.GroupFuture;
import com.tencent.TIMGroupReceiveMessageOpt;
import com.tencent.TIMManager;
import com.tencent.TIMOfflinePushListener;
import com.tencent.TIMOfflinePushNotification;
import com.tencent.qalsdk.sdk.MsfSdkUtils;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.utils.Foreground;


/**
 * 全局Application
 */
public class IMContext {

    private static Context context;

    private static AppCollback appCollback;

    public static GroupFuture groupFuture;

    public static void initMI(final Application  application) {

        Foreground.init(application);
        context = application.getApplicationContext();
        if(MsfSdkUtils.isMainProcess(application)) {
            TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
                @Override
                public void handleNotification(TIMOfflinePushNotification notification) {
                    if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify){
                        //消息被设置为需要提醒
//                        notification.doNotify(application.getApplicationContext(), R.mipmap.ic_launcher);
                    }
                }
            });
        }
    }

    public static Context getContext() {
        return context;
    }


    public static AppCollback getAppCollback() {
        return appCollback;
    }

    public static void setAppCollback(AppCollback appCollback) {
        IMContext.appCollback = appCollback;
    }
}
