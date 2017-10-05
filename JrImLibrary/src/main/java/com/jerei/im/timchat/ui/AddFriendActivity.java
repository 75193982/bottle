package com.jerei.im.timchat.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jerei.im.timchat.model.UserInfo;
import com.jerei.im.timchat.utils.GetHeadImageUtil;
import com.jerei.im.ui.CircleImageView;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendStatus;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.jerei.im.presentation.presenter.FriendshipManagerPresenter;
import com.jerei.im.presentation.viewfeatures.FriendshipManageView;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.model.FriendshipInfo;
import com.jerei.im.ui.LineControllerView;
import com.jerei.im.ui.ListPickerDialog;
import com.jerei.im.ui.NotifyDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 申请添加好友界面
 */
public class AddFriendActivity extends FragmentActivity implements View.OnClickListener, FriendshipManageView {


    private TextView tvName, btnAdd,maxLength;
    private EditText editRemark, editMessage;
    private LineControllerView idField, groupField;
    private FriendshipManagerPresenter presenter;
    private String id;
    private CircleImageView avatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        tvName = (TextView) findViewById(R.id.name);
        idField = (LineControllerView) findViewById(R.id.id);

        avatar= (CircleImageView) findViewById(R.id.avatar);
        id = getIntent().getStringExtra("id");

        idField.setContent(id);
        groupField = (LineControllerView) findViewById(R.id.group);
        btnAdd = (TextView) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        editMessage = (EditText) findViewById(R.id.editMessage);
        editRemark = (EditText) findViewById(R.id.editNickname);
        presenter = new FriendshipManagerPresenter(this);
        maxLength = (TextView) findViewById(R.id.maxLength);

        List<String> users = new ArrayList<String>();
        users.add(id);
        showProgressDialog("正在获取资料请稍后");
        //获取用户资料
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int code, String desc) {
                Log.e("","");
                closeProgressDialog();
            }

            @Override
            public void onSuccess(List<TIMUserProfile> result) {
                closeProgressDialog();
                for (TIMUserProfile res : result) {

                    GetHeadImageUtil.setImageUrl(avatar, res.getFaceUrl(),AddFriendActivity.this);
                    tvName.setText(res.getNickName());
                }

            }
        });

        editMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(TextUtils.isEmpty(editMessage.getText())){
                    maxLength.setText("(0/100)");
                }else {
                    maxLength.setText("("+editMessage.getText().toString().length()+"/100)");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    ProgressDialog progressDialog = null;
    /**
     * 显示进度对话框
     */
    public void showProgressDialog(String message) {
        closeProgressDialog();
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);

            progressDialog.setMessage(message);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAdd) {

            if(id.equals(UserInfo.getInstance().getId())){
                Toast.makeText(AddFriendActivity.this,"自己不能加自己为好友",Toast.LENGTH_LONG).show();
                return;
            }

            presenter.addFriend(id, editRemark.getText().toString(), groupField.getContent().equals(getString(R.string.default_group_name))?"":groupField.getContent(), editMessage.getText().toString());
        }else if (view.getId() == R.id.group){
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
                    groupField.setContent(groups[which]);
                }
            });
        }
    }

    /**
     * 添加好友结果回调
     *
     * @param status 返回状态
     */
    @Override
    public void onAddFriend(TIMFriendStatus status) {
        switch (status){
            case TIM_ADD_FRIEND_STATUS_PENDING:
                Toast.makeText(this, getResources().getString(R.string.add_friend_succeed), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case TIM_FRIEND_STATUS_SUCC:
                Toast.makeText(this, getResources().getString(R.string.add_friend_added), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case TIM_ADD_FRIEND_STATUS_FRIEND_SIDE_FORBID_ADD:
                Toast.makeText(this, getResources().getString(R.string.add_friend_refuse_all), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case TIM_ADD_FRIEND_STATUS_IN_OTHER_SIDE_BLACK_LIST:
                Toast.makeText(this, getResources().getString(R.string.add_friend_to_blacklist), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case TIM_ADD_FRIEND_STATUS_IN_SELF_BLACK_LIST:
                NotifyDialog dialog = new NotifyDialog();
                dialog.show(getString(R.string.add_friend_del_black_list), getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FriendshipManagerPresenter.delBlackList(Collections.singletonList(id), new TIMValueCallBack<List<TIMFriendResult>>() {
                            @Override
                            public void onError(int i, String s) {
                                Toast.makeText(AddFriendActivity.this, getResources().getString(R.string.add_friend_del_black_err), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(List<TIMFriendResult> timFriendResults) {
                                Toast.makeText(AddFriendActivity.this, getResources().getString(R.string.add_friend_del_black_succ), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                break;
            default:
//                Toast.makeText(this, getResources().getString(R.string.add_friend_error), Toast.LENGTH_SHORT).show();
                break;
        }

    }

    /**
     * 删除好友结果回调
     *
     * @param status 返回状态
     */
    @Override
    public void onDelFriend(TIMFriendStatus status) {

    }

    /**
     * 修改好友分组回调
     *
     * @param status    返回状态
     * @param groupName 分组名
     */
    @Override
    public void onChangeGroup(TIMFriendStatus status, String groupName) {

    }

}
