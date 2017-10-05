package com.jerei.im.timchat.model;


import android.text.TextUtils;
import android.util.Log;

import com.jerei.im.timchat.R;
import com.tencent.TIMFriendGroup;
import com.tencent.TIMFriendshipProxy;
import com.tencent.TIMUserProfile;
import com.jerei.im.presentation.event.FriendshipEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * 好友列表缓存数据结构
 */
public class FriendshipInfo implements Observer {

    private final String TAG = "FriendshipInfo";

//    private List<String> groups;
    //    private Map<String, List<FriendProfile>> friends;
    List<FriendProfile> friendItemList = new ArrayList<>();
    private static FriendshipInfo instance;

    private FriendshipInfo(){
//        groups = new ArrayList<>();
//        friends = new HashMap<>();
        try {
            FriendshipEvent.getInstance().addObserver(this);
            refresh();
        }catch (Exception e){e.printStackTrace();}
    }

    private void initRefresh(){
//        groups = new ArrayList<>();
//        friends = new HashMap<>();
        try {
            FriendshipEvent.getInstance().addObserver(this);
            refresh();
        }catch (Exception e){e.printStackTrace();}
    }

    public synchronized static FriendshipInfo getInstance(){
        if (instance == null){
            instance = new FriendshipInfo();
            instance.initRefresh();
        }

        return instance;
    }

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof FriendshipEvent){
            if (data instanceof FriendshipEvent.NotifyCmd){
                FriendshipEvent.NotifyCmd cmd = (FriendshipEvent.NotifyCmd) data;
                Log.d(TAG, "get notify type:" + cmd.type);
                switch (cmd.type){
                    case REFRESH:
                    case DEL:
                    case ADD:
                    case PROFILE_UPDATE:
                    case ADD_REQ:
                    case GROUP_UPDATE:
                        refresh();
                        break;

                }
            }
        }
    }


    private void refresh(){
//        groups.clear();
//        friends.clear();
        friendItemList.clear();
//        Log.d(TAG, "get friendship info id :" + UserInfo.getInstance().getId());
//        List<TIMFriendGroup> timFriendGroups = TIMFriendshipProxy.getInstance().getFriendsByGroups(null);
//        if (timFriendGroups == null) return;
//        for (TIMFriendGroup group : timFriendGroups){
//            groups.add(group.getGroupName());
//            List<FriendProfile> friendItemList = new ArrayList<>();
//            for (TIMUserProfile profile : group.getProfiles()){
//                friendItemList.add(new FriendProfile(profile));
//            }
//            friends.put(group.getGroupName(), friendItemList);
//        }
        List<TIMUserProfile> timFriendGroups =TIMFriendshipProxy.getInstance().getFriends();
        for (TIMUserProfile profile : timFriendGroups){
                if(TextUtils.isEmpty(profile.getNickName()))continue;
                friendItemList.add(new FriendProfile(profile));
            }


    }

    /**
     * 获取分组列表
     */
    public List<String> getGroups(){
        return new ArrayList<>();
}
    public String[] getGroupsArray(){
        return null;
    }


    /**
     * 获取好友列表摘要
     */
    public List<FriendProfile> getFriends(){
        return friendItemList;
    }

    /**
     * 判断是否是好友
     *
     * @param identify 需判断的identify
     */
    public boolean isFriend(String identify){
        for (FriendProfile friendProfile: friendItemList){
                if (identify.equals(friendProfile.getIdentify())) return true;
        }
        return false;
    }


    /**
     * 获取好友资料
     *
     * @param identify 好友id
     */
    public FriendProfile getProfile(String identify){

        for (FriendProfile friendProfile: friendItemList){
            if (identify.equals(friendProfile.getIdentify())) return friendProfile;
        }
        return null;
    }

    /**
     * 清除数据
     */
    public void clear(){
        if (instance == null) return;

        friendItemList.clear();
        instance = null;
    }


}
