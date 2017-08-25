package com.hhkj.gas.www.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.SharedUtils;
import com.hhkj.gas.www.db.DB;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2017/8/25/025.
 */

public class DetailProblemActivity extends TakePhotoActivity {
    private TakePhoto takePhoto;
    private SharedUtils sharedUtils;
    public LayoutInflater inflater;
    private TextView back;
    private ReserItemBean bean;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
    private DetailProblemHandler detailProblemHandler;
    private TextView item0, item1, item2, item3, item4, item5, item6;
    private void init(){
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity(DetailProblemActivity.this);
            }
        });

        item0 = (TextView) findViewById(R.id.item0);
        item1 = (TextView) findViewById(R.id.item1);
        item2 = (TextView) findViewById(R.id.item2);
        item3 = (TextView) findViewById(R.id.item3);
        item4 = (TextView) findViewById(R.id.item4);
        item5 = (TextView) findViewById(R.id.item5);
        item6 = (TextView) findViewById(R.id.item6);
        new Thread(){
            @Override
            public void run() {
                super.run();
                detailProblemHandler.sendEmptyMessage(70);
            }
        }.start();

    }
    private class DetailProblemHandler extends Handler {
        WeakReference<DetailProblemActivity> mLeakActivityRef;

        public DetailProblemHandler(DetailProblemActivity leakActivity) {
            mLeakActivityRef = new WeakReference<DetailProblemActivity>(leakActivity);
        }

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            if (mLeakActivityRef.get() != null) {
                switch (msg.what){
                    case 70:
                        DB.getInstance().updataStand(bean);
                        detailProblemHandler.sendEmptyMessage(71);
                        break;
                    case 71:
                        //装载基础数据
                        item0.setText(getString(R.string.nor_item_txt0, bean.getNo()));
                        item1.setText(getString(R.string.nor_item_txt1, bean.getName()));
                        item2.setText(getString(R.string.nor_item_txt2, bean.getTel()));
                        item3.setText(getString(R.string.nor_item_txt3, bean.getAdd()));
                        if (bean.getTime().equals("NON")) {
                            item4.setText(getString(R.string.nor_item_txt4, "____-__-__ __:__"));
                        } else {
                            item4.setText(getString(R.string.nor_item_txt4, bean.getTime()));
                        }
                        String status = "";
                        switch (bean.getOrderStatus()) {
                            case 3:
                                status = "进行中";
                                break;
                            case 4:
                                status = "审核中";

                                break;
                            case 6:
                                status = "重新安检中";
                                break;
                            case 7:
                                status = "等待再次执行";
                                break;
                            case 8:
                                status = "整改中";
                                break;


                        }
                        String vs = getString(R.string.curr_status, status);
                        SpannableStringBuilder sbBuilder=new SpannableStringBuilder(vs);

                        sbBuilder.setSpan
                                (new ForegroundColorSpan(Color.parseColor("#0084ff")), 5, vs.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                        item5.setText(sbBuilder);
                        item6.setText(getString(R.string.curr_person, bean.getStaffName()));
                        //----------
                        break;
                }
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_problem_layout);
        Intent intent = getIntent();
        if(intent.hasExtra("obj")){
            bean = (ReserItemBean) intent.getSerializableExtra("obj");
        }
        takePhoto = getTakePhoto();
        sharedUtils = new SharedUtils(Common.config);
        detailProblemHandler = new DetailProblemHandler(DetailProblemActivity.this);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        AppManager.getAppManager().addActivity(this);
    }
}
