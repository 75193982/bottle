package com.jerei.im.timchat.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jerei.im.timchat.R;
import com.jerei.im.timchat.model.Conversation;
import com.jerei.im.timchat.model.FriendshipConversation;
import com.jerei.im.timchat.model.Message;
import com.jerei.im.timchat.model.NomalConversation;
import com.jerei.im.timchat.model.SystemConversation;
import com.jerei.im.timchat.ui.ImageViewActivity;
import com.jerei.im.timchat.utils.BitmapCompressTools;
import com.jerei.im.timchat.utils.GetHeadImageUtil;
import com.jerei.im.timchat.utils.TimeUtil;
import com.jerei.im.ui.CircleImageView;

import com.tencent.TIMConversationType;
import com.tencent.TIMFaceElem;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 会话界面adapter
 */
public class ConversationAdapter extends ArrayAdapter<Conversation> {

    private int resourceId;
    private View view;


    private ConversationOperationlistening conversationOperationlistening;
    private Context context;
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ConversationAdapter(Context context, int resource, List<Conversation> objects, ConversationOperationlistening conversationOperationlistening) {
        super(context, resource, objects);
        resourceId = resource;
        this. context = context;
        this.conversationOperationlistening = conversationOperationlistening;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) view.findViewById(R.id.name);

            viewHolder.lastMessage = (TextView) view.findViewById(R.id.last_message);
            viewHolder.time = (TextView) view.findViewById(R.id.message_time);
            viewHolder.unread = (TextView) view.findViewById(R.id.unread_num);

            view.setTag(viewHolder);
        }
        final Conversation data = getItem(position);
        viewHolder.tvName.setText(data.getName());

        String lstMessage = data.getLastMessageSummary();
        viewHolder.lastMessage.setText(lstMessage);


        viewHolder.time.setText(TimeUtil.getTimeStr(data.getLastMessageTime()));
        viewHolder.avatar = (ImageView) view.findViewById(R.id.avatar);
        viewHolder.delete = (TextView) view.findViewById(R.id.delete);
        viewHolder.read = (TextView) view.findViewById(R.id.read);
        long unRead = data.getUnreadNum();
        if (unRead <= 0) {
            viewHolder.unread.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.unread.setVisibility(View.VISIBLE);
            String unReadStr = String.valueOf(unRead);
            if (unRead < 10) {
                viewHolder.unread.setBackground(getContext().getResources().getDrawable(R.drawable.point1));
            } else {
                viewHolder.unread.setBackground(getContext().getResources().getDrawable(R.drawable.point2));
                if (unRead > 99) {
                    unReadStr = getContext().getResources().getString(R.string.time_more);
                }
            }
            viewHolder.unread.setText(unReadStr);


        }

        if(data.getClass().getName().contains("SystemConversation")){
            viewHolder.avatar.setTag("SystemConversation");
            viewHolder.avatar.setImageResource(R.drawable.friend_news);
        }else {
            try{
                switch (data.getType()){
                    case C2C:
                        GetHeadImageUtil.setImage(viewHolder.avatar, data.getIdentify(),context);
                        break;
                    case Group:
                        GetHeadImageUtil.setImageGroup(viewHolder.avatar, data.getIdentify(),context);
                        break;
                    default:

                }

            }catch (Exception e){}
            //设置带表情的消息
            try{
            NomalConversation nomalConversation  = (NomalConversation) data;
            Message message = nomalConversation.getLastMessage();
            TIMMessage tiMMessage= message.getMessage();
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            for (int i = 0; i<tiMMessage.getElementCount(); ++i){
                switch (tiMMessage.getElement(i).getType()){
                    case Face:
                        TIMFaceElem faceElem = (TIMFaceElem) tiMMessage.getElement(i);
                        int startIndex = stringBuilder.length();
                        try{
                            AssetManager am = context.getAssets();
                            InputStream is = am.open(String.format("emoticon/%d.gif", faceElem.getIndex()));
                            if (is == null) continue;
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            Matrix matrix = new Matrix();
                            int width = bitmap.getWidth();
                            int height = bitmap.getHeight();
                            matrix.postScale(2, 2);
                            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                    width, height, matrix, true);
                            bitmap.recycle();
                            ImageSpan span = new ImageSpan(context, BitmapCompressTools.zoomImage(resizedBitmap,viewHolder.lastMessage.getHeight(),viewHolder.lastMessage.getHeight()) , ImageSpan.ALIGN_BASELINE);
                            stringBuilder.append(String.valueOf(faceElem.getIndex()));
                            stringBuilder.setSpan(span, startIndex, startIndex + getNumLength(faceElem.getIndex()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            is.close();

                            resizedBitmap.recycle();
                        }catch (IOException e){
                        }
                        viewHolder.lastMessage.setText(stringBuilder);
                        break;
                    case Text:
                        TIMTextElem textElem = (TIMTextElem) tiMMessage.getElement(i);
                        stringBuilder.append(textElem.getText());
                        viewHolder.lastMessage.setText(stringBuilder);
                        break;
                    default:

                        viewHolder.lastMessage.setText(data.getLastMessageSummary());
                }

            }

            }catch (Exception e){}
        }

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conversationOperationlistening.delete(position);
            }
        });


        viewHolder.read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conversationOperationlistening.readMessage(position);
            }
        });
        return view;
    }

    public class ViewHolder {
        public TextView tvName;
        public ImageView avatar;
        public TextView lastMessage;
        public TextView time;
        public TextView unread;
        public TextView delete;
        public TextView read;
    }

    public static interface ConversationOperationlistening {
        public void delete(int i);

        public void readMessage(int i);
    }

    private int getNumLength(int n){
        return String.valueOf(n).length();
    }
}
