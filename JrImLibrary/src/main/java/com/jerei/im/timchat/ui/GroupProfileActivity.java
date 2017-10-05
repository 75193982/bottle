package com.jerei.im.timchat.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.jerei.im.IMContext;
import com.jerei.im.presentation.presenter.ChatPresenter;
import com.jerei.im.presentation.viewfeatures.ChatView;
import com.jerei.im.timchat.adapters.GridViewAdapter;
import com.jerei.im.timchat.model.FriendshipInfo;
import com.jerei.im.timchat.model.GroupMemberProfile;
import com.jerei.im.timchat.model.InviteGroupMessage;
import com.jerei.im.timchat.model.LocationMessage;
import com.jerei.im.timchat.model.Message;
import com.jerei.im.timchat.model.ProfileSummary;
import com.jerei.im.timchat.utils.CustomDialog;
import com.jerei.im.timchat.utils.GetHeadImageUtil;
import com.jerei.im.ui.MyGridView;
import com.jerei.im.ui.RoundCornerImageView;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupAddOpt;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberRoleType;
import com.tencent.TIMGroupReceiveMessageOpt;
import com.jerei.im.presentation.presenter.GroupInfoPresenter;
import com.jerei.im.presentation.presenter.GroupManagerPresenter;
import com.jerei.im.presentation.viewfeatures.GroupInfoView;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.model.GroupInfo;
import com.jerei.im.timchat.model.UserInfo;
import com.jerei.im.ui.LineControllerView;
import com.jerei.im.ui.ListPickerDialog;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupProfileActivity extends FragmentActivity implements ChatView, GroupInfoView, View.OnClickListener,TIMValueCallBack<List<TIMGroupMemberInfo>> {

    private final String TAG = "GroupProfileActivity";
    public final static int REQUESTCODE = 1000;
    private final int CHOOSE_MEM_CODE = 400;
    private String identify,type;
    private GroupInfoPresenter groupInfoPresenter;
    private TIMGroupDetailInfo info;
    private boolean isInGroup;
    private boolean isGroupOwner;
    private final int REQ_CHANGE_NAME = 100, REQ_CHANGE_INTRO = 200,REQ_UPDATE_NICKNAME = 300;
    private TIMGroupMemberRoleType roleType = TIMGroupMemberRoleType.NotMember;
    private Map<String, TIMGroupAddOpt> allowTypeContent;
    private Map<String, TIMGroupReceiveMessageOpt> messageOptContent;
    private LineControllerView name,intro,qr_code,nickName;
    private MyGridView gridView;
    private GridViewAdapter gridViewAdapter;

    GroupMemberProfile groupMemberProfileMy;
    List<ProfileSummary> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_setting);
        identify = getIntent().getStringExtra("identify");
        roleType = GroupInfo.getInstance().getRole(identify);
        isInGroup = GroupInfo.getInstance().isInGroup(identify);
        groupInfoPresenter = new GroupInfoPresenter(this, Collections.singletonList(identify), isInGroup);
        groupInfoPresenter.getGroupDetailInfo();
        name = (LineControllerView) findViewById(R.id.nameText);
        nickName= (LineControllerView) findViewById(R.id.nickName);
        intro = (LineControllerView) findViewById(R.id.groupIntro);
        qr_code= (LineControllerView) findViewById(R.id.qr_code);
        gridView= (MyGridView) findViewById(R.id.gridView);
        gridViewAdapter =new GridViewAdapter(this,list,isManager());
        gridView.setAdapter(gridViewAdapter);
        LinearLayout controlInGroup = (LinearLayout) findViewById(R.id.controlInGroup);
        controlInGroup.setVisibility(isInGroup? View.VISIBLE: View.GONE);
        TextView controlOutGroup = (TextView) findViewById(R.id.controlOutGroup);
        controlOutGroup.setVisibility(isInGroup ? View.GONE : View.VISIBLE);
        TIMGroupManager.getInstance().getGroupMembers(identify, this);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>=list.size()){
                    if(i==list.size()){
                        Intent intent = new Intent(GroupProfileActivity.this, ChooseFriendActivity.class);
                        startActivityForResult(intent, CHOOSE_MEM_CODE);
                    }else {
                        Intent intentGroupMem = new Intent(GroupProfileActivity.this, GroupMemberActivity.class);
                        intentGroupMem.putExtra("id", identify);
                        intentGroupMem.putExtra("type",type);
                        startActivity(intentGroupMem);
                        //选择好友页面

                    }
                }else {
                    ProfileSummary profileSummary =list.get(i);
                    Intent intent = new Intent(GroupProfileActivity.this,ProfileActivity.class);
                    intent.putExtra("identify",profileSummary.getIdentify());
                    startActivity(intent);

                }
            }
        });
    }

    /**
     * 显示群资料
     *
     * @param groupInfos 群资料信息列表
     */
    @Override
    public void showGroupInfo(List<TIMGroupDetailInfo> groupInfos) {
        info = groupInfos.get(0);
        isGroupOwner = info.getGroupOwner().equals(UserInfo.getInstance().getId());

        type = info.getGroupType();
        LineControllerView member = (LineControllerView) findViewById(R.id.member);
        if (isInGroup){
//            member.setContent(String.valueOf(info.getMemberNum()));
            member.setName("全部群成员("+String.valueOf(info.getMemberNum())+")");
            member.setOnClickListener(this);
        }else{
            member.setName("全部群成员("+String.valueOf(info.getMemberNum())+")");
//            member.setVisibility(View.GONE);
            member.setOnClickListener(this);
        }
        name.setContent(info.getGroupName());
        LineControllerView id = (LineControllerView) findViewById(R.id.idText);
        id.setContent(info.getGroupId());

        intro.setContent(info.getGroupIntroduction());
        LineControllerView opt = (LineControllerView) findViewById(R.id.addOpt);
        switch (info.getGroupAddOpt()){
            case TIM_GROUP_ADD_AUTH:
                opt.setContent(getString(R.string.chat_setting_group_auth));
                break;
            case TIM_GROUP_ADD_ANY:
                opt.setContent(getString(R.string.chat_setting_group_all_accept));
                break;
            case TIM_GROUP_ADD_FORBID:
                opt.setContent(getString(R.string.chat_setting_group_all_reject));
                break;
        }
        LineControllerView msgNotify = (LineControllerView) findViewById(R.id.messageNotify);
        if (GroupInfo.getInstance().isInGroup(identify)){
            switch (GroupInfo.getInstance().getMessageOpt(identify)){
                case NotReceive:
                    msgNotify.setContent(getString(R.string.chat_setting_no_rev));
                    break;
                case ReceiveAndNotify:
                    msgNotify.setContent(getString(R.string.chat_setting_rev_notify));
                    break;
                case ReceiveNotNotify:
                    msgNotify.setContent(getString(R.string.chat_setting_rev_not_notify));
                    break;
            }
            msgNotify.setOnClickListener(this);
            messageOptContent = new HashMap<>();
            messageOptContent.put(getString(R.string.chat_setting_no_rev), TIMGroupReceiveMessageOpt.NotReceive);
            messageOptContent.put(getString(R.string.chat_setting_rev_not_notify), TIMGroupReceiveMessageOpt.ReceiveNotNotify);
            messageOptContent.put(getString(R.string.chat_setting_rev_notify), TIMGroupReceiveMessageOpt.ReceiveAndNotify);
        }else{
            msgNotify.setVisibility(View.GONE);
        }
        if (isManager()){
            opt.setCanNav(true);
            opt.setOnClickListener(this);
            allowTypeContent = new HashMap<>();
            allowTypeContent.put(getString(R.string.chat_setting_group_auth), TIMGroupAddOpt.TIM_GROUP_ADD_AUTH);
            allowTypeContent.put(getString(R.string.chat_setting_group_all_accept), TIMGroupAddOpt.TIM_GROUP_ADD_ANY);
            allowTypeContent.put(getString(R.string.chat_setting_group_all_reject), TIMGroupAddOpt.TIM_GROUP_ADD_FORBID);
            name.setCanNav(true);
            name.setOnClickListener(this);
            intro.setCanNav(true);
            intro.setOnClickListener(this);
        }
        TextView btnDel = (TextView) findViewById(R.id.btnDel);
        btnDel.setText(isGroupOwner ? getString(R.string.chat_setting_dismiss) : getString(R.string.chat_setting_quit));


        qr_code.setOnClickListener(this);
        nickName.setCanNav(true);
        nickName.setOnClickListener(this);
//            nickName.setContent(GroupInfo.getInstance().(identify));
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
            ChatActivity.navToChat(this,identify, TIMConversationType.Group);

        } else if (id == R.id.btnDel) {
            if (isGroupOwner){
                GroupManagerPresenter.dismissGroup(identify, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Log.i(TAG, "onError code" + i + " msg " + s);
                        if (i == 10004 && type.equals(GroupInfo.privateGroup)){
                            Toast.makeText(GroupProfileActivity.this, getString(R.string.chat_setting_quit_fail_private), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(GroupProfileActivity.this, getString(R.string.chat_setting_dismiss_succ), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }else{
                GroupManagerPresenter.quitGroup(identify, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Log.i(TAG, "onError code" + i + " msg " + s);
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(GroupProfileActivity.this, getString(R.string.chat_setting_quit_succ), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        } else if (id == R.id.controlOutGroup) {
            Intent intent = new Intent(this, ApplyGroupActivity.class);
            intent.putExtra("identify", identify);
            startActivity(intent);
        }else if (id == R.id.member) {
            Intent intentGroupMem = new Intent(this, GroupMemberActivity.class);
            intentGroupMem.putExtra("id", identify);
            intentGroupMem.putExtra("type",type);
            startActivity(intentGroupMem);
        }else if (id == R.id.addOpt) {
            final String[] stringList = allowTypeContent.keySet().toArray(new String[allowTypeContent.size()]);
            new ListPickerDialog().show(stringList,getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, final int which) {
                    TIMGroupManager.getInstance().modifyGroupAddOpt(identify, allowTypeContent.get(stringList[which]), new TIMCallBack() {
                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(GroupProfileActivity.this, getString(R.string.chat_setting_change_err), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess() {
                            LineControllerView opt = (LineControllerView) findViewById(R.id.addOpt);
                            opt.setContent(stringList[which]);
                        }
                    });
                }
            });
        }else if (id == R.id.nameText) {
            EditActivity.navToEdit(GroupProfileActivity.this, getString(R.string.chat_setting_change_group_name), info.getGroupName(), REQ_CHANGE_NAME, new EditActivity.EditInterface() {
                @Override
                public void onEdit(final String text, TIMCallBack callBack) {
                    TIMGroupManager.getInstance().modifyGroupName(identify, text, callBack);
                }
            },20);
        }
        else if (id == R.id.groupIntro) {
            intro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditActivity.navToEdit(GroupProfileActivity.this, getString(R.string.chat_setting_change_group_intro), intro.getContent(), REQ_CHANGE_INTRO, new EditActivity.EditInterface() {
                        @Override
                        public void onEdit(final String text, TIMCallBack callBack) {
                            TIMGroupManager.getInstance().modifyGroupIntroduction(identify, text, callBack);
                        }
                    },20);

                }
            });
        }
        else if (id == R.id.messageNotify) {
            final String[] messageOptList = messageOptContent.keySet().toArray(new String[messageOptContent.size()]);
            new ListPickerDialog().show(messageOptList,getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, final int which) {
                    TIMGroupManager.getInstance().modifyReceiveMessageOpt(identify, messageOptContent.get(messageOptList[which]), new TIMCallBack() {
                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(GroupProfileActivity.this, getString(R.string.chat_setting_change_err), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess() {
                            LineControllerView msgNotify = (LineControllerView) findViewById(R.id.messageNotify);
                            msgNotify.setContent(messageOptList[which]);
                        }
                    });
                }
            });
        }
        else if (id == R.id.qr_code) {
            try {
                qrCodeDialog(Create2DCode("group,"+identify+",hehehedsf"));
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
        else if (id == R.id.nickName) {
            EditActivity.navToEdit(GroupProfileActivity.this, getString(R.string.chat_setting_change_group_nickname), groupMemberProfileMy.getName(), REQ_UPDATE_NICKNAME, new EditActivity.EditInterface() {
                @Override
                public void onEdit(final String text,final TIMCallBack callBack) {


                    //修改群名片
                    TIMGroupManager.getInstance().modifyGroupMemberInfoSetNameCard(info.getGroupId(),
                            UserInfo.getInstance().getId(), text, new TIMCallBack() {
                                @Override
                                public void onError(int code, String desc) {
                                    Log.e("", "set name card failed, code: " + code + "|descr: " + desc);
                                }

                                @Override
                                public void onSuccess() {
                                    Log.e("", "set name card succ");
                                    nickName.setContent(text);
                                    gridViewAdapter.notifyDataSetChanged();
                                    callBack.onSuccess();

                                }
                            });
                }
            },10);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CHANGE_NAME){
            if (resultCode == RESULT_OK){
                name.setContent(data.getStringExtra(EditActivity.RETURN_EXTRA));
            }
        }else if (requestCode == REQ_CHANGE_INTRO){
            if (resultCode == RESULT_OK){
                intro.setContent(data.getStringExtra(EditActivity.RETURN_EXTRA));
            }
        }


        if (CHOOSE_MEM_CODE == requestCode) {

            if (resultCode == RESULT_OK){
//                GroupManagerPresenter.addMember(identify,
//                        data.getStringArrayListExtra("select")

//                );
             ArrayList<String> list=   data.getStringArrayListExtra("select");
                for(String id:list){
                    ChatPresenter    presenter = new ChatPresenter(GroupProfileActivity.this, id, TIMConversationType.C2C);
                    TIMMessage msg = new TIMMessage();

                    TIMCustomElem elem= new TIMCustomElem();
                    elem.setData(identify.getBytes());      //自定义byte[]
                    elem.setDesc("您邀请"+FriendshipInfo.getInstance().getProfile(id).getName()+"加入群组["+GroupInfo.getInstance().getGroupName(identify)+"]--您的好友"+UserInfo.getInstance().getNickname()+"邀请你加入群组"+"["+GroupInfo.getInstance().getGroupName(identify)+"],点击添加"); //自定义描述信息
                    if(msg.addElement(elem) != 0) {
                        return;
                    }
                    Message message = new InviteGroupMessage(msg);
                    presenter.start();
                    presenter.sendMessage(message.getMessage());
                }
            }
        }
    }

    private boolean isManager(){
        return roleType == TIMGroupMemberRoleType.Owner;
    }

    @Override
    public void onError(int i, String s) {

    }

    @Override
    public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
        list.clear();

        if (timGroupMemberInfos == null) return;
        for (TIMGroupMemberInfo item : timGroupMemberInfos){
            list.add(new GroupMemberProfile(item));
        }

        queryNickname(list);



    }

    void queryNickname(final List<ProfileSummary> list){
        List<String> users = new ArrayList<String>();


        for(ProfileSummary profileSummary:list){
            users.add(profileSummary.getIdentify());
        }
//获取用户资料
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>(){
            @Override
            public void onError(int code, String desc){
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表

            }

            @Override
            public void onSuccess(List<TIMUserProfile> result){

                for(TIMUserProfile tiMUserProfile:result){

                    for(ProfileSummary profileSummary:list){
                        if(profileSummary.getIdentify().equals(tiMUserProfile.getIdentifier())){

                            if(TextUtils.isEmpty(profileSummary.getName())){
                                ((GroupMemberProfile)profileSummary).setName(tiMUserProfile.getNickName());
                            }
                            ((GroupMemberProfile)profileSummary).setAvatarUr(tiMUserProfile.getFaceUrl());
                        }
                    }

                }
                for(ProfileSummary profileSummary:list){

                    if(profileSummary.getIdentify().equals(UserInfo.getInstance().getId())){
                        groupMemberProfileMy = (GroupMemberProfile) profileSummary;
                    }
                }
              nickName.setContent(groupMemberProfileMy.getName());
                gridViewAdapter.notifyDataSetChanged();
            }
        });
    }


    /**
     * 用字符串生成二维码
     * @param str
     * @author zhouzhe@lenovo-cw.com
     * @return
     * @throws WriterException
     */
    public Bitmap Create2DCode(String str) throws WriterException {
        //生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 300, 300);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        //二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(matrix.get(x, y)){
                    pixels[y * width + x] = 0xff000000;
                }

            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    CustomDialog.Builder customBuilder;
    private CustomDialog dialog;
    private void qrCodeDialog(Bitmap bitmap) {
        Context mContext = GroupProfileActivity.this;
        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.qr_code_dialog, null);
        customBuilder = new CustomDialog.Builder(GroupProfileActivity.this);

        ImageView ImageView = (ImageView) layout.findViewById(R.id.image);
        RoundCornerImageView avatar = (RoundCornerImageView) layout.findViewById(R.id.avatar);
        TextView name = (TextView) layout.findViewById(R.id.name);
        TextView area = (TextView) layout.findViewById(R.id.area);
        TextView hint = (TextView) layout.findViewById(R.id.hint);

        area.setText("全部群成员("+String.valueOf(info.getMemberNum())+")");
        name.setText(GroupInfo.getInstance().getGroupName(identify));
        hint.setText("扫描上面二维码,申请加入群组");
        GetHeadImageUtil.setImageGroup(avatar,identify,GroupProfileActivity.this);
        ImageView.setImageBitmap(bitmap);
        dialog = customBuilder.create();
        dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.setContentView(layout);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();

        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.show();

    }

    @Override
    public void showMessage(TIMMessage message) {

    }

    @Override
    public void showMessage(List<TIMMessage> messages) {

    }

    @Override
    public void clearAllMessage() {

    }

    @Override
    public void onSendMessageSuccess(TIMMessage message) {

    }

    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {

    }

    @Override
    public void sendImage() {

    }

    @Override
    public void sendPhoto() {

    }

    @Override
    public void sendText() {

    }

    @Override
    public void sendFile() {

    }

    @Override
    public void sendLocation() {

    }

    @Override
    public void startSendVoice() {

    }

    @Override
    public void endSendVoice() {

    }

    @Override
    public void sendVideo(String fileName) {

    }

    @Override
    public void cancelSendVoice() {

    }

    @Override
    public void sending() {

    }
}
