package com.hhkj.gas.www.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.db.DB;
import com.hhkj.gas.www.widget.ChangeTime;
import com.nostra13.universalimageloader.core.ImageLoader;

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
    private ImageView user_tag;
    private TextView user;
    private ImageView setting_btn;
    private LinearLayout start0,start1,start2;
    private TextView num;
    @Override
    public void init() {
        user_tag = (ImageView) findViewById(R.id.user_tag);
        user = (TextView) findViewById(R.id.user);
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
        num = (TextView) findViewById(R.id.num);
        start0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,Start0Activity.class);
                startActivity(intent);
            }
        });
        start1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,Start1Activity.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        int num0 = DB.getInstance().getStaffCount(sharedUtils.getStringValue("userid"));
        if(num0!=0){
            num.setText(String.valueOf(num0));
            num.setVisibility(View.VISIBLE);
        }else{
            num.setVisibility(View.GONE);
        }
        if(sharedUtils.getBooleanValue("head")){
            ImageLoader.getInstance().displayImage("drawable://"+R.mipmap.login_users,user_tag);
        }else{
            ImageLoader.getInstance().displayImage("drawable://"+R.mipmap.login_user,user_tag);
        }
        user.setText(sharedUtils.getStringValue("name"));
    }
}
