package com.jerei.im.timchat.model;


import android.text.TextUtils;

import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;

/**
 * 消息工厂
 */
public class MessageFactory {

    private MessageFactory() {}


    /**
     * 消息工厂方法
     */
    public static Message getMessage(TIMMessage message){
        switch (message.getElement(0).getType()){
            case Text:

            case Face:
                return new TextMessage(message);
            case Image:
                return new ImageMessage(message);
            case Sound:
                return new VoiceMessage(message);
            case Video:
                return new VideoMessage(message);
            case GroupTips:
                return new GroupTipMessage(message);
            case File:
                return new FileMessage(message);
            case Location:
                return new LocationMessage(message);
            case Custom:
                TIMCustomElem elem = (TIMCustomElem) message.getElement(0);
                if(!TextUtils.isEmpty(elem.getDesc())&&elem.getDesc().contains("邀请你加入群组")){
                    return new InviteGroupMessage(message);
                }
                return new CustomMessage(message);
            default:
                return null;
        }
    }



}
