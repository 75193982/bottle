package com.jerei.im.timchat.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jerei.im.timchat.R;
import com.jerei.im.timchat.model.FriendProfile;
import com.jerei.im.timchat.utils.GetHeadImageUtil;
import com.jerei.im.ui.letterlist.LetterBaseListAdapter;
import com.tencent.TIMUserProfile;

import java.util.List;

/**
 * Created by Administrator on 2016/8/11.
 */
public class ContactAdapter extends LetterBaseListAdapter<FriendProfile> implements AdapterView.OnItemClickListener {

    private Context context;

    private boolean selectable;

    private ContactCall contactCall;

    public ContactAdapter(List<FriendProfile> dataList, Context context,ContactCall contactCall) {
        this(dataList, context, false,contactCall);

    }

    public ContactAdapter(List<FriendProfile> dataList, Context context, boolean selectable,ContactCall contactCall) {
        super(dataList);

        this.context = context;
        this.selectable = selectable;
        this.contactCall = contactCall;
        setContainerList(dataList);
    }

    @Override
    public String getItemString(FriendProfile friendProfile) {
        return friendProfile.getName();
    }

    @Override
    public FriendProfile create(char letter) {
        //生成一个 对象 用于标记 是否是字母
        FriendProfile ff = new FriendProfile(null);
        ff.setFirstLetter(String.valueOf(letter));
        return ff;
    }

    @Override
    public boolean isLetter(FriendProfile friendProfile) {
        return friendProfile.getProfile() == null;
    }

    @Override
    public View getLetterView(int position, View convertView, ViewGroup parent) {
        //这里是字母的item界面设置.
        if (convertView == null) {
            convertView = new TextView(context);
            ((TextView) convertView).setGravity(Gravity.CENTER_VERTICAL);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.background));
        }
        ((TextView) convertView).setText("   "+list.get(position).getFirstLetter());



        return convertView;
    }

    @Override
    public View getContainerView(int position, View convertView, ViewGroup parent) {
        ChildrenHolder itemHolder;
        if (convertView == null) {
            itemHolder = new ChildrenHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_childmember, null);
            itemHolder.tag = (ImageView) convertView.findViewById(R.id.chooseTag);
            itemHolder.name = (TextView) convertView.findViewById(R.id.name);
            itemHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ChildrenHolder) convertView.getTag();
        }
        FriendProfile data = list.get(position);
        itemHolder.name.setText(data.getName());
        itemHolder.tag.setVisibility(selectable ? View.VISIBLE : View.GONE);
        itemHolder.tag.setImageResource(data.isSelected() ? R.drawable.selected : R.drawable.unselected);

        GetHeadImageUtil.setImage(itemHolder.avatar, data.getIdentify(),context);
        return convertView;
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        contactCall.onClickFriendProfile(list.get(i));
    }


    class ChildrenHolder {
        public TextView name;
        public ImageView avatar;
        public ImageView tag;

    }

    public static interface ContactCall{
        void onClickFriendProfile(FriendProfile friendProfile);
    }


}
