package com.bottle.com.bottle.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bottle.com.bottle.R;
import com.bottle.com.bottle.activity.ChatActivity;
import com.bottle.com.bottle.activity.ChatsActivity;
import com.bottle.com.bottle.adapter.BottleAdapter;
import com.bottle.com.bottle.animation.BottleAnimation;
import com.dou361.dialogui.DialogUIUtils;
import com.jude.utils.JUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by lenovo on 2017/9/1.
 */

public class BottleFragment extends BaseFragment implements BottleAdapter.OnItemClickListener, View.OnClickListener {
    private  List<String> list =new ArrayList<String>();
    private RelativeLayout frameLayout ;
    private  View rootView;
    private BottleAdapter bottleAdapter ;
    private com.bottle.com.bottle.activity.RandomLayout randomLayout ;
    private int screenHeight ;//屏幕高度
    @BindView(R.id.startView)
    View startView;
    @BindView(R.id.iv_land)
    ImageView iv_land;
    @BindView(R.id.endView)
    View endView;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View initView() {
        View view =  View.inflate(mActivity, R.layout.fragment_bottle,null);
        ButterKnife.bind(this,view);
        frameLayout = (RelativeLayout)view.findViewById(R.id.fl_bottle);
        final ImageView iv_sky = (ImageView)view.findViewById(R.id.iv_sky);
        ViewTreeObserver vto = iv_sky.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                iv_sky.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                final int height_sky = iv_sky.getHeight();

                ViewTreeObserver vto = iv_land.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        iv_land.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        int height = iv_land.getHeight();
                        int  iv_land_height = (int) (height*0.7);
                        final int landHeight  = height - iv_land_height ;
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)iv_land.getLayoutParams();
                        lp.setMargins(0, (int) (height_sky - iv_land_height), 0, 0);
                        iv_land.setLayoutParams(lp);

                        ViewTreeObserver vto = frameLayout.getViewTreeObserver();
                        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                frameLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                int height = frameLayout.getHeight();
                                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)frameLayout.getLayoutParams();
                                lp.setMargins(0, (int) (landHeight), 0, 0);
                                frameLayout.setLayoutParams(lp);
                                randomLayout = new com.bottle.com.bottle.activity.RandomLayout(getContext());
                                randomLayout.setRegularity(11, 11);
                                int paddingLeft = 15;
                                randomLayout.setPadding(paddingLeft, paddingLeft, paddingLeft, paddingLeft);
                                Random random = new Random();
                                int i = random.nextInt(15);
                                bottleAdapter = new BottleAdapter(i <= 8 ? 8: i ,mActivity);
                                bottleAdapter.setOnItemClickListener(BottleFragment.this);
                                randomLayout.setAdapter(bottleAdapter);
                                randomLayout.refresh();
                                randomLayout.setVisibility(View.VISIBLE);
                                frameLayout.addView(randomLayout);
                            }
                        });
                    }
                });

            }
        });





        return view;
    }

    int y ;
    @Override
    public void onResume() {

        super.onResume();
        screenHeight = JUtils.getScreenHeight();

    }

    @Override
    public void initData() {
        super.initData();


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onItemClick(View view, int position) {
        randomLayout.removeView(view);
      /*rootView = View.inflate(getContext(), R.layout.chat_dialog, null);
        ImageButton ib_speak = (ImageButton) rootView.findViewById(R.id.ib_speak);
        ib_speak.setOnClickListener(this);
        ViewTreeObserver vtos = rootView.getViewTreeObserver();
        vtos.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)rootView.getLayoutParams();
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(layoutParams);
                params.height  = (int) (screenHeight*0.55);
                rootView.setLayoutParams(params);
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

       Dialog dialog = DialogUIUtils.showCustomAlert(getContext(), rootView, Gravity.CENTER, false, true).show();*/


          Intent intent = new Intent(getActivity(), ChatsActivity.class);
         startActivityForResult(intent,100);
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        BottleAnimation bottleAnimation = new BottleAnimation();
        bottleAnimation.playReadingAnimation(getActivity(),startView,endView);
    }
}
