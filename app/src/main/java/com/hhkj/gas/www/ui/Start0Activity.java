package com.hhkj.gas.www.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;

/**
 * Created by Administrator on 2017/8/8/008.
 */

public class Start0Activity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.start0_layout);
    }
    private TextView back;
    @Override
    public void init() {
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity(Start0Activity.this);
            }
        });
    }
}
