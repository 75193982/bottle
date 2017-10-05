package com.bottle.com.bottle.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

import com.bottle.com.bottle.R;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017-09-30.
 */

public class ExpressionUtil {
    public static void dealExpression(Context context,SpannableString spannableString, Pattern patten, int start) throws SecurityException, NoSuchFieldException, NumberFormatException, IllegalArgumentException, IllegalAccessException {
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            if (matcher.start() < start) {
                continue;
            }
            try {
                Map<String, Integer> emojisMap = EmojiUtils.getEmojisMap();
                Integer resId = emojisMap.get(key);
                Drawable drawable = context.getResources().getDrawable(resId);
                drawable.setBounds(0, 0, 42, 42);
              //  Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                ImageSpan imageSpan = new ImageSpan(drawable);                //通过图片资源id来得到bitmap，用一个ImageSpan来包装
                int end = matcher.start() + key.length();                   //计算该图片名字的长度，也就是要替换的字符串的长度
                spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);   //将该图片替换字符串中规定的位置中
                if (end < spannableString.length()) {                        //如果整个字符串还未验证完，则继续。。
                    dealExpression(context,spannableString,  patten, end);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

             
        }
    }

    /**
     * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
     * @param context
     * @param str
     * @return
     */
    public static SpannableString getExpressionString(Context context, String str, String zhengze){
        SpannableString spannableString = new SpannableString(str);
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);        //通过传入的正则表达式来生成一个pattern
        try {
            dealExpression(context,spannableString, sinaPatten, 0);
        } catch (Exception e) {
            Log.e("dealExpression", e.getMessage());
        }
        return spannableString;
    }
}
