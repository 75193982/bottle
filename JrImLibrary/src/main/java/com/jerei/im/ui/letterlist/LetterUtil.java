package com.jerei.im.ui.letterlist;

import net.sourceforge.pinyin4j.PinyinHelper;


/**
 * 字母工具类
 *@Title:
 *@Description:
 *@Author:harlan
 *@Since:2014-5-8
 *@Version:
 */
public class LetterUtil
{
    /**
     * @param chinese 一个汉字
     * @return 拼音首字母
     * @Description:
     * @Author harlan
     * @Date 2014-5-8
     */
    public static String[] getFirstPinyin(char chinese)
    {
        return PinyinHelper.toHanyuPinyinStringArray(chinese);
    }

    /**
     * 是否是字母
     *
     * @return true 字母,false 非字母
     * @Description:
     * @Author harlan
     * @Date 2014-5-8
     */
    public static boolean isLetter(char c)
    {


        String str = "ABCDEFGHIZKLMNOPQRSTUVWXYZabcdefghizklmnopqrstuvwxyz@!$%^&*_-";

        return str.contains(c+"");
    }
}
