package com.bottle.com.bottle.activity;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Button;


import com.bottle.com.bottle.Fragment.BottleFragment;
import com.bottle.com.bottle.Fragment.ChatFragment;
import com.bottle.com.bottle.Fragment.ConversationFragment;
import com.bottle.com.bottle.Fragment.MeFragment;
import com.bottle.com.bottle.R;
import com.bottle.com.bottle.view.DecoratorViewPager;
import com.kevin.tabindicator.TabPageIndicatorEx;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private TabPageIndicatorEx mTabPageIndicatorEx;
    private Button mChangeModeBtn;
    private List<Fragment> mTabs = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;

    private boolean isGradualChange;

    private String[] mTitles = new String[] { "First Fragment!",
            "Second Fragment!", "Third Fragment!" };

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initEvents();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mTabPageIndicatorEx = (TabPageIndicatorEx) findViewById(R.id.tabpage_act_tpi);


        initTabIndicator();
        initViewPager();
    }

    private void initViewPager() {

        BottleFragment BottleFragment = new BottleFragment();
        mTabs.add(BottleFragment);
        ConversationFragment ChatFragment = new ConversationFragment();
        mTabs.add(ChatFragment);
        MeFragment MeFragment = new MeFragment();
        mTabs.add(MeFragment);



        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mTabs.get(arg0);
            }
        };

        mViewPager.setAdapter(mAdapter);

    }

    /**
     * 初始化事件
     */
    private void initEvents() {
       /* mChangeModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTabPageIndicatorEx.setIsGradualChange(!isGradualChange);
                isGradualChange = !isGradualChange;
            }
        });*/
        mTabPageIndicatorEx.setIsGradualChange(true);
        mTabPageIndicatorEx.setOnTabSelectedListener(new TabPageIndicatorEx.OnTabSelectedListener() {

            @Override
            public void onTabSelected(int index) {
                mViewPager.setCurrentItem(index, false);
            }
        });

    }

    private void initTabIndicator() {
        mTabPageIndicatorEx.setViewPager(mViewPager);
        mTabPageIndicatorEx.setIndicateDisplay(2, true);
    }
}
