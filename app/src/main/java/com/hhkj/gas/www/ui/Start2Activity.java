package com.hhkj.gas.www.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.hhkj.gas.www.R;
import com.hhkj.gas.www.adapter.AreasAdapter;
import com.hhkj.gas.www.adapter.Start2Adapter;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.bean.AreaBean;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.FileUtils;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.common.U;
import com.hhkj.gas.www.widget.HeadTips;
import com.hhkj.gas.www.widget.LoadView;
import com.hhkj.gas.www.widget.NewToast;
import com.hhkj.gas.www.widget.PullToRefreshView;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

import library.view.GregorianLunarCalendarView;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by Administrator on 2017/8/8/008.
 */

public class Start2Activity extends BaseActivity {
    private StartHandler startHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.start2_layout);
        startHandler = new StartHandler(Start2Activity.this);
    }
    private LoadView loadView;
    private RadioGroup nav_grp;
    private RadioButton nav_0,nav_1;
    private ListView gas_list;
    private TextView back;
    private Start2Adapter start2Adapter;
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
                    loadList();

                }
            },runTime);
        }
    };
    private final int runTime = 400;
    private RadioGroup market_group;
    private RadioButton market_group_item1,market_group_item2,market_group_item3;
    private View drop;

    @Override
    public void init() {
        drop = findViewById(R.id.drop);


        pull_to_refresh_list = (PullToRefreshView) findViewById(R.id.pull_to_refresh_list);
        pull_to_refresh_list.setOnHeaderRefreshListener(listHeadListener);
        pull_to_refresh_list.setOnFooterRefreshListener(listFootListener);
        gas_list = (ListView) findViewById(R.id.gas_list);
        start2Adapter = new Start2Adapter(Start2Activity.this,ribs);
        market_group = (RadioGroup) findViewById(R.id.market_group);
        market_group_item1 = (RadioButton) findViewById(R.id.market_group_item1);
        market_group_item2 = (RadioButton) findViewById(R.id.market_group_item2);
        market_group_item3 = (RadioButton) findViewById(R.id.market_group_item3);
        nav_grp = (RadioGroup) findViewById(R.id.nav_grp);
        nav_0 = (RadioButton) findViewById(R.id.nav_0);
        nav_1 = (RadioButton) findViewById(R.id.nav_1);
        gas_list.setAdapter(start2Adapter);
        back = (TextView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity(Start2Activity.this);
            }
        });
        nav_grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                ribs.clear();
                CURRENT_PAGE = 1;
                loadList();

                market_group.clearCheck();
               switch (i){
                    case R.id.nav_0:
                        if(nav_0.isChecked()){

                        }
                        break;
                    case R.id.nav_1:
                        if(nav_1.isChecked()){

                        }
                        break;
                }
            }
        });
        //默认
        nav_0.setChecked(true);
        //菜单

        market_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {


                switch (checkedId){

                    case R.id.market_group_item2:
                        if(market_group_item2.isChecked()){
                            //选中才打开
                            dataPop();
                            showDataPop(dataPopupWindow,drop);
                        }
                        break;
                    case R.id.market_group_item1:
                        if(market_group_item1.isChecked()){
                            areaPop();
                            showDataPop(areaPopupWindow,drop);
                        }

                        break;
                }
            }
        });



      //  loadList(CURRENT_PAGE);
    }


    private   ArrayList<ReserItemBean> ribs = new ArrayList<>();
    private int CURRENT_PAGE = 1;
    private RequestCall requestCall;
    private void loadList(){
        if(loadView==null){
            loadView = new LoadView(Start2Activity.this);
            loadView.showSheet();
        }


        int type = Integer.parseInt(getCheckedId());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("toKen",sharedUtils.getStringValue("token"));
            jsonObject.put("cls","Gas.SecurityOrder");
            jsonObject.put("method","GetSelfOrder");
            JSONObject pms = new JSONObject();
            //0 自己和下属的，还包括未领取的，1自己和下属的，2未领取的
            pms.put("OrderStatus","4,5");
            pms.put("OrderType",type);
            pms.put("Index",CURRENT_PAGE);
            pms.put("Size",Common.SHOW_NUM);
            jsonObject.put("param",pms.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        P.c("发送"+jsonObject.toString());
        requestCall = OkHttpUtils.postString().url(U.VISTER(U.BASE_URL)+U.LIST).mediaType(MediaType.parse("application/json; charset=utf-8")).content(jsonObject.toString()).build();
        P.c(U.VISTER(U.BASE_URL)+U.LIST);
        requestCall.execute(stringCallback);
    }
    private void closeLoad(){
        if(loadView!=null){
            loadView.cancle();
            loadView = null;
        }

    }
    private StringCallback stringCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {
            closeLoad();
        }

        @Override
        public void onResponse(String response, int id) {
            closeLoad();
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
                    if(len<Common.SHOW_NUM){
                        startHandler.sendEmptyMessage(2);
                    }else{
                        CURRENT_PAGE = CURRENT_PAGE+1;
                    }

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

                }else {
                    if(jsonObject.getString("Result").equals(Common.UNLOGIN)){
                        NewToast.makeText(Start2Activity.this, "未登录", 1000).show();
                        startHandler.sendEmptyMessage(4);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
    /**
     * 关于区域的pop
     */


    /**
     * 关于时间的pop
     */
    private LayoutInflater dateInflater,areaInflater;
    private View dataPop,areaPop;
    private PopupWindow dataPopupWindow,areaPopupWindow;
    private ListView area_list;
    private AreasAdapter areasAdapter;
    private ArrayList<AreaBean> rbs = new ArrayList<>();
    private void areaPop(){
        areaInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        areaPop = areaInflater.inflate(R.layout.area_list_layout,null);
        areaPopupWindow = new PopupWindow(areaPop,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        areaPopupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.color.bcolor));
        areaPopupWindow.setAnimationStyle(android.R.style.TextAppearance_DeviceDefault_Widget_TextView_PopupMenu);
        areaPopupWindow.update();
        areaPopupWindow.setTouchable(true);
        areaPopupWindow.setFocusable(true);
        area_list = (ListView) areaPop.findViewById(R.id.area_list);
        areasAdapter = new AreasAdapter(Start2Activity.this,rbs);
        area_list.setAdapter(areasAdapter);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("toKen",sharedUtils.getStringValue("token"));
            jsonObject.put("cls","Gas.AreaSet");
            jsonObject.put("method","AreaList");
            jsonObject.put("param","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        areaPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                // ((RadioButton)findViewById(market_group.getCheckedRadioButtonId())).setChecked(false);
                // market_group_item2.setChecked(false);
                market_group.clearCheck();
            }
        });
        View diss = areaPop.findViewById(R.id.diss);
        diss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disDataPop(areaPopupWindow,areaPop,new Object[]{area_list,areasAdapter});
            }
        });
        OkHttpUtils.postString().url(U.VISTER(U.BASE_URL)+U.LIST).mediaType(MediaType.parse("application/json; charset=utf-8")).content(jsonObject.toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {


                try {
                    JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                    if(jsonObject.getBoolean("Success")){
                        //成功状态
                        String result = jsonObject.getString("Result");
                        JSONArray jsonArray = new JSONArray(result);
                        int len = jsonArray.length();
                        rbs.clear();
                        for(int i=0;i<len;i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            AreaBean ab = new AreaBean();
                            ab.setName(object.getString("AreaName"));
                            ab.setId(object.getString("AreaCode"));
                            rbs.add(ab);
                        }
                        startHandler.sendEmptyMessage(3);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    private void dataPop(){
        dateInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        dataPop = dateInflater.inflate(R.layout.date_double_layout, null);
        dataPopupWindow = new PopupWindow(dataPop, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout. LayoutParams.MATCH_PARENT);
        dataPopupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.color.bcolor));
        //android.R.style.TextAppearance_DeviceDefault_Widget_TextView_PopupMenu)
        dataPopupWindow.setAnimationStyle(android.R.style.TextAppearance_DeviceDefault_Widget_TextView_PopupMenu);

        dataPopupWindow.update();
        dataPopupWindow.setTouchable(true);
        dataPopupWindow.setFocusable(true);
        final GregorianLunarCalendarView  mGLCView0 = (GregorianLunarCalendarView) dataPop.findViewById(R.id.calendar_view_start);
        final GregorianLunarCalendarView  mGLCView1 = (GregorianLunarCalendarView) dataPop.findViewById(R.id.calendar_view_end);
        mGLCView0.init();//init has no scroll effection, to today
        mGLCView1.init();
        dataPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

               // ((RadioButton)findViewById(market_group.getCheckedRadioButtonId())).setChecked(false);
               // market_group_item2.setChecked(false);
                market_group.clearCheck();
            }
        });
        TextView sure = (TextView) dataPop.findViewById(R.id.sure);
        TextView cancle = (TextView) dataPop.findViewById(R.id.cancle);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GregorianLunarCalendarView.CalendarData calendarData0 = mGLCView0.getCalendarData();
                Calendar calendar0 = calendarData0.getCalendar();
                GregorianLunarCalendarView.CalendarData calendarData1 = mGLCView1.getCalendarData();
                Calendar calendar1 = calendarData1.getCalendar();
                //相同是0   第一个大于第二个是1   第一个小于第二个是-1
                String showToast = calendar0.getTime().compareTo(calendar1.getTime())+"start : " + calendar0.get(Calendar.YEAR) + "-"
                        + (calendar0.get(Calendar.MONTH) + 1) + "-"
                        + calendar0.get(Calendar.DAY_OF_MONTH)+"---end"+calendar1.get(Calendar.YEAR) + "-"
                        + (calendar1.get(Calendar.MONTH) + 1) + "-"
                        + calendar1.get(Calendar.DAY_OF_MONTH);

                NewToast.makeText(Start2Activity.this,showToast,1000).show();

            }
        });
        View diss = dataPop.findViewById(R.id.diss);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disDataPop(dataPopupWindow,dataPop,null);
            }
        });
        diss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disDataPop(dataPopupWindow,dataPop,null);
            }
        });
    }
    private void disDataPop(PopupWindow popupWindow,View v,Object[] objs){
        if(popupWindow!=null&&popupWindow.isShowing()){
            popupWindow.dismiss();
            popupWindow = null;
            v = null;
            if(objs!=null){
                for(int i=0;i<objs.length;i++){
                    objs[i] = null;
                }
            }
        }
    }




    private void showDataPop(PopupWindow popupWindow,View view) {
        if (!popupWindow.isShowing()) {
            // mPopupWindow.showAsDropDown(view,0,0);
            // 第一个参数指定PopupWindow的锚点view，即依附在哪个view上。
            // 第二个参数指定起始点为parent的右下角，第三个参数设置以parent的右下角为原点，向左、上各偏移10像素。
            int[] location = new int[2];

            view.getLocationOnScreen(location);

            popupWindow.showAsDropDown(view);
        }
    }



    private class StartHandler extends Handler {
        WeakReference<Start2Activity> mLeakActivityRef;

        public StartHandler(Start2Activity leakActivity) {
            mLeakActivityRef = new WeakReference<Start2Activity>(leakActivity);
        }

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            if (mLeakActivityRef.get() != null) {
                switch (msg.what){
                    case 1:
                        start2Adapter.updata(ribs);
                        break;
                    case 2:
                        NewToast.makeText(Start2Activity.this,"最后一页",1000).show();
                        break;
                    case 3:
                        areasAdapter.updata(rbs);
                        break;
                    case 4:
                        //用户未登录处理

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
