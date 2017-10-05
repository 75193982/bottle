package com.jerei.im.timchat.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jerei.im.IMContext;
import com.jerei.im.presentation.presenter.FriendshipManagerPresenter;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.model.GroupFuture;
import com.jerei.im.timchat.utils.GetHeadImageUtil;
import com.jerei.im.ui.CircleImageView;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupPendencyHandledStatus;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加好友请求 处理页面
 */
public class GroupHandleActivity extends Activity implements View.OnClickListener {

    private String id;
    TextView tvName;
    TextView tvWord;
    CircleImageView circleImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendship_handle);
        id = getIntent().getStringExtra("id");
        tvName = (TextView) findViewById(R.id.name);
        tvWord = (TextView) findViewById(R.id.word);
        circleImageView = (CircleImageView) findViewById(R.id.avatar);
        initData(id);
    }



    void initData(String id){
        List<String> users = new ArrayList<String>();
        users.add(id);
        showProgressDialog("正在加载..");
//获取用户资料
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int code, String desc) {
                Log.e("","");
                closeProgressDialog();
            }

            @Override
            public void onSuccess(List<TIMUserProfile> result) {

                for (TIMUserProfile res : result) {
                    tvName.setText(res.getNickName());

                    String word = getIntent().getStringExtra("word");
                    if(TextUtils.isEmpty(word)) {
                        word="您好，我是"+res.getNickName();
                    }
                    tvWord.setText(word);

                    GetHeadImageUtil.setImageUrl(circleImageView,res.getFaceUrl(),GroupHandleActivity.this);
                }
                closeProgressDialog();
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

        int viewid = v.getId();
        if (viewid == R.id.btnReject) {

          final   GroupFuture groupFuture = IMContext.groupFuture;
            if(groupFuture!=null){

                groupFuture.getFutureItem().refuse(null, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        if (i == 10013){
                            //已经是群成员
                            Toast.makeText(GroupHandleActivity.this,getString(R.string.group_member_already), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSuccess() {
                        groupFuture.setType(TIMGroupPendencyHandledStatus.HANDLED_BY_OTHER);
                        Intent intent = new Intent();
                        intent.putExtra("operate", false);
                        setResult(RESULT_OK, intent);
                        finish();

                    }
                });
            }
        } else if (viewid == R.id.btnAgree) {
            final   GroupFuture groupFuture = IMContext.groupFuture;
            if(groupFuture!=null){

                groupFuture.getFutureItem().accept(null, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {

                    }

                    @Override
                    public void onSuccess() {
                        groupFuture.setType(TIMGroupPendencyHandledStatus.HANDLED_BY_SELF);
                        Intent intent = new Intent();
                        intent.putExtra("operate", true);
                        setResult(RESULT_OK, intent);
                        finish();

                    }
                });
            }
        }

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
}
