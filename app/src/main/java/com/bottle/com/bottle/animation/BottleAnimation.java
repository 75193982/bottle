package com.bottle.com.bottle.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bottle.com.bottle.R;

/**
 * Created by Administrator on 2017-09-30.
 */

public class BottleAnimation {
    private Button begin ;
    private TextView finishTV;
    private TextView startTV;
    private ViewGroup anim_mask_layout;//动画层
    private Activity activity ;

    /**
     * 抛物线 动画
     *
     */
    @TargetApi(11)
    public void playReadingAnimation(Activity activity,View startTV ,View finishTV ) {
        this.activity =activity ;
        final ImageView moveingIV = (ImageView) activity.getLayoutInflater().inflate(R.layout.animator_image, null);
        anim_mask_layout = createAnimLayout();
        anim_mask_layout.addView(moveingIV);//把动画图片添加到动画层
        //获取起点坐标
        int[] start_location = new int[2];
        startTV.getLocationInWindow(start_location);
        final int x1 = start_location[0];
        final int y1 = start_location[1];
        //设置动画图片的起始位置
        addViewToAnimLayout(moveingIV, start_location);

        //获取终点坐标，最近拍摄的坐标
        int[] location = new int[2];
        finishTV.getLocationInWindow(location);
        final int x2 = location[0];
        final int y2 = location[1];
        //此处控制偏移量
        int offsetX;
        int offsetY;
        offsetX = 0;//未做偏移，需要的自己计算
        offsetY = 0;
        //抛物线动画，原理：两个位移动画，一个横向匀速移动，一个纵向变速移动，两个动画同时执行，就有了抛物线的效果。
        ObjectAnimator translateAnimationX = ObjectAnimator.ofFloat(moveingIV, "translationX", 0, -(x1 - x2) - offsetX);
        translateAnimationX.setDuration(1500);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        ObjectAnimator translateAnimationY = ObjectAnimator.ofFloat(moveingIV, "translationY", 0, y2 - y1 + offsetY);
        translateAnimationY.setDuration(1500);
        translateAnimationY.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator animator = ObjectAnimator.ofFloat(moveingIV,"rotation",0,-1080);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1500);
       //animator.setRepeatCount(-1);
        //到终点后的缩小动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(moveingIV, "scaleX", 1.7f, 1.1f);
        scaleX.setDuration(1500);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(moveingIV, "scaleY", 1.7f, 1.1f);
        scaleY.setDuration(1500);
        scaleY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                moveingIV.setImageResource(R.drawable.animation);
               // moveingIV.setBackgroundResource(R.drawable.animation);
                AnimationDrawable animationDrawable = (AnimationDrawable) moveingIV.getDrawable();
                animationDrawable.start();
                int duration = 0;
                for(int i=0;i<animationDrawable.getNumberOfFrames();i++){
                    duration += animationDrawable.getDuration(i);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        anim_mask_layout.removeView(moveingIV); //动画结束后移除动画图片
                    }
                }, duration);

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();//设置动画播放顺序

        animatorSet .play(translateAnimationX ).with( animator).with( scaleY).with( scaleX).with(translateAnimationY) ;
       // animatorSet.play(translateAnimationX).with(translateAnimationY);
       //animatorSet.play(scaleX).after(translateAnimationX);

        animatorSet.start();
    }

    /**
     *  创建动画层
     *
     */
    private ViewGroup createAnimLayout() {
        ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        int a=Integer.MAX_VALUE;
        animLayout.setId(a);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    private void addViewToAnimLayout(final View view,
                                     int[] location) {
        int x = location[0];
        int y = location[1];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setLayoutParams(lp);
    }

}
