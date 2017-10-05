package com.bottle.com.bottle.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.bottle.com.bottle.R;
import com.bottle.com.bottle.Fragment.BottleFragment;
import com.bumptech.glide.Glide;


/**
 * Created by lenovo on 2017/9/3.
 */

public class BottleAdapter implements   com.bottle.com.bottle.activity.RandomLayout.Adapter , View.OnClickListener {
    int size ;
    private Context mContext ;
    private OnItemClickListener onItemClickListener;
    public BottleAdapter(int size,Context mContext) {
        this.size = size ;
        this.mContext = mContext ;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public View getView(int position, View convertView) {
        View view;
        if (convertView == null)
        {
           view = View.inflate(mContext, R.layout.item_bottle, null);

        } else
        {
            view = convertView;
        }
        ImageView bottle_h =(ImageView) view.findViewById(R.id.bottle_h);
        view.setTag(position);
        ObjectAnimator icon_anim = ObjectAnimator.ofFloat(view, "rotation", 0.0F, 0,8,0);
        icon_anim.setRepeatCount(-1); icon_anim.setDuration(4000);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        icon_anim.setInterpolator(interpolator); //设置匀速旋转，不卡顿
         icon_anim.start();

        Glide.with(mContext)
                .load(R.mipmap.bottle_h)

                .crossFade()
                .into(bottle_h);
         view.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if( null != onItemClickListener ){
            onItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    public void setOnItemClickListener(BottleFragment onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener{
        public void onItemClick(View view,int position);

    }

}
