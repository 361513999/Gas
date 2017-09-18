package com.hhkj.gas.www.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.widget.NewToast;

import java.util.Set;

/**
 * Created by cloor on 2017/8/7.
 */

public class SettingActivity extends BaseActivity {
    private RelativeLayout item1,item0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gas_setting);
    }
    private TextView setting_exit,back;
    @Override
    public void init() {
        item1 = (RelativeLayout) findViewById(R.id.item1);
        item0 = (RelativeLayout) findViewById(R.id.item0);
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
                Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
                startActivity(intent);
                try {
                    AppManager.getAppManager().finishActivity(HomeActivity.class);
                }catch (Exception e){

                }
                try {
                    AppManager.getAppManager().finishActivity(SettingActivity.class);
                }catch (Exception e){

                }


            }
        });
        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(SettingActivity.this,Gas_Setting_IP.class);
                startActivity(intent);
            }
        });
    }
}
