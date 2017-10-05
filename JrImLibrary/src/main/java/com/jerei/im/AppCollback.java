package com.jerei.im;

import android.app.Activity;

/**
 * Created by Administrator on 2016/9/8.
 */
public interface AppCollback {
    /**
     * 获取经度
     * @return
     */
    public double getLontitude();
    /**
     * 获取纬度
     * @return
     */
    public double getLatitude();

    /**
     * 获取地址
     * @return
     */
    public String getLocation();

    /**
     * 举报联系人
     */
    public void reportContacts(Activity activity,String id);

    /**
     * 获取未读数量
     * @param ucRead
     */
    public void getUnRead(int ucRead);
}
