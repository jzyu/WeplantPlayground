package com.wohuizhong.client.app.util;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.example.jzyu.weplantplayground.R;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

/**
 * Author: Administrator
 * Date  : 2016/11/29
 */

public class WidgetUtil {
    public static void setViewWidthHeight(View v, int width, int height) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        if (width >= 0) lp.width = width;
        if (height >= 0) lp.height = height;
        v.setLayoutParams(lp);
    }

    public static void setMarginTop(View v, int topMargin) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        lp.topMargin = topMargin;
        v.setLayoutParams(lp);
    }

    public static int getMarginTop(View v) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        return  lp.topMargin;
    }

    public static void setMarginBottom(View v, int bottomMargin) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        lp.bottomMargin = bottomMargin;
        v.setLayoutParams(lp);
    }

    public static int getMarginBottom(View v) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        return  lp.bottomMargin;
    }

    public static void ptrAddUIHeader(Context context,
                                      PtrFrameLayout ptrContainer,
                                      SimpleListener ptrEndListener,
                                      int bkgColorId,
                                      int paddingBottomDp) {
        final MaterialHeader header = new MaterialHeaderWithPtrEndListener(context, ptrEndListener);
        int[] colors = context.getResources().getIntArray(R.array.google_colors);

        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, PtrLocalDisplay.dp2px(12), 0, PtrLocalDisplay.dp2px(
                paddingBottomDp == 0 ? 12 : paddingBottomDp));
        if (bkgColorId > 0) {
            header.setBackgroundColor(CompatUtil.getColor(context, bkgColorId));
        }
        header.setPtrFrameLayout(ptrContainer);

        ptrContainer.setHeaderView(header);
        ptrContainer.addPtrUIHandler(header);
    }

    private static class MaterialHeaderWithPtrEndListener extends in.srain.cube.views.ptr.header.MaterialHeader {

        private SimpleListener listener;
        private boolean isPtrDone;

        public MaterialHeaderWithPtrEndListener(Context context, SimpleListener listener) {
            super(context);
            this.listener = listener;
        }

        @Override
        public void onUIReset(PtrFrameLayout frame) {
            super.onUIReset(frame);

            if (isPtrDone && listener != null) {
                listener.callback();
            }
            isPtrDone = false;
        }



        @Override
        public void onUIRefreshComplete(PtrFrameLayout frame) {
            super.onUIRefreshComplete(frame);

            isPtrDone = true;
        }
    }

    public abstract static class AnimatorEndListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
