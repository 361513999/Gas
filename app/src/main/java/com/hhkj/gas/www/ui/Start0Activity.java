package com.hhkj.gas.www.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.adapter.Start0Adapter;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.common.FileUtils;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.common.SharedUtils;
import com.hhkj.gas.www.common.U;
import com.hhkj.gas.www.widget.PullToRefreshView;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by Administrator on 2017/8/8/008.
 */

public class Start0Activity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.start0_layout);
    }
    private RadioGroup nav_grp;
    private RadioButton nav_0,nav_1;
    private ListView gas_list;
    private TextView back;
    private Start0Adapter start0Adapter;
    private PullToRefreshView pull_to_refresh_list;
    private PullToRefreshView.OnHeaderRefreshListener listHeadListener = new PullToRefreshView.OnHeaderRefreshListener(){

        @Override
        public void onHeaderRefresh(PullToRefreshView view) {
            pull_to_refresh_list.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pull_to_refresh_list.onHeaderRefreshComplete();

                }
            },runTime);
        }
    };
    private PullToRefreshView.OnFooterRefreshListener listFootListener = new PullToRefreshView.OnFooterRefreshListener() {
        @Override
        public void onFooterRefresh(PullToRefreshView view) {
            pull_to_refresh_list.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pull_to_refresh_list.onFooterRefreshComplete();
                }
            },runTime);
        }
    };
    private final int runTime = 1000;
    private TextView head_btn;
    @Override
    public void init() {
        pull_to_refresh_list = (PullToRefreshView) findViewById(R.id.pull_to_refresh_list);
        pull_to_refresh_list.setOnHeaderRefreshListener(listHeadListener);
        pull_to_refresh_list.setOnFooterRefreshListener(listFootListener);
        gas_list = (ListView) findViewById(R.id.gas_list);
        start0Adapter = new Start0Adapter(Start0Activity.this);
        gas_list.setAdapter(start0Adapter);
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity(Start0Activity.this);
            }
        });
        head_btn = (TextView) findViewById(R.id.head_btn);
        if(sharedUtils.getBooleanValue("head")){
            head_btn.setVisibility(View.VISIBLE);
        }else{
            head_btn.setVisibility(View.GONE);
        }
        nav_grp = (RadioGroup) findViewById(R.id.nav_grp);
        nav_0 = (RadioButton) findViewById(R.id.nav_0);
        nav_1 = (RadioButton) findViewById(R.id.nav_1);
        nav_grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.nav_0:

                        break;
                    case R.id.nav_1:

                        break;
                }
            }
        });
        //默认
        nav_0.setChecked(true);
        loadList();
    }
    private final int REP_TAG = 0;
    private RequestCall requestCall;
    private void loadList(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("toKen",sharedUtils.getStringValue("token"));
            jsonObject.put("cls","Gas.SecurityOrder");
            jsonObject.put("method","GetOrderList");
            JSONObject pms = new JSONObject();
            //0 自己和下属的，还包括未领取的，1自己和下属的，2未领取的
            pms.put("GetNone",REP_TAG);
            pms.put("OrderType",getCheckedId());
            jsonObject.put("param",pms.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        P.c("发送"+jsonObject.toString());
        requestCall = OkHttpUtils.postString().url(U.VISTER(U.BASE_URL)+U.LIST).mediaType(MediaType.parse("application/json; charset=utf-8")).content(jsonObject.toString()).build();
        P.c(U.VISTER(U.BASE_URL)+U.LIST);
        requestCall.execute(stringCallback);
    }
    private StringCallback stringCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(String response, int id) {
            try {
                P.c(FileUtils.formatJson(response));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private String getCheckedId(){
        String tag = null;
        try {
            tag = ((RadioButton)findViewById(nav_grp.getCheckedRadioButtonId())).getTag().toString();
        }catch (Exception e){
        }
        return  tag;
    }
}
