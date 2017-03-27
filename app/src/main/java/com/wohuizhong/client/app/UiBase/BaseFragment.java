package com.wohuizhong.client.app.UiBase;

import android.support.v4.app.Fragment;

/**
 * Created by weplant on 16/4/19.
 */
public class BaseFragment extends Fragment {
    /*@Override
    public void onDestroy() {
        super.onDestroy();
        WeplantApplication.getRefWatcher(getActivity()).watch(this);
    }*/

    public BaseActivity getAty() {
        return (BaseActivity) getActivity();
    }
}
