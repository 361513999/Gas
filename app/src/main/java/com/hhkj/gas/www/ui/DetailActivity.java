package com.hhkj.gas.www.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hhkj.gas.www.R;
import com.hhkj.gas.www.adapter.DetailBAdapter;
import com.hhkj.gas.www.adapter.DetailImageAdapter;
import com.hhkj.gas.www.adapter.StaffItemAdapter;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.bean.DetailStaff;
import com.hhkj.gas.www.bean.ReserItemBean;

import com.hhkj.gas.www.bean.StaffB;
import com.hhkj.gas.www.bean.StaffImageItem;
import com.hhkj.gas.www.bean.StaffQj;
import com.hhkj.gas.www.bean.StaffTxtItem;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.CopyFile;
import com.hhkj.gas.www.common.FileUtils;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.common.U;
import com.hhkj.gas.www.db.DB;
import com.hhkj.gas.www.inter.TimeSelect;
import com.hhkj.gas.www.widget.ChangeTime;
import com.hhkj.gas.www.widget.InScrollListView;
import com.hhkj.gas.www.widget.LoadView;
import com.hhkj.gas.www.widget.NewToast;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by cloor on 2017/8/15.
 */

public class DetailActivity extends BaseActivity {
    private TextView back;
    private ReserItemBean bean;
    private DetailHandler detailHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);
        detailHandler = new DetailHandler(DetailActivity.this);
        Intent intent = getIntent();
        if (intent.hasExtra("obj")) {
            bean = (ReserItemBean) intent.getSerializableExtra("obj");
        }
        ini();
    }

    private class DetailHandler extends Handler {
        WeakReference<DetailActivity> mLeakActivityRef;

        public DetailHandler(DetailActivity leakActivity) {
            mLeakActivityRef = new WeakReference<DetailActivity>(leakActivity);
        }

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            if (mLeakActivityRef.get() != null) {
                switch (msg.what) {
                    case 1:
                        P.c("本地数据装载");
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
                            case 6:
                                status = "重新安检中";
                                break;
                            case 8:
                                status = "整改中";
                                break;

                        }
                        item5.setText(getString(R.string.curr_status, status));
                        item6.setText(getString(R.string.curr_person, bean.getStaffName()));
                        //----------
                        //装载图片列表
                        //控制图片
                        int imageLen = staffImages.size();
                        item7.setNumColumns(imageLen);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(FileUtils.dip2px(DetailActivity.this, 110) * imageLen, LinearLayout.LayoutParams.WRAP_CONTENT);
                        item7.setLayoutParams(params);
                        imageAdapter.updata(staffImages);
                        //装载安检条目
                        //控制安检条目
                        if (dss.size() > SHOW_STAFF) {
                            staffItemAdapter.updata(dss, SHOW_STAFF);
                            item9.setVisibility(View.VISIBLE);
                        } else {
                            staffItemAdapter.updata(dss);
                            item9.setVisibility(View.GONE);
                        }
                        //装载燃气表
                        if(staffBs.size()==1){
                            item_b00.setText(staffBs.get(0).getName());
                            item_b01.setText(staffBs.get(0).getValue());

                        }else if(staffBs.size()>1){
                            item_b00.setText(staffBs.get(0).getName());
                            item_b01.setText(staffBs.get(0).getValue());
                            item_b10.setText(staffBs.get(1).getName());
                            item_b11.setText(staffBs.get(1).getValue());
                        }
                        //装载燃气具
                        bAdapter.updata(staffQjs);


                        break;
                    case 2:
                        //装载数据操作


                        break;
                    case 3:

                        break;
                    case 4:
                        //用户未登录处理
                        reLogin();
                        break;

                }
            }
        }
    }

    private final int SHOW_STAFF = 5;
    private TextView item0, item1, item2, item3, item4, item5, item6, item9, item13;
    private ImageView item_edit;
    private GridView item7;
    private ImageView item15, item16;
    private LinearLayout item18, item19;
    //图片
    private DetailImageAdapter imageAdapter;
    private LoadView loadView;
    private InScrollListView item8, item14;
    //安检条目
    private StaffItemAdapter staffItemAdapter;
    //器具
    private DetailBAdapter bAdapter;
    private EditText item_b00,item_b01,item_b10,item_b11;
    private CheckBox item_b02,item_b12;

    @Override
    public void init() {

    }

    public void ini() {
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity(DetailActivity.this);
            }
        });
        item0 = (TextView) findViewById(R.id.item0);
        item1 = (TextView) findViewById(R.id.item1);
        item2 = (TextView) findViewById(R.id.item2);
        item3 = (TextView) findViewById(R.id.item3);
        item4 = (TextView) findViewById(R.id.item4);
        item5 = (TextView) findViewById(R.id.item5);
        item6 = (TextView) findViewById(R.id.item6);
        item7 = (GridView) findViewById(R.id.item7);
        item8 = (InScrollListView) findViewById(R.id.item8);
        item9 = (TextView) findViewById(R.id.item9);
        item13 = (TextView) findViewById(R.id.item13);
        item14 = (InScrollListView) findViewById(R.id.item14);
        item15 = (ImageView) findViewById(R.id.item15);
        item16 = (ImageView) findViewById(R.id.item16);
        item_b00 = (EditText) findViewById(R.id.item_b00);
        item_b01 = (EditText) findViewById(R.id.item_b01);
        item_b10 = (EditText) findViewById(R.id.item_b10);
        item_b11 = (EditText) findViewById(R.id.item_b11);
        item_b02 = (CheckBox) findViewById(R.id.item_b02);
        item_b12 = (CheckBox) findViewById(R.id.item_b12);

        item18 = (LinearLayout) findViewById(R.id.item18);
        item19 = (LinearLayout) findViewById(R.id.item19);


        imageAdapter = new DetailImageAdapter(DetailActivity.this, staffImages);
        item7.setAdapter(imageAdapter);

        staffItemAdapter = new StaffItemAdapter(DetailActivity.this, dss);
        item8.setAdapter(staffItemAdapter);

        bAdapter = new DetailBAdapter(DetailActivity.this, staffQjs);
        item14.setAdapter(bAdapter);
        item_edit = (ImageView) findViewById(R.id.item_edit);


        item9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dss.size() > SHOW_STAFF) {
                    staffItemAdapter.updata(dss);
                    item9.setVisibility(View.GONE);
                }
            }
        });
