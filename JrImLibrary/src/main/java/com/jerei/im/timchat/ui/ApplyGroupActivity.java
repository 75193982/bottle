package com.jerei.im.timchat.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jerei.im.timchat.utils.GetHeadImageUtil;
import com.jerei.im.ui.RoundCornerImageView;
import com.tencent.TIMCallBack;
import com.jerei.im.presentation.presenter.GroupManagerPresenter;
import com.jerei.im.timchat.R;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 申请群组界面
 */
public class ApplyGroupActivity extends Activity implements TIMCallBack {

    private final String TAG = "ApplyGroupActivity";

    private String identify;

    private RoundCornerImageView avatar;
    TextView des;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_group);
        identify = getIntent().getStringExtra("identify");
         des = (TextView) findViewById(R.id.description);
        avatar = (RoundCornerImageView) findViewById(R.id.avatar);
        des.setText("申请加入 " );
        final EditText editText = (EditText) findViewById(R.id.input);
        TextView btnSend = (TextView) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupManagerPresenter.applyJoinGroup(identify, editText.getText().toString(), ApplyGroupActivity.this);
            }
        });

        //创建待获取公开信息的群组列表
        List<String> groupList = new ArrayList<String>();

        groupList.add(identify);
        showProgressDialog("正在加载..");
        //获取群组公开信息
        TIMGroupManager.getInstance().getGroupPublicInfo(groupList, new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
            @Override
            public void onError(int code, String desc) {
                closeProgressDialog();
            }

            @Override
            public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                for(TIMGroupDetailInfo itMGroupDetailInfo:timGroupDetailInfos){
                    des.setText("申请加入 [" + itMGroupDetailInfo.getGroupName()+"]");
                    GetHeadImageUtil.setImageUrl(avatar,itMGroupDetailInfo.getFaceUrl(),ApplyGroupActivity.this);
                }
                closeProgressDialog();
            }
        });

    }

    @Override
    public void onError(int i, String s) {
        if (i == 10013){
            //已经是群成员
            Toast.makeText(this, getString(R.string.group_member_already), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, getResources().getString(R.string.send_success), Toast.LENGTH_SHORT).show();
        finish();
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
