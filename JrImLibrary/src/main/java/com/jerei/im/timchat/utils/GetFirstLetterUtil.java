package com.jerei.im.timchat.utils;

import android.text.TextUtils;
import android.util.Log;

import com.jerei.im.timchat.model.FriendProfile;
import com.jerei.im.ui.letterlist.LetterUtil;

/**
 * Created by zhush on 2016/8/11.
 */
public class GetFirstLetterUtil {

    public static void getFirstLetter(FriendProfile friendProfile){
        if(TextUtils.isEmpty(friendProfile.getName())){
            friendProfile.setFirstLetter("1");
            Log.e("meiyoumingzi",friendProfile.getIdentify());
        }else {
            friendProfile.setFirstLetter(getHeaderLetter(friendProfile.getName())+"");
        }
    }


    private static String getHeaderLetter(String str)
    {


        char l;
        //获取第一个字母
        char firstChar = str.charAt(0);
        if(LetterUtil.isLetter(firstChar))
            {
            l = firstChar;//如果是头,尾,字母,直接赋值
        }
        else
        {
            String[] letterArray = LetterUtil.getFirstPinyin(firstChar);
            //如果是汉字,取拼音首字母
            if(letterArray != null && letterArray.length > 0)
            {
                l = letterArray[0].charAt(0);
            }
            else
            {

                return "A";
            }
        }

        //如果是小写字母,转换为大写字母
//        if(l >= 'a')
//        {
//            l = (char) (l - 32);
//        }
        return (l+"").toUpperCase();
    }
}