//         能进入的就直接装载数据

//        loadData();
        item_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeTime changeTime = new ChangeTime(DetailActivity.this, sharedUtils, bean);
                changeTime.setI(new TimeSelect() {
                    @Override
                    public void success(String time) {
                        item4.setText(time);
                    }
                });
                changeTime.showSheet();
            }
        });
        item13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaffQj bItem = new StaffQj();
                staffQjs.add(bItem);

                bAdapter.updata(staffQjs);
            }
        });
        item15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, CommonLineActivity.class);
                startActivity(intent);
            }
        });
        item16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, CommonLineActivity.class);
                startActivity(intent);
            }
        });
        item18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, StaffBtActivity.class);
                intent.putExtra("obj",bean);
                startActivity(intent);
            }
        });
        load();
    }

    private ArrayList<StaffImageItem> staffImages = new ArrayList<>();//图片
    private Map<String, Object> txtListMap = new HashMap<>();//未处理的栏目
    private ArrayList<DetailStaff> dss = new ArrayList<>();//栏目
    private ArrayList<StaffB> staffBs = new ArrayList<>();//燃气表
    private ArrayList<StaffQj> staffQjs = new ArrayList<>();//燃气具

    private void parseTxtItem() {
        //解析安检项目数据
        Set set = txtListMap.keySet();
        Iterator it = set.iterator();
        dss.clear();
        while (it.hasNext()) {
            String key = it.next().toString();
            Object obj = txtListMap.get(key);
            if (obj instanceof StaffTxtItem) {
                //普通
                StaffTxtItem item = (StaffTxtItem) obj;
                DetailStaff ds = new DetailStaff();
                ds.setItem(item);
                dss.add(ds);

            } else if (obj instanceof ArrayList) {
                ArrayList<StaffTxtItem> items = (ArrayList<StaffTxtItem>) obj;
                DetailStaff ds = new DetailStaff();
                ds.setItems(items);
                ds.setItems_tag(key);
                dss.add(ds);
                for (int i = 0; i < items.size(); i++) {
                    StaffTxtItem item = items.get(i);

                }

            }
        }
    }

    private void load() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                DB.getInstance().updataStand(bean);
                DB.getInstance().updataStaffImageItem(staffImages, bean);
                DB.getInstance().updataStaffTxtItem(txtListMap, bean);
                parseTxtItem();
                DB.getInstance().updataStaffBs(staffBs,bean);
                DB.getInstance().updataStaffQj(staffQjs,bean);
                detailHandler.sendEmptyMessage(1);
            }
        }.start();
    }
}
