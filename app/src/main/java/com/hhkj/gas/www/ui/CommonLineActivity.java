package com.hhkj.gas.www.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.db.DB;
import com.hhkj.gas.www.widget.LinePathView;
import com.hhkj.gas.www.widget.NewToast;

import java.io.File;
import java.io.IOException;

/**
 * Created by cloor on 2017/8/6.
 */

public class CommonLineActivity extends BaseActivity {
    private ReserItemBean bean;
    private boolean staffPrint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_line_layout);
        Intent intent = getIntent();
        if(intent.hasExtra("obj")){
            bean = (ReserItemBean) intent.getSerializableExtra("obj");
            staffPrint = intent.getBooleanExtra("staffPrint",false);
        }
    }
    private TextView back,cancle,sure;
    private LinePathView line;
    @Override
    public void init() {
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity(CommonLineActivity.this);
            }
        });
        line = (LinePathView) findViewById(R.id.line);
        line.setBackColor(Color.parseColor("#ededed"));
        line.setPaintWidth(10);
        line.setPenColor(Color.BLACK);
        line.clear();
        cancle = (TextView) findViewById(R.id.cancle);
        sure = (TextView) findViewById(R.id.sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (line.getTouched()) {
                    try {
                        String path = Common.CACHE_STAFF_IMAGES+System.currentTimeMillis()+".png";
                        File file = new File(path);
                        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
                        line.save(path, true, 10);
                        //保存数据库
                        DB.getInstance().staff_print(bean,staffPrint,path);
                        Intent intent = new Intent();
                        intent.putExtra("path",path);
                        setResult(1000,intent);
                        AppManager.getAppManager().finishActivity(CommonLineActivity.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    NewToast.makeText(CommonLineActivity.this,"您没有签名", Common.TTIME).show();
                }
            }
        });

    }
}
