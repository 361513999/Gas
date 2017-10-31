package com.hhkj.gas.www.ui;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.AdapterView;
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
import com.hhkj.gas.www.adapter.Start0Adapter;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.bean.AreaBean;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.FileUtils;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.common.U;
import com.hhkj.gas.www.widget.HeadChildsList;
import com.hhkj.gas.www.widget.LoadView;
import com.hhkj.gas.www.widget.NewToast;
import com.hhkj.gas.www.widget.PullToRefreshView;
import com.hhkj.gas.www.widget.SearchTips;
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

public class Start0Activity extends BaseActivity {
    private StartHandler startHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start0_layout);
        startHandler = new StartHandler(Start0Activity.this);
    }

    private LoadView loadView;
    private RadioGroup nav_grp;
    private RadioButton nav_0, nav_1;
    private ListView gas_list;
    private TextView back;
    private Start0Adapter start0Adapter;
    private PullToRefreshView pull_to_refresh_list;
    private PullToRefreshView.OnHeaderRefreshListener listHeadListener = new PullToRefreshView.OnHeaderRefreshListener() {

        @Override
        public void onHeaderRefresh(PullToRefreshView view) {
            pull_to_refresh_list.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pull_to_refresh_list.onHeaderRefreshComplete();
                    initListReq(ribs.size() == 0 ? SHOWNUM : ribs.size(), null, null, null, null,null);
                    loadList();

                }
            }, runTime);
        }
    };

    /**
     * 列表请求初始化
     *
     * @param num
     */
    private String SORT = null;
    private String Search = null;

    private void initListReq(int num, String AreaId, String beginDate, String endDate, String SORT, String Search) {
        isMore = true;
        CURRENT_PAGE = 1;
        SHOWNUM = num;
        this.AreaId = AreaId == null ? Common.COMMON_DEFAULT : AreaId;
        this.BeginDate = beginDate;
        this.EndDate = endDate;
        this.SORT = SORT;
        this.Search = Search;
        ribs.clear();
    }

    private int SHOWNUM = Common.SHOW_NUM;
    private String AreaId = Common.COMMON_DEFAULT;
    private String BeginDate = null;
    private String EndDate = null;
    private PullToRefreshView.OnFooterRefreshListener listFootListener = new PullToRefreshView.OnFooterRefreshListener() {
        @Override
        public void onFooterRefresh(PullToRefreshView view) {
            pull_to_refresh_list.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pull_to_refresh_list.onFooterRefreshComplete();
                    if (isMore) {
                        loadList();
                    } else {
                        NewToast.makeText(Start0Activity.this, "没有数据可加载", Common.TTIME).show();

                    }

                }
            }, runTime);
        }
    };
    private final int runTime = 400;
    private TextView head_btn;
    private RadioGroup market_group;
    private RadioButton market_group_item0, market_group_item1, market_group_item2, market_group_item3;
    private View drop;
    private LinearLayout get_layout;
    private CheckBox get_all;
    private TextView get_sure, search;

    @Override
    public void init() {
        drop = findViewById(R.id.drop);
        get_layout = (LinearLayout) findViewById(R.id.get_layout);
        get_all = (CheckBox) findViewById(R.id.get_all);
        get_sure = (TextView) findViewById(R.id.get_sure);
        search = (TextView) findViewById(R.id.search);
        pull_to_refresh_list = (PullToRefreshView) findViewById(R.id.pull_to_refresh_list);
        pull_to_refresh_list.setOnHeaderRefreshListener(listHeadListener);
        pull_to_refresh_list.setOnFooterRefreshListener(listFootListener);
        gas_list = (ListView) findViewById(R.id.gas_list);
        start0Adapter = new Start0Adapter(Start0Activity.this, ribs);
        market_group = (RadioGroup) findViewById(R.id.market_group);
        market_group_item0 = (RadioButton) findViewById(R.id.market_group_item0);
        market_group_item1 = (RadioButton) findViewById(R.id.market_group_item1);
        market_group_item2 = (RadioButton) findViewById(R.id.market_group_item2);
        market_group_item3 = (RadioButton) findViewById(R.id.market_group_item3);
        nav_grp = (RadioGroup) findViewById(R.id.nav_grp);
        nav_0 = (RadioButton) findViewById(R.id.nav_0);
        nav_1 = (RadioButton) findViewById(R.id.nav_1);
        gas_list.setAdapter(start0Adapter);
        back = (TextView) findViewById(R.id.back);
        head_btn = (TextView) findViewById(R.id.head_btn);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchTips searchTips = new SearchTips(Start0Activity.this, startHandler);
                searchTips.showSheet();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity(Start0Activity.this);
            }
        });

        if (sharedUtils.getBooleanValue("head")) {
            head_btn.setVisibility(View.VISIBLE);
        } else {
            head_btn.setVisibility(View.GONE);
        }
        head_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //进行任务指派操作
                market_group.clearCheck();
                get_layout.setVisibility(View.VISIBLE);
                get_sure.setText("指派");
                start0Adapter.changeItem(true);
                if (head_btn.getTag().toString().equals("0")) {
                    head_btn.setTag("1");
                    //开启指派模式
                }

            }
        });

        nav_grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                initListReq(Common.SHOW_NUM, null, null, null, null,null);
                loadList();
                /*get_layout.setVisibility(View.GONE);
                start0Adapter.changeItem(false);
                market_group_item0.setChecked(false);*/
                market_group.clearCheck();
                get_all.setChecked(false);
                switch (i) {
                    case R.id.nav_0:
                        if (nav_0.isChecked()) {
                            get_sure.setText("确认");
                        }
                        break;
                    case R.id.nav_1:
                        if (nav_1.isChecked()) {
                            get_sure.setText("领取");
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
                get_layout.setVisibility(View.GONE);
                start0Adapter.changeItem(false);
                head_btn.setTag("0");//重置指派模式
                get_all.setChecked(false);
                switch (checkedId) {
                    case R.id.market_group_item0:

                        if (market_group_item0.isChecked()) {
                            get_layout.setVisibility(View.VISIBLE);
                            start0Adapter.changeItem(true);
                            if (nav_0.isChecked()) {
                                get_sure.setText("确认");
                            }
                            if (nav_1.isChecked()) {
                                get_sure.setText("领取");
                            }

                        }
                        break;
                    case R.id.market_group_item3:
                        if (market_group_item3.isChecked()) {
                            //选中才打开

                            sortPop();
                            showDataPop(sortPopupWindow, drop);
                        }
                        break;
                    case R.id.market_group_item2:
                        if (market_group_item2.isChecked()) {
                            //选中才打开

                            dataPop();
                            showDataPop(dataPopupWindow, drop);
                        }
                        break;
                    case R.id.market_group_item1:
                        if (market_group_item1.isChecked()) {
                            areaPop();
                            showDataPop(areaPopupWindow, drop);
                        }

                        break;
                }
            }
        });

        get_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (head_btn.getTag().toString().equals("1")) {
                    HeadChildsList childsList = new HeadChildsList(Start0Activity.this, sharedUtils, ribs, startHandler);
                    childsList.showSheet();

                } else {

                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < ribs.size(); i++) {
                        ReserItemBean rtb = ribs.get(i);
                        if (rtb.isOpen()) {
                            builder.append(rtb.getId() + ",");
                        }
                    }
                    final String temp = builder.toString();
                    if (temp.length() > 0) {

                        //在这里进行领取任务
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("toKen", sharedUtils.getStringValue("token"));
                            jsonObject.put("cls", "Gas.SecurityOrder");
                            jsonObject.put("method", "AssignOrder");
                            JSONObject object = new JSONObject();
                            object.put("OrderIds", temp);
                            jsonObject.put("param", object.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (loadView == null) {
                            loadView = new LoadView(Start0Activity.this);
                            loadView.showSheet();
                        }

                        OkHttpUtils.postString().url(U.VISTER(U.BASE_URL) + U.LIST).mediaType(MediaType.parse("application/json; charset=utf-8")).content(jsonObject.toString()).build().execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                closeLoad();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                closeLoad();


                                try {
                                    JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                                    if (jsonObject.getBoolean("Success")) {
                                        //领取操作
                                        String[] strs = temp.split(",");
                                        for (int i = 0; i < ribs.size(); i++) {
                                            for (int j = 0; j < strs.length; j++) {
                                                if (ribs.get(i).getId().equals(strs[j])) {
                                                    ribs.remove(i);
                                                }
                                            }
                                        }
                                        head_btn.setTag("0");//重置指派模式
                                        get_all.setChecked(false);
                                        market_group.clearCheck();

                                        NewToast.makeText(Start0Activity.this, "成功确认", Common.TTIME).show();
                                        startHandler.sendEmptyMessage(1);
                                    }else{
                                        NewToast.makeText(Start0Activity.this,jsonObject.getString("Error").contains("已被")?"已被领取":"领取失败",Common.TTIME).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }


                }

            }
        });
        get_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for (int i = 0; i < ribs.size(); i++) {
                    ReserItemBean rtb = ribs.get(i);
                    rtb.setOpen(b);
                }
                start0Adapter.notifyDataSetChanged();
