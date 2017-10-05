package com.jerei.im.timchat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jerei.im.IMContext;
import com.jerei.im.presentation.presenter.FriendshipManagerPresenter;
import com.jerei.im.presentation.presenter.GroupManagerPresenter;
import com.jerei.im.presentation.viewfeatures.FriendshipMessageView;
import com.jerei.im.presentation.viewfeatures.GroupManageMessageView;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.adapters.GroupManageMessageAdapter;
import com.jerei.im.timchat.adapters.SystemManageMessageAdapter;
import com.jerei.im.timchat.model.FriendFuture;
import com.jerei.im.timchat.model.Future;
import com.jerei.im.timchat.model.GroupFuture;
import com.tencent.TIMFriendFutureItem;
import com.tencent.TIMFutureFriendType;
import com.tencent.TIMGroupPendencyHandledStatus;
import com.tencent.TIMGroupPendencyItem;

import java.util.ArrayList;
import java.util.List;

public class SystemManageMessageActivity extends Activity implements GroupManageMessageView,FriendshipMessageView {

    private final String TAG = "GroupManageMessageActivity";
    private GroupManagerPresenter presenter;
    private ListView listView;
    private List<Future> list= new ArrayList<>();
    private SystemManageMessageAdapter adapter;
    private final int PAGE_SIZE = 20;

    private FriendshipManagerPresenter friendshipManagerPresenter;

    private final int FRIENDSHIP_REQ = 100;
    private final int FRIENDSHIP_REQ_GROUP = 101;
    FriendFuture friendFuture;
    GroupFuture groupFuture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_manage_message);

        listView =(ListView) findViewById(R.id.list);
        adapter = new SystemManageMessageAdapter(this, R.layout.item_three_line, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Future future =list.get(position);
                if(future.getClass().getName().contains("FriendFuture")){
               friendFuture= (FriendFuture) future;
                    if (friendFuture.getType() == TIMFutureFriendType.TIM_FUTURE_FRIEND_PENDENCY_IN_TYPE){

                        Intent intent = new Intent(SystemManageMessageActivity.this, FriendshipHandleActivity.class);
                        intent.putExtra("id", friendFuture.getIdentify());
                        intent.putExtra("word", friendFuture.getMessage());
                        startActivityForResult(intent, FRIENDSHIP_REQ);
                    }
                }else {

                    groupFuture= (GroupFuture) future;
                    if (groupFuture.getType() == TIMGroupPendencyHandledStatus.NOT_HANDLED){
                        Intent intent = new Intent(SystemManageMessageActivity.this, GroupHandleActivity.class);
                        intent.putExtra("id", groupFuture.getFutureItem().getFromUser());
                        intent.putExtra("word", groupFuture.getFutureItem().getHandledMsg());
                        IMContext.groupFuture = groupFuture;
                        startActivityForResult(intent, FRIENDSHIP_REQ_GROUP);

                    }


                }


            }
        });


        presenter = new GroupManagerPresenter(this);
        presenter.getGroupManageMessage(PAGE_SIZE);

        friendshipManagerPresenter = new FriendshipManagerPresenter(this);
        friendshipManagerPresenter.getFriendshipMessage();
    }

    /**
     * 获取群管理最后一条系统消息的回调
     *
     * @param message     最后一条消息
     * @param unreadCount 未读数
     */
    @Override
    public void onGetGroupManageLastMessage(TIMGroupPendencyItem message, long unreadCount) {

    }

    /**
     * 获取群管理系统消息的回调
     *
     * @param message 分页的消息列表
     */
    @Override
    public void onGetGroupManageMessage(List<TIMGroupPendencyItem> message) {
        List<GroupFuture> futures = new ArrayList<>();
        for (TIMGroupPendencyItem item : message){
            futures.add(new GroupFuture(item));
        }
        list.addAll(futures);
//        adapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();


    }




    @Override
    public void onGetFriendshipLastMessage(TIMFriendFutureItem message, long unreadCount) {

    }

    @Override
    public void onGetFriendshipMessage(List<TIMFriendFutureItem> message) {
        if (message != null && message.size() != 0){
            for (TIMFriendFutureItem item : message){
                list.add(new FriendFuture(item));
            }
            friendshipManagerPresenter.readFriendshipMessage(message.get(0).getAddTime());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FRIENDSHIP_REQ){
            if (resultCode == RESULT_OK){
                if (list.contains(friendFuture)){
                    boolean isAccept = data.getBooleanExtra("operate", true);
                    if (isAccept){
                        friendFuture.setType(TIMFutureFriendType.TIM_FUTURE_FRIEND_DECIDE_TYPE);
                    }else{
                        list.remove(friendFuture);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }

        if (requestCode == FRIENDSHIP_REQ_GROUP){
            if (resultCode == RESULT_OK){
                if (list.contains(groupFuture)){
                    boolean isAccept = data.getBooleanExtra("operate", true);
                    if (isAccept){
                        groupFuture.setType(TIMGroupPendencyHandledStatus.HANDLED_BY_SELF);
                    }else{
                        groupFuture.setType(TIMGroupPendencyHandledStatus.HANDLED_BY_OTHER);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }



    }
}
