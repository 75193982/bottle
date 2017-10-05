package com.jerei.im.timchat.ui;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;

import com.jerei.im.IMContext;
import com.jerei.im.presentation.business.InitBusiness;
import com.jerei.im.presentation.business.LoginBusiness;
import com.jerei.im.presentation.event.FriendshipEvent;
import com.jerei.im.presentation.event.GroupEvent;
import com.jerei.im.presentation.event.RefreshEvent;



import com.jerei.im.timchat.model.FriendProfile;
import com.jerei.im.timchat.model.FriendshipInfo;
import com.jerei.im.timchat.model.SystemConversation;
import com.jerei.im.timchat.model.UserInfo;
import com.jerei.im.timchat.utils.NetWorkUtils;
import com.jerei.im.ui.SideslipListView;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendFutureItem;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMGroupPendencyItem;
import com.tencent.TIMLogLevel;
import com.tencent.TIMMessage;
import com.jerei.im.presentation.presenter.ConversationPresenter;
import com.jerei.im.presentation.presenter.FriendshipManagerPresenter;
import com.jerei.im.presentation.presenter.GroupManagerPresenter;
import com.jerei.im.presentation.viewfeatures.ConversationView;
import com.jerei.im.presentation.viewfeatures.FriendshipMessageView;
import com.jerei.im.presentation.viewfeatures.GroupManageMessageView;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.adapters.ConversationAdapter;
import com.jerei.im.timchat.model.Conversation;
import com.jerei.im.timchat.model.CustomMessage;
import com.jerei.im.timchat.model.GroupManageConversation;
import com.jerei.im.timchat.model.MessageFactory;
import com.jerei.im.timchat.model.NomalConversation;
import com.jerei.im.ui.TemplateTitle;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.openqq.protocol.imsdk.im_open_common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 会话列表界面
 */
public abstract class ConversationFragment extends Fragment implements
        ConversationView, FriendshipMessageView, GroupManageMessageView
        , ConversationAdapter.ConversationOperationlistening, TIMCallBack {

    private final String TAG = "ConversationFragment";

    private View view;
    private List<Conversation> conversationList = new LinkedList<>();
    private ConversationAdapter adapter;
    public SideslipListView listView;
    private ConversationPresenter presenter;
    private FriendshipManagerPresenter friendshipManagerPresenter;
    private GroupManagerPresenter groupManagerPresenter;
    private List<String> groupList;
//    private FriendshipConversation friendshipConversation;
//    private GroupManageConversation groupManageConversation;

    private SystemConversation systemConversation;

    private FrameLayout no_network;
    private Button load_button;
    private TemplateTitle templateTitle;
    public ConversationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_conversation, container, false);
            listView = (SideslipListView) view.findViewById(R.id.list);
            adapter = new ConversationAdapter(getActivity(), R.layout.item_conversation, conversationList, this);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (listView.canClick()) {
                        conversationList.get(position).navToDetail(getActivity());
                        if (conversationList.get(position) instanceof GroupManageConversation) {
                            groupManagerPresenter.getGroupManageLastMessage();
                        }
                        refresh();
                    }

                }
            });


            no_network = (FrameLayout) view.findViewById(R.id.no_network);
            load_button = (Button) view.findViewById(R.id.load_button);
            load_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reloadload();
                }
            });

            //设置标题
            templateTitle = (TemplateTitle) view.findViewById(R.id.templateTitl);
            templateTitle.setTitleText(getString(R.string.home_conversation_tab));
            templateTitle.setMoreImg(R.drawable.contacts_hover);
            templateTitle.setMoreImgAction(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    goToContact();
                }
            });


            //ListView 长按
//            registerForContextMenu(listView);
        }




