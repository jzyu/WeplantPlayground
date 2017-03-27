package com.wohuizhong.client.app.UiBase;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.zhy.utils.L;

import java.lang.reflect.Field;

public abstract class ParentFragment extends BaseFragment {
    public static final String TAG = "BaseParentFragment";

    /**
     * Patch for getChildFragmentManager when fragment nested.
     * Refer: http://stackoverflow.com/a/15656428/3386185
     */
    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     看了 hideFragment(FragmentManager.java) 源码，父fragment不会嵌套调用
     childFragment的onHiddenChanged，这里加处理，否则 childFragment 的onHiddenChanged 不被调用
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        L.d(TAG, "onHiddenChanged(" + hidden + ") + @" + getClass().getSimpleName());
        super.onHiddenChanged(hidden);
        Fragment sub = getSubFragment();
        if (sub != null) {
            sub.onHiddenChanged(hidden);
        }
    }

    public abstract Fragment getSubFragment();

    /**
     childFragment 默认不支持 onActivityResult，需要
     1.在childFragment中 getParentFragment().startActivityForResult
     2.此patch
     Refer: http://stackoverflow.com/questions/6147884/onactivityresult-not-being-called-in-fragment
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment subFragment = getSubFragment();
        if (subFragment != null) {
            subFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
