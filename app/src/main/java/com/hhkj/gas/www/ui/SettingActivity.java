package com.hhkj.gas.www.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.widget.ClearTips;
import com.hhkj.gas.www.widget.NewToast;

import java.io.File;
import java.text.DecimalFormat;
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
    private String FormetFileSize(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        if (fileS == 0) {
            return "0.00";
        }
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }
    // 递归方式 计算文件的大小
    private long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }
    private Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        switch (msg.what){
            case 0:
                getTotal();
                break;
        }
        }
    };
    private TextView setting_exit,back,merry;
    private void getTotal(){
        File file = new File(Common.SD);
        File dbFile = new File(Common.DB_DIR+Common.DB_NAME);
        merry.setText(((dbFile != null && dbFile.exists()) ? FormetFileSize(getTotalSizeOfFilesInDir(file)+dbFile.length()) : "0kb"));
    }
    @Override
    public void init() {

        merry = (TextView) findViewById(R.id.merry);
        handler.sendEmptyMessage(0);
        item1 = (RelativeLayout) findViewById(R.id.item1);
        item0 = (RelativeLayout) findViewById(R.id.item0);
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               backActivity();
            }
        });
        item0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearTips tips = new ClearTips(SettingActivity.this,handler);
                tips.showSheet();
            }
        });
        setting_exit = (TextView) findViewById(R.id.setting_exit);
        setting_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedUtils.clear("token");
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
