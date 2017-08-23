package com.hhkj.gas.www.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.hhkj.gas.www.common.SharedUtils;
import com.hhkj.gas.www.common.U;
import com.hhkj.gas.www.db.DB;
import com.hhkj.gas.www.inter.PhotoSelect;
import com.hhkj.gas.www.inter.TimeSelect;
import com.hhkj.gas.www.widget.ChangeTime;
import com.hhkj.gas.www.widget.CommonLogin;
import com.hhkj.gas.www.widget.CommonPhotoPop;
import com.hhkj.gas.www.widget.DetailImageDlg;
import com.hhkj.gas.www.widget.HeadTips;
import com.hhkj.gas.www.widget.InScrollListView;
import com.hhkj.gas.www.widget.LoadView;
import com.hhkj.gas.www.widget.NewToast;
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

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by cloor on 2017/8/15.
 */

public class DetailActivity extends TakePhotoActivity {

    private TextView back;
    private ReserItemBean bean;
    private DetailHandler detailHandler;
    public SharedUtils sharedUtils;
    public LayoutInflater inflater;
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
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(FileUtils.dip2px(DetailActivity.this, 110) * imageLen, LinearLayout.LayoutParams.WRAP_CONTENT);
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


                        }
                        String vs = getString(R.string.curr_status, status);
                        SpannableStringBuilder sbBuilder=new SpannableStringBuilder(vs);

                        sbBuilder.setSpan
                                (new ForegroundColorSpan(Color.parseColor("#0084ff")), 5, vs.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                        item5.setText(sbBuilder);
                        item6.setText(getString(R.string.curr_person, bean.getStaffName()));
                        //----------
                        break;
                    case 11:
                        DB.getInstance().updataStaffBs(staffBs,bean);
                        DB.getInstance().updataStaffQj(staffQjs,bean);
                        DB.getInstance().getStaffPrint(printMap,bean);
                        detailHandler.sendEmptyMessage(1);
                        break;
                    case 1:
                        P.c("何时装载");
                        //装载安检条目

                        //装载燃气表
                        if(staffBs.size()==1){
                            item_b00.setText(staffBs.get(0).getName());
                            item_b01.setText(staffBs.get(0).getValue());
                            item_b02.setChecked(staffBs.get(0).isCheck());
                            StaffB staffB = new StaffB();
                            staffB.setI(-1);
                            staffBs.add(staffB);
                        }else if(staffBs.size()>1){
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
                        //装载签名
                        ImageLoader.getInstance().displayImage("file://"+printMap.get("staffLine"),item15);
                    ImageLoader.getInstance().displayImage("file://"+printMap.get("personLine"),item16);
                    ImageLoader.getInstance().displayImage("file://"+printMap.get("personPhoto"),item17);
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
                            staffItemAdapter.updata(dss, SHOW_STAFF);
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
                                DB.getInstance().updateTab(staffBs,bean);
                                detailHandler.sendEmptyMessage(8);
                            }

                        }.start();
                        break;
                    case 8:
                        if(headTips!=null){
                            headTips.cancle();
                            headTips = null;
                        }

                        NewToast.makeText(DetailActivity.this,"更新成功",Common.TTIME).show();
                        break;
                    case 9:
                        int position = msg.arg1;
                        //打开对话框
                        CommonPhotoPop.showSheet(DetailActivity.this,photoSelect,position);
                        break;
                }
            }
        }
    }

    private final int SHOW_STAFF = 5;
    private TextView item0, item1, item2, item3, item4, item5, item6, item9, item10,item11,item12,item13;
    private ImageView item_edit;
    private GridView item7;
    private ImageView item15, item16,item17;
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

    private boolean check(){

        for(int i=0;i<dss.size();i++){
            DetailStaff rb =  dss.get(i);
            if(rb.getItem()!=null){
                if(rb.getItem().isCheck()){
                    return true;
                }
            }else if(rb.getItems()!=null){
                for(int j=0;j<rb.getItems().size();j++){
                    if(rb.getItems().get(j).isCheck()){
                        return  true;
                    }
                }
            }

        }
        return  false;
    }
    public void ini() {
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Common.copy();
//                AppManager.getAppManager().finishActivity(DetailActivity.this);
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
        item10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(check()){
                        //存在安全隐患
                        P.c("什么情况");

                    }else{
                        //不存在不能进行
                        NewToast.makeText(DetailActivity.this,"请勾选安检隐患条目",Common.TTIME).show();
                    }
            }
        });
        item11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DB.getInstance().setStandCt(bean.getOrderStatus(),"Y",bean);
                detailHandler.sendEmptyMessage(70);
            }
        });

        item12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this,StaffCtActivity.class);
                intent.putExtra("obj",bean);
                startActivityForResult(intent,100);
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
                detailHandler.sendEmptyMessage(7);
                P.c(bean.getProblem()+"bean.getStaffTag()"+bean.getStaffTag()+"=="+(bean.getStaffTag()==null));

                if(bean.getStaffTag().equals("Y")){
                    Intent intent = new Intent(DetailActivity.this, StaffBtActivity.class);
                    intent.putExtra("obj",bean);
                    startActivity(intent);
                }else{
                    NewToast.makeText(DetailActivity.this,"任务状态不合格,拒绝打印安检单",Common.TTIME).show();
                }

            }
        });
        item19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(headTips==null){
                    headTips = new HeadTips(DetailActivity.this,detailHandler);
                    headTips.showSheet();
                }
            }
        });
        load();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100&&resultCode==1000){
            //更新基本信息
            detailHandler.sendEmptyMessage(70);
        }else if(requestCode==101&&resultCode==1000){
            //安检员
            P.c("返回的地址"+data.getStringExtra("path"));
            ImageLoader.getInstance().displayImage("file://"+data.getStringExtra("path"),item15);
        }else if(requestCode==102&&resultCode==1000){
            //客户签名
            ImageLoader.getInstance().displayImage("file://"+data.getStringExtra("path"),item16);
        }
    }

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
                DB.getInstance().staff_print(bean,2,imsge.get(0).getCompressPath());
                detailHandler.sendEmptyMessage(11);
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
                    DB.getInstance().addStaffImages(staffImages.get(SELECT_INDEX),bean,imsge,detailHandler);

                }
            }.start();
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

    private HeadTips headTips;
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
    private Map<String,String> printMap = new HashMap<>();
    private void load() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                detailHandler.sendEmptyMessage(6);
                detailHandler.sendEmptyMessage(5);
                detailHandler.sendEmptyMessage(11);
                detailHandler.sendEmptyMessage(70);
            }
        }.start();
    }
}
