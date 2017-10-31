package com.hhkj.gas.www.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.adapter.StaffCtAdapter;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffCtBean;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.FileUtils;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.common.U;
import com.hhkj.gas.www.db.DB;
import com.hhkj.gas.www.widget.LoadView;
import com.hhkj.gas.www.widget.NewToast;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by Administrator on 2017/8/22/022.
 */

public class StaffCtActivity extends BaseActivity {
    private ListView lists;
    private StaffCtAdapter staffCtAdapter;
    private TextView back,sure;
    private ReserItemBean bean;
    private  ArrayList<StaffCtBean> staffCtBeen = new ArrayList<>();
    @Override
    public void init() {
        lists = (ListView) findViewById(R.id.lists);

        loadData(staffCtBeen);
        staffCtAdapter = new StaffCtAdapter(StaffCtActivity.this,staffCtBeen);
        lists.setAdapter(staffCtAdapter);
        lists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                staffCtAdapter.select(i);
            }
        });
        back  = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity(StaffCtActivity.this);
            }
        });
        sure = (TextView) findViewById(R.id.sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  try {
                      post();
                  }catch (ArrayIndexOutOfBoundsException e){
                      if(loadView!=null){
                          loadView.cancle();
                          loadView = null;
                      }
                      NewToast.makeText(StaffCtActivity.this,"请选择",200).show();
                  }
            }
        });
    }
    private Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what){
                case 2:
                    NewToast.makeText(StaffCtActivity.this,"数据获取失败",Common.TTIME).show();
                    break;
                case 1:
                    staffCtAdapter.updata(staffCtBeen);
                    break;
                case 4:
                    reLogin();
                    break;
            }
        }
    };
    LoadView loadView;
    private void post(){
        if(loadView==null){
            loadView = new LoadView(StaffCtActivity.this);
            loadView.showSheet();
        }
            final JSONObject object = new JSONObject();
            try {
                object.put("cls","Gas.SecurityOrder");
                object.put("method","SecurityRecord");
                object.put("toKen",sharedUtils.getStringValue("token"));
                JSONObject obj = new JSONObject();
                obj.put("OrderId",bean.getId());
                obj.put("Reason",staffCtBeen.get(staffCtAdapter.getSelect()).getName());
                object.put("param",obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OkHttpUtils.postString().url(U.VISTER(U.BASE_URL)+U.LIST).mediaType(MediaType.parse("application/json; charset=utf-8")).content(object.toString()).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    P.c("错误了"+e.getLocalizedMessage());
                    if(loadView!=null){
                        loadView.cancle();
                        loadView = null;
                    }
                }

                @Override
                public void onResponse(String response, int id) {
                        P.c(response);
                    if(loadView!=null){
                        loadView.cancle();
                        loadView = null;
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                        if(jsonObject.getBoolean("Success")){
                            DB.getInstance().setStandCt(7,"O",bean);
                            Intent intent = new Intent();
                            setResult(1000,intent);
                            AppManager.getAppManager().finishActivity(StaffCtActivity.this);
                        }else {
                            if(jsonObject.getString("Result").equals(Common.UNLOGIN)){
                                NewToast.makeText(StaffCtActivity.this, "未登录", 1000).show();
                                handler.sendEmptyMessage(4);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });


    }

  //  private String [] str = new String[]{"用户拒检","无人在家","用户原因不方便开门","其他"};
    private void loadData(final ArrayList<StaffCtBean> staffCtBeen ){
        final JSONObject object = new JSONObject();
        try {
            object.put("cls","Sys.DataDic");
            object.put("method","GetDataDicList");
            object.put("toKen",sharedUtils.getStringValue("token"));
            object.put("param","");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString().url(U.VISTER(U.BASE_URL)+U.LIST).mediaType(MediaType.parse("application/json; charset=utf-8")).content(object.toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                handler.sendEmptyMessage(2);
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                    if(jsonObject.getBoolean("Success")){
                       String jArray = jsonObject.getString("Result");
                        JSONArray jsonArray = new JSONArray(jArray);
                        int len = jsonArray.length();

                        for(int i=0;i<len;i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            if(object.getString("FvchrDataCode").equals("Reason")){
                                StaffCtBean bean0 = new StaffCtBean();
                                bean0.setName(object.getString("FvchrValue"));
                                staffCtBeen.add(bean0);
                            }
                        }
                        handler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_cannot_layout);
        Intent intent  = getIntent();
        if(intent.hasExtra("obj")){
            bean = (ReserItemBean) intent.getSerializableExtra("obj");
        }

    }
}
