package com.jerei.im.timchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.model.Message;
import com.jerei.im.timchat.model.UserInfo;
import com.jerei.im.timchat.utils.GetHeadImageUtil;
import com.jerei.im.ui.RoundCornerImageView;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.List;

/**
 * 聊天界面adapter
 *
 *
 */
public class ChatAdapter extends ArrayAdapter<Message> {

    private final String TAG = "ChatAdapter";

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
    public ChatAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
        resourceId = resource;
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
     final     ViewHolder viewHolder;
        if (convertView != null){
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }else{
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftMessage = (RelativeLayout) view.findViewById(R.id.leftMessage);
            viewHolder.rightMessage = (RelativeLayout) view.findViewById(R.id.rightMessage);
            viewHolder.leftPanel = (RelativeLayout) view.findViewById(R.id.leftPanel);
            viewHolder.rightPanel = (RelativeLayout) view.findViewById(R.id.rightPanel);
            viewHolder.sending = (ProgressBar) view.findViewById(R.id.sending);
            viewHolder.error = (ImageView) view.findViewById(R.id.sendError);
            viewHolder.sender = (TextView) view.findViewById(R.id.sender);
            viewHolder.rightDesc = (TextView) view.findViewById(R.id.rightDesc);
            viewHolder.systemMessage = (TextView) view.findViewById(R.id.systemMessage);
            viewHolder.leftAvatar = (RoundCornerImageView) view.findViewById(R.id.leftAvatar);
            viewHolder.rightAvatar = (RoundCornerImageView) view.findViewById(R.id.rightAvatar);
            view.setTag(viewHolder);
        }

//        viewHolder.leftMessage.setBackgroundResource(R.drawable.bg_bubble_gray);
//        viewHolder.rightMessage.setBackgroundResource(R.drawable.bg_bubble_blue);
        final Message data = getItem(position);
        data.showMessage(viewHolder, getContext());
        if(data.isSelf()){
            GetHeadImageUtil.setImageUrl(viewHolder.rightAvatar, UserInfo.getInstance().getUrl(),context);
        }else {
            GetHeadImageUtil.setImage(viewHolder.leftAvatar, data.getSender(),context);
        }

        return view;
    }


    public class ViewHolder{
        public RelativeLayout leftMessage;
        public RelativeLayout rightMessage;
        public RelativeLayout leftPanel;
        public RelativeLayout rightPanel;
        public ProgressBar sending;
        public ImageView error;
        public TextView sender;
        public TextView systemMessage;
        public TextView rightDesc;
        public RoundCornerImageView leftAvatar;
        public RoundCornerImageView rightAvatar;

    }
}
