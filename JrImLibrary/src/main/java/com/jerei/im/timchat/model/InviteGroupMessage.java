package com.jerei.im.timchat.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.jerei.im.IMContext;
import com.jerei.im.timchat.R;
import com.jerei.im.timchat.adapters.ChatAdapter;
import com.jerei.im.timchat.ui.ApplyGroupActivity;
import com.jerei.im.timchat.utils.BitmapCompressTools;
import com.tencent.TIMCustomElem;
import com.tencent.TIMFaceElem;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * 邀请群组消息
 */
public class InviteGroupMessage extends Message {


    private String TAG = getClass().getSimpleName();

    private final int TYPE_TYPING = 14;

    private Type type;
    private String desc;
    private String data;



    public InviteGroupMessage(TIMMessage message){
        this.message = message;
        TIMCustomElem elem = (TIMCustomElem) message.getElement(0);
        parse(elem.getData());

    }




    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    private void parse(byte[] data){
        type = Type.INVALID;
        try{
            String str = new String(data, "UTF-8");
            JSONObject jsonObj = new JSONObject(str);
            int action = jsonObj.getInt("userAction");
            switch (action){
                case TYPE_TYPING:
                    type = Type.TYPING;
                    this.data = jsonObj.getString("actionParam");
                    if (this.data.equals("EIMAMSG_InputStatus_End")){
                        type = Type.INVALID;
                    }
                    break;
            }

        }catch (IOException | JSONException e){
            Log.e(TAG, "parse json error");

        }
    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder,final Context context) {
        clearView(viewHolder);



        TextView tv = new TextView(IMContext.getContext());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setTextColor(IMContext.getContext().getResources().getColor(isSelf() ? R.color.btn_blue : R.color.btn_blue));
            TIMCustomElem elem = (TIMCustomElem) message.getElement(0);
            data = new String(elem.getData());
       String[] messageStr = elem.getDesc().split("--");
        if(message.isSelf()){
            tv.setText(messageStr[0]);
        }else {
            if(messageStr.length==1){
                tv.setText(messageStr[0]);
            }else {
                tv.setText(messageStr[1]);
            }

        }

        getBubbleView(viewHolder).addView(tv);
        showStatus(viewHolder);
        if(!message.isSelf()){
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(((Activity) context), ApplyGroupActivity.class);
                    intent.putExtra("identify",data);
                    ((Activity) context).startActivity(intent);
                }
            });
        }

    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        TIMCustomElem elem = (TIMCustomElem) message.getElement(0);

        String[] messageStr = elem.getDesc().split("--");
        if(message.isSelf()){
            return messageStr[0];
        }else {
            return messageStr[1];
        }

    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    public enum Type{
        TYPING,
        INVALID,
    }
}
