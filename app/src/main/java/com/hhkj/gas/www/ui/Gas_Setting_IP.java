package com.hhkj.gas.www.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.SharedUtils;
import com.hhkj.gas.www.widget.NewToast;

/**
 * Created by Administrator on 2017/9/18/018.
 */

public class Gas_Setting_IP extends BaseActivity {
    private  SharedUtils sharedUtils = new SharedUtils(Common.config);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gas_setting_ip);
    }
    private EditText ip;
    private TextView save;
    @Override
    public void init() {
        ip = (EditText) findViewById(R.id.ip);
        if(sharedUtils.getStringValue("BASEIP").toString().length()!=0){
            ip.setText(sharedUtils.getStringValue("IP"));
        }
        save = (TextView) findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedUtils.setStringValue("BASEIP",ip.getText().toString().trim());
                NewToast.makeText(Gas_Setting_IP.this,"保存成功",2000).show();
                AppManager.getAppManager().finishActivity(Gas_Setting_IP.this);
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity(Gas_Setting_IP.this);
            }
        });
    }
}
