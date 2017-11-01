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
import com.hhkj.gas.www.common.CopyFile;
import com.hhkj.gas.www.common.P;
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
    private int staffPrint;
    private int from = 0;
    private String tag ;
    private  Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_line_layout);
          intent = getIntent();
        if(intent.hasExtra("obj")){
            bean = (ReserItemBean) intent.getSerializableExtra("obj");
        }
        if(intent.hasExtra("staffPrint")){
            staffPrint = intent.getIntExtra("staffPrint",-1);
        }
        if(intent.hasExtra("from")){
            from = intent.getIntExtra("from",-1);
            tag = intent.getStringExtra("tag");
        }
    }
    private TextView back,cancle,sure,dt_line;
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

        dt_line = (TextView) findViewById(R.id.dt_line);
        if(staffPrint==1){
            dt_line.setVisibility(View.VISIBLE);
        }else{
            dt_line.setVisibility(View.GONE);
        }
        if(from==1&&tag!=null&&tag.equals("personLine")){
            dt_line.setVisibility(View.VISIBLE);
        }
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
                        if(from==1){

                            DB.getInstance().prother(tag,path,bean);
                        }else{

                            DB.getInstance().staff_print(bean,staffPrint,path);
                        }

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
        dt_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String path = Common.CACHE_STAFF_IMAGES+System.currentTimeMillis()+".png";
                File file = new File(path);
                if (!file.getParentFile().exists())file.getParentFile().mkdirs();
                CopyFile copyFile = new CopyFile();
                try {
                    copyFile.copyFile(getAssets().open("no_line.png"),path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(from==1){

                    DB.getInstance().prother(tag,path,bean);
                }else {
                    DB.getInstance().staff_print(bean,staffPrint,path);
                }

                Intent intent = new Intent();
                intent.putExtra("path",path);
                setResult(1000,intent);
                AppManager.getAppManager().finishActivity(CommonLineActivity.this);
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                line.clear();
            }
        });

    }
}
