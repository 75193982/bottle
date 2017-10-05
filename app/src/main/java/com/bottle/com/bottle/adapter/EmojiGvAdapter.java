package com.bottle.com.bottle.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bottle.com.bottle.R;
import com.bottle.com.bottle.domain.Emojis;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.jude.utils.JUtils;

import java.util.List;


/**
 * Created by dell on 2016/10/13.
 */
public class EmojiGvAdapter extends BaseAdapter {
    private Context mContext;
    private List<Emojis> mEmojis;

    public EmojiGvAdapter(Context context, List<Emojis> eachPageEmojis) {
        this.mContext = context;
        this.mEmojis = eachPageEmojis;
    }

    @Override
    public int getCount() {
        return null == mEmojis ? 0 : mEmojis.size();
    }

    @Override
    public Emojis getItem(int position) {
        return null == mEmojis ? null : mEmojis.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();

            convertView = View.inflate(mContext, R.layout.item_emoji, null);
            holder.emojiTv = (ImageView) convertView.findViewById(R.id.tv_emoji);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == 27) {
            //第28个显示删除按钮
            holder.emojiTv.setBackgroundResource(R.mipmap.ic_emojis_delete);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.emojiTv.getLayoutParams();
            lp.bottomMargin = (int) mContext.getResources().getDimension(R.dimen.dp12);
        } else {
            // holder.emojiTv.setText(getItem(position));
            Emojis item = getItem(position);
            if(null != item ){

                ViewHolder finalHolder1 = holder;
                Glide.with(mContext)                     //配置上下文
                        .load(item.id)                  //设置图片路径
                        .asBitmap()
                       /* .error(R.mipmap.default_image)           //设置错误图片
                        .placeholder(R.mipmap.default_image)     //设置占位图片*/
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                        .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                            @Override
                            public void onResourceReady(Bitmap bitmap, @SuppressWarnings("rawtypes") GlideAnimation glideAnimation) {
                                finalHolder1.emojiTv.setImageBitmap(bitmap);
                            }
                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);
                                BitmapDrawable bd = (BitmapDrawable) errorDrawable;
                                finalHolder1.emojiTv.setImageBitmap( bd.getBitmap());
                            }
                        });
            }else{
                Glide.with(mContext)
                        .load(R.mipmap.emotion_001)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.emojiTv);
            }

        }
        ViewHolder finalHolder = holder;

        int  height =  (int) (JUtils.getScreenHeight()*0.3)/4;
        ViewGroup.LayoutParams layoutParams = finalHolder.emojiTv.getLayoutParams();
        layoutParams.height = height;
        finalHolder.emojiTv.setLayoutParams(layoutParams);
        return convertView;
    }

    private static class ViewHolder {
        private ImageView emojiTv;
    }


}
