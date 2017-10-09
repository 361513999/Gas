package com.hhkj.gas.www.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hhkj.gas.www.R;
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
import com.hhkj.gas.www.common.FileUtils;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.common.U;
import com.hhkj.gas.www.db.DB;
import com.hhkj.gas.www.widget.ChangeTime;
import com.hhkj.gas.www.widget.NewToast;
import com.hhkj.gas.www.widget.UpTips;
import com.hhkj.gas.www.widget.UploadTips;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by cloor on 2017/8/7.
 */

public class HomeActivity extends BaseActivity {
    private HomeHandler homeHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gas_home);
        homeHandler = new HomeHandler(HomeActivity.this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

         if(keyCode==KeyEvent.KEYCODE_BACK){

          //此处不处理返回按钮事件
         }
        return super.onKeyDown(keyCode, event);
    }
    private ImageView user_tag;
    private TextView user;
    private ImageView setting_btn;
    private LinearLayout start0,start1,start2,start3;
    private TextView num,up_num;
    @Override
    public void init() {
        user_tag = (ImageView) findViewById(R.id.user_tag);
        user = (TextView) findViewById(R.id.user);
        setting_btn = (ImageView) findViewById(R.id.setting_btn);
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,SettingActivity.class);
                startActivity(intent);

            }
        });
        start0 = (LinearLayout) findViewById(R.id.start0);
        start1 = (LinearLayout) findViewById(R.id.start1);
        start2 = (LinearLayout) findViewById(R.id.start2);
        start3 = (LinearLayout) findViewById(R.id.start3);
        num = (TextView) findViewById(R.id.num);
        up_num = (TextView) findViewById(R.id.up_num);
        start0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,Start0Activity.class);
                startActivity(intent);
            }
        });
        start1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,Start1Activity.class);
                startActivity(intent);
            }
        });
        start2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,Start2Activity.class);
                startActivity(intent);
            }
        });
        start3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num1 = getUpCount();
                if(num1==0){
                    NewToast.makeText(HomeActivity.this,"没有符合条件的任务",Common.TTIME).show();
                }else{
                    UpTips upTips = new UpTips(HomeActivity.this,homeHandler);
                    upTips.showSheet();
                }

            }
        });
    }
    private  UploadTips uploadTips;
    private void cancelUp(){
        if(uploadTips!=null){
            uploadTips.cancle();
            uploadTips= null;

            int num0 = DB.getInstance().getStaffCount(sharedUtils.getStringValue("userid"));
            if(num0!=0){
                num.setText(String.valueOf(num0));
                num.setVisibility(View.VISIBLE);
            }else{
                num.setVisibility(View.GONE);
            }
            int num1 = getUpCount();
            if(num1!=0){
                up_num.setText(String.valueOf(num1));
                up_num.setVisibility(View.VISIBLE);
            }else{
                up_num.setVisibility(View.GONE);
            }


        }
    }
    private int getUpCount(){
        int COUNT = 0;
        ArrayList<ReserItemBean> rbs = DB.getInstance().getRbs();
        for(int i=0;i<rbs.size();i++){
            ReserItemBean bean = rbs.get(i);
            ArrayList<StaffImageItem> staffImages = new ArrayList<>();//图片
            DB.getInstance().updataStaffImageItem(staffImages, bean);
            //判断
            if(bean.getStaffTag()!=null&&bean.getStaffTag().equals("Y")){
                if(isImage(staffImages)){

                    if( DB.getInstance().getCanSend()){
                        P.c("可以发送");
                        if(DB.getInstance().canLinePrint(bean)){
                            //发送操作

                            COUNT+=1;

                        }else{

                        }

                    }else {


                    }
                }else{

                }
            }else{

                if(DB.getInstance().isExitPro(bean)){

                    ArrayList<StaffTxtItem> txtItems = new ArrayList<>();
                    DB.getInstance().getProblemList(txtItems,bean);
                    if(isCan(txtItems)){
                        if(DB.getInstance().canProStand(bean)){

                            Map<String,String> map = DB.getInstance().ProLinePrint(bean);
                            if(map.get("proNo")==null){
                                COUNT+=1;

                            }else{

                            }

                        }else {

                        }
                    }else{

                    }

                }else{

                }

            }
        }



        return  COUNT;

    }
    private class HomeHandler extends Handler {
        WeakReference<HomeActivity> mLeakActivityRef;

        public HomeHandler(HomeActivity leakActivity) {
            mLeakActivityRef = new WeakReference<HomeActivity>(leakActivity);
        }

        private  ArrayList<ReserItemBean> rbs = new ArrayList<>();
        private int INDEX = 0;
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            if (mLeakActivityRef.get() != null) {
                switch (msg.what){
                    case 4:
                        //用户未登录处理
                        cancelUp();
                        reLogin();
                        break;
                    case 0:
                        P.c("开始操作批量上传");
                        if(uploadTips==null){
                            uploadTips = new UploadTips(HomeActivity.this);
                        }
                        uploadTips.showSheet();
                        rbs.clear();
                        INDEX = 0;
                         rbs = DB.getInstance().getRbs();

                    case 1:
                                //处理上传操作
                                    if(rbs.size()!=0&&INDEX<rbs.size()){
                                    ReserItemBean bean = rbs.get(INDEX++);
                                    ArrayList<StaffImageItem> staffImages = new ArrayList<>();//图片
                                    DB.getInstance().updataStaffImageItem(staffImages, bean);
                                    //判断
                                    if(bean.getStaffTag()!=null&&bean.getStaffTag().equals("Y")){
                                        if(isImage(staffImages)){

                                            if( DB.getInstance().getCanSend()){
                                                P.c("可以发送");
                                                if(DB.getInstance().canLinePrint(bean)){
                                                   //发送操作

                                                    sendMsg(10,bean);

                                                }else{
                                                    P.c("批量上传-->请签名");
                                                    homeHandler.sendEmptyMessage(1);
                                                   // NewToast.makeText(context,"请签名", Common.TTIME).show();
                                                }

                                            }else {
                                                homeHandler.sendEmptyMessage(1);
                                                P.c("批量上传-->请确认签名和安检单状态");
                                               // NewToast.makeText(context,"请确认签名和安检单状态", Common.TTIME).show();
                                            }
                                        }else{
                                            homeHandler.sendEmptyMessage(1);
                                            P.c("批量上传-->至少每项上传一张安检图片");
                                            //NewToast.makeText(context,"至少每项上传一张安检图片", Common.TTIME).show();
                                        }
                                    }else{
                                      //  homeHandler.sendEmptyMessage(1);
                                        P.c("批量上传-->安检项目不合格");
                                       // NewToast.makeText(context,"安检项目不合格", Common.TTIME).show();
                                        if(DB.getInstance().isExitPro(bean)){
                                           //处理不合格的单据
                                            //
                                            ArrayList<StaffTxtItem> txtItems = new ArrayList<>();
                                            DB.getInstance().getProblemList(txtItems,bean);
                                            if(isCan(txtItems)){
                                                if(DB.getInstance().canProStand(bean)){
                                                   //批量处理隐患单
                                                    Map<String,String> map = DB.getInstance().ProLinePrint(bean);
                                                    if(map.get("proNo")==null){
                                                        sendMsg(11,bean);
                                                    }else{
                                                        P.c("批量上传隐患--->已经提交过隐患单");
                                                        homeHandler.sendEmptyMessage(1);
                                                    }

                                                }else {
                                                    // NewToast.makeText(context,"请签名及确定整改日期",Common.TTIME).show();
                                                    P.c("批量上传隐患--->请签名及确定整改日期");
                                                    homeHandler.sendEmptyMessage(1);
                                                }
                                            }else{
                                                homeHandler.sendEmptyMessage(1);
                                            }

                                        }else{
                                            homeHandler.sendEmptyMessage(1);
                                        }

                                    }

                                    }else{
                                        NewToast.makeText(HomeActivity.this,"上传完毕",Common.TTIME).show();
                                        cancelUp();
                                    }

                        break;
                    case 10:
                        ReserItemBean bean = (ReserItemBean) msg.obj;
                        ArrayList<ImageRdy> irs = DB.getInstance().photoIsend(bean);
                        if(irs.size()!=0){
                            //有未完成的单据
                            sendImage(irs,bean);
                        }else{
                            //进行查询
                            Map<String,String> map = DB.getInstance().linePrint(bean);
                            if(!map.get("send").equals("3")){
                                sendImage(map,bean);

                            }else{
                                //签名上传完毕
                                if(!DB.getInstance().standIsend(bean)){
                                    //基础数据未上传
                                    sendStand(bean);
                                }else{
                                    //完成任务单
                                homeHandler.sendEmptyMessage(1);

                                }
                            }
                        }
                        break;
                    case 11:
                        ReserItemBean bean11 = (ReserItemBean) msg.obj;
                        ArrayList<ImageRdy> irs11 = DB.getInstance().photoProIsend(bean11);
                        if(irs11.size()!=0){
                            //有未完成的单据
                            sendProblemImage(irs11,bean11);
                        }else{
                            //进行查询
                            Map<String,String> map = DB.getInstance().ProLinePrint(bean11);
                            if(!map.get("send").equals("3")){
                                sendProblemImage(map,bean11);

                            }else{
                                //签名上传完毕
                                sendProblemStand(map,bean11);

                            }
                        }
                        break;
                }
            }
        }
    }
    private void sendMsg(int what,Object o){
        Message msg = new Message();
        msg.what = what;
        msg.obj  = o;
        homeHandler.sendMessage(msg);
    }
    private boolean isImage( ArrayList<StaffImageItem> staffImageItems){
        for(int i=-0;i<staffImageItems.size();i++){
            if(staffImageItems.get(i).getPath()==null){
                return  false;
            }
        }
        return  true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        int num0 = DB.getInstance().getStaffCount(sharedUtils.getStringValue("userid"));
        if(num0!=0){
            num.setText(String.valueOf(num0));
            num.setVisibility(View.VISIBLE);
        }else{
            num.setVisibility(View.GONE);
        }
        int num1 = getUpCount();
        if(num1!=0){
            up_num.setText(String.valueOf(num1));
            up_num.setVisibility(View.VISIBLE);
        }else{
            up_num.setVisibility(View.GONE);
        }


        if(sharedUtils.getBooleanValue("head")){
            ImageLoader.getInstance().displayImage("drawable://"+R.mipmap.login_users,user_tag);
        }else{
            ImageLoader.getInstance().displayImage("drawable://"+R.mipmap.login_user,user_tag);
        }
        user.setText(sharedUtils.getStringValue("name"));
    }
    private String Bitmap2StrByBase64(Bitmap bit){
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 40, bos);//参数100表示不压缩
        byte[] bytes=bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    private void changeText(String txt){
        if(uploadTips!=null){
            uploadTips.setText("正在上传:"+txt);
        }
    }
    private String getFile(String path){
        File file = new File(path);
        if(file!=null){
            return  file.getName();
        }
        return  "";
    }
    //上传安检
    private void sendImage(final Object o, final ReserItemBean bean) {
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
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String finalDtlId = dtlId;
        OkHttpUtils.postString().url(U.VISTER(U.BASE_URL) + U.STAFF_IMAGE).mediaType(MediaType.parse("application/json; charset=utf-8")).content(jsonObject.toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

//                cancelUp();
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
                            sendMsg(10,bean);

                        }else  if(o instanceof  Map){
                            //签名组
                            Map map = (Map) o;
                            int send =  Integer.parseInt(map.get("send").toString())+1;
                            DB.getInstance().changeLS(bean,send);
                            sendMsg(10,bean);
                        }

                    }else {
                        if(jsonObject.getString("Result").equals(Common.UNLOGIN)){
                            NewToast.makeText(HomeActivity.this, "未登录", 1000).show();
                            homeHandler.sendEmptyMessage(4);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
    }
    private void parseTxtItem( Map<String, Object> txtListMap,ArrayList<DetailStaff> dss ) {
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
    private void sendStand(final ReserItemBean bean){
          Map<String, Object> txtListMap = new HashMap<>();//未处理的栏目
          ArrayList<DetailStaff> dss = new ArrayList<>();//栏目
          ArrayList<StaffB> staffBs = new ArrayList<>();//燃气表
          ArrayList<StaffQj> staffQjs = new ArrayList<>();//燃气具
        DB.getInstance().updataStaffBs(staffBs,bean);
        DB.getInstance().updataStaffQj(staffQjs,bean);
        DB.getInstance().updataStaffTxtItem(txtListMap, bean);
        parseTxtItem(txtListMap,dss);

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

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                    if(jsonObject.getBoolean("Success")){
                        //解析成功
                        DB.getInstance().changeSs(bean);
                        sendMsg(10,bean);
                    }else {
                        if(jsonObject.getString("Result").equals(Common.UNLOGIN)){
                            NewToast.makeText(HomeActivity.this, "未登录", 1000).show();
                            homeHandler.sendEmptyMessage(4);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
//隐患单
private boolean isCan(ArrayList<StaffTxtItem> txtItems){
    for(int i=0;i<txtItems.size();i++){
        if(txtItems.get(i).getImageItems().size()==0){
            return  false;
        }
    }
    return  true;
}
    private void sendProblemImage(final Object o, final ReserItemBean bean) {
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
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String finalDtlId = dtlId;
        OkHttpUtils.postString().url(U.VISTER(U.BASE_URL) + U.PROBLEM_IMAGE).mediaType(MediaType.parse("application/json; charset=utf-8")).content(jsonObject.toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                P.c("失败"+e.getMessage());

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
                            DB.getInstance().changePSIV(bean,ir.getPath(), finalDtlId);

                            sendMsg(11,bean);

                        }else  if(o instanceof  Map){
                            //签名组
                            Map map = (Map) o;
                            int send =  Integer.parseInt(map.get("send").toString())+1;
                            DB.getInstance().changePLS(bean,send);
                            sendMsg(11,bean);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
    }
    private void sendProblemStand(final Map<String,String> map, final ReserItemBean bean){
        String create = "00000000-0000-0000-0000-000000000000";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("toKen",sharedUtils.getStringValue("token"));
            jsonObject.put("cls","Gas.RiskOrder");
            jsonObject.put("method","SubmitRiskOrder");
            JSONObject object = new JSONObject();
            object.put("RiskId",map.get("proNo")==null?"":map.get("proNo"));
            object.put("OrderId",bean.getId());
            object.put("StopDate",map.get("startTime"));
            object.put("EndDate",map.get("endTime"));
            //安检条目
            jsonObject.put("param",object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString().url(U.VISTER(U.BASE_URL)+U.LIST).mediaType(MediaType.parse("application/json; charset=utf-8")).content(jsonObject.toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                    if(jsonObject.getBoolean("Success")){

                        if(map.get("proNo")==null){
                            //证明是整改中
                            DB.getInstance().changePLSB(bean,jsonObject.getString("Result"));
                            DB.getInstance().setStandCt(8,"N",bean);

                        }else{
                            //解除整改
                            P.c("解除隐患");
                            if(map.get("bis").equals("1")){
                                //已结解除过


                            }else{
                                P.c("执行了清除");
                                DB.getInstance().setStandCt(7,null,bean);
                                DB.getInstance().resetItem(bean);
                                DB.getInstance().changePLSBI(bean,true);
                            }


                        }

                        homeHandler.sendEmptyMessage(1);

                    }else {
                        if(jsonObject.getString("Result").equals(Common.UNLOGIN)){

                            NewToast.makeText(HomeActivity.this, "未登录", 1000).show();
                            homeHandler.sendEmptyMessage(4);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
