package com.jerei.im.timchat.ui;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jerei.im.timchat.R;
import com.jerei.im.timchat.model.GroupInfo;
import com.jerei.im.ui.TemplateTitle;

public class ContactActivity extends FragmentActivity {


    RadioGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        TemplateTitle title = (TemplateTitle)findViewById(R.id.contact_antionbar);
        title.setMoreImgAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showMoveDialog();
                startActivity(new Intent(ContactActivity.this,MyAddFriendActivity.class));
            }
        });

        group = (RadioGroup) findViewById(R.id.radio_group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId ==R.id.radiobutton1){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.id_fragment_title,  new ContactFragment()).commit();
                }
                if(checkedId ==R.id.radiobutton2){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.id_fragment_title, GropListFragment.newInstance(GroupInfo.publicGroup)).commit();
                }

            }
        });
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.id_fragment_title,  new ContactFragment()).commit();
    }



    private Dialog inviteDialog;
    private TextView addFriend, managerGroup,addGroup;

    private void showMoveDialog() {
        inviteDialog = new Dialog(this, R.style.dialog);
        inviteDialog.setContentView(R.layout.contact_more);
        addFriend = (TextView) inviteDialog.findViewById(R.id.add_friend);
//        managerGroup = (TextView) inviteDialog.findViewById(R.id.manager_group);
//        addGroup = (TextView) inviteDialog.findViewById(R.id.add_group);
        //TODO 1989   添加好友 添加分组 等
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactActivity.this, SearchFriendActivity.class);
                startActivity(intent);
                inviteDialog.dismiss();
            }
        });
//        managerGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ContactActivity.this, ManageFriendGroupActivity.class);
//                startActivity(intent);
//                inviteDialog.dismiss();
//            }
//        });
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, SearchGroupActivity.class);
               startActivity(intent);
                inviteDialog.dismiss();
            }
        });
        Window window = inviteDialog.getWindow();
        window.setGravity(Gravity.TOP | Gravity.RIGHT);
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        inviteDialog.show();
    }
}