//        adapter.notifyDataSetChanged();


        return view;

    }


    public void initData() {
        friendshipManagerPresenter = new FriendshipManagerPresenter(ConversationFragment.this);
        groupManagerPresenter = new GroupManagerPresenter(ConversationFragment.this);
        presenter = new ConversationPresenter(ConversationFragment.this);
        presenter.getConversation();

        if (listView.getVisibility() == View.GONE) {
            listView.setVisibility(View.VISIBLE);
            no_network.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();


        if (NetWorkUtils.isConn(getActivity())) {
            listView.setVisibility(View.VISIBLE);
            no_network.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.GONE);
            no_network.setVisibility(View.VISIBLE);
        }

    }


    /**
     * 初始化界面或刷新界面
     *
     * @param conversationList
     */
    @Override
    public void initView(List<TIMConversation> conversationList) {
        this.conversationList.clear();
        groupList = new ArrayList<>();
        for (TIMConversation item : conversationList) {
            switch (item.getType()) {
                case C2C:
                case Group:
                    this.conversationList.add(new NomalConversation(item));
                    groupList.add(item.getPeer());
                    break;
            }
        }
        refresh();
        friendshipManagerPresenter.getFriendshipLastMessage();

    }

    /**
     * 更新最新消息显示
     *
     * @param message 最后一条消息
     */
    @Override
    public void updateMessage(TIMMessage message) {
        if (message == null) {
            refresh();
            return;
        }
        if (message.getConversation().getType() == TIMConversationType.System) {
            groupManagerPresenter.getGroupManageLastMessage();
            return;
        }
        if (MessageFactory.getMessage(message) instanceof CustomMessage) return;
        NomalConversation conversation = new NomalConversation(message.getConversation());
        Iterator<Conversation> iterator = conversationList.iterator();
        while (iterator.hasNext()) {
            Conversation c = iterator.next();
            if (conversation.equals(c)) {
                conversation = (NomalConversation) c;
                iterator.remove();
                break;
            }
        }
        conversation.setLastMessage(MessageFactory.getMessage(message));
        conversationList.add(conversation);
        Collections.sort(conversationList);
        refresh();
    }

    /**
     * 更新好友关系链消息
     */
    @Override
    public void updateFriendshipMessage() {
        friendshipManagerPresenter.getFriendshipLastMessage();
    }

    /**
     * 删除会话
     *
     * @param identify
     */
    @Override
    public void removeConversation(String identify) {
        Iterator<Conversation> iterator = conversationList.iterator();
        while (iterator.hasNext()) {
            Conversation conversation = iterator.next();
            if (conversation.getIdentify() != null && conversation.getIdentify().equals(identify)) {
                iterator.remove();
                return;
            }
        }
        refresh();
    }

    /**
     * 更新群信息
     *
     * @param info
     */
    @Override
    public void updateGroupInfo(TIMGroupCacheInfo info) {
        for (Conversation conversation : conversationList) {
            if (conversation.getIdentify() != null && conversation.getIdentify().equals(info.getGroupInfo().getGroupId())) {
                String name = info.getGroupInfo().getGroupName();
                if (name.equals("")) {
                    name = info.getGroupInfo().getGroupId();
                }
                conversation.setName(name);
                refresh();
                return;
            }
        }
    }

    /**
     * 刷新
     */
    @Override
    public void refresh() {
        try {
            if(systemConversation!=null){
                conversationList.remove(systemConversation);
                conversationList.add(0,systemConversation);
            }
            adapter.notifyDataSetChanged();

            if (getActivity() instanceof HomeActivityInterface) {
                ((HomeActivityInterface) getActivity()).setMsgUnread((int) getTotalUnreadNum());
            }
            if ((int) getTotalUnreadNum() != 0) {
                templateTitle.setTitleText(getString(R.string.home_conversation_tab) + "(" + getTotalUnreadNum() + ")");
            } else {
                templateTitle.setTitleText(getString(R.string.home_conversation_tab) + "");
            }
        } catch (Exception e) {
        }


    }


    /**
     * 获取好友关系链管理系统最后一条消息的回调
     *
     * @param message     最后一条消息
     * @param unreadCount 未读数
     */
    @Override
    public void onGetFriendshipLastMessage(TIMFriendFutureItem message, long unreadCount) {
        if (systemConversation == null){
            systemConversation = new SystemConversation(message,null);
            conversationList.add(0,systemConversation);
        }else{
            systemConversation.setLastMessage(message);
        }
        systemConversation.setUnreadCount(unreadCount);
        Collections.sort(conversationList);
        refresh();
        groupManagerPresenter.getGroupManageLastMessage();


    }

    /**
     * 获取好友关系链管理最后一条系统消息的回调
     *
     * @param message 消息列表
     */
    @Override
    public void onGetFriendshipMessage(List<TIMFriendFutureItem> message) {
        friendshipManagerPresenter.getFriendshipLastMessage();
    }

    /**
     * 获取群管理最后一条系统消息的回调
     *
     * @param message     最后一条消息
     * @param unreadCount 未读数
     */
    @Override
    public void onGetGroupManageLastMessage(final TIMGroupPendencyItem message,final long unreadCount) {


        List<String> users = new ArrayList<String>();
        users.add(message.getFromUser());


        //获取用户资料
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>(){
            @Override
            public void onError(int code, String desc){

            }

            @Override
            public void onSuccess(List<TIMUserProfile> result){

                for(TIMUserProfile res : result){
                    if (systemConversation == null) {
                        systemConversation = new SystemConversation(null,message);
                        conversationList.add(0,systemConversation);
                    } else {
                        systemConversation.setGroupLastMessage(message);
                    }
                    systemConversation.setUnreadCountGroup(unreadCount);
                    systemConversation.setFromNmae(res.getNickName());
                    Collections.sort(conversationList);
                    refresh();
                }
            }
        });




    }

    /**
     * 获取群管理系统消息的回调
     *
     * @param message 分页的消息列表
     */
    @Override
    public void onGetGroupManageMessage(List<TIMGroupPendencyItem> message) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Conversation conversation = conversationList.get(info.position);
        if (conversation instanceof NomalConversation) {
            menu.add(0, 1, Menu.NONE, getString(R.string.conversation_del));
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        NomalConversation conversation = (NomalConversation) conversationList.get(info.position);
        switch (item.getItemId()) {
            case 1:
                if (conversation != null){
                    if (presenter.delConversation(conversation.getType(), conversation.getIdentify())){
                        conversationList.remove(conversation);
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    //删除当前会话
    @Override
    public void delete(int position) {
        NomalConversation conversation = (NomalConversation) conversationList.get(position);
        if (presenter.delConversation(conversation.getType(), conversation.getIdentify())) {

            conversationList.remove(conversation);
            listView.turnToNormal();//list归位
            refresh();
        }

    }

    //全部标记已读
    @Override
    public void readMessage(int position) {
        NomalConversation conversation = (NomalConversation) conversationList.get(position);
        conversation.readAllMessage();
        listView.turnToNormal();//list归位
        refresh();

    }

    private long getTotalUnreadNum() {
        long num = 0;
        for (Conversation conversation : conversationList) {
            num += conversation.getUnreadNum();
        }
        try {
            IMContext.getAppCollback().getUnRead((int)num);
        }catch (Exception e){}

        return num;
    }


    /**
     * 重新加载  子类实现
     */
    public void reloadload() {

        showProgressDialog("正在连接服务器");

        SharedPreferences pref = getActivity().getSharedPreferences("data", getActivity().MODE_PRIVATE);
        int loglvl = pref.getInt("loglvl", TIMLogLevel.DEBUG.ordinal());
        //初始化IMSDK
        InitBusiness.start(getActivity().getApplicationContext(), loglvl);
        //初始化TLS
//        TlsBusiness.init(getActivity().getApplicationContext());
        //设置刷新监听
        RefreshEvent.getInstance();

        SharedPreferences pref2 = getActivity().getSharedPreferences("imlogin", getActivity().MODE_PRIVATE);
        UserInfo.getInstance().setUserSig(pref2.getString("sing", ""));
        UserInfo.getInstance().setId(pref2.getString("id", ""));
        navToHome();
    }

    public void navToHome() {
        //登录之前要初始化群和好友关系链缓存
        FriendshipEvent.getInstance().init();
        GroupEvent.getInstance().init();
        LoginBusiness.loginIm(UserInfo.getInstance().getId(), UserInfo.getInstance().getUserSig(), this);
    }



    @Override
    public void onError(int i, String s) {
        closeProgressDialog();
    }

    @Override
    public void onSuccess() {
        closeProgressDialog();
        initData();
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }


    ProgressDialog progressDialog = null;

    /**
     * 显示进度对话框
     */
    public void showProgressDialog(String message) {
        closeProgressDialog();
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());

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


    public abstract void goToContact();
}
