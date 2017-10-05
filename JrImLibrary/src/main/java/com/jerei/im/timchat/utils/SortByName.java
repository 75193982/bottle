package com.jerei.im.timchat.utils;

import com.jerei.im.timchat.model.FriendProfile;

import java.util.Comparator;

/**
 * Created by zhush on 2016/8/11.
 */
public class SortByName implements Comparator {
    @Override
    public int compare(Object o, Object t1) {
        FriendProfile s1 = (FriendProfile) o;
        GetFirstLetterUtil.getFirstLetter(s1);
        FriendProfile s2 = (FriendProfile) t1;
        GetFirstLetterUtil.getFirstLetter(s2);
        return s1.getFirstLetter().compareTo(s2.getFirstLetter());
    }
}
