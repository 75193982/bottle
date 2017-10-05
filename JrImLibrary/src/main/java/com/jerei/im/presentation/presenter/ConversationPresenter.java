package com.jerei.im.presentation.presenter;

import android.util.Log;

import com.jerei.im.timchat.model.FriendProfile;
import com.jerei.im.timchat.model.FriendshipInfo;
import com.jerei.im.timchat.model.GroupInfo;
import com.jerei.im.timchat.model.ProfileSummary;
import com.jerei.im.timchat.utils.NetWorkUtils;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;
import com.jerei.im.presentation.event.FriendshipEvent;
import com.jerei.im.presentation.event.GroupEvent;
import com.jerei.im.presentation.event.MessageEvent;
import com.jerei.im.presentation.event.RefreshEvent;
import com.jerei.im.presentation.viewfeatures.ConversationView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * 会话界面逻辑
 */
public class ConversationPresenter implements Observer {

    private static final String TAG = "ConversationPresenter";
    private ConversationView view;

    public ConversationPresenter(ConversationView view){
        //注册消息监听
        MessageEvent.getInstance().addObserver(this);
        //注册刷新监听
        RefreshEvent.getInstance().addObserver(this);
        //注册好友关系链监听
        FriendshipEvent.getInstance().addObserver(this);
        //注册群关系监听
        GroupEvent.getInstance().addObserver(this);
        this.view = view;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent){
            TIMMessage msg = (TIMMessage) data;
            view.updateMessage(msg);
        }else if (observable instanceof FriendshipEvent){
            FriendshipEvent.NotifyCmd cmd = (FriendshipEvent.NotifyCmd) data;
            switch (cmd.type){
                case ADD_REQ:
                case READ_MSG:
                case ADD:
                    view.updateFriendshipMessage();
                    break;
            }
        }else if (observable instanceof GroupEvent){
            GroupEvent.NotifyCmd cmd = (GroupEvent.NotifyCmd) data;
            switch (cmd.type){
                case UPDATE:
                case ADD:
                    view.updateGroupInfo((TIMGroupCacheInfo) cmd.data);
                    break;
                case DEL:
                    view.removeConversation((String) cmd.data);
                    break;

            }
        }else if (observable instanceof RefreshEvent){
            view.refresh();
        }
    }



    public void getConversation(){


        List<TIMConversation> list = new ArrayList<>();
        //获取会话个数
        long cnt = TIMManager.getInstance().getConversationCount();
        Log.d(TAG, "get " + cnt + " conversations");

        List<FriendProfile> friendsList =  FriendshipInfo.getInstance().getFriends();
        List<ProfileSummary> grouplist= GroupInfo.getInstance().getGroupListByType(GroupInfo.publicGroup);
        //遍历会话列表
        for(long i = 0; i < cnt; ++i) {
            //根据索引获取会话
            final TIMConversation conversation = TIMManager.getInstance().getConversationByIndex(i);
            if (conversation.getType() == TIMConversationType.System) continue;

            for(FriendProfile friendProfile:friendsList){
                //如果 该会话 不是一对一  或者 该会话 在好友列表里存在
                if(friendProfile.getProfile().getIdentifier().equals(conversation.getPeer())&&!list.contains(conversation)){
                    list.add(conversation);
                    conversation.getMessage(1, null, new TIMValueCallBack<List<TIMMessage>>() {
                        @Override
                        public void onError(int i, String s) {
                            Log.e(TAG,"get message error"+s);
                        }

                        @Override
                        public void onSuccess(List<TIMMessage> timMessages) {
                            if(timMessages!=null&&timMessages.size()>0)
                                view.updateMessage(timMessages.get(0));
                        }
                    });
                }
            }

            for(ProfileSummary profileSummary:grouplist){
                //如果 该会话 不是一对一  或者 该会话 在好友列表里存在
                if(profileSummary.getIdentify().equals(conversation.getPeer())&&!list.contains(conversation)){
                    list.add(conversation);
                    conversation.getMessage(1, null, new TIMValueCallBack<List<TIMMessage>>() {
                        @Override
                        public void onError(int i, String s) {
                            Log.e(TAG,"get message error"+s);
                        }

                        @Override
                        public void onSuccess(List<TIMMessage> timMessages) {
                            if(timMessages!=null&&timMessages.size()>0)
                                view.updateMessage(timMessages.get(0));
                        }
                    });
                }
            }
        }
        view.initView(list);
    }


    /**
     * 删除会话
     *
     * @param type 会话类型
     * @param id 会话对象id
     */
    public boolean delConversation(TIMConversationType type, String id){
        return TIMManager.getInstance().deleteConversationAndLocalMsgs(type, id);
    }

}
