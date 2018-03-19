package com.hhkj.gas.www.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hhkj.gas.www.R;
import com.hhkj.gas.www.adapter.DetailBAdapter;
import com.hhkj.gas.www.adapter.DetailBt_Item6Adapter;
import com.hhkj.gas.www.adapter.DetailImageAdapter;
import com.hhkj.gas.www.adapter.StaffItemAdapter;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.bean.DetailStaff;
import com.hhkj.gas.www.bean.ImageRdy;
import com.hhkj.gas.www.bean.ReserItemBean;

import com.hhkj.gas.www.bean.StaffB;
import com.hhkj.gas.www.bean.StaffImageItem;
import com.hhkj.gas.www.bean.StaffQj;
import com.hhkj.gas.www.bean.StaffTxtItem;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.CopyFile;
import com.hhkj.gas.www.common.FileUtils;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.common.SharedUtils;
import com.hhkj.gas.www.common.TimeUtil;
import com.hhkj.gas.www.common.U;
import com.hhkj.gas.www.db.DB;
import com.hhkj.gas.www.inter.PhotoSelect;
import com.hhkj.gas.www.inter.TimeSelect;
import com.hhkj.gas.www.utils.ImageUtil;
import com.hhkj.gas.www.widget.CallTips;
import com.hhkj.gas.www.widget.ChangeTime;
import com.hhkj.gas.www.widget.CommonLogin;
import com.hhkj.gas.www.widget.CommonPhotoPop;
import com.hhkj.gas.www.widget.DetailImageDlg;
import com.hhkj.gas.www.widget.DetailTips;
import com.hhkj.gas.www.widget.HeadTips;
import com.hhkj.gas.www.widget.InScrollListView;
import com.hhkj.gas.www.widget.LoadView;
import com.hhkj.gas.www.widget.NewToast;
import com.hhkj.gas.www.widget.UploadTips;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.LubanOptions;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class DetailActivity extends TakePhotoActivity {
    private LinearLayout proble;
    private TextView back;
    private ReserItemBean bean;
    private DetailHandler detailHandler;
    public SharedUtils sharedUtils;
    public LayoutInflater inflater;
    private LoadView loadView;
    public void backActivity(){
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
    /**
     * 重新登录
     */
    public void reLogin(){
        CommonLogin login = new CommonLogin(this,sharedUtils);
        login.showSheet();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        takePhoto = getTakePhoto();
        sharedUtils = new SharedUtils(Common.config);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        AppManager.getAppManager().addActivity(this);
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
                    case 5:
                       new Thread(){
                           @Override
                           public void run() {
                               super.run();
                               DB.getInstance().updataStaffImageItem(staffImages, bean);
                               detailHandler.sendEmptyMessage(51);
                           }
                       }.start();
                        break;
                    case 51:
                        //装载图片列表
                        //控制图片
                        int imageLen = staffImages.size();
                        item7.setNumColumns(imageLen);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(FileUtils.dip2px(DetailActivity.this, 90) * imageLen, LinearLayout.LayoutParams.WRAP_CONTENT);
                        item7.setLayoutParams(params);
                        imageAdapter.updata(staffImages);
                        break;
                    case 70:
                        DB.getInstance().updataStand(bean);
                        detailHandler.sendEmptyMessage(71);
                        break;
                    case 71:
                        //装载基础数据
                        item0.setText(getString(R.string.nor_item_txt0, bean.getNo()));
                        item1.setText(getString(R.string.nor_item_txt1, bean.getName()));
                        item2.setText(getString(R.string.nor_item_txt2, bean.getTel()));
                        item2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CallTips callTips = new CallTips(DetailActivity.this);
                                callTips.showSheet(bean.getTel());
                        }
                        });
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
                            case 11:
                                status = "隐患已解除";
                                break;


                        }
                        String vs = getString(R.string.curr_status, status);
                        SpannableStringBuilder sbBuilder=new SpannableStringBuilder(vs);

                        sbBuilder.setSpan
                                (new ForegroundColorSpan(Color.parseColor("#0084ff")), 5, vs.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                        item5.setText(sbBuilder);
                        item6.setText(getString(R.string.curr_person, bean.getStaffName()));

                        staffItemAdapter.changeClick(true);
//                        bean.setStaffTag(DB.getInstance().getStafftag(bean));
//                        P.c("staffTag"+DB.getInstance().getStafftag(bean));
                        P.c("bean.getStaffTag()"+bean.getStaffTag());
                        tag_view_no.setVisibility(View.GONE);
                        tag_view_ok.setVisibility(View.GONE);
                        tag_con.setVisibility(View.VISIBLE);
//                        item10.setVisibility(View.VISIBLE);
//                        item11.setVisibility(View.VISIBLE);
//                        item12.setVisibility(View.VISIBLE);
                            if(bean.getStaffTag()!=null&&bean.getStaffTag().equals("Y")){
                                staffItemAdapter.changeClick(false);

                                tag_view_no.setVisibility(View.GONE);
                                tag_view_jc.setVisibility(View.GONE);
                                tag_view_ok.setVisibility(View.VISIBLE);
                                tag_con.setVisibility(View.GONE);
//                                item10.setVisibility(View.GONE);
//                                item12.setVisibility(View.GONE);
//                                RelativeLayout.LayoutParams par = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
//                                par.addRule(RelativeLayout.CENTER_IN_PARENT);
//                                item11.setLayoutParams(par);
                            }
                            if(bean.getStaffTag()!=null&&bean.getStaffTag().equals("N")){
                                staffItemAdapter.changeClick(false);
                                tag_view_no.setVisibility(View.VISIBLE);
                                tag_view_ok.setVisibility(View.GONE);
                                tag_view_jc.setVisibility(View.GONE);
                                tag_con.setVisibility(View.GONE);

//                                item11.setVisibility(View.GONE);
//                                item12.setVisibility(View.GONE);
//                                RelativeLayout.LayoutParams par = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
//                                par.addRule(RelativeLayout.CENTER_IN_PARENT);
//                                item10.setLayoutParams(par);
                            }
                        if(bean.getStaffTag()!=null&&bean.getStaffTag().equals("J")){
                            staffItemAdapter.changeClick(false);
                            tag_view_no.setVisibility(View.GONE);
                            tag_view_ok.setVisibility(View.GONE);
                            tag_view_jc.setVisibility(View.VISIBLE);
                            tag_con.setVisibility(View.GONE);
//                                item11.setVisibility(View.GONE);
//                                item12.setVisibility(View.GONE);
//                                RelativeLayout.LayoutParams par = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
//                                par.addRule(RelativeLayout.CENTER_IN_PARENT);
//                                item10.setLayoutParams(par);
                        }



                        proble.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clickDel();
                                if(DB.getInstance().isExitPro(bean)){
                                    goProblem();
                                }else{
                                    NewToast.makeText(DetailActivity.this,"暂无隐患单",2000).show();
                                }
                            }
                        });
                        view_problem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clickDel();
                                if(DB.getInstance().isExitPro(bean)){
                                    goProblem();
                                }else{
                                    NewToast.makeText(DetailActivity.this,"暂无隐患单",2000).show();
                                }
                            }
                        });


                        //----------
                        break;
                    case 11:
                        DB.getInstance().updataStaffBs(staffBs,bean);
                        DB.getInstance().updataStaffQj(staffQjs,bean);
                        detailHandler.sendEmptyMessage(1);
                        break;
                    case 12:
                        DB.getInstance().getStaffPrint(printMap,bean);
                        detailHandler.sendEmptyMessage(22);
                        break;
                    case 22:
                        //装载签名
                        ImageLoader.getInstance().displayImage("file://"+printMap.get("staffLine"),item15);
                        ImageLoader.getInstance().displayImage("file://"+printMap.get("personLine"),item16);
                        ImageLoader.getInstance().displayImage("file://"+printMap.get("personPhoto"),item17);
                        ImageLoader.getInstance().displayImage("file://"+printMap.get("staffOtherLine"),item20);
                        break;
                    case 1:
                        P.c("何时装载");
                        //装载安检条目

                        //装载燃气表
                        if(staffBs.size()==1){
                            item_b00.setText(staffBs.get(0).getName());
                            if(staffBs.get(0).getName()!=null&&staffBs.get(0).getName().length()!=0){
                                item_b00.setEnabled(false);
                            }
                            item_b01.setText(staffBs.get(0).getValue());
                            item_b02.setChecked(staffBs.get(0).isCheck());
                            StaffB staffB = new StaffB();
                            staffB.setI(-1);
                            staffBs.add(staffB);
                        }else if(staffBs.size()>1){
                            if(staffBs.get(0).getName()!=null&&staffBs.get(0).getName().length()!=0){
                                item_b00.setEnabled(false);
                            }
                            item_b00.setText(staffBs.get(0).getName());
                            item_b01.setText(staffBs.get(0).getValue());
                            item_b02.setChecked(staffBs.get(0).isCheck());
                            item_b10.setText(staffBs.get(1).getName());
                            item_b11.setText(staffBs.get(1).getValue());
                            item_b12.setChecked(staffBs.get(1).isCheck());
                        }
                    {
                        item_b02.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                staffBs.get(0).setCheck(isChecked);
                            }
                        });
                        item_b12.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                staffBs.get(1).setCheck(isChecked);
                            }
                        });
                        //--1
                        item_b00.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                staffBs.get(0).setName(s.toString().trim());
                            }
                        });
                        //--2
                        item_b01.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                staffBs.get(0).setValue(s.toString().trim());
                            }
                        });
                        item_b10.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                staffBs.get(1).setName(s.toString().trim());
                            }
                        });
                        item_b11.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                staffBs.get(1).setValue(s.toString().trim());
                            }
                        });

                    }

                        //装载燃气具
                        bAdapter.updata(staffQjs);

                        break;
                    case -22:

                        Intent intent = new Intent(DetailActivity.this, CommonLineActivity.class);
                        intent.putExtra("obj",bean);
                        intent.putExtra("staffPrint",-1);
                        startActivityForResult(intent,99);
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
                    case 6:
                        //更新安检栏目
                        P.c("更新栏目");
                       new Thread(){
                           @Override
                           public void run() {
                               super.run();
                               DB.getInstance().updataStaffTxtItem(txtListMap, bean);
                               parseTxtItem();
                               detailHandler.sendEmptyMessage(61);
                           }
                       }.start();
                        break;
                    case 61:
                        //控制安检条目
                        if (dss.size() > SHOW_STAFF&&item9.getVisibility()==View.VISIBLE) {
                            if(item9.getTag()!=null&&item9.getTag().equals("0")){
                                staffItemAdapter.updata(dss);

                            }else {
                                staffItemAdapter.updata(dss,SHOW_STAFF);
                            }

                            item9.setVisibility(View.VISIBLE);
                        } else {
                            staffItemAdapter.updata(dss);
                            item9.setVisibility(View.GONE);
                        }
                        break;
                    case 7:
                        //保存到本地
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                //保存燃气具
                                DB.getInstance().updateQjs(staffQjs,bean);
                                P.c("燃气比"+staffBs.get(0).getValue());
                                DB.getInstance().updateTab(staffBs,bean);
                                detailHandler.sendEmptyMessage(8);
                            }

                        }.start();
                        break;
                    case 8:


                        NewToast.makeText(DetailActivity.this,"更新成功",Common.TTIME).show();
                        break;
                    case 9:
                        int position = msg.arg1;
                        //打开对话框
                        CommonPhotoPop.showSheet(DetailActivity.this,photoSelect,position);
                        break;
                    case 101:
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                //保存燃气具
                                DB.getInstance().updateQjs(staffQjs,bean);
                                P.c("燃气比"+staffBs.get(0).getValue());
                                DB.getInstance().updateTab(staffBs,bean);
                                detailHandler.sendEmptyMessage(10);
                            }

                        }.start();
                        break;
                    case 10:
                        //提交数据到服务器
                        if(uploadTips==null){
                            uploadTips = new UploadTips(DetailActivity.this);
                            uploadTips.showSheet();
                        }
                          /*  */
                            ArrayList<ImageRdy> irs = DB.getInstance().photoIsend(bean);
                            if(irs.size()!=0){
                                //有未完成的单据
                                sendImage(irs);
                            }else{
                                //进行查询
                                Map<String,String> map = DB.getInstance().linePrint(bean);
                                if((map.get("staffOtherLine")!=null&&!map.get("send").equals("4"))||(map.get("staffOtherLine")==null&&!map.get("send").equals("3"))){
                                    sendImage(map);

                                }else{
                                    //签名上传完毕
                                    if(!DB.getInstance().standIsend(bean)){
                                        //基础数据未上传
                                        sendStand();
                                    }else{
                                        //完成任务单
                                        DB.getInstance().setStandJC(4,bean);
                                        cancelUp();
                                        NewToast.makeText(DetailActivity.this,"上传完毕",Common.TTIME).show();
                                        setResult(1000);
                                        AppManager.getAppManager().finishActivity(DetailActivity.this);

                                    }
                                }
                            }

                        break;
                    case -1:
                        if(!DB.getInstance().isExitPro(bean)){
                            createProblem();
                            DB.getInstance().setStandCt(8,"N",bean);
//                            DB.getInstance().setStandCt(bean.getOrderStatus(),"N",bean);
                        }

                        goProblem();
                        break;
                    case -2:
                        DB.getInstance().setStandCt(bean.getOrderStatus(),"Y",bean);
                        detailHandler.sendEmptyMessage(70);
                        break;
                }
            }
        }
    }
    private UploadTips uploadTips;
    private void changeText(String txt){
        if(uploadTips!=null){
            uploadTips.setText("正在上传:"+txt);
        }
    }
    private void cancelUp(){
        if(uploadTips!=null){
            uploadTips.cancle();
            uploadTips= null;
        }
    }
    private String getFile(String path){
        File file = new File(path);
        if(file!=null){
            return  file.getName();
        }
        return  "";
    }
    private String Bitmap2StrByBase64(Bitmap bit){
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 40, bos);//参数100表示不压缩
        byte[] bytes=bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    private void sendImage(final Object o) {
        JSONObject jsonObject = new JSONObject();
        String dtlId = null;
        try {
            jsonObject.put("orderId", bean.getId());
            jsonObject.put("toKen", sharedUtils.getStringValue("token"));
            if (o instanceof ArrayList) {
                ArrayList<ImageRdy> irs = (ArrayList<ImageRdy>) o;
                ImageRdy ir = irs.get(0);
                dtlId = ir.getId();
                jsonObject.put("dtlId", dtlId);
                jsonObject.put("type", 1);
                jsonObject.put("base64", Bitmap2StrByBase64(BitmapFactory.decodeFile(ir.getPath())));
                changeText(getFile(ir.getPath()));

            } else if (o instanceof Map) {
                //签名部分
                Map map = (Map) o;
                jsonObject.put("dtlId", "");
                if (map.get("send").equals("0")) {
                    //提交
                    jsonObject.put("type", 3);
                    String path  =map.get("staffLine").toString();
                    jsonObject.put("base64", Bitmap2StrByBase64(BitmapFactory.decodeFile(path)));
                    changeText(getFile(path));
                } else if (map.get("send").equals("1")) {
                    jsonObject.put("type", 4);
                    String path  =map.get("personLine").toString();
                    changeText(getFile(path));
                    jsonObject.put("base64", Bitmap2StrByBase64(BitmapFactory.decodeFile(path)));
                } else if (map.get("send").equals("2")) {
                    jsonObject.put("type", 4);

                    String path  =map.get("personPhoto").toString();
                    changeText(getFile(path));
                    jsonObject.put("base64", Bitmap2StrByBase64(BitmapFactory.decodeFile(path)));
                } else if(map.get("send").equals("3")){
                    jsonObject.put("type", 3);
                    String path  =map.get("staffOtherLine").toString();
                    jsonObject.put("base64", Bitmap2StrByBase64(BitmapFactory.decodeFile(path)));
                    changeText(getFile(path));
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String finalDtlId = dtlId;
        OkHttpUtils.postString().url(U.VISTER(U.BASE_URL) + U.STAFF_IMAGE).mediaType(MediaType.parse("application/json; charset=utf-8")).content(jsonObject.toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

                cancelUp();
            }

            @Override
            public void onResponse(String response, int id) {

                try {
                    JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                    if(jsonObject.getBoolean("Success")){
                            if(o instanceof ArrayList){
                                //图片组
                                ArrayList<ImageRdy> irs = (ArrayList<ImageRdy>) o;
                                ImageRdy ir = irs.get(0);
                                 DB.getInstance().changeSIV(bean,ir.getPath(), finalDtlId);
                                detailHandler.sendEmptyMessage(10);

                            }else  if(o instanceof  Map){
                                //签名组
                                Map map = (Map) o;
                                int send =  Integer.parseInt(map.get("send").toString())+1;
                                DB.getInstance().changeLS(bean,send);
                                detailHandler.sendEmptyMessage(10);
                            }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
    }


    private void sendStand(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("toKen",sharedUtils.getStringValue("token"));
            jsonObject.put("cls","Gas.SecurityOrder");
            jsonObject.put("method","SubmitOrderDtl");
            JSONObject object = new JSONObject();
            object.put("OrderId",bean.getId());
            //安检条目
            changeText("基本信息");
            JSONArray array0 = new JSONArray();
            for(int i=0;i<dss.size();i++){
                DetailStaff item = dss.get(i);
                JSONObject obj = new JSONObject();
                if(item.getItem()!=null){
                    obj.put("Id",item.getItem().getId());
                    if(item.getItem()==null){
                        obj.put("Status",false);
                    }else {
                        obj.put("Status",item.getItem().isCheck());
                    }
                    obj.put("ItemType",0);
                    array0.put(obj);
                }else if(item.getItems()!=null){
                   ArrayList<StaffTxtItem> staffs =  item.getItems();
                    for(int j=0;j<staffs.size();j++){
                        JSONObject obj0 = new JSONObject();
                        StaffTxtItem si= staffs.get(j);
                        obj0.put("ItemType",0);
                        obj0.put("ItemGroup",item.getItems_tag());
                            obj0.put("Id",si.getId());
                            if(item.getItem()==null){
                                obj0.put("Status",false);
                            }else {
                                obj0.put("Status",si.isCheck());
                            }
                        array0.put(obj0);
                    }
                }
            }
            //燃气具
            for(int i=0;i<staffQjs.size();i++){
                JSONObject obj = new JSONObject();
                StaffQj sf = staffQjs.get(i);
                obj.put("Id",sf.getId()==null?Common.COMMON_DEFAULT:sf.getId());
                obj.put("ItemName",sf.getName());
                obj.put("ItemGroup",sf.getPosition());
                obj.put("ItemType",2);
                obj.put("Status",sf.isCheck());
                array0.put(obj);
            }
            //---燃气表
              JSONArray array1 = new JSONArray();
            for(int i=0;i< staffBs.size();i++){
                StaffB sb  = staffBs.get(i);
                if(sb.getName()!=null&&sb.getValue()!=null){
                    JSONObject obj = new JSONObject();
                    obj.put("Id",sb.getId()==null?Common.COMMON_DEFAULT:sb.getId());
                    obj.put("TabCode",sb.getName());
                    obj.put("TabQty",sb.getValue());
                    obj.put("UseStatus",sb.isCheck());
                    array1.put(obj);
                }

            }
            object.put("Dtls",array0);
            object.put("Tabs",array1);
            P.c(object.toString());
            jsonObject.put("param",object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString().url(U.VISTER(U.BASE_URL)+U.LIST).mediaType(MediaType.parse("application/json; charset=utf-8")).content(jsonObject.toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                cancelUp();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                    if(jsonObject.getBoolean("Success")){
                        //解析成功
                        DB.getInstance().changeSs(bean);

                        detailHandler.sendEmptyMessage(10);
                    }else {
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



    private final int SHOW_STAFF = 5;
    private TextView item0, item1, item2, item3, item4, item5, item6, item9, item10,item11,item12,item13;
    private ImageView item_edit;
    private GridView item7;
    private ImageView item15, item16,item17,item20;
    private LinearLayout item18, item19;
    //图片
    private DetailImageAdapter imageAdapter;

    private InScrollListView item8, item14;
    //安检条目
    private StaffItemAdapter staffItemAdapter;
    //器具
    private DetailBAdapter bAdapter;
    private EditText item_b00,item_b01,item_b10,item_b11;
    private CheckBox item_b02,item_b12;

    private boolean check(){

        for(int i=0;i<dss.size();i++){
            DetailStaff rb =  dss.get(i);
            if(rb.getItem()!=null){
                if(rb.getItem().isCheck()){
                    return true;
                }
            }else if(rb.getItems()!=null){
                ArrayList<StaffTxtItem> temps = rb.getItems();
                for(int j=0;j<temps.size();j++){
                    if(temps.get(j).isCheck()){
                        return  true;
                    }
                }
            }

        }
        return  false;
    }

    /**
     * 创建隐患单
     */
    private void createProblem(){
        ArrayList<StaffTxtItem> item = new ArrayList<>();
        for(int i=0;i<dss.size();i++){
            DetailStaff rb =  dss.get(i);
            if(rb.getItem()!=null){
                if(rb.getItem().isCheck()){
                    item.add(rb.getItem());
                }
            }else if(rb.getItems()!=null){
                StaffTxtItem tt = new StaffTxtItem();
                StringBuilder builder  = new StringBuilder();
                ArrayList<StaffTxtItem> temps = rb.getItems();
                String id = null;
                for(int j=0;j<temps.size();j++){
                    if(temps.get(j).isCheck()){
                        if(id==null){
                            id = temps.get(j).getId();
                        }
                        builder.append(temps.get(j).getTxt()+"、");
                    }
                }
                if(builder.length()!=0){
                    String valu = builder.toString();
                    tt.setId(id);
                    tt.setTxt(rb.getItems_tag()+"："+valu.substring(0,valu.length()-1));
                    item.add(tt);
                }
            }

        }
        DB.getInstance().addProblem(bean,item);


    }
    CountDownTimer countDownTimer = new CountDownTimer(200,10) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            if(loadView!=null){
                loadView.cancle();
                loadView = null;
            }

        }
    };
    private void clickDel(){
        if(loadView==null){
            loadView = new LoadView(DetailActivity.this);
            loadView.showSheet();
        }

        countDownTimer.start();

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
            if(isProblem){
                setResult(1000);
            }
            AppManager.getAppManager().finishActivity(DetailActivity.this);
        }
        return super.dispatchKeyEvent(event);
    }

    private LinearLayout tag_view_ok,tag_view_no,tag_view_jc;
    private RelativeLayout tag_con;
    private TextView view_problem;
    public void ini() {
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//               Common.copy();
                P.c("isProblem"+isProblem);
                if(isProblem){
                    setResult(1000);
                }
                AppManager.getAppManager().finishActivity(DetailActivity.this);
            }
        });
        view_problem = (TextView) findViewById(R.id.view_problem);
        tag_view_ok = (LinearLayout) findViewById(R.id.tag_view_ok);
        tag_view_no = (LinearLayout) findViewById(R.id.tag_view_no);
        tag_view_jc = (LinearLayout) findViewById(R.id.tag_view_jc);
        tag_con = (RelativeLayout) findViewById(R.id.tag_con);
        proble = (LinearLayout) findViewById(R.id.proble);
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
        item10 = (TextView) findViewById(R.id.item10);
        item11 = (TextView) findViewById(R.id.item11);
        item12 = (TextView) findViewById(R.id.item12);
        item13 = (TextView) findViewById(R.id.item13);
        item14 = (InScrollListView) findViewById(R.id.item14);
        item15 = (ImageView) findViewById(R.id.item15);
        item16 = (ImageView) findViewById(R.id.item16);
        item17 = (ImageView) findViewById(R.id.item17);
        item_b00 = (EditText) findViewById(R.id.item_b00);
        item_b01 = (EditText) findViewById(R.id.item_b01);
        item_b10 = (EditText) findViewById(R.id.item_b10);
        item_b11 = (EditText) findViewById(R.id.item_b11);
        item_b02 = (CheckBox) findViewById(R.id.item_b02);
        item_b12 = (CheckBox) findViewById(R.id.item_b12);
        item18 = (LinearLayout) findViewById(R.id.item18);
        item19 = (LinearLayout) findViewById(R.id.item19);
        item20 = (ImageView) findViewById(R.id.item20);
        imageAdapter = new DetailImageAdapter(DetailActivity.this, staffImages);
        item7.setAdapter(imageAdapter);

        staffItemAdapter = new StaffItemAdapter(DetailActivity.this, dss,bean,detailHandler);
        item8.setAdapter(staffItemAdapter);

        bAdapter = new DetailBAdapter(DetailActivity.this, staffQjs);
        item14.setAdapter(bAdapter);
        item_edit = (ImageView) findViewById(R.id.item_edit);
        item7.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(staffImages.get(position).getPath()!=null){
                    DetailImageDlg dlg = new DetailImageDlg(DetailActivity.this,sharedUtils,staffImages.get(position),bean,detailHandler,position);
                    dlg.showSheet();
                }else{
                    CommonPhotoPop.showSheet(DetailActivity.this,photoSelect,position);
                }


            }
        });

        item9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickDel();

                if (dss.size() > SHOW_STAFF) {
                    if(item9.getText().toString().equals("收回列表")){
                        staffItemAdapter.updata(dss,SHOW_STAFF);
                        item9.setText("查看更多");
                        item9.setTag("1");
                    }else{
                        staffItemAdapter.updata(dss);
                        item9.setText("收回列表");
                        item9.setTag("0");
                    }

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
                        DB.getInstance().changeStaffTime(bean,time);
                    }
                });
                changeTime.showSheet();
            }
        });


        item10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickDel();
                Map<String,String> map = DB.getInstance().ProLinePrint(bean);
                if(map.get("proNo")!=null&&map.get("bis").equals("1")){
                    NewToast.makeText(DetailActivity.this,"已处理过隐患单",2000).show();
                    return;
                }
                if(check()){
                    //存在安全隐患
                    P.c("什么情况");
                    DetailTips detailTips = new DetailTips(DetailActivity.this,detailHandler,"注意:修改该安检单为【不合格】",-1);
                    detailTips.showSheet();


                }else{
                    //不存在不能进行
                    NewToast.makeText(DetailActivity.this,"请勾选安检隐患条目",Common.TTIME).show();
                }
            }
        });
        item11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickDel();
                if(bean.getStaffTag()!=null&&bean.getStaffTag().equals("N")){
                    NewToast.makeText(DetailActivity.this,"请先解除隐患",Common.TTIME).show();
                }else{

                    DetailTips detailTips = new DetailTips(DetailActivity.this,detailHandler,"注意:修改该安检单为【合格】",-2);
                    detailTips.showSheet();
                }

            }
        });

        item12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickDel();
                if(bean.getStaffTag()!=null&&bean.getStaffTag().equals("N")){
                    NewToast.makeText(DetailActivity.this,"请先解除隐患",Common.TTIME).show();
                }else {
                    Intent intent = new Intent(DetailActivity.this,StaffCtActivity.class);
                    intent.putExtra("obj",bean);
                    startActivityForResult(intent,100);
                }

            }
        });
        item13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaffQj bItem = new StaffQj();
                bItem.setI(-1);
                staffQjs.add(bItem);

                bAdapter.updata(staffQjs);
            }
        });
        item15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, CommonLineActivity.class);
                intent.putExtra("obj",bean);
                intent.putExtra("staffPrint",0);
                startActivityForResult(intent,101);
            }
        });
        item16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, CommonLineActivity.class);
                intent.putExtra("obj",bean);
                intent.putExtra("staffPrint",1);
               startActivityForResult(intent,102);
            }
        });
        item17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //固定-2为客户点单
                CommonPhotoPop.showSheet(DetailActivity.this,photoSelect,-2);
            }
        });
        item18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickDel();
                detailHandler.sendEmptyMessage(7);
                P.c(bean.getProblem()+"bean.getStaffTag()"+bean.getStaffTag()+"=="+(bean.getStaffTag()==null));

                if((bean.getStaffTag()!=null&&bean.getStaffTag().equals("Y"))||(bean.getStaffTag()!=null&&bean.getStaffTag().equals("J"))){
                    Intent intent = new Intent(DetailActivity.this, StaffBtActivity.class);
                    intent.putExtra("obj",bean);
                    startActivity(intent);
                } else{
                    NewToast.makeText(DetailActivity.this,"任务状态不合格,拒绝打印安检单",Common.TTIME).show();
                }

            }
        });
        item19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDel();
                    HeadTips headTips = new HeadTips(DetailActivity.this,detailHandler,staffImages,bean);
                    headTips.showSheet();

            }
        });
        item20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonLogin login = new CommonLogin(DetailActivity.this,sharedUtils);
                login.setResult(false,-22,detailHandler);
                login.showSheet();
            }
        });

    }
    private void goProblem(){

        Intent intent = new Intent(DetailActivity.this,DetailProblemActivity.class);
        intent.putExtra("obj",bean);
        startActivityForResult(intent,100);
    }
    @Override
    protected void onResume() {
        super.onResume();
        load();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100&&resultCode==1000){
            //更新基本信息
            detailHandler.sendEmptyMessage(70);
            setResult(1000);
            AppManager.getAppManager().finishActivity(DetailActivity.this);
        }else if(requestCode==101&&resultCode==1000){
            //安检员
            P.c("返回的地址"+data.getStringExtra("path"));
            ImageLoader.getInstance().displayImage("file://"+data.getStringExtra("path"),item15);
        }else if(requestCode==102&&resultCode==1000){
            //客户签名
            ImageLoader.getInstance().displayImage("file://"+data.getStringExtra("path"),item16);
        }else if(requestCode==100&&resultCode==999){
            isProblem = true;
            detailHandler.sendEmptyMessage(70);
        }else  if(requestCode==99&&resultCode==1000){
            ImageLoader.getInstance().displayImage("file://"+data.getStringExtra("path"),item20);
        }
    }
    private boolean isProblem = false;

    int SELECT_INDEX = -1;
    private TakePhoto takePhoto;
    private PhotoSelect photoSelect = new PhotoSelect() {
        private void init(){


            int width = 1280;
            int height = 800;
            LubanOptions option=new LubanOptions.Builder()
                    .setMaxHeight(height)
                    .setMaxWidth(width)
                    .setMaxSize(200*1024)
                    .create();
            CompressConfig config = CompressConfig.ofLuban(option);
            config.enableReserveRaw(true);
            takePhoto.onEnableCompress(config,true);

            TakePhotoOptions.Builder builder=new TakePhotoOptions.Builder();
            builder.setWithOwnGallery(true);//使用自带相册
            builder.setCorrectImage(true);//纠正拍照角度
            takePhoto.setTakePhotoOptions(builder.create());
        }
        @Override
        public void camcer(int index) {
            SELECT_INDEX = index;
            File file=new File(Common.CACHE_STAFF_IMAGES+bean.getId()+"/"+System.currentTimeMillis()+".jpg");
            if (!file.getParentFile().exists())file.getParentFile().mkdirs();
            Uri imageUri = Uri.fromFile(file);
            init();
            takePhoto.onPickFromCapture(imageUri);
        }

        @Override
        public void photos(int index) {
            SELECT_INDEX = index;
            init();
            if(index==-2){
                takePhoto.onPickMultiple(1);
            }else{
                takePhoto.onPickMultiple(9);
            }

//            takePhoto.onPickFromGallery();
        }
    };

    @Override
    public void takeSuccess(final TResult result) {
        super.takeSuccess(result);
        if(SELECT_INDEX==-2){
            //签名部分
            ArrayList<TImage> imsge = result.getImages();
            if(imsge.size()!=0){
                try {
                    shuiyin(imsge);
                } catch (IOException e) {
                    e.printStackTrace();
                    P.c("水印异常");
                }
                DB.getInstance().staff_print(bean,2,imsge.get(0).getCompressPath());
                detailHandler.sendEmptyMessage(12);
            }

        }else{
            //图片部分
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    ArrayList<TImage> imsge = result.getImages();
              /*  CopyFile copyFile = new CopyFile();
                for(int i=0;i<imsge.size();i++){
                    TImage img = imsge.get(i);
                    if(img.getCompressPath().startsWith("/data/data/")){
                        //压缩至data下面，需要移动
                        String fileName = img.getCompressPath().substring(img.getCompressPath().lastIndexOf("/")+1);
                        copyFile.copyFile(img.getCompressPath(),Common.CACHE_STAFF_IMAGES+bean.getId()+"/"+fileName);
                        img.setCompressPath(Common.CACHE_STAFF_IMAGES+bean.getId()+"/"+fileName);
                        P.c(img.getCompressPath()+"复制到"+Common.CACHE_STAFF_IMAGES+bean.getId()+"/"+fileName);
                    }

                }*/
                    try {
                        shuiyin(imsge);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    DB.getInstance().addStaffImages(staffImages.get(SELECT_INDEX),bean,imsge,detailHandler);

                }
            }.start();
        }


    }

    /**
     * 水印处理
     * @param imsges
     */
    private void shuiyin( ArrayList<TImage> imsges) throws IOException {
        for(int i=0;i<imsges.size();i++){
            String path = imsges.get(i).getCompressPath();
            Bitmap sourBitmap = BitmapFactory.decodeFile(path);
            Bitmap waterBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo_p);
            Bitmap watermarkBitmap = ImageUtil.createWaterMaskRightBottom(DetailActivity.this,sourBitmap, waterBitmap,20,30);
            P.c("处理水印"+path);
            Bitmap  textBitmap = ImageUtil.drawTextToRightBottom(this, watermarkBitmap, TimeUtil.getTimePri(System.currentTimeMillis()), 8, Color.RED, 0, 5);
            saveMyBitmap(path,textBitmap);
        }

    }
    public void saveMyBitmap(String path, Bitmap mBitmap) throws IOException {
        File f = new File(path);
        if(f.exists()){
            f.delete();
        }
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        P.c("操作--"+msg);
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
        P.c("操--作");
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
                P.c("item"+item.getId());
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
    private Map<String,String> printMap = new HashMap<>();
    private void load() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                detailHandler.sendEmptyMessage(6);
                detailHandler.sendEmptyMessage(5);
                detailHandler.sendEmptyMessage(11);
                detailHandler.sendEmptyMessage(12);

                detailHandler.sendEmptyMessage(70);
            }
        }.start();
    }
}
