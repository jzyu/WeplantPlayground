package com.wohuizhong.client.app.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jzyu.weplantplayground.R;
import com.wohuizhong.client.app.util.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2015/7/30.
 */
public class TitleBarView extends RelativeLayout {

    @Bind(R.id.ivLeft)
    ImageView ivLeft;
    @Bind(R.id.tvRight)
    TextView tvRight;
    @Bind(R.id.tvCenter)
    TextView tvCenter;
    @Bind(R.id.ivRight)
    ImageView ivRight;
    @Bind(R.id.container)
    RelativeLayout parentView;
    @Bind(R.id.underline)
    View underline;


    public TitleBarView(Context context) {
        this(context, null, 0);
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        View view = LayoutInflater.from(context).inflate(R.layout.widget_titlebar, this);
        ButterKnife.bind(this, view);

        TypedArray typedArray;

        //apply system attribute id
        int[] sysAttrIds = {
                android.R.attr.background,
        };
        typedArray = context.obtainStyledAttributes(attrs, sysAttrIds);
        try {
            applySystemAttribute(typedArray);
        } finally {
            typedArray.recycle();
        }

        //apply custom attribute id
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleBarView);
        try {
            applyAttribute(typedArray);
        } finally {
            typedArray.recycle();
        }
    }

    private void applySystemAttribute(TypedArray array) {
        int bkgResId = array.getResourceId(0, 0);
        if (bkgResId != 0) {
            parentView.setBackgroundResource(bkgResId);
        }
    }

//    public static boolean strIsEmpty(String str) {
//        return str == null || str.equals("");
//    }

    private void applyAttribute(TypedArray array) {
        int height = array.getDimensionPixelSize(R.styleable.TitleBarView_title_height, 0);
        if (height > 0) {
            parentView.getLayoutParams().height = height;
        }

        int touchWidth = array.getDimensionPixelSize(R.styleable.TitleBarView_title_lr_image_width, 0);
        if (touchWidth > 0) {
            ((LayoutParams) ivLeft.getLayoutParams()).width = touchWidth;
            ((LayoutParams) ivRight.getLayoutParams()).width = touchWidth;
        }

        int touchBkgId = array.getResourceId(R.styleable.TitleBarView_title_lr_background, 0);
        if (touchBkgId > 0) {
            ivLeft.setBackgroundResource(touchBkgId);
            ivRight.setBackgroundResource(touchBkgId);
            tvRight.setBackgroundResource(touchBkgId);
        }

        underline.setVisibility(
                array.getBoolean(R.styleable.TitleBarView_title_underline, false) ? VISIBLE : GONE);
        underline.setBackgroundColor(array.getColor(R.styleable.TitleBarView_title_underline_color,
                Color.GREEN));

        //center text
        String text = array.getString(R.styleable.TitleBarView_title_text);
        int textColor = array.getColor(R.styleable.TitleBarView_title_center_text_color, Color.BLUE);
        float textSize = array.getDimensionPixelSize(R.styleable.TitleBarView_title_center_text_size, 0);

        if (text != null) {
            tvCenter.setText(text);
        }
        tvCenter.setTextColor(textColor);
        if (textSize > 0) {
            tvCenter.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }

        //right text
        text = array.getString(R.styleable.TitleBarView_title_right_text);
        textColor = array.getColor(R.styleable.TitleBarView_title_right_text_color, Color.YELLOW);
        textSize = array.getDimensionPixelSize(R.styleable.TitleBarView_title_right_text_size, 0);
        int padding = array.getDimensionPixelSize(R.styleable.TitleBarView_title_right_text_padding, 0);

        if (! strIsEmpty(text)) {
            tvRight.setText(text);
        } else {
            tvRight.setVisibility(GONE); //避免按压效果
        }
        tvRight.setTextColor(textColor);
        if (textSize > 0) {
            tvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        tvRight.setPadding(padding, 0, padding, 0);

        //left image
        int resId = array.getResourceId(R.styleable.TitleBarView_title_left_image, 0);
        if (resId > 0) {
            ivLeft.setImageResource(resId);
            ivLeft.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity)getContext()).finish(); //默认关闭activity
                }
            });
        } else {
            ivLeft.setVisibility(GONE); //避免按压效果
        }

        //right image
        resId = array.getResourceId(R.styleable.TitleBarView_title_right_image, 0);
        if (resId > 0) {
            ivRight.setImageResource(resId);
        } else {
            ivRight.setVisibility(GONE); //避免按压效果
        }
    }

    private static boolean strIsEmpty(String str) {
        return str == null || str.equals("");
    }

    public void setTitle(String title) {
        if (title != null) {
            tvCenter.setText(title);
        }
    }

    // iconId: >0 设置，=0不变，<0 清除
    // text: null - 不变
    public void setData(String title, int leftIconId, String rightText, int rightIconId) {
        if (title != null) {
            tvCenter.setText(title);
        }

        if (leftIconId > 0) {
            ivLeft.setVisibility(VISIBLE);
            ivLeft.setImageResource(leftIconId);
        } else if (leftIconId < 0) {
            ivLeft.setVisibility(GONE);
        }

        if (rightText != null) {
            tvRight.setVisibility(VISIBLE);
            tvRight.setText(rightText);
        } else {
            tvRight.setVisibility(GONE);
        }

        if (rightIconId > 0) {
            ivRight.setVisibility(VISIBLE);
            ivRight.setImageResource(rightIconId);
        } else if (rightIconId < 0) {
            ivRight.setVisibility(GONE);
        }
    }

    public void setLeftIcon(int iconId) {
        if (iconId > 0) {
            ivLeft.setVisibility(VISIBLE);
            ivLeft.setImageResource(iconId);
        } else {
            ivLeft.setVisibility(GONE);
        }
    }

    public void setRightText(String text) {
        tvRight.setVisibility(StringUtil.isEmpty(text) ? INVISIBLE : VISIBLE);
        tvRight.setText(text);
    }

    public void setLeftRightClickListener(OnClickListener left, OnClickListener right) {
        if (left != null) {
            ivLeft.setOnClickListener(left);
        }

        if (right != null) {
            ivRight.setOnClickListener(right);
            tvRight.setOnClickListener(right);
        }
    }
}
