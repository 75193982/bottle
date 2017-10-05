package com.jerei.im.timchat.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jerei.im.presentation.presenter.FriendshipManagerPresenter;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.model.FriendFuture;
import com.jerei.im.timchat.model.FriendshipInfo;
import com.jerei.im.timchat.model.Future;
import com.jerei.im.timchat.model.GroupFuture;
import com.jerei.im.timchat.model.GroupInfo;
import com.jerei.im.timchat.model.UserInfo;
import com.jerei.im.timchat.utils.GetHeadImageUtil;
import com.jerei.im.ui.CircleImageView;
import com.jerei.im.ui.RoundCornerImageView;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMFutureFriendType;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupPendencyGetType;
import com.tencent.TIMGroupPendencyHandledStatus;
import com.tencent.TIMGroupPendencyItem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 群管理消息adapter
 */
public class SystemManageMessageAdapter extends ArrayAdapter<Future> {


    private int resourceId;
    private View view;
    private Context context;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public SystemManageMessageAdapter(Context context, int resource, List<Future> objects) {
        super(context, resource, objects);
        resourceId = resource;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView != null){
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }else{
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.avatar = (RoundCornerImageView) view.findViewById(R.id.avatar);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.des = (TextView) view.findViewById(R.id.description);
            viewHolder.remark = (TextView) view.findViewById(R.id.remark);
            viewHolder.status = (TextView) view.findViewById(R.id.status);
            view.setTag(viewHolder);
        }
        Resources res = getContext().getResources();
        Future future=  getItem(position);
        if(future.getClass().getName().contains("GroupFuture")){
            viewHolder.remark.setVisibility(View.VISIBLE);
            final    GroupFuture groupFuture = (GroupFuture) future;

            final TIMGroupPendencyItem data = groupFuture.getFutureItem();
            String from = data.getFromUser(), to = data.getToUser();

            if (data.getPendencyType() == TIMGroupPendencyGetType.APPLY_BY_SELF){
                if (from.equals(UserInfo.getInstance().getId())){
                    //自己申请加入群
                    viewHolder.avatar.setImageResource(R.drawable.head_group);
                    List<String> groupList = new ArrayList<String>();
                    groupList.add(data.getGroupId());
                    //获取群组公开信息
                    TIMGroupManager.getInstance().getGroupPublicInfo(groupList, new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
                        @Override
                        public void onError(int code, String desc) {
                        }

                        @Override
                        public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                            for(TIMGroupDetailInfo tiMGroupDetailInfo:timGroupDetailInfos){
                                viewHolder.name.setText(tiMGroupDetailInfo.getGroupName());
                            }

                        }
                    });

                    viewHolder.des.setText(String.format("%s%s",res.getString(R.string.summary_me),res.getString(R.string.summary_group_apply)));
                }else{
                    //别人申请加入我的群
                    viewHolder.avatar.setImageResource(R.drawable.head_other);


                    List<String> users = new ArrayList<String>();
                    users.add(from);
//获取用户资料
                    TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>(){
                        @Override
                        public void onError(int code, String desc){

                        }

                        @Override
                        public void onSuccess(List<TIMUserProfile> result){

                            for(TIMUserProfile res : result){
                                viewHolder.name.setText(res.getNickName());
                            }
                        }
                    });