//                startHandler.sendEmptyMessage(1);
            }
        });

        //  loadList(CURRENT_PAGE);
    }


    private ArrayList<ReserItemBean> ribs = new ArrayList<>();
    private int CURRENT_PAGE = 1;
    private RequestCall requestCall;

    private void loadList() {
        if (loadView == null) {
            loadView = new LoadView(Start0Activity.this);
            loadView.showSheet();
        }


        int type = Integer.parseInt(getCheckedId());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("toKen", sharedUtils.getStringValue("token"));
            jsonObject.put("cls", "Gas.SecurityOrder");
            jsonObject.put("method", type == 0 ? "GetSelfOrder" : "GetCommonOrder");
            JSONObject pms = new JSONObject();
            //0 自己和下属的，还包括未领取的，1自己和下属的，2未领取的
            if(sharedUtils.getBooleanValue("head")){
                pms.put("OrderStatus", "1,2,3");
            }else {
                pms.put("OrderStatus", "1,2");
            }


            if (type == 0) {
                pms.put("OrderType", type);
            }

            pms.put("AreaId", AreaId);
            if(Search!=null){
                pms.put("Search",Search);
            }
            if (BeginDate != null && EndDate != null) {
                pms.put("BeginDate", BeginDate);
                pms.put("EndDate", EndDate);
            }
            pms.put("Index", CURRENT_PAGE);
            pms.put("Size", Common.SHOW_NUM);
            jsonObject.put("param", pms.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestCall = OkHttpUtils.postString().url(U.VISTER(U.BASE_URL) + U.LIST).mediaType(MediaType.parse("application/json; charset=utf-8")).content(jsonObject.toString()).build();

        requestCall.execute(stringCallback);
    }

    private void closeLoad() {
        if (loadView != null) {
            loadView.cancle();
            loadView = null;
        }

    }

    private boolean isMore = true;
    private StringCallback stringCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {
            closeLoad();
        }

        @Override
        public void onResponse(String response, int id) {
            closeLoad();

            try {
                JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                if (jsonObject.getBoolean("Success")) {
                    //成功状态
                    String result = jsonObject.getString("Result");
                    JSONArray jsonArray = new JSONArray(result);
                    int len = jsonArray.length();
                    if (len < Common.SHOW_NUM) {
                        isMore = false;
                        startHandler.sendEmptyMessage(2);
                    } else {
                        CURRENT_PAGE = CURRENT_PAGE + 1;
                    }

                    for (int i = 0; i < len; i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        ReserItemBean ib = new ReserItemBean();
                        ib.setAdd(object.getString("Address"));
                        ib.setName(object.getString("CName"));
                        ib.setNo(object.getString("OrderCode"));
                        ib.setId(object.getString("Id"));
                        ib.setTel(object.getString("MobilePhone"));
                        ib.setOrderStatus(object.getInt("OrderStatus"));
                        if(object.getInt("OrderStatus")==3){
                            P.c(object.getString("OrderCode"));
                        }
//                        int type = Integer.parseInt(getCheckedId());
                        int type = object.getInt("OrderType");
                        switch (type) {
                            case 0:
                                ib.setTime(object.getString("SecurityTime"));
                                break;
                            case 1:
                                if(object.isNull("SecurityTime")){
                                    ib.setTime("待定");
                                }else{
                                    ib.setTime(object.getString("SecurityTime"));
                                }

                                break;
                        }

                        ribs.add(ib);
                    }
                    startHandler.sendEmptyMessage(1);

                } else {
                    if (jsonObject.getString("Result").equals(Common.UNLOGIN)) {
                        NewToast.makeText(Start0Activity.this, "未登录", 1000).show();
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
    private void add(ArrayList<AreaBean> rbs, String name, String id) {
        AreaBean ab = new AreaBean();
        ab.setName(name);
        ab.setId(id);
        rbs.add(ab);
    }

    private void sortPop() {
        {
            sortPop = inflater.inflate(R.layout.area_list_layout, null);
            sortPopupWindow = new PopupWindow(sortPop, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            sortPopupWindow.setBackgroundDrawable(getResources().getDrawable(
                    R.color.bcolor));
            sortPopupWindow.setAnimationStyle(android.R.style.TextAppearance_DeviceDefault_Widget_TextView_PopupMenu);
            sortPopupWindow.update();
            sortPopupWindow.setTouchable(true);
            sortPopupWindow.setFocusable(true);
            sortList = (ListView) sortPop.findViewById(R.id.area_list);
            final ArrayList<AreaBean> rbs = new ArrayList<>();
            add(rbs, "单号由低到高", "OrderCode asc");
            add(rbs, "单号由高到低", "OrderCode desc");
            add(rbs, "时间由低到高", "SecurityTime asc");
            add(rbs, "时间由高到低", "SecurityTime desc");
            sortAdapter = new AreasAdapter(Start0Activity.this, rbs);
            sortList.setAdapter(sortAdapter);
            sortPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {

                    // ((RadioButton)findViewById(market_group.getCheckedRadioButtonId())).setChecked(false);
                    // market_group_item2.setChecked(false);
                    market_group.clearCheck();
                }
            });
            sortList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    initListReq(Common.SHOW_NUM, null, null, null, rbs.get(i).getId(),null);
                    loadList();
                    disDataPop(sortPopupWindow, sortPop, null);
                }
            });
            View diss = sortPop.findViewById(R.id.diss);
            diss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    disDataPop(sortPopupWindow, sortPop, null);
                }
            });
        }
    }

    private View dataPop, areaPop, sortPop;
    private PopupWindow dataPopupWindow, areaPopupWindow, sortPopupWindow;
    private ListView area_list, sortList;
    private AreasAdapter areasAdapter, sortAdapter;
    private ArrayList<AreaBean> rbs = new ArrayList<>();

    private void areaPop() {
        areaPop = inflater.inflate(R.layout.area_list_layout, null);
        areaPopupWindow = new PopupWindow(areaPop, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        areaPopupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.color.bcolor));
        areaPopupWindow.setAnimationStyle(android.R.style.TextAppearance_DeviceDefault_Widget_TextView_PopupMenu);
        areaPopupWindow.update();
        areaPopupWindow.setTouchable(true);
        areaPopupWindow.setFocusable(true);
        area_list = (ListView) areaPop.findViewById(R.id.area_list);
        areasAdapter = new AreasAdapter(Start0Activity.this, rbs);
        area_list.setAdapter(areasAdapter);
        area_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                initListReq(Common.SHOW_NUM, rbs.get(i).getId(), null, null, null,null);
                loadList();
                disDataPop(areaPopupWindow, areaPop, new Object[]{area_list, areasAdapter});
            }
        });
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("toKen", sharedUtils.getStringValue("token"));
            jsonObject.put("cls", "Gas.AreaSet");
            jsonObject.put("method", "AreaList");
            jsonObject.put("param", "");
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
                disDataPop(areaPopupWindow, areaPop, new Object[]{area_list, areasAdapter});
            }
        });
        OkHttpUtils.postString().url(U.VISTER(U.BASE_URL) + U.LIST).mediaType(MediaType.parse("application/json; charset=utf-8")).content(jsonObject.toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {


                try {
                    JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                    if (jsonObject.getBoolean("Success")) {
                        //成功状态
                        String result = jsonObject.getString("Result");
                        JSONArray jsonArray = new JSONArray(result);
                        int len = jsonArray.length();
                        rbs.clear();
                        for (int i = 0; i < len; i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            AreaBean ab = new AreaBean();
                            ab.setName(object.getString("AreaName"));
                            ab.setId(object.getString("Id") == null ? Common.COMMON_DEFAULT : object.getString("Id"));
                            rbs.add(ab);
                        }
                        startHandler.sendEmptyMessage(3);
                    } else {
                        if (jsonObject.getString("Result").equals(Common.UNLOGIN)) {
                            NewToast.makeText(Start0Activity.this, "未登录", 1000).show();
                            startHandler.sendEmptyMessage(4);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    private void dataPop() {

        dataPop = inflater.inflate(R.layout.date_double_layout, null);
        dataPopupWindow = new PopupWindow(dataPop, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        dataPopupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.color.bcolor));
        //android.R.style.TextAppearance_DeviceDefault_Widget_TextView_PopupMenu)
        dataPopupWindow.setAnimationStyle(android.R.style.TextAppearance_DeviceDefault_Widget_TextView_PopupMenu);
        dataPopupWindow.update();
        dataPopupWindow.setTouchable(true);
        dataPopupWindow.setFocusable(true);
        final GregorianLunarCalendarView mGLCView0 = (GregorianLunarCalendarView) dataPop.findViewById(R.id.calendar_view_start);
        final GregorianLunarCalendarView mGLCView1 = (GregorianLunarCalendarView) dataPop.findViewById(R.id.calendar_view_end);
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
//                String showToast = calendar0.getTime().compareTo(calendar1.getTime())+"start : " + calendar0.get(Calendar.YEAR) + "-"
//                        + (calendar0.get(Calendar.MONTH) + 1) + "-"
//                        + calendar0.get(Calendar.DAY_OF_MONTH)+"---end"+calendar1.get(Calendar.YEAR) + "-"
//                        + (calendar1.get(Calendar.MONTH) + 1) + "-"
//                        + calendar1.get(Calendar.DAY_OF_MONTH);

                String begin = calendar0.get(Calendar.YEAR) + "-"
                        + (calendar0.get(Calendar.MONTH) + 1) + "-"
                        + calendar0.get(Calendar.DAY_OF_MONTH);
                String end = calendar1.get(Calendar.YEAR) + "-"
                        + (calendar1.get(Calendar.MONTH) + 1) + "-"
                        + calendar1.get(Calendar.DAY_OF_MONTH);
                if (begin.compareTo(end) == 1) {
                    NewToast.makeText(Start0Activity.this, "结束时间小于起始时间", Common.TTIME).show();
                    return;
                }
                //时间筛选
                initListReq(Common.SHOW_NUM, null, begin, end, null,null);
                loadList();
                disDataPop(dataPopupWindow, dataPop, null);
            }
        });
        View diss = dataPop.findViewById(R.id.diss);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disDataPop(dataPopupWindow, dataPop, null);
            }
        });
        diss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disDataPop(dataPopupWindow, dataPop, null);
            }
        });
    }

    private void disDataPop(PopupWindow popupWindow, View v, Object[] objs) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
            v = null;
            if (objs != null) {
                for (int i = 0; i < objs.length; i++) {
                    objs[i] = null;
                }
            }
        }
    }


    private void showDataPop(PopupWindow popupWindow, View view) {
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
        WeakReference<Start0Activity> mLeakActivityRef;

        public StartHandler(Start0Activity leakActivity) {
            mLeakActivityRef = new WeakReference<Start0Activity>(leakActivity);
        }

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            if (mLeakActivityRef.get() != null) {
                switch (msg.what) {
                    case -8:
                        initListReq(Common.SHOW_NUM, null, null, null, null,(String)msg.obj);
                        loadList();
                        break;
                    case 1:
                        start0Adapter.updata(ribs);
                        break;
                    case 2:
                        NewToast.makeText(Start0Activity.this, "最后一页", 1000).show();
                        break;
                    case 3:
                        areasAdapter.updata(rbs);
                        break;
                    case 4:
                        //用户未登录处理
                        reLogin();
                        break;
                    case 5:

//                        HeadTips tips = new HeadTips(Start0Activity.this,getCheckedId());
//                        tips.showSheet();
                        head_btn.setTag("0");//重置指派模式
                        get_all.setChecked(false);
                        market_group.clearCheck();
                        start0Adapter.updata(ribs);
                        NewToast.makeText(Start0Activity.this, "成功指派", Common.TTIME).show();
                        break;
                }
            }
        }
    }

    private String getCheckedId() {
        String tag = null;
        try {
            tag = ((RadioButton) findViewById(nav_grp.getCheckedRadioButtonId())).getTag().toString();
        } catch (Exception e) {
        }
        return tag;
    }
}
