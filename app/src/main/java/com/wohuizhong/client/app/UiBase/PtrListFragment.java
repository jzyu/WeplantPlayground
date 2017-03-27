package com.wohuizhong.client.app.UiBase;

import com.wohuizhong.client.app.util.ScrollSpeedLinearLayoutManger;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.utils.L;

import java.util.List;

public abstract class PtrListFragment<T>
        extends PtrRecyclerViewFragment<T>
        implements PtrRecyclerViewFragment.RowConvert<T> {

    protected ConfigBuilder<T> getBuilder(int rowLayoutId, List<T> items) {
        MultiItemTypeAdapter<T> adapter = new CommonAdapter<T>(getContext(), rowLayoutId, items) {
            @Override
            protected void convert(ViewHolder holder, T item, int position) {
                if (item == null) {
                    // patch： 搜索api有bug，某些时候带的数据是null，比如搜索：测测，第二条就是null
                    L.e(TAG, "convert() t == nul !!");
                    return;
                }

                onRowConvert(holder, item, position);
            }
        };

        return new ConfigBuilder<T>()
                .items(items)
                .adapter(adapter)
                .layoutManager(new ScrollSpeedLinearLayoutManger(getContext()));
    }

    final protected ConfigBuilder<T> getBuilder(List<T> items, ItemViewDelegate<T>... delegates) {
        MultiItemTypeAdapter<T> adapter = new MultiItemTypeAdapter<>(getContext(), items);

        for (ItemViewDelegate<T> delegate: delegates) {
            adapter.addItemViewDelegate(delegate);
        }

        return new ConfigBuilder<T>()
                .items(items)
                .adapter(adapter)
                .layoutManager(new ScrollSpeedLinearLayoutManger(getContext()));
    }
}