                    viewHolder.des.setText(String.format("%s%s",res.getString(R.string.summary_group_apply),GroupInfo.getInstance().getGroupName(data.getGroupId())));
                }
                viewHolder.remark.setText(data.getRequestMsg());
            }else{
                if (to.equals(UserInfo.getInstance().getId())){
                    //别人邀请我入群
                    viewHolder.avatar.setImageResource(R.drawable.head_group);

                    viewHolder.des.setText(String.format("%s%s%s",res.getString(R.string.summary_group_invite),res.getString(R.string.summary_me),res.getString(R.string.summary_group_add)));
                    List<String> groupList = new ArrayList<String>();
                    groupList.add(data.getGroupId());
                    //获取群组公开信息
                    TIMGroupManager.getInstance().getGroupPublicInfo(groupList, new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
                        @Override
                        public void onError(int code, String desc) {
                        }

                        @Override
                        public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                            for(TIMGroupDetailInfo tiMGroupDetailInfo:timGroupDetailInfos){
                                viewHolder.name.setText(tiMGroupDetailInfo.getGroupName());
                            }

                        }
                    });
                }else{
                    //邀请别人入群
                    viewHolder.avatar.setImageResource(R.drawable.head_other);
                    viewHolder.name.setText(FriendshipInfo.getInstance().getProfile(to).getName());
                    viewHolder.des.setText(String.format("%sTA%s%s",res.getString(R.string.summary_group_invite),res.getString(R.string.summary_group_add),GroupInfo.getInstance().getGroupName(data.getGroupId())));
                }
                viewHolder.remark.setText(String.format("%s %s",res.getString(R.string.summary_invite_person),from));
            }

            switch (groupFuture.getType()){
                case HANDLED_BY_OTHER:
                case HANDLED_BY_SELF:
                    viewHolder.status.setText(res.getString(R.string.handle));
                    viewHolder.status.setTextColor(res.getColor(R.color.text_gray1));
                    viewHolder.status.setOnClickListener(null);
                    viewHolder.status.setBackgroundResource(0);
                    break;
                case NOT_HANDLED:
                    viewHolder.status.setText(res.getString(R.string.agree));
                    viewHolder.status.setTextColor(res.getColor(R.color.white));
                    viewHolder.status.setBackgroundResource(R.drawable.bg_item_button_agree);
                    viewHolder.status.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            data.accept(null, new TIMCallBack() {
                                @Override
                                public void onError(int i, String s) {
                                    if (i == 10013){
                                        //已经是群成员
                                        Toast.makeText(getContext(), getContext().getString(R.string.group_member_already), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onSuccess() {
                                    groupFuture.setType(TIMGroupPendencyHandledStatus.HANDLED_BY_SELF);
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    break;
            }
            GetHeadImageUtil.setImageGroup(viewHolder.avatar,groupFuture.getFutureItem().getGroupId(),context);

        }else {

            final FriendFuture friendFuture= (FriendFuture) future;

            viewHolder.remark.setVisibility(View.GONE);
            viewHolder.avatar.setImageResource(R.drawable.head_other);
            viewHolder.name.setText(friendFuture.getName());
            viewHolder.des.setText(friendFuture.getMessage());
            viewHolder.status.setTextColor(res.getColor(R.color.text_gray1));
            switch (friendFuture.getType()){
                case TIM_FUTURE_FRIEND_PENDENCY_IN_TYPE:
                    viewHolder.status.setText(res.getString(R.string.newfri_agree));
                    viewHolder.status.setTextColor(res.getColor(R.color.white));
                    viewHolder.status.setBackgroundResource(R.drawable.bg_item_button_agree);
                    viewHolder.status.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FriendshipManagerPresenter.acceptFriendRequest(friendFuture.getIdentify(), new TIMValueCallBack<TIMFriendResult>() {
                                @Override
                                public void onError(int i, String s) {

                                }

                                @Override
                                public void onSuccess(TIMFriendResult timFriendResult) {
                                    friendFuture.setType(TIMFutureFriendType.TIM_FUTURE_FRIEND_DECIDE_TYPE);
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    break;
                case TIM_FUTURE_FRIEND_PENDENCY_OUT_TYPE:
                    viewHolder.status.setText(res.getString(R.string.newfri_wait));
                    viewHolder.status.setBackgroundResource(0);
                    break;
                case TIM_FUTURE_FRIEND_DECIDE_TYPE:
                    viewHolder.status.setText(res.getString(R.string.newfri_accept));
                    viewHolder.status.setBackgroundResource(0);
                    break;
            }
            GetHeadImageUtil.setImage(viewHolder.avatar,friendFuture.getIdentify(),context);

        }

        return view;
    }


    public class ViewHolder{
        RoundCornerImageView avatar;
        TextView name;
        TextView des;
        TextView remark;
        TextView status;
    }
}
