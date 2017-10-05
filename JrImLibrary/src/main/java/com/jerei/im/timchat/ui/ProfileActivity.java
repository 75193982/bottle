package com.jerei.im.timchat.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jerei.im.IMContext;
import com.jerei.im.timchat.adapters.ChatAdapter;
import com.jerei.im.timchat.utils.HttpPictureUtils;
import com.jerei.im.ui.CircleImageView;
import com.jerei.im.ui.TemplateTitle;

import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendStatus;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.jerei.im.presentation.event.FriendshipEvent;
import com.jerei.im.presentation.presenter.FriendshipManagerPresenter;
import com.jerei.im.presentation.viewfeatures.FriendshipManageView;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.model.FriendProfile;
import com.jerei.im.timchat.model.FriendshipInfo;
import com.jerei.im.ui.LineControllerView;
import com.jerei.im.ui.ListPickerDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class ProfileActivity extends FragmentActivity implements FriendshipManageView,  View.OnClickListener {


    private static final String TAG = ProfileActivity.class.getSimpleName();

    private final int CHANGE_CATEGORY_CODE = 100;
    private final int CHANGE_REMARK_CODE = 200;

    private FriendshipManagerPresenter friendshipManagerPresenter;
    private String identify, categoryStr;


    private TemplateTitle templateTitle;

    public static void navToProfile(Context context, String identify){
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("identify", identify);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        identify = getIntent().getStringExtra("identify");
        friendshipManagerPresenter = new FriendshipManagerPresenter(this);
        showProfile(identify);
        templateTitle = (TemplateTitle) findViewById(R.id.profile_title);
        templateTitle.setMoreImg(R.drawable.profile_more);
        templateTitle.setMoreImgAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoveDialog();
            }
        });
        instance = this;

    }




    private static ProfileActivity instance;


    /**
     * 显示用户信息
     *
     * @param identify
     */
    public void showProfile(final String identify) {
        final FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
        Log.d(TAG, "show profile isFriend " + (profile!=null));
        if (profile == null){//如果不是好友去网络上获取
            List<String> users = new ArrayList<String>();
            users.add(identify);
            //获取用户资料
            TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
                @Override
                public void onError(int code, String desc) {
                    Log.e("","");

                }

                @Override
                public void onSuccess(List<TIMUserProfile> result) {

                    for (TIMUserProfile res : result) {
                        initUserData(res.getNickName(),res.getIdentifier(),res.getRemark());
                    }

                }
            });
        }else {
            initUserData(profile.getName(),profile.getIdentify(),profile.getRemark());
        }




    }

    void initUserData(String urserName,final String userIdentify,String remarkStr){
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(urserName);

        LineControllerView id = (LineControllerView) findViewById(R.id.id);
        id.setContent(userIdentify);
        final LineControllerView remark = (LineControllerView) findViewById(R.id.remark);
        remark.setContent(remarkStr);
        remark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.navToEdit(ProfileActivity.this, getString(R.string.profile_remark_edit), remark.getContent(), CHANGE_REMARK_CODE, new EditActivity.EditInterface() {
                    @Override
                    public void onEdit(String text, TIMCallBack callBack) {
                        FriendshipManagerPresenter.setRemarkName(userIdentify, text, callBack);
                    }
                },20);

            }
        });
//        LineControllerView category = (LineControllerView) findViewById(R.id.group);
        //一个用户可以在多个分组内，客户端逻辑保证一个人只存在于一个分组
