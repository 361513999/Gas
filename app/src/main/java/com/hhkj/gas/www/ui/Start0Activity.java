package com.hhkj.gas.www.ui;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.adapter.Start0Adapter;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.common.FileUtils;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.common.U;
import com.hhkj.gas.www.widget.PullToRefreshView;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by Administrator on 2017/8/8/008.
 */

public class Start0Activity extends BaseActivity {
    private StartHandler startHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.start0_layout);
        startHandler = new StartHandler(Start0Activity.this);
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
    private RadioGroup market_group;
    private RadioButton market_group_item0,market_group_item1,market_group_item2,market_group_item3;
    @Override
    public void init() {
        pull_to_refresh_list = (PullToRefreshView) findViewById(R.id.pull_to_refresh_list);
        pull_to_refresh_list.setOnHeaderRefreshListener(listHeadListener);
        pull_to_refresh_list.setOnFooterRefreshListener(listFootListener);
        gas_list = (ListView) findViewById(R.id.gas_list);
        start0Adapter = new Start0Adapter(Start0Activity.this,ribs);
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
                CURRENT_PAGE = 0;
                loadList();
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
        //菜单
        market_group = (RadioGroup) findViewById(R.id.market_group);
        market_group_item0 = (RadioButton) findViewById(R.id.market_group_item0);
        market_group_item1 = (RadioButton) findViewById(R.id.market_group_item1);
        market_group_item2 = (RadioButton) findViewById(R.id.market_group_item2);
        market_group_item3 = (RadioButton) findViewById(R.id.market_group_item3);
        market_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.market_group_item2:
                        dataPop();
                        showDataPop(market_group_item2);
                        break;
                }
            }
        });



        loadList();
    }


    private   ArrayList<ReserItemBean> ribs = new ArrayList<>();
    private int CURRENT_PAGE = 1;
    private final int SHOW_NUM = 8;
    private RequestCall requestCall;
    private void loadList(){
        int type = Integer.parseInt(getCheckedId());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("toKen",sharedUtils.getStringValue("token"));
            jsonObject.put("cls","Gas.SecurityOrder");
            jsonObject.put("method",type==0?"GetSelfOrder":"GetCommonOrder");
            JSONObject pms = new JSONObject();
            //0 自己和下属的，还包括未领取的，1自己和下属的，2未领取的
            pms.put("GetNone",type==0?0:2);
            pms.put("OrderType",type);
            pms.put("Index",CURRENT_PAGE);
            pms.put("Size",SHOW_NUM);
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
            try {
                JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                if(jsonObject.getBoolean("Success")){
                    //成功状态
                    String result = jsonObject.getString("Result");
                    JSONArray jsonArray = new JSONArray(result);
                    int len = jsonArray.length();
                    ribs.clear();
                    for(int i=0;i<len;i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        ReserItemBean ib = new ReserItemBean();
                        ib.setAdd(object.getString("Address"));
                        ib.setName(object.getString("CName"));
                        ib.setNo(object.getString("OrderCode"));
                        ib.setTel(object.getString("MobilePhone"));
                        int type = Integer.parseInt(getCheckedId());
                       switch (type){
                           case 0:
                               ib.setTime(object.getString("FdtmCreateTime"));
                               break;
                           case 1:
                               ib.setTime("待定");
                               break;
                       }

                        ribs.add(ib);
                    }
                    startHandler.sendEmptyMessage(1);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
    //---------
    private LayoutInflater mLayoutInflater;
    private View dataPop;
    private PopupWindow dataPopupWindow;
    private void dataPop(){
        mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        dataPop = mLayoutInflater.inflate(null, null);
        dataPopupWindow = new PopupWindow(dataPop, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout. LayoutParams.WRAP_CONTENT);
        dataPopupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.color.font_light));
        //android.R.style.TextAppearance_DeviceDefault_Widget_TextView_PopupMenu)
        dataPopupWindow.setAnimationStyle(R.style.picker);

        dataPopupWindow.update();
        dataPopupWindow.setTouchable(true);
        dataPopupWindow.setFocusable(true);



       // testy.setValue(11);

//        DatePicker datePickerStart = (DatePicker) dataPop.findViewById(R.id.datePickerStart);
//        DatePicker datePickerEnd = (DatePicker) dataPop.findViewById(R.id.datePickerEnd);
//        resizePikcer(datePickerStart);
//        setDatePickerDividerColor(datePickerEnd);
//        resizePikcer(datePickerEnd);
//        ViewHelper.setScaleX(datePickerStart,0.8f);
//        ViewHelper.setScaleX(datePickerEnd,0.8f);

    }




    private void showDataPop(View view) {
        if (!dataPopupWindow.isShowing()) {
            // mPopupWindow.showAsDropDown(view,0,0);
            // 第一个参数指定PopupWindow的锚点view，即依附在哪个view上。
            // 第二个参数指定起始点为parent的右下角，第三个参数设置以parent的右下角为原点，向左、上各偏移10像素。
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            dataPopupWindow.showAsDropDown(view);
        }
    }



    private class StartHandler extends Handler {
        WeakReference<Start0Activity> mLeakActivityRef;

        public StartHandler(Start0Activity leakActivity) {
            mLeakActivityRef = new WeakReference<Start0Activity>(leakActivity);
        }

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            if (mLeakActivityRef.get() != null) {
                switch (msg.what){
                    case 1:
                        start0Adapter.updata(ribs);
                        break;
                }
            }
        }
    }
    private String getCheckedId(){
        String tag = null;
        try {
            tag = ((RadioButton)findViewById(nav_grp.getCheckedRadioButtonId())).getTag().toString();
        }catch (Exception e){
        }
        return  tag;
    }
}
