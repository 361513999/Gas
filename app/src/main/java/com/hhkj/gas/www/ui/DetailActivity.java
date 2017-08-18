package com.hhkj.gas.www.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
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
import com.hhkj.gas.www.bean.DetailBItem;
import com.hhkj.gas.www.bean.DetailStaff;
import com.hhkj.gas.www.bean.ReserItemBean;

import com.hhkj.gas.www.bean.StaffImageItem;
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
import com.hhkj.gas.www.widget.LinePathView;
import com.hhkj.gas.www.widget.LoadView;
import com.hhkj.gas.www.widget.NewToast;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
        if(intent.hasExtra("obj")){
            bean = (ReserItemBean) intent.getSerializableExtra("obj");
        }
    }
    private void copy(){
        CopyFile cf = new CopyFile();
        cf.copyFile("data/data/com.hhkj.gas.www/databases/"+Common.DB_NAME,Common.BASE_DIR +"/droid4xShare/2.db");
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
                switch (msg.what){
                    case 1:


                        //进行数据更新
                        int imageLen = staffImages.size();
                        item7.setNumColumns(imageLen);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( FileUtils.dip2px(DetailActivity.this,110)*imageLen,LinearLayout.LayoutParams.WRAP_CONTENT);
                        item7.setLayoutParams(params);
                        imageAdapter.updata(staffImages);
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
                        DB.getInstance().addStaff(bean,staffImages,dss);
                        copy();
                        //整理完毕
                        if(dss.size()>SHOW_STAFF){
                            staffItemAdapter.updata(dss,SHOW_STAFF);
                            item9.setVisibility(View.VISIBLE);
                        }else{
                            staffItemAdapter.updata(dss);
                            item9.setVisibility(View.GONE);
                        }

                        break;
                    case 2:

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
    private TextView item0,item1,item2,item3,item4,item5,item6,item9,item13;
    private ImageView item_edit;
    private GridView item7;
    private ImageView item15,item16;
    private LinearLayout item18,item19;
    //图片
    private DetailImageAdapter imageAdapter;
    private LoadView loadView;
    private InScrollListView item8,item14;
    //安检条目
    private StaffItemAdapter staffItemAdapter;
    //器具
    private DetailBAdapter bAdapter;
    private ArrayList<DetailBItem> detailBItems = new ArrayList<>();
    @Override
    public void init() {
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

        item18 = (LinearLayout) findViewById(R.id.item18);
        item19 = (LinearLayout) findViewById(R.id.item19);
         imageAdapter = new DetailImageAdapter(DetailActivity.this,staffImages);
         item7.setAdapter(imageAdapter);

        staffItemAdapter = new StaffItemAdapter(DetailActivity.this,dss);
        item8.setAdapter(staffItemAdapter);

        bAdapter = new DetailBAdapter(DetailActivity.this,detailBItems);
        item14.setAdapter(bAdapter);
         item_edit = (ImageView) findViewById(R.id.item_edit);
         item0.setText(getString(R.string.nor_item_txt0,bean.getNo()));
         item1.setText(getString(R.string.nor_item_txt1,bean.getName()));
         item2.setText(getString(R.string.nor_item_txt2,bean.getTel()));
         item3.setText(getString(R.string.nor_item_txt3,bean.getAdd()));
         if(bean.getTime().equals("NON")){
            item4.setText(getString(R.string.nor_item_txt4,"____-__-__ __:__"));
         }else{
            item4.setText(getString(R.string.nor_item_txt4,bean.getTime()));
         }
         String status = "";
         switch (bean.getOrderStatus()){
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
        item5.setText(getString(R.string.curr_status,status));
        item6.setText(getString(R.string.curr_person,bean.getStaffName()));

        item9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dss.size()>SHOW_STAFF){
                    staffItemAdapter.updata(dss);
                    item9.setVisibility(View.GONE);
                }
            }
        });
        loadData();
        item_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeTime changeTime = new ChangeTime(DetailActivity.this,sharedUtils,bean);
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
                DetailBItem bItem = new DetailBItem();
                detailBItems.add(bItem);

               bAdapter.updata(detailBItems);
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
                startActivity(intent);
            }
        });
    }
    private ArrayList<StaffImageItem> staffImages = new ArrayList<>();
    private Map<String,Object> txtListMap = new HashMap<>();
    private ArrayList<DetailStaff> dss = new ArrayList<>();
    /**
     * 初始化详细数据信息
     */
    private void loadData(){
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
            loadView = new LoadView(DetailActivity.this);
            loadView.showSheet();
        }
        OkHttpUtils.postString().url(U.VISTER(U.BASE_URL)+U.LIST).mediaType(MediaType.parse("application/json; charset=utf-8")).content(object.toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                P.c("错误了"+e.getLocalizedMessage());

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

                                    break;
                            }

                        }
                        detailHandler.sendEmptyMessage(1);


                        String value = jsonObject.getString("Value");
                        JSONArray values = new JSONArray(value);
                        P.c(values.toString());
                    }else{
                           if(jsonObject.getString("Result").equals(Common.UNLOGIN)){
                            NewToast.makeText(DetailActivity.this, "未登录", 1000).show();
                               detailHandler.sendEmptyMessage(4);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
