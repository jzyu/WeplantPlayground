package com.example.jzyu.weplantplayground;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.wohuizhong.client.app.UiBase.LocalDataListFragment;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;

public class PtrBubbleMsgActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptr_bubble_msg);
        ButterKnife.bind(this);
    }

    private ListFragment getListFragment() {
        return (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment);
    }

    private int preAppendIndex = 0;
    private int appendIndex = 0;

    public void onClickPreAppend01(View view) {
        getListFragment().onPreAppend();
    }

    public void onClickPreAppend02(View view) {
        getListFragment().getItems().add(0, "preAppendIndex = " + preAppendIndex++);
        getListFragment().getRvAdapter().notifyDataSetChanged();
    }

    public void onClickAppend(View view) {
        getListFragment().getItems().add("appendIndex = " + appendIndex++);
        getListFragment().getRvAdapter().notifyItemInserted(getListFragment().getItems().size());
    }

    public void onClickPreAppend11(View view) {
        getListFragment().getItems().add(1, "preAppendIndex = " + preAppendIndex++);
        getListFragment().getRvAdapter().notifyItemInserted(1);
    }


    public static class ListFragment extends LocalDataListFragment<String> {

        private List<String> texts = new ArrayList<>();

        public PtrBubbleMsgActivity getAty1() {
            return (PtrBubbleMsgActivity) getActivity();
        }

        private void onPreAppend() {
            getItems().add(0, "preAppendIndex = " + getAty1().preAppendIndex++);
            getRvAdapter().notifyItemInserted(0);

            LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
            //if (lm.findFirstCompletelyVisibleItemPosition() == 0) {
            //    lm.scrollToPosition(0);
            ///}
            //scrollToTop();
            lm.scrollToPosition(0);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            texts.addAll(Arrays.asList("one"/*, "two", "three", "four", "five", "six"*/));
            init(getBuilder(R.layout.row_text, texts).build());
        }

        @Override
        public void onRowConvert(ViewHolder holder, String item, int position) {
            holder.setText(android.R.id.text1, item);
        }
    }
}
