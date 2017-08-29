package com.hhkj.gas.www.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.adapter.AreasAdapter;
import com.hhkj.gas.www.adapter.Start1Adapter;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.bean.AreaBean;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import library.view.GregorianLunarCalendarView;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by Administrator on 2017/8/8/008.
 */

public class Start1Activity extends BaseActivity {
    private StartHandler startHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.start1_layout);
        startHandler = new StartHandler(Start1Activity.this);
    }
    private LoadView loadView;
    private RadioGroup nav_grp;
    private RadioButton nav_0,nav_1;
    private ListView gas_list;
    private TextView back;
    private Start1Adapter start1Adapter;
    private PullToRefreshView pull_to_refresh_list;
    private PullToRefreshView.OnHeaderRefreshListener listHeadListener = new PullToRefreshView.OnHeaderRefreshListener(){

        @Override
        public void onHeaderRefresh(PullToRefreshView view) {
            pull_to_refresh_list.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pull_to_refresh_list.onHeaderRefreshComplete();
                    isMore = false;
                    CURRENT_PAGE = 1;
                    SHOWNUM = ribs.size()==0?SHOWNUM:ribs.size();
                    ribs.clear();
                    loadList();
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
                    if(isMore){
                        loadList();
                    }else{
                        NewToast.makeText(Start1Activity.this,"没有数据可加载",Common.TTIME).show();

                    }

                }
            },runTime);
        }
    };
    private ArrayList<StaffImageItem> staffImages = new ArrayList<>();//图片
    private Map<String,Object> txtListMap = new HashMap<>();//未处理的栏目
    private ArrayList<DetailStaff> dss = new ArrayList<>();//栏目
    private ArrayList<StaffB> staffBs = new ArrayList<>();//燃气表
    private ArrayList<StaffQj> staffQjs = new ArrayList<>();//燃气具
    /**
     * 初始化详细数据信息
     */
    private void loadData(final ReserItemBean bean){
        final JSONObject object = new JSONObject();
        try {
            object.put("cls","Gas.SecurityOrder");
            object.put("method","GetOrderDtl");
            object.put("toKen",sharedUtils.getStringValue("token"));
            JSONObject obj = new JSONObject();
            obj.put("OrderId",bean.getId());
            object.put("param",obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(loadView==null){
            loadView = new LoadView(Start1Activity.this);
            loadView.showSheet();
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
                //0 安检条目  1安检图片   2燃气具
                if(loadView!=null){
                    loadView.cancle();
                    loadView = null;
                }
                try {
                    JSONObject jsonObject = new JSONObject( FileUtils.formatJson(response));
                    if(jsonObject.getBoolean("Success")){
                        String result = jsonObject.getString("Result");
                        JSONArray jsonArray = new JSONArray(result);
                        P.c(jsonArray.toString());
                        int len = jsonArray.length();
                        //解析安检条目，燃气具，安检图片信息
                        staffImages.clear();
                        staffQjs.clear();
                        txtListMap.clear();
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                            switch (obj.getInt("ItemType")){
                                case 0:
                                    //安检条目
                                    String key = obj.getString("ItemGroup");
                                    String tag = obj.getString("ItemName");
                                    StaffTxtItem item0 = new StaffTxtItem();
                                    item0.setId(obj.getString("Id"));
                                    item0.setTxt(tag);

                                    if(key.length()==0){
                                        //单独的数据
                                        txtListMap.put(tag,item0);
                                    }else{
                                        if(txtListMap.containsKey(key)){
                                            //存在这个组就添加
                                            ((ArrayList<StaffTxtItem>)txtListMap.get(key)).add(item0);
                                        }else{
                                            ArrayList<StaffTxtItem> txts = new ArrayList<StaffTxtItem>();
                                            txts.add(item0);
                                            txtListMap.put(key,txts);
                                        }
                                    }
                                    break;
                                case 1:
                                    //安检图片
                                    StaffImageItem item = new StaffImageItem();
                                    item.setTag(obj.getString("ItemName"));
                                    item.setId(obj.getString("Id"));
                                    staffImages.add(item);
                                    break;
                                case 2:
                                    //燃气具
                                    StaffQj tab = new StaffQj();
                                    tab.setId(obj.getString("Id"));
                                    tab.setName(obj.getString("ItemName"));
                                    tab.setPosition(obj.getString("ItemGroup"));
                                    staffQjs.add(tab);
                                    break;
                            }

                        }

                        String value = jsonObject.getString("Value");
                        JSONArray values = new JSONArray(value);
                        int jen = values.length();
                        staffBs.clear();
                        for(int i=0;i<jen;i++){
                            JSONObject obj = values.getJSONObject(i);
                            StaffB staffB = new StaffB();
                            staffB.setId(obj.getString("TabId"));
                            staffB.setName(obj.getString("TabCode"));
                            staffB.setValue(obj.getString("TabQty"));
                            staffBs.add(staffB);
                        }
                       Message msg = new Message();
                        msg.what = 5;
                        msg.obj = bean;
                        startHandler.sendMessage(msg);
                        P.c(values.toString());
                    }else{
                        if(jsonObject.getString("Result").equals(Common.UNLOGIN)){
                            NewToast.makeText(Start1Activity.this, "未登录", 1000).show();
                            startHandler.sendEmptyMessage(4);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private final int runTime = 400;
    private RadioGroup market_group;
    private RadioButton market_group_item0,market_group_item1,market_group_item2,market_group_item3;
    private View drop;

    @Override
    public void init() {
        drop = findViewById(R.id.drop);


        pull_to_refresh_list = (PullToRefreshView) findViewById(R.id.pull_to_refresh_list);
        pull_to_refresh_list.setOnHeaderRefreshListener(listHeadListener);
        pull_to_refresh_list.setOnFooterRefreshListener(listFootListener);
        gas_list = (ListView) findViewById(R.id.gas_list);
        start1Adapter = new Start1Adapter(Start1Activity.this,ribs,sharedUtils,startHandler);
        market_group = (RadioGroup) findViewById(R.id.market_group);
        market_group_item0 = (RadioButton) findViewById(R.id.market_group_item0);
        market_group_item1 = (RadioButton) findViewById(R.id.market_group_item1);
        market_group_item2 = (RadioButton) findViewById(R.id.market_group_item2);
        market_group_item3 = (RadioButton) findViewById(R.id.market_group_item3);
        nav_grp = (RadioGroup) findViewById(R.id.nav_grp);
        nav_0 = (RadioButton) findViewById(R.id.nav_0);
        nav_1 = (RadioButton) findViewById(R.id.nav_1);
        gas_list.setAdapter(start1Adapter);

        back = (TextView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity(Start1Activity.this);
            }
        });
        nav_grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                ribs.clear();
                isMore = true;
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
                    case R.id.market_group_item0:
                        break;
                    case R.id.market_group_item2:
                        if(market_group_item2.isChecked()){
                            //选中才打开
                           // dataPop();
                           // showDataPop(dataPopupWindow,drop);
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
    private String AreaId = "Guid.Empty";
    private int SHOWNUM = Common.SHOW_NUM;
    private   ArrayList<ReserItemBean> ribs = new ArrayList<>();
    private int CURRENT_PAGE = 1;
    private RequestCall requestCall;
    private void loadList(){
        if(loadView==null){
            loadView = new LoadView(Start1Activity.this);
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
            pms.put("OrderStatus","3,4,6,7,8,9");
            pms.put("OrderType",type);
           pms.put("AreaId",AreaId);
            pms.put("Index",CURRENT_PAGE);
            pms.put("Size",SHOWNUM);
            jsonObject.put("param",pms.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestCall = OkHttpUtils.postString().url(U.VISTER(U.BASE_URL)+U.LIST).mediaType(MediaType.parse("application/json; charset=utf-8")).content(jsonObject.toString()).build();

        requestCall.execute(stringCallback);
    }
    private void closeLoad(){
        if(loadView!=null){
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
                if(jsonObject.getBoolean("Success")){
                    //成功状态
                    String result = jsonObject.getString("Result");
                    JSONArray jsonArray = new JSONArray(result);
                    int len = jsonArray.length();
                    if(len<SHOWNUM){
                        isMore = false;
                        startHandler.sendEmptyMessage(2);
                    }else{
                        CURRENT_PAGE = CURRENT_PAGE+1;
                    }

                    for(int i=0;i<len;i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        ReserItemBean ib = new ReserItemBean();
                        ib.setId(object.getString("Id"));
                        ib.setAdd(object.getString("Address"));
                        ib.setName(object.getString("CName"));
                        ib.setNo(object.getString("OrderCode"));
                        ib.setTel(object.getString("MobilePhone"));
                        ib.setOrderStatus(object.getInt("OrderStatus")==9?7:object.getInt("OrderStatus"));
//                        int type = Integer.parseInt(getCheckedId());
                        ib.setStaffName(object.getString("StaffName"));
                        int type = object.getInt("OrderType");
                       switch (type){
                           case 0:
                               ib.setTime(object.getString("FdtmCreateTime"));
                               break;
                           case 1:
                               //
//                               SecurityTime

                               if(object.isNull("SecurityTime")){
                                   ib.setTime("NON");
                               }else{
                                   ib.setTime(object.getString("SecurityTime"));
                               }

                               break;
                       }

                        ribs.add(ib);
                    }
                    startHandler.sendEmptyMessage(1);

                }else {
                    if(jsonObject.getString("Result").equals(Common.UNLOGIN)){
                        NewToast.makeText(Start1Activity.this, "未登录", 1000).show();
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
    private View dataPop,areaPop;
    private PopupWindow dataPopupWindow,areaPopupWindow;
    private ListView area_list;
    private AreasAdapter areasAdapter;
    private ArrayList<AreaBean> rbs = new ArrayList<>();
    private void areaPop(){

        areaPop = inflater.inflate(R.layout.area_list_layout,null);
        areaPopupWindow = new PopupWindow(areaPop,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        areaPopupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.color.bcolor));
        areaPopupWindow.setAnimationStyle(android.R.style.TextAppearance_DeviceDefault_Widget_TextView_PopupMenu);
        areaPopupWindow.update();
        areaPopupWindow.setTouchable(true);
        areaPopupWindow.setFocusable(true);
        area_list = (ListView) areaPop.findViewById(R.id.area_list);
        areasAdapter = new AreasAdapter(Start1Activity.this,rbs);
        area_list.setAdapter(areasAdapter);
        area_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                isMore = false;
                CURRENT_PAGE = 1;
                SHOWNUM = Common.SHOW_NUM;
                AreaId = rbs.get(i).getId();
                loadList();
                disDataPop(areaPopupWindow,areaPop,new Object[]{area_list,areasAdapter});
            }
        });
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
                            ab.setId(object.getString("AreaCode")==null?"Guid.Empty":object.getString("AreaCode"));
                            rbs.add(ab);
                        }
                        startHandler.sendEmptyMessage(3);
                    }else {
                        if(jsonObject.getString("Result").equals(Common.UNLOGIN)){
                            NewToast.makeText(Start1Activity.this, "未登录", 1000).show();
                            startHandler.sendEmptyMessage(4);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    private void dataPop(){

        dataPop = inflater.inflate(R.layout.date_double_layout, null);
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

                NewToast.makeText(Start1Activity.this,showToast,1000).show();

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
        WeakReference<Start1Activity> mLeakActivityRef;

        public StartHandler(Start1Activity leakActivity) {
            mLeakActivityRef = new WeakReference<Start1Activity>(leakActivity);
        }

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            if (mLeakActivityRef.get() != null) {
                switch (msg.what){
                    case 1:
                        start1Adapter.updata(ribs);
                        break;
                    case 2:
                        NewToast.makeText(Start1Activity.this,"最后一页",1000).show();
                        break;
                    case 3:
                        areasAdapter.updata(rbs);
                        break;
                    case 4:
                        //用户未登录处理
                        reLogin();
                        break;
                    case 5:
                        //装载数据
                        ReserItemBean bean = (ReserItemBean) msg.obj;
                        //进行数据更新

                        //解析安检项目数据
                        Set set =  txtListMap.keySet();
                        Iterator it = set.iterator();

                        dss.clear();
                        while(it.hasNext()){
                            String key =  it.next().toString();
                            Object obj = txtListMap.get(key);
                            if(obj instanceof StaffTxtItem){
                                //普通
                                StaffTxtItem item = (StaffTxtItem) obj;
                                DetailStaff ds = new DetailStaff();
                                ds.setItem(item);
                                dss.add(ds);
                                P.c("单项:"+item.getTxt());
                            }else  if(obj instanceof  ArrayList){
                                ArrayList<StaffTxtItem> items = (ArrayList<StaffTxtItem>) obj;
                                DetailStaff ds = new DetailStaff();
                                ds.setItems(items);
                                ds.setItems_tag(key);
                                dss.add(ds);
                                for(int i=0;i<items.size();i++){
                                    StaffTxtItem item = items.get(i);
                                    P.c("复项:"+item.getTxt());
                                }

                            }
                        }
                        DB.getInstance().addStaff(bean,staffImages,dss,staffBs,staffQjs);
//                        Common.copy();
                        Message mg = new Message();
                        mg.what = 6;
                        mg.obj = bean;
                        startHandler.sendMessage(mg);
                        //整理完毕
                        break;
                    case 6:
                        ReserItemBean obj = (ReserItemBean) msg.obj;
                        Intent intent = new Intent(Start1Activity.this,DetailActivity.class);
                        intent.putExtra("obj",obj);
                        startActivity(intent);
                        break;
                    case 7:
                        int position = msg.arg1;
                        ReserItemBean ba =  ribs.get(position);
                        if(DB.getInstance().isExitsId(ba.getId(),ba.getNo())==0){
                            loadData(ba);
                        }else {
                            //直接装载数据

                            Message mg7 = new Message();
                            mg7.what = 6;
                            mg7.obj = ba;
                            startHandler.sendMessage(mg7);
                        }
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
