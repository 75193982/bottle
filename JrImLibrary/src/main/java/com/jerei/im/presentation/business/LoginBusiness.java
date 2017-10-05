package com.jerei.im.presentation.business;

import android.content.Context;

import com.jerei.im.IMContext;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.model.UserInfo;
import com.tencent.TIMAddFriendRequest;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendAllowType;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMManager;
import com.tencent.TIMUser;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录
 */
public class LoginBusiness {

    private static final String TAG = "LoginBusiness";

    private LoginBusiness(){}



    /**
     * 登录imsdk
     *
     * @param identify 用户id
     * @param userSig 用户签名
     * @param callBack 登录后回调
     */
    public static void loginIm(String identify, String userSig, TIMCallBack callBack){

        if (identify == null || userSig == null) return;
        TIMUser user = new TIMUser();
        user.setAccountType(IMContext.getContext().getString(R.string.account_type));
        user.setAppIdAt3rd(IMContext.getContext().getString(R.string.sdk_appid));
        user.setIdentifier(identify);
        //发起登录请求
        TIMManager.getInstance().login(
                Integer.parseInt(IMContext.getContext().getString(R.string.sdk_appid)),
                user,
                userSig,                    //用户帐号签名，由私钥加密获得，具体请参考文档
                callBack);
    }

    /**
     * 登出imsdk
     *
     * @param callBack 登出后回调
     */
    public static void logout(TIMCallBack callBack){
        TIMManager.getInstance().logout(callBack);
    }

    /**
     * 登陆时候 设置好友验证方式
     */
    public static void setAllowType(Context context){
        TIMFriendAllowType tiMFriendAllowType =null;
            tiMFriendAllowType  = TIMFriendAllowType.TIM_FRIEND_NEED_CONFIRM;

        //设置自己的好友验证方式为需要验证
        TIMFriendshipManager.getInstance().setAllowType(tiMFriendAllowType, new TIMCallBack(){
            @Override
            public void onError(int code, String desc){
            }

            @Override
            public void onSuccess(){
            }
        });
    }
}
