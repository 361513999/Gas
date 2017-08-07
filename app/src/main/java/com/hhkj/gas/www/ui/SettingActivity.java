package com.hhkj.gas.www.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.widget.NewToast;

/**
 * Created by cloor on 2017/8/7.
 */

public class SettingActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gas_setting);
    }
    private TextView setting_exit,back;
    @Override
    public void init() {
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               backActivity();
            }
        });
        setting_exit = (TextView) findViewById(R.id.setting_exit);
        setting_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewToast.makeText(SettingActivity.this,"退出",200).show();
            }
        });
    }
}
