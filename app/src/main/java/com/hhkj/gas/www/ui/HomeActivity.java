package com.hhkj.gas.www.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;

/**
 * Created by cloor on 2017/8/7.
 */

public class HomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gas_home);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

         if(keyCode==KeyEvent.KEYCODE_BACK){

          //此处不处理返回按钮事件
         }
        return super.onKeyDown(keyCode, event);
    }
    private ImageView setting_btn;
    private LinearLayout start0,start1,start2;
    @Override
    public void init() {
        setting_btn = (ImageView) findViewById(R.id.setting_btn);
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,SettingActivity.class);
                startActivity(intent);

            }
        });
        start0 = (LinearLayout) findViewById(R.id.start0);
        start1 = (LinearLayout) findViewById(R.id.start1);
        start2 = (LinearLayout) findViewById(R.id.start2);
        start0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,Start0Activity.class);
                startActivity(intent);
            }
        });
        start2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,Start2Activity.class);
                startActivity(intent);
            }
        });
    }
}
