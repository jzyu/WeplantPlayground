package com.wohuizhong.client.app.util;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.jzyu.weplantplayground.R;
import com.zhy.utils.DensityUtils;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Author: Administrator
 * Date  : 2016/11/29
 */

public class WidgetUtil {
    public static void resize(View v, int width, int height) {
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

    /*public static PtrUIHandler newPtrUIGrowth(Context context, PtrFrameLayout ptrFrame) {
        ptrFrame.setLoadingMinTime(2500);
        return WidgetUtil.newPtrUIGrowth(context, null, 0, 0);
    }*/

    /*public static PtrUIHandler newPtrUIGrowth(Context context,
                                              SimpleListener ptrDoneListener,
                                              int bkgColorId, int bottomPaddingDp) {
        PtrHeaderGrowth header = new PtrHeaderGrowth(context);

        header.setPtrDoneListener(ptrDoneListener);
        header.setPadding(0, DensityUtils.dp2px(context, 12), 0, DensityUtils.dp2px(context,
                bottomPaddingDp == 0 ? 12 : bottomPaddingDp));
        if (bkgColorId > 0) {
            header.setBackgroundColor(CompatUtil.getColor(context, bkgColorId));
        }

        return header;
    }*/

    public static PtrUIHandler newPtrUIMaterial(Context context, PtrFrameLayout ptrContainer) {
        MaterialHeader header = new MaterialHeader(context);

        header.setLayoutParams(new PtrFrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        header.setPadding(0, DensityUtils.dp2px(context, 12), 0, DensityUtils.dp2px(context, 12));
        header.setPtrFrameLayout(ptrContainer);
        header.setColorSchemeColors(context.getResources().getIntArray(R.array.google_colors));

        return header;
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

    public static Drawable getDrawable(Context context, int drawableId) {
        return ResourcesCompat.getDrawable(context.getResources(), drawableId, null);
    }

    public static boolean isLinearRecyclerViewAtTop(RecyclerView recyclerView) {
        // 不能上滚动时，已到顶
        return ! recyclerView.canScrollVertically(-1);
    }

    public static void linearRvScrollToTop(RecyclerView recyclerView) {
        if (((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition() < 30) {
            recyclerView.smoothScrollToPosition(0);
        } else {
            recyclerView.scrollToPosition(0);
        }
    }

    public static int getDimension(Context context, int dimenId) {
        return context.getResources().getDimensionPixelSize(dimenId);
    }
}
