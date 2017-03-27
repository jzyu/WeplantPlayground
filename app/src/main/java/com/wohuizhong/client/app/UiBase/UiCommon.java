package com.wohuizhong.client.app.UiBase;

import android.content.Context;

import com.example.jzyu.weplantplayground.R;
import com.wohuizhong.client.app.util.WidgetUtil;
import com.wohuizhong.client.app.widget.DrawableItemDecoration;
import com.zhy.utils.DensityUtils;

/**
 * Author: jzyu
 * Date  : 2017/3/27
 */

public class UiCommon {
    public static DrawableItemDecoration newRvDivideLine(Context context) {
        return new DrawableItemDecoration(context,
                R.color.divide_line,
                DensityUtils.dp2px(context, 0.5f),
                WidgetUtil.getDimension(context, R.dimen.margin_h));
    }
}
