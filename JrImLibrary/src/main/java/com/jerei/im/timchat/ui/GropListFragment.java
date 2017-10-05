package com.jerei.im.timchat.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.jerei.im.presentation.event.GroupEvent;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.adapters.ContactAdapter;
import com.jerei.im.timchat.adapters.ProfileSummaryAdapter;
import com.jerei.im.timchat.model.FriendProfile;
import com.jerei.im.timchat.model.GroupInfo;
import com.jerei.im.timchat.model.GroupProfile;
import com.jerei.im.timchat.model.ProfileSummary;
import com.jerei.im.ui.TemplateTitle;
import com.tencent.TIMConversationType;
import com.tencent.TIMGroupCacheInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class GropListFragment extends Fragment implements Observer {


    private ProfileSummaryAdapter adapter;
    private ListView listView;
    private String type  =GroupInfo.publicGroup;
    private List<ProfileSummary> list;
    private List<ProfileSummary> listAll;
    private final int CREATE_GROUP_CODE = 100;
    EditText search;
    public GropListFragment() {

    }


    public static GropListFragment newInstance(String type) {
        GropListFragment fragment = new GropListFragment();

        fragment.setType(type);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_grop_list, container, false);
        search = (EditText) view.findViewById(R.id.search);
        listView =(ListView) view.findViewById(R.id.list);
        list = new ArrayList<>();
        listAll = GroupInfo.getInstance().getGroupListByType(type);
        list.addAll(listAll);

        adapter = new ProfileSummaryAdapter(getActivity(), R.layout.item_profile_summary, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                list.get(position).onClick(getActivity());
                ChatActivity.navToChat(getActivity(),list.get(position).getIdentify(), TIMConversationType.Group);

            }
        });


        GroupEvent.getInstance().addObserver(this);



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
                list.clear();
                if(TextUtils.isEmpty(str)){

                    list.addAll(listAll);
                    adapter.notifyDataSetChanged();
                    return;
                }


                for(ProfileSummary profileSummary:listAll){
                    if(!TextUtils.isEmpty(profileSummary.getName())&&profileSummary.getName().contains(str)){
                        list.add(profileSummary);
                        adapter.notifyDataSetChanged();
                    }
                }

            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GroupEvent.getInstance().deleteObserver(this);
    }

    public void update(Observable observable, Object data) {
        if (observable instanceof GroupEvent){
            if (data instanceof GroupEvent.NotifyCmd){
                GroupEvent.NotifyCmd cmd = (GroupEvent.NotifyCmd) data;
                switch (cmd.type){
                    case DEL:
                        delGroup((String) cmd.data);
                        break;
                    case ADD:
                        addGroup((TIMGroupCacheInfo) cmd.data);
                        break;
                    case UPDATE:
                        updateGroup((TIMGroupCacheInfo) cmd.data);
                        break;
                }
            }
        }
    }

    private void delGroup(String groupId){

        Iterator<ProfileSummary> it2 = list.iterator();
        while (it2.hasNext()){
            ProfileSummary item = it2.next();
            if (item.getIdentify().equals(groupId)){
                it2.remove();
            }
        }

        Iterator<ProfileSummary> it = list.iterator();
        while (it.hasNext()){
            ProfileSummary item = it.next();
            if (item.getIdentify().equals(groupId)){
                it.remove();
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }


    private void addGroup(TIMGroupCacheInfo info){
        if (info!=null && info.getGroupInfo().getGroupType().equals(type)){
            GroupProfile profile = new GroupProfile(info);
            list.add(profile);
            listAll.add(profile);
            adapter.notifyDataSetChanged();
        }

    }

    private void updateGroup(TIMGroupCacheInfo info){
        delGroup(info.getGroupInfo().getGroupId());
        addGroup(info);
    }

    public void setType(String type) {
        this.type = type;
    }
}
