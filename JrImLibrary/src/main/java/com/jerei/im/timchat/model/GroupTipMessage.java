package com.jerei.im.timchat.model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jerei.im.IMContext;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.adapters.ChatAdapter;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupTipsElem;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 群tips消息
 */
public class GroupTipMessage extends Message {


    public GroupTipMessage(TIMMessage message){
        this.message = message;
    }


    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */

    private TextView textView;
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        viewHolder.leftPanel.setVisibility(View.GONE);
        viewHolder.rightPanel.setVisibility(View.GONE);
        viewHolder.systemMessage.setVisibility(View.VISIBLE);
//        viewHolder.systemMessage.setText(getSummary());
        setSummary(viewHolder.systemMessage);
        textView= viewHolder.systemMessage;
    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        final TIMGroupTipsElem e = (TIMGroupTipsElem) message.getElement(0);
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Map.Entry<String, TIMGroupMemberInfo>> iterator = e.getChangedGroupMemberInfo().entrySet().iterator();
        String userid = "";
        TIMGroupMemberInfo timGroupMemberInfo = null;
        switch (e.getTipsType()) {
            case CancelAdmin:
            case SetAdmin:
                return IMContext.getContext().getString(R.string.summary_group_admin_change);
            case Join:

                while (iterator.hasNext()) {
                    Map.Entry<String, TIMGroupMemberInfo> item = iterator.next();
                    userid=    item.getValue().getUser();
                    timGroupMemberInfo=item.getValue();
                }
                FriendProfile friendProfile =   FriendshipInfo.getInstance().getProfile(userid);
                if(friendProfile!=null){
                    return friendProfile.getName() +" "+
                            IMContext.getContext().getString(R.string.summary_group_mem_add);
                }
                return getName(timGroupMemberInfo) +
                        IMContext.getContext().getString(R.string.summary_group_mem_add);
            case Kick:

                Map<String, TIMUserProfile> changeduserinfo =   e.getChangedUserInfo();
                    if(changeduserinfo.size()>0){
                        TIMUserProfile tiMUserProfile=   changeduserinfo.get(e.getUserList().get(0));
                        return  tiMUserProfile.getNickName()+   IMContext.getContext().getString(R.string.summary_group_mem_kick);
                    }else {
                        return "";
                    }
            case ModifyMemberInfo:


            while (iterator.hasNext()) {
                Map.Entry<String, TIMGroupMemberInfo> item = iterator.next();
                userid=    item.getValue().getUser();
                timGroupMemberInfo=item.getValue();
            }
            FriendProfile friendProfile2 =   FriendshipInfo.getInstance().getProfile(userid);
            if(friendProfile2!=null){
                return friendProfile2.getName() +" "+
                        IMContext.getContext().getString(R.string.summary_group_mem_modify);
            }
            return getName(timGroupMemberInfo) +
                    IMContext.getContext().getString(R.string.summary_group_mem_modify);


            case Quit:
                FriendProfile friendProfile3 =   FriendshipInfo.getInstance().getProfile(e.getOpUser() );
                if(friendProfile3!=null){
                    return friendProfile3.getName() +" "+
                            IMContext.getContext().getString(R.string.summary_group_mem_quit);
                }

                return e.getOpUser() +
                        IMContext.getContext().getString(R.string.summary_group_mem_quit);


            case ModifyGroupInfo:
                return IMContext.getContext().getString(R.string.summary_group_info_change);
        }
        return "";
    }


    /**
     * 获取消息摘要
     */
    public void setSummary(final TextView text) {
        final TIMGroupTipsElem e = (TIMGroupTipsElem) message.getElement(0);
    final     StringBuilder stringBuilder = new StringBuilder();
        Iterator<Map.Entry<String, TIMGroupMemberInfo>> iterator = e.getChangedGroupMemberInfo().entrySet().iterator();
        Log.e("getTipsType",e.getTipsType()+"");
        switch (e.getTipsType()) {
            case CancelAdmin:
            case SetAdmin:
                text.setText(IMContext.getContext().getString(R.string.summary_group_admin_change));
                break;
            case Join:

                while (iterator.hasNext()) {
                    Map.Entry<String, TIMGroupMemberInfo> item = iterator.next();
                    stringBuilder.append(item.getValue().getUser());
                }
                text.setText(stringBuilder.toString()+
                        IMContext.getContext().getString(R.string.summary_group_mem_add));


                //待获取用户资料的用户列表
                List<String> users = new ArrayList<String>();
                users.add(stringBuilder.toString());
//获取用户资料
                text.setTag(stringBuilder.toString()+IMContext.getContext().getString(R.string.summary_group_mem_add));
                TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>(){
                    @Override
                    public void onError(int code, String desc){
                        Log.e(code+"",desc);
                        text.setVisibility(View.GONE);
                    }

                    @Override
                    public void onSuccess(List<TIMUserProfile> result){

                        for(TIMUserProfile res : result){
                            if(text.getTag().toString()!=null&&text.getTag().toString().equals(stringBuilder.toString()+IMContext.getContext().getString(R.string.summary_group_mem_add))){
                                text.setText(res.getNickName() +
                                        IMContext.getContext().getString(R.string.summary_group_mem_add));
                            }


                        }
                    }
                });

                break;
            case Kick:


                Map<String, TIMUserProfile> changeduserinfo =   e.getChangedUserInfo();
                if(changeduserinfo.size()>0){
                    TIMUserProfile tiMUserProfile=   changeduserinfo.get(e.getUserList().get(0));
                    text.setText(tiMUserProfile.getNickName()+
                            IMContext.getContext().getString(R.string.summary_group_mem_kick));
                }
                break;
            case ModifyMemberInfo:

            while (iterator.hasNext()) {
                Map.Entry<String, TIMGroupMemberInfo> item = iterator.next();
                stringBuilder.append(item.getValue().getUser());
            }
            //待获取用户资料的用户列表
            List<String> users2 = new ArrayList<String>();
                users2.add(stringBuilder.toString());
//获取用户资料
                text.setTag(stringBuilder.toString()+IMContext.getContext().getString(R.string.summary_group_mem_modify));
            TIMFriendshipManager.getInstance().getUsersProfile(users2, new TIMValueCallBack<List<TIMUserProfile>>(){
                @Override
                public void onError(int code, String desc){
                    Log.e(code+"",desc);

                }

                @Override
                public void onSuccess(List<TIMUserProfile> result){

                    for(TIMUserProfile res : result){

                        if(text.getTag().toString()!=null&&text.getTag().toString().equals(stringBuilder.toString()+IMContext.getContext().getString(R.string.summary_group_mem_modify))){
                            text.setText(res.getNickName() +
                                    IMContext.getContext().getString(R.string.summary_group_mem_modify));
                        }
                    }
                }
            });
                break;
            case Quit:
                text.setText(e.getOpUser()+
                        IMContext.getContext().getString(R.string.summary_group_mem_quit));
                //待获取用户资料的用户列表
                List<String> users3 = new ArrayList<String>();
                users3.add(e.getOpUser());
//获取用户资料
 text.setTag(e.getOpUser()+IMContext.getContext().getString(R.string.summary_group_mem_quit));
                TIMFriendshipManager.getInstance().getUsersProfile(users3, new TIMValueCallBack<List<TIMUserProfile>>(){
                    @Override
                    public void onError(int code, String desc){
                        Log.e(code+"",desc);
                        text.setVisibility(View.GONE);
                    }

                    @Override
                    public void onSuccess(List<TIMUserProfile> result){

                        for(TIMUserProfile res : result){

                            if(text.getTag().toString()!=null&&text.getTag().toString().equals(e.getOpUser()+IMContext.getContext().getString(R.string.summary_group_mem_quit))){
                                text.setText(res.getNickName() +
                                        IMContext.getContext().getString(R.string.summary_group_mem_quit));
                            }

                        }
                    }
                });

            break;
            case ModifyGroupInfo:
                text.setText(
                        IMContext.getContext().getString(R.string.summary_group_info_change));

        }

    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    private String getName(TIMGroupMemberInfo info){
        try{
            if (info.getUser().equals(UserInfo.getInstance().getId())){
                return UserInfo.getInstance().getNickname();
            }

            if (info.getNameCard().equals("")){
                return info.getUser();
            }
            return info.getNameCard();
        }catch (Exception e) {

        }
        return "";


    }
}
