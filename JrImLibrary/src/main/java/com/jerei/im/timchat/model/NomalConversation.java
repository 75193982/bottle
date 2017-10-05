package com.jerei.im.timchat.model;

import android.content.Context;

import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.ui.ChatActivity;


/**
 * 好友或群聊的会话
 */
public class NomalConversation extends Conversation {


    private TIMConversation conversation;


    //最后一条消息
    private Message lastMessage;

    public Message getLastMessage() {
        return lastMessage;
    }

    public NomalConversation(TIMConversation conversation){
        this.conversation = conversation;

        type = conversation.getType();
        identify = conversation.getPeer();
        if (type == TIMConversationType.Group){
            name= GroupInfo.getInstance().getGroupName(identify);
            if (name.equals("")) name = identify;
        }else{
            FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
            name=profile == null?identify:profile.getName();
        }
    }


    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }


    @Override
    public int getAvatar() {
        switch (type){
            case C2C:
                return R.drawable.head_other;
            case Group:
                return R.drawable.head_group;
        }
        return 0;
    }

    @Override
    public String getAvatarUrl() {
        switch (type){
            case C2C:
                return lastMessage.getMessage().getSenderProfile().getFaceUrl();
            default:
                return "";
        }

    }

    /**
     * 跳转到聊天界面或会话详情
     *
     * @param context 跳转上下文
     */
    @Override
    public void navToDetail(Context context) {
        //聊天界面

        ChatActivity.navToChat(context,identify,type);
    }

    /**
     * 获取最后一条消息摘要
     */
    @Override
    public String getLastMessageSummary(){
        if (lastMessage == null) return "";
        return lastMessage.getSummary();
    }


    /**
     * 获取未读消息数量
     */
    @Override
    public long getUnreadNum(){
        if (conversation == null) return 0;
        return conversation.getUnreadMessageNum();
    }

    /**
     * 将所有消息标记为已读
     */
    @Override
    public void readAllMessage(){
        if (conversation != null){
            conversation.setReadMessage();
        }
    }


    /**
     * 获取最后一条消息的时间
     */
    @Override
    public long getLastMessageTime() {
        if (lastMessage == null) return 0;
        return lastMessage.getMessage().timestamp();
    }

    /**
     * 获取会话类型
     */
    public TIMConversationType getType(){
        return conversation.getType();
    }
}
