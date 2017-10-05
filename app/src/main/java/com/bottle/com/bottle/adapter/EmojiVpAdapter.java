package com.bottle.com.bottle.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.bottle.com.bottle.R;
import com.bottle.com.bottle.domain.Emojis;
import com.bottle.com.bottle.utils.EmojiUtils;
import com.bottle.com.bottle.utils.MyGridView;



import java.io.BufferedReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by dell on 2016/10/13.
 */
public class EmojiVpAdapter extends PagerAdapter {
    private Context mContext;
    private List<Emojis> mEmojis;//216个表情字符
    private List<View> mPagers;//展示的页面
    private OnEmojiClickListener mEmojiClickListener;//表情点击监听接口

    public EmojiVpAdapter(Context ctx) {
        this.mContext = ctx;
        this.mEmojis = getEmojis();
        this.mPagers = getPagerList();

    }

    @Override
    public int getCount() {
        return null == mPagers ? 0 : mPagers.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mPagers.get(position);
        if (null != view) {
            container.addView(view);
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * 从assets目录下获取所有表情
     *
     * @return
     */
    public List<Emojis> getEmojis() {
        BufferedReader br = null;
        List<Emojis> emojis = null;

                /*InputStream is = mContext.getAssets().open("emoji.json");
                StringBuffer sb = new StringBuffer();
                br = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while (null != (line = br.readLine())) {
                    sb.append(line).append("\r\n");
                }
                JSONArray emojiArray = new JSONArray(sb.toString());
                if (null != emojiArray && emojiArray.length() > 0) {
                    emojis = new String[emojiArray.length()];
                    for (int i = 0; i < emojiArray.length(); i++) {
                        emojis[i] = emojiArray.optString(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != br) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/


        return EmojiUtils.getInstance();
    }

    /**
     * 获取所有表情GridView页面的集合
     *
     * @return
     */
    public List<View> getPagerList() {
        List<View> pagers = null;
        List<Emojis> eachPageEmojis = null;
        if (null != mEmojis && mEmojis.size() > 0) {
            pagers = new ArrayList<>();
            int pageCount = mEmojis.size() / 27;//共8页表情
            for (int i = 1; i <= pageCount; i++) {
                eachPageEmojis = new ArrayList<Emojis>();
                MyGridView gridView = new MyGridView(mContext);
                gridView.setNumColumns(7);
                gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
                gridView.setVerticalScrollBarEnabled(false);
                gridView.setCacheColorHint(Color.TRANSPARENT);
                gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
                gridView.setGravity(Gravity.CENTER);
                //gridView.setOverScrollMode(View.OVER_SCROLL_NEVER);

                //总共216个表情字符,索引变化为:0-26,27-53,54-80,81-107,108-134,135-161,162-188,189-215
                //eachPageEmojis.add((Emojis) mEmojis.subList(i * 27,(i * 27)+27));

                //System.arraycopy(mEmojis, i * 27, eachPageEmojis, 0, 27);
                for (int j = (i-1)*27; j <i * 27 ; j++) {
                    eachPageEmojis.add(mEmojis.get(j));
                }
                Emojis emojis = new Emojis();
                emojis.name="del";
                emojis.id = R.mipmap.ic_emojis_delete;
               // eachPageEmojis[27] = "del";//第28是删除按钮,用特殊字符串表示
                eachPageEmojis.add(emojis);
                gridView.setAdapter(new EmojiGvAdapter(mContext, eachPageEmojis));
                //点击表情监听
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //获取选中的表情字符
                        Emojis emoji = (Emojis) parent.getAdapter().getItem(position);
                        if (null != mEmojiClickListener) {
                            mEmojiClickListener.onClick(emoji);
                        }
                    }
                });
                pagers.add(gridView);
            }
        }
        return pagers;
    }

    /**
     * 关联指示器点
     *
     * @param viewPager
     * @param pointLayout
     */
    public void setupWithPagerPoint(ViewPager viewPager, final LinearLayout pointLayout) {
        //初始表情指示器
        int pageCount = getCount();
        for (int i = 0; i < pageCount; i++) {
            ImageView point = new ImageView(mContext);
            point.setImageResource(R.drawable.shape_vp_dot_unselected);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
            params.rightMargin = (int) mContext.getResources().getDimension(R.dimen.dp10);
            if (i == 0) {
                point.setImageResource(R.drawable.shape_vp_dot_selected);
            }
            pointLayout.addView(point, params);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //切换指示器
                if (null != pointLayout && pointLayout.getChildCount() > 0) {
                    for (int i = 0; i < pointLayout.getChildCount(); i++) {
                        ((ImageView) pointLayout.getChildAt(i)).setImageResource(R.drawable.shape_vp_dot_unselected);
                    }
                    ((ImageView) pointLayout.getChildAt(position)).setImageResource(R.drawable.shape_vp_dot_selected);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 表情点击监听器
     */
    public interface OnEmojiClickListener {
        void onClick(Emojis emoji);
    }

    public void setOnEmojiClickListener(OnEmojiClickListener l) {
        this.mEmojiClickListener = l;
    }
}