//        category.setContent(categoryStr = profile.getGroupName());
        LineControllerView black = (LineControllerView) findViewById(R.id.blackList);
        black.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    FriendshipManagerPresenter.addBlackList(Collections.singletonList(identify), new TIMValueCallBack<List<TIMFriendResult>>() {
                        @Override
                        public void onError(int i, String s) {
                            Log.e(TAG, "add black list error " + s);
                        }

                        @Override
                        public void onSuccess(List<TIMFriendResult> timFriendResults) {
                            if (timFriendResults.get(0).getStatus() == TIMFriendStatus.TIM_FRIEND_STATUS_SUCC){
                                Toast.makeText(ProfileActivity.this, getString(R.string.profile_black_succ), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnChat) {
            try {
                ChatActivity.chatActivity.finish();
            }catch (Exception e){}

            finish();
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("identify", identify);
            intent.putExtra("type", TIMConversationType.C2C);
            startActivity(intent);

        } else if (id == R.id.btnDel) {
            friendshipManagerPresenter.delFriend(identify);
        } else if (id == R.id.group) {
            final String[] groups = FriendshipInfo.getInstance().getGroupsArray();
            for (int i = 0; i < groups.length; ++i) {
                if (groups[i].equals("")) {
                    groups[i] = getString(R.string.default_group_name);
                    break;
                }
            }
            new ListPickerDialog().show(groups, getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (groups[which].equals(categoryStr)) return;
                    friendshipManagerPresenter.changeFriendGroup(identify,
                            categoryStr.equals(getString(R.string.default_group_name))?null:categoryStr,
                            groups[which].equals(getString(R.string.default_group_name))?null:groups[which]);
                }
            });
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHANGE_CATEGORY_CODE) {
            if (resultCode == RESULT_OK) {
                LineControllerView category = (LineControllerView) findViewById(R.id.group);
                category.setContent(categoryStr = data.getStringExtra("category"));
            }
        }else if (requestCode == CHANGE_REMARK_CODE){
            if (resultCode == RESULT_OK) {
                LineControllerView remark = (LineControllerView) findViewById(R.id.remark);
                remark.setContent(data.getStringExtra(EditActivity.RETURN_EXTRA));

            }
        }

    }

    /**
     * 添加好友结果回调
     *
     * @param status 返回状态
     */
    @Override
    public void onAddFriend(TIMFriendStatus status) {

    }

    /**
     * 删除好友结果回调
     *
     * @param status 返回状态
     */
    @Override
    public void onDelFriend(TIMFriendStatus status) {
        switch (status){
            case TIM_FRIEND_STATUS_SUCC:
                Toast.makeText(this, getResources().getString(R.string.profile_del_succeed), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case TIM_FRIEND_STATUS_UNKNOWN:
                Toast.makeText(this, getResources().getString(R.string.profile_del_fail), Toast.LENGTH_SHORT).show();
                break;
        }

    }

    /**
     * 修改好友分组回调
     *
     * @param status    返回状态
     * @param groupName 分组名
     */
    @Override
    public void onChangeGroup(TIMFriendStatus status, String groupName) {
        LineControllerView category = (LineControllerView) findViewById(R.id.group);
        if (groupName == null){
            groupName = getString(R.string.default_group_name);
        }
        switch (status){
            case TIM_FRIEND_STATUS_UNKNOWN:
                Toast.makeText(this, getString(R.string.change_group_error), Toast.LENGTH_SHORT).show();
            case TIM_FRIEND_STATUS_SUCC:
                category.setContent(groupName);
                FriendshipEvent.getInstance().OnFriendGroupChange();
                break;
            default:
                Toast.makeText(this, getString(R.string.change_group_error), Toast.LENGTH_SHORT).show();
                category.setContent(getString(R.string.default_group_name));
                break;
        }
    }


    private Dialog inviteDialog;
    private TextView deleteFriend, reportFriend;

    private void showMoveDialog() {
        inviteDialog = new Dialog(this, R.style.dialog);
        inviteDialog.setContentView(R.layout.contact_more);
        deleteFriend = (TextView) inviteDialog.findViewById(R.id.add_friend);
//        managerGroup = (TextView) inviteDialog.findViewById(R.id.manager_group);
        reportFriend = (TextView) inviteDialog.findViewById(R.id.reportfriend);
        //TODO 1989   添加好友 添加分组 等
        deleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendshipManagerPresenter.delFriend(identify);
            }
        });

        reportFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMContext.getAppCollback().reportContacts(ProfileActivity.this,identify);
            }
        });
        Window window = inviteDialog.getWindow();
        window.setGravity(Gravity.TOP | Gravity.RIGHT);
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        inviteDialog.show();
    }
}
