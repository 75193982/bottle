package com.jerei.im.timchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jerei.im.timchat.R;
import com.jerei.im.timchat.model.ProfileSummary;
import com.jerei.im.timchat.utils.GetHeadImageUtil;
import com.jerei.im.ui.RoundCornerImageView;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;


/**
 * gridView适配器
 * Created by dxkj on 2015/9/14.
 */
public class GridViewAdapter extends BaseAdapter {

    private Context context;
    List<ProfileSummary> list ;
    boolean isManager;

    public GridViewAdapter(Context context, List<ProfileSummary> list,boolean isManager) {
        this.context = context;
        this.list = list;
        this.isManager = isManager;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        //如果是管理者 多出 加号 和减号 反之 只有加号
        if(isManager){
            return list.size()+2;
        }else {
            return list.size()+1;
        }

    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_layout, parent, false);
            holder.iv_goods_picture = (RoundCornerImageView) convertView.findViewById(R.id.iv_goods);
            holder.tv_goods_name = (TextView) convertView.findViewById(R.id.tv_goods);
            convertView.setTag(holder);
//            AutoUtils.autoSize(convertView);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if(position<list.size()){
            ProfileSummary profileSummary  = list.get(position);
            holder.tv_goods_name.setText(profileSummary.getName());
            GetHeadImageUtil.setImage(holder.iv_goods_picture, profileSummary.getIdentify(),context);
        }
        if(position==list.size()){
            holder.tv_goods_name.setText("");
            holder.iv_goods_picture.setTag("add_group");
            holder.iv_goods_picture.setImageResource(R.drawable.add_group);
        }

        if(position>list.size()){
            holder.iv_goods_picture.setTag("delete_group");
            holder.iv_goods_picture.setImageResource(R.drawable.delete_group);
            holder.tv_goods_name.setText("");
        }
        return convertView;
    }

    class Holder {
        TextView tv_goods_name;
        RoundCornerImageView iv_goods_picture;
    }


}
