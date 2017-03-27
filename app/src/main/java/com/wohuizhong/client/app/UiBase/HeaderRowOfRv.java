package com.wohuizhong.client.app.UiBase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.finalteam.loadingviewfinal.RecyclerViewFinal;

/**
 * HeaderRow of recyclerView
 * Author: jzyu
 * Date  : 2017/3/16
 */

public abstract class HeaderRowOfRv<T> {
    protected ViewGroup view;
    private T item;

    protected abstract void onSetData(T data);

    final public void init(RecyclerViewFinal rv, int layoutId, T item) {
        this.item = item;

        if (this.view == null) {
            this.view = (ViewGroup) LayoutInflater.from(rv.getContext()).inflate(layoutId, rv, false);
            rv.addHeaderView(view);
            onInit(item);
        }

        onSetData(item);
    }

    protected void onInit(T data) {}

    final public void updateData() {
        onSetData(item);
    }

    final protected <V extends View> V getView(int id) {
        return (V) view.findViewById(id);
    }

    final protected void setVisible(int id, boolean visible) {
        view.findViewById(id).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    final protected void setClick(int id, View.OnClickListener listener) {
        view.findViewById(id).setOnClickListener(listener);
    }

    final protected void setText(int id, CharSequence charSequence) {
        ((TextView) view.findViewById(id)).setText(charSequence);
    }
}
