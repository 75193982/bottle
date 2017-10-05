package com.jerei.im.timchat.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.jerei.im.timchat.R;
import com.jerei.im.timchat.model.FriendProfile;
import com.jerei.im.timchat.model.FriendshipInfo;
import com.jerei.im.timchat.model.GroupInfo;
import com.jerei.im.timchat.model.GroupProfile;
import com.jerei.im.timchat.model.UserInfo;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/17.
 */
public class GetHeadImageUtil {

    public  static void setImage(final ImageView image,final String id, final Context context) {

        image.setTag(id);
        if(TextUtils.isEmpty(id)){
            return;
        }
        Log.e("userid",id+"");
        //获取本地好友列表
        FriendProfile  friendProfile= FriendshipInfo.getInstance().getProfile(id);
        if(friendProfile!=null&&!TextUtils.isEmpty(friendProfile.getProfile().getFaceUrl())){
            setImageUrl(image,friendProfile.getProfile().getFaceUrl(),context);
            return;
        }

        List<String> users = new ArrayList<String>();
        users.add(id);

//获取用户资料
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int code, String desc) {
                Log.e("","");

            }
            @Override
            public void onSuccess(List<TIMUserProfile> result) {

                for (TIMUserProfile res : result) {

                    if (!TextUtils.isEmpty(res.getFaceUrl()) && res.getFaceUrl().startsWith("http")) {
                        HttpPictureUtils.ggetAvatarBitmap(res.getFaceUrl(), image, context, R.drawable.head_other,id);
                    }else {
                        image.setImageResource(R.drawable.head_other);
                    }

                }

                if(result.size()==0){
                    setImageGroup(image,id,context);
                }
            }
        });
    }


    public static void setImageGroup(final ImageView image, final String id, final Context context) {
        image.setTag(id);
        Log.e("groupid",id+"");
        if(TextUtils.isEmpty(id)){
            return;
        }
        GroupProfile groupProfile= GroupInfo.getInstance().getGroupProfile(GroupInfo.publicGroup,id);
        if(groupProfile!=null&&!TextUtils.isEmpty(groupProfile.getAvatarUrl())){
            setImageUrl(image,groupProfile.getAvatarUrl(),context);
            return;
        }

        ArrayList<String> groupList = new ArrayList<String>();

        groupList.add(id);
//创建回调

        TIMGroupManager.getInstance().getGroupPublicInfo(groupList,new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
            @Override
            public void onError(int code, String desc) {
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                Log.e("",desc);
            }


            @Override
            public void onSuccess(List<TIMGroupDetailInfo> infoList) { //参数中返回群组信息列表
                for (TIMGroupDetailInfo info : infoList) {
//                    Log.d("", "groupId: " + info.getGroupId()           //群组Id
//                            + " group name: " + info.getGroupName()              //群组名称
//                            + " group owner: " + info.getGroupOwner()            //群组创建者帐号
//                            + " group create time: " + info.getCreateTime()      //群组创建时间
//                            + " group last info time: " + info.getLastInfoTime() //群组信息最后修改时间
//                            + " group last msg time: " + info.getLastMsgTime()  //最新群组消息时间
//                            + " group member num: " + info.getMemberNum());      //群组成员数量
                    if (!TextUtils.isEmpty(info.getFaceUrl()) && info.getFaceUrl().startsWith("http")) {
                        HttpPictureUtils.ggetAvatarBitmap(info.getFaceUrl(), image, context, R.drawable.head_other,id);
                    }


                }
            }
        });

    }


    public static void setImageUrl(final ImageView image, String url, final Context context) {
        image.setTag(url);
        HttpPictureUtils.ggetAvatarBitmap(url, image, context, R.drawable.head_other,url);

    }


}
