package com.bottle.com.bottle.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bottle.com.bottle.R;
import com.bottle.com.bottle.adapter.EmojiVpAdapter;
import com.bottle.com.bottle.domain.Emojis;
import com.bottle.com.bottle.utils.EmotionKeyboardSwitchHelper;
import com.bottle.com.bottle.utils.ExpressionUtil;
import com.bottle.com.bottle.utils.KeyBoardUtils;
import com.bottle.com.bottle.utils.KeyboardChangeListener;
import com.jude.utils.JUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by lenovo on 2017/9/23.
 */

public class ChatsActivity extends Activity implements View.OnClickListener, TextWatcher {
    private int screenWidth;
    private View rootView;
    private ScrollView scrollView ;
    private EditText et_info ;
    private ImageButton ib_face ;
    private RelativeLayout  mEmojiFl;
    private ViewPager mEmojiVp;
    private LinearLayout mVpPointLl ;
    private LinearLayout  rootView1;
    private EmotionKeyboardSwitchHelper mEmotionKeyboardSwitchHelper;
    private  RelativeLayout   btn_onkey ,rl_head;
    private  int scrollViewHeight ;
    private Context mContext;
    private ViewTreeObserver.OnScrollChangedListener onScrollChangedListener;
    @BindView(R.id.sendMessage)
    TextView sendMessage;
    @BindView(R.id.tv_throw)
    TextView tv_throw;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        ButterKnife.bind(this);
        mContext = this ;
        getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mEmotionKeyboardSwitchHelper = EmotionKeyboardSwitchHelper.with(this);
        screenWidth = JUtils.getScreenWidth();
        rootView = findViewById(R.id.l_rootView);
        rootView1 = (LinearLayout)findViewById(R.id.l_rootView1);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        mEmojiFl = (RelativeLayout) findViewById(R.id.fl_emoji);
        btn_onkey = (RelativeLayout) findViewById(R.id.btn_onkey);
        rl_head = (RelativeLayout) findViewById(R.id.rl_head);
        scrollViewHeight = (int) (JUtils.getScreenHeight()*0.65);
        et_info = (EditText) findViewById(R.id.et_info);
        et_info.addTextChangedListener(this);
        ViewTreeObserver vto2 = btn_onkey.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                int height = btn_onkey.getHeight();
                int height1 = rl_head.getHeight();
                ViewGroup.LayoutParams layoutParams = et_info.getLayoutParams();
                layoutParams.height = scrollViewHeight-height-height1;
                et_info.setLayoutParams(layoutParams);
                btn_onkey.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        ib_face= (ImageButton) findViewById(R.id.ib_face);
        mEmojiVp = (ViewPager) findViewById(R.id.vp_emoji);
        mVpPointLl = (LinearLayout) findViewById(R.id.ll_point);
        et_info.setOnClickListener(this);
        rootView.getBackground().setAlpha(0);
        initListener();
        initViewPager();
    }

    private void initListener() {
        tv_throw.setOnClickListener(this);
        new KeyboardChangeListener(this).setKeyBoardListener(new KeyboardChangeListener.KeyBoardListener() {
            @Override
            public void onKeyboardChange(boolean isShow, int keyboardHeight) {
                Log.d("hhhhhhhhhhh", "isShow = [" + isShow + "], keyboardHeight = [" + keyboardHeight + "]");
                if(keyboardHeight > 0 ){
                    if( mEmojiFl.getVisibility() == View.VISIBLE){
                        mEmojiFl.setVisibility(View.GONE);
                    }
                    ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
              /*  if (scrollView.getScrollY() + scrollView.getHeight() - scrollView.getPaddingTop() - scrollView.getPaddingBottom() == scrollView.getChildAt(0).getHeight()) {
                    if (mEmojiFl.getVisibility() == View.GONE) {
                        //scrollView.getViewTreeObserver().removeOnScrollChangedListener(this);

                    }
                }*/
                        }
                    };
                    scrollView.getViewTreeObserver().addOnScrollChangedListener(onScrollChangedListener);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.getViewTreeObserver().removeOnScrollChangedListener(onScrollChangedListener);
                        }
                    }, 900);
                }


            }
        });

        ib_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mEmojiFl.getVisibility() == View.VISIBLE){
                    mEmojiFl.setVisibility(View.GONE);
                }else{
                    mEmojiFl.setVisibility(View.VISIBLE);
                }

                KeyBoardUtils.closeKeybord(et_info,ChatsActivity.this);
                scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        if (scrollView.getScrollY() + scrollView.getHeight() - scrollView.getPaddingTop()-scrollView.getPaddingBottom() == scrollView.getChildAt(0).getHeight()) {
                            scrollView.getViewTreeObserver().removeOnScrollChangedListener(this);

                        }
                    }
                });
            }
        });
    }

    /**
     * 设置ViewPager表情
     */
    private void initViewPager() {
        EmojiVpAdapter adapter = new EmojiVpAdapter(this);
        mEmojiVp.setAdapter(adapter);
        //表情点击监听
        adapter.setOnEmojiClickListener(new EmojiVpAdapter.OnEmojiClickListener() {
            @Override
            public void onClick(Emojis emoji) {
                if ("del".equals(emoji.name)) {
                    //表示点击的是删除按钮
                    KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL);
                    et_info.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                } else {
                    SpannableString expressionString = ExpressionUtil.getExpressionString(mContext, emoji.name, "^\\[:.{1,8}\\]$");
                    int index = et_info.getSelectionStart();//获取光标所在位置
                    Editable edit = et_info.getEditableText();
                    if (index < 0 || index >= edit.length() ){
                        edit.append(expressionString);
                    }else{
                        edit.insert(index,expressionString);//光标所在位置插入文字
                    }
                }
            }
        });
        mEmojiVp.setCurrentItem(0);
        //关联指示器点
        adapter.setupWithPagerPoint(mEmojiVp, mVpPointLl);

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    ViewTreeObserver vto ;
    ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener ;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_throw:
                ChatsActivity.this.finish();

                break;
            default:
                break;
        }



    }

    @Override
    public void onBackPressed() {
        if (mEmojiFl.getVisibility() == View.VISIBLE) {
            mEmojiFl.setVisibility(View.GONE);
        }else{
            ChatsActivity.this.finish();
        }
    }

    private CharSequence temp;
    /**
     * 监听输入
     * @param charSequence
     * @param i
     * @param i1
     * @param i2
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        temp = charSequence;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if( temp.length() > 60 ){
            JUtils.Toast("字符超过60");
        }

        if (temp.length() > 0) {//限制长度
            sendMessage.setTextColor(Color.parseColor("#00CBD8"));
        } else {
            sendMessage.setTextColor(Color.parseColor("#DCDCDC"));
        }

    }
}
