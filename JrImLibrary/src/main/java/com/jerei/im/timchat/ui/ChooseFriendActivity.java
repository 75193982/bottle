package com.jerei.im.timchat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.jerei.im.timchat.R;
import com.jerei.im.timchat.adapters.ContactAdapter;
import com.jerei.im.timchat.adapters.ExpandGroupListAdapter;
import com.jerei.im.timchat.model.FriendProfile;
import com.jerei.im.timchat.model.FriendshipInfo;
import com.jerei.im.timchat.utils.GetFirstLetterUtil;
import com.jerei.im.timchat.utils.SortByName;
import com.jerei.im.ui.TemplateTitle;
import com.jerei.im.ui.letterlist.LetterListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChooseFriendActivity extends Activity implements ContactAdapter.ContactCall {


    private ExpandGroupListAdapter mGroupListAdapter;
    private ExpandableListView mGroupListView;
    private List<FriendProfile> selectList = new ArrayList<>();
     Map<String, List<FriendProfile>> friends;
    List<FriendProfile> friendsList;
    private LetterListView letterListView;
    private ContactAdapter contactAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_friend);
        TemplateTitle title = (TemplateTitle) findViewById(R.id.chooseTitle);
        title.setMoreTextAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectList.size() == 0){
                    Toast.makeText(ChooseFriendActivity.this, getString(R.string.choose_need_one), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra("select", getSelectIds());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

//        mGroupListView = (ExpandableListView) findViewById(R.id.groupList);
        letterListView= (LetterListView) findViewById(R.id.groupList);
//        mGroupListAdapter = new ExpandGroupListAdapter(this, FriendshipInfo.getInstance().getGroups(), friends, true);
//        mGroupListView.setAdapter(mGroupListAdapter);
//        mGroupListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
//                onSelect(friends.get(FriendshipInfo.getInstance().getGroups().get(groupPosition)).get(childPosition));
//                mGroupListAdapter.notifyDataSetChanged();
//                return false;
//            }
//        });
//        mGroupListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        for (FriendProfile item : selectList){
            item.setIsSelected(false);
        }
    }

    private void onSelect(FriendProfile profile){
        if (!profile.isSelected()){
            selectList.add(profile);
        }else{
            selectList.remove(profile);
        }
        profile.setIsSelected(!profile.isSelected());
    }

    private ArrayList<String> getSelectIds(){
        ArrayList<String> result = new ArrayList<>();
        for (FriendProfile item : selectList){
            result.add(item.getIdentify());
        }
        return result;
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

        friendsList  = FriendshipInfo.getInstance().getFriends();

        Collections.sort(friendsList, new SortByName());

        contactAdapter = new ContactAdapter(friendsList,this,true,this);
        letterListView.setAdapter(contactAdapter);
        letterListView.setOnItemClickListener(contactAdapter);
    }

    @Override
    public void onClickFriendProfile(FriendProfile friendProfile) {
        onSelect(friendProfile);
        contactAdapter.notifyDataSetChanged();
    }
}
