package com.example.jzyu.weplantplayground;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.wohuizhong.client.app.util.WidgetUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TestMiscActivity extends AppCompatActivity {

    @Bind(R.id.iv_status)
    ImageView ivStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_misc);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.progressbar_circle);
        ivStatus.setImageResource(R.drawable.loading);
        ivStatus.startAnimation(anim);
    }

    private boolean tvIsBold = false;

    public void onClickTestSwitchBold(View view) {
        tvIsBold = ! tvIsBold;
        WidgetUtil.tvSetBold((TextView)view, tvIsBold).setText(tvIsBold ? "Bold 粗体" : "Normal 普通");
    }
}
