package com.wohuizhong.client.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Author: jzyu
 * Date  : 2017/3/10
 */

public class MaskItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable maskDrawable;
    private CheckListener checkListener;

    public interface CheckListener {
        boolean onCheck(int position);
    }

    public MaskItemDecoration(Context context, @DrawableRes int drawableId, CheckListener checkListener) {
        this.maskDrawable = ResourcesCompat.getDrawable(context.getResources(), drawableId, null);
        this.checkListener = checkListener;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);

            if (! checkListener.onCheck(parent.getChildAdapterPosition(child))) {
                continue;
            }

            //RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            maskDrawable.setBounds(left, child.getTop(), right, child.getBottom());
            maskDrawable.draw(c);
        }
    }
}
