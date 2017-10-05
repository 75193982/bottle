package com.jerei.im.timchat.ui;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jerei.im.timchat.R;
import com.jerei.im.timchat.adapters.ContactAdapter;
import com.jerei.im.timchat.adapters.ExpandGroupListAdapter;
import com.jerei.im.timchat.model.FriendProfile;
import com.jerei.im.timchat.model.FriendshipInfo;
import com.jerei.im.timchat.model.GroupInfo;
import com.jerei.im.timchat.utils.GetFirstLetterUtil;
import com.jerei.im.timchat.utils.GetHeadImageUtil;
import com.jerei.im.timchat.utils.SortByName;
import com.jerei.im.ui.RoundCornerImageView;
import com.jerei.im.ui.TemplateTitle;
import com.jerei.im.ui.letterlist.LetterListView;
import com.tencent.TIMConversationType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 联系人界面
 */
public class ContactFragment extends Fragment implements  View.OnClickListener,ContactAdapter.ContactCall {

    private View view;
//    private ExpandGroupListAdapter mGroupListAdapter;
//    private ExpandableListView mGroupListView;
    private ContactAdapter contactAdapter;
    private LetterListView letterListView;
    private LinearLayout mNewFriBtn, mPublicGroupBtn, mChatRoomBtn,mPrivateGroupBtn;
    Map<String, List<FriendProfile>> friends;
    List<FriendProfile> friendsList = new ArrayList<>();
    List<FriendProfile> friendsListbackups =new ArrayList<>(); //好友备份

    EditText search;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null){
            view = inflater.inflate(R.layout.fragment_contact, container, false);
//            mGroupListView = (ExpandableListView) view.findViewById(R.id.groupList);

            letterListView = (LetterListView)view.findViewById(R.id.groupList);

            mNewFriBtn = (LinearLayout) view.findViewById(R.id.btnNewFriend);
            mNewFriBtn.setOnClickListener(this);
            mPublicGroupBtn = (LinearLayout) view.findViewById(R.id.btnPublicGroup);
            mPublicGroupBtn.setOnClickListener(this);
            mChatRoomBtn = (LinearLayout) view.findViewById(R.id.btnChatroom);
            mChatRoomBtn.setOnClickListener(this);
            mPrivateGroupBtn = (LinearLayout) view.findViewById(R.id.btnPrivateGroup);
            mPrivateGroupBtn.setOnClickListener(this);
            search = (EditText) view.findViewById(R.id.search);


        }
        return view;
    }


    @Override
    public void onResume(){
        super.onResume();











        //分组写法
//        mGroupListAdapter = new ExpandGroupListAdapter(getActivity(), FriendshipInfo.getInstance().getGroups(), friends);
//        mGroupListView.setAdapter(mGroupListAdapter);
//        mGroupListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
//                friends.get(FriendshipInfo.getInstance().getGroups().get(groupPosition)).get(childPosition).onClick(getActivity());
//                return false;
//            }
//        });


        //字母排序写法
        friendsList.clear();
        friendsList.addAll(FriendshipInfo.getInstance().getFriends()) ;

        Collections.sort(friendsList, new SortByName());
        friendsListbackups.clear();
        friendsListbackups.addAll(friendsList);


        contactAdapter = new ContactAdapter(friendsList,getActivity(),this);
        letterListView.setAdapter(contactAdapter);
        letterListView.setOnItemClickListener(contactAdapter);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String str = search.getText()+"";
                friendsList.clear();
                if(TextUtils.isEmpty(str)){

                    friendsList.addAll(friendsListbackups);
                    contactAdapter = new ContactAdapter(friendsList,getActivity(),ContactFragment.this);
                    letterListView.setAdapter(contactAdapter);
                    return;
                }


                for(FriendProfile friendProfile:friendsListbackups){
                    if(!TextUtils.isEmpty(friendProfile.getName())&&friendProfile.getName().contains(str)){
                        friendsList.add(friendProfile);

                    }
                }

                contactAdapter = new ContactAdapter(friendsList,getActivity(),ContactFragment.this);
                letterListView.setAdapter(contactAdapter);

            }
        });
    }



    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btnNewFriend) {
            Intent intent = new Intent(getActivity(), FriendshipManageMessageActivity.class);
            getActivity().startActivity(intent);
        }
        if (view.getId() == R.id.btnPublicGroup) {
            showGroups(GroupInfo.publicGroup);
        }
        if (view.getId() == R.id.btnChatroom) {
            showGroups(GroupInfo.chatRoom);
        }
        if (view.getId() == R.id.btnPrivateGroup) {
            showGroups(GroupInfo.privateGroup);
        }


    }




    private void showGroups(String type){

        Intent intent = new Intent(getActivity(), GroupListActivity.class);
        intent.putExtra("type", type);
        getActivity().startActivity(intent);
    }

    @Override
    public void onClickFriendProfile(FriendProfile friendProfile) {
        friendProfile.onClick(getActivity());
    }
}
