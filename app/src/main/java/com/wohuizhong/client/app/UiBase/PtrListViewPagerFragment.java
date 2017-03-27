package com.wohuizhong.client.app.UiBase;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zhy.utils.L;

abstract public class PtrListViewPagerFragment<T> extends PtrListFragment<T> {
    private static final String TAG = PtrListViewPagerFragment.class.getSimpleName();

    private boolean isVisible;
    private boolean isCreated;

    /**
     * 页面可见时执行此方法
     * @param onCreate
     */
    protected abstract void pagerOnForeground(boolean onCreate);

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        L.v(TAG, "setUserVisibleHint isVisibleToUser = " + isVisibleToUser);

        if(getUserVisibleHint()) {
            L.d(TAG, "OnVisible");

            isVisible = true;
            if (isCreated) {
                pagerOnForeground(false);
            }
        } else {
            L.d(TAG, "onInvisible");

            isVisible = false;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        isCreated = true;
        if (isVisible) {
            pagerOnForeground(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        L.v(TAG, "onDestroyView");
        isCreated = false;
    }

    protected boolean isPagerForeground() {
        return isCreated && isVisible;
    }
}
