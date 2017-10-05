package com.jerei.im.timchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jerei.im.timchat.R;
import com.jerei.im.timchat.model.GroupProfile;
import com.jerei.im.timchat.model.ProfileSummary;
import com.jerei.im.timchat.utils.GetHeadImageUtil;
import com.jerei.im.ui.CircleImageView;
import com.jerei.im.ui.RoundCornerImageView;

import java.util.List;

/**
 * 好友或群等资料摘要列表的adapter
 */
public class ProfileSummaryAdapter extends ArrayAdapter<ProfileSummary> {


    private int resourceId;
    private View view;

    private Context context;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ProfileSummaryAdapter(Context context, int resource, List<ProfileSummary> objects) {
        super(context, resource, objects);
    resourceId = resource;
        this.context = context;
}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.avatar = (RoundCornerImageView) view.findViewById(R.id.avatar);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.des = (TextView) view.findViewById(R.id.description);
            view.setTag(viewHolder);
        }
        ProfileSummary data = getItem(position);
        viewHolder.avatar.setImageResource(data.getAvatarRes());
        viewHolder.name.setText(data.getName());
        if(data.getClass().getName().contains("GroupProfile")){

            GetHeadImageUtil.setImageGroup(viewHolder.avatar, data.getIdentify(),context);
        }else {
            GetHeadImageUtil.setImageUrl(viewHolder.avatar, data.getAvatarUrl(),context);
        }



        return view;
    }


    public class ViewHolder{
        public RoundCornerImageView avatar;
        public TextView name;
        public TextView des;
    }
}
