package com.hhkj.gas.www.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.adapter.DetailProItemAdapter;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffTxtItem;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.common.SharedUtils;
import com.hhkj.gas.www.db.DB;
import com.hhkj.gas.www.inter.PhotoSelect;
import com.hhkj.gas.www.inter.ProSelect;
import com.hhkj.gas.www.widget.CommonPhotoPop;
import com.hhkj.gas.www.widget.InScrollListView;
import com.hhkj.gas.www.widget.NewToast;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.LubanOptions;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import library.view.GregorianLunarCalendarOneView;

/**
 * Created by Administrator on 2017/8/25/025.
 */

public class DetailProblemActivity extends TakePhotoActivity {
    private TakePhoto takePhoto;
    private SharedUtils sharedUtils;
    public LayoutInflater inflater;
    private TextView back;
    private ReserItemBean bean;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
    private LinearLayout item18,item19;
    private DetailProblemHandler detailProblemHandler;
    private TextView item0, item1, item2, item3, item4, item5, item6;
    private InScrollListView pro_list;
    private DetailProItemAdapter itemAdapter;
    private ArrayList<StaffTxtItem> txtItems = new ArrayList<>();
    int SELECT_INDEX = -1;
    private PhotoSelect proPhotoSelect = new PhotoSelect() {
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
    private LinearLayout layout;
    private TextView itme8,itme7;
    private ImageView item15,item16,item17;
    private void init(){
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity(DetailProblemActivity.this);
            }
        });
        itme7 = (TextView) findViewById(R.id.itme7);
        itme8 = (TextView) findViewById(R.id.itme8);
        item0 = (TextView) findViewById(R.id.item0);
        item1 = (TextView) findViewById(R.id.item1);
        item2 = (TextView) findViewById(R.id.item2);
        item3 = (TextView) findViewById(R.id.item3);
        item4 = (TextView) findViewById(R.id.item4);
        item5 = (TextView) findViewById(R.id.item5);
        item6 = (TextView) findViewById(R.id.item6);
        layout = (LinearLayout) findViewById(R.id.layout);
        item15 = (ImageView) findViewById(R.id.item15);
        item16 = (ImageView) findViewById(R.id.item16);
        item17 = (ImageView) findViewById(R.id.item17);

        item18 = (LinearLayout) findViewById(R.id.item18);
        item19 = (LinearLayout) findViewById(R.id.item19);
        pro_list = (InScrollListView) findViewById(R.id.pro_list);
        itemAdapter = new DetailProItemAdapter(DetailProblemActivity.this, txtItems, new ProSelect() {
            @Override
            public void select(int st) {
                CommonPhotoPop.showSheet(DetailProblemActivity.this,proPhotoSelect ,st);
            }
        });
        pro_list.setAdapter(itemAdapter);
        new Thread(){
            @Override
            public void run() {
                super.run();
                detailProblemHandler.sendEmptyMessage(70);
                detailProblemHandler.sendEmptyMessage(60);
                detailProblemHandler.sendEmptyMessage(50);
            }
        }.start();
        item15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailProblemActivity.this, CommonLineActivity.class);
                intent.putExtra("obj",bean);
                intent.putExtra("from",1);
                intent.putExtra("tag","staffLine");
                startActivityForResult(intent,101);
            }
        });
        item16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailProblemActivity.this, CommonLineActivity.class);
                intent.putExtra("obj",bean);
                intent.putExtra("from",1);
                intent.putExtra("tag","personLine");
                startActivityForResult(intent,102);
            }
        });
        item17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonPhotoPop.showSheet(DetailProblemActivity.this,proPhotoSelect,-2);
            }
        });
        item18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailProblemActivity.this,DetailProBtActivity.class);
                intent.putExtra("obj",txtItems);
                intent.putExtra("bean",bean);
                intent.putExtra("map",(Serializable) proStand);
                startActivity(intent);
            }
        });
        itme7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate("startTime");
                showDataPop(dataPopupWindow,layout);
            }
        });
        itme8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate("endTime");
                showDataPop(dataPopupWindow,layout);
            }
        });
    }
    private Map<String ,String> proStand = new HashMap<>();
    private View dataPop;
    private PopupWindow dataPopupWindow;
    private void showDate(final String index){
        {

            dataPop = inflater.inflate(R.layout.date_one_layout, null);
            dataPopupWindow = new PopupWindow(dataPop, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout. LayoutParams.MATCH_PARENT);
            dataPopupWindow.setBackgroundDrawable(getResources().getDrawable(
                    R.color.bcolor));
            //android.R.style.TextAppearance_DeviceDefault_Widget_TextView_PopupMenu)
            dataPopupWindow.setAnimationStyle(android.R.style.TextAppearance_DeviceDefault_Widget_TextView_PopupMenu);

            dataPopupWindow.update();
            dataPopupWindow.setTouchable(true);
            dataPopupWindow.setFocusable(true);
            final GregorianLunarCalendarOneView mGLCView0 = (GregorianLunarCalendarOneView) dataPop.findViewById(R.id.calendar_view_start);

            mGLCView0.init();//init has no scroll effection, to today

            dataPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {

                   //更新数据
                    detailProblemHandler.sendEmptyMessage(60);

                }
            });
            TextView sure = (TextView) dataPop.findViewById(R.id.sure);
            TextView cancle = (TextView) dataPop.findViewById(R.id.cancle);
            sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    GregorianLunarCalendarOneView.CalendarData calendarData0 = mGLCView0.getCalendarData();
                    Calendar calendar0 = calendarData0.getCalendar();

                    //相同是0   第一个大于第二个是1   第一个小于第二个是-1
                    String showToast =   calendar0.get(Calendar.YEAR) + "-"
                            + (calendar0.get(Calendar.MONTH) + 1) + "-"
                            + calendar0.get(Calendar.DAY_OF_MONTH);

//                    NewToast.makeText(DetailProblemActivity.this,showToast,1000).show();
                    DB.getInstance().prother(index,showToast,bean);
                    //更新数据库
                    disDataPop(dataPopupWindow,dataPop,null);

                }
            });
            View diss = dataPop.findViewById(R.id.diss);
            View diss0 = dataPop.findViewById(R.id.diss0);
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
            diss0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    disDataPop(dataPopupWindow,dataPop,null);
                }
            });
        }
    }
    private void showDataPop(PopupWindow popupWindow,View view) {
        if (!popupWindow.isShowing()) {
            // mPopupWindow.showAsDropDown(view,0,0);
            // 第一个参数指定PopupWindow的锚点view，即依附在哪个view上。
            // 第二个参数指定起始点为parent的右下角，第三个参数设置以parent的右下角为原点，向左、上各偏移10像素。
            int[] location = new int[2];

            view.getLocationOnScreen(location);

            popupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER,0,0);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101&&resultCode==1000){
            //安检员
            P.c("返回的地址"+data.getStringExtra("path"));
            ImageLoader.getInstance().displayImage("file://"+data.getStringExtra("path"),item15);
        }else if(requestCode==102&&resultCode==1000){
            //客户签名
            ImageLoader.getInstance().displayImage("file://"+data.getStringExtra("path"),item16);
        }
    }

    private class DetailProblemHandler extends Handler {
        WeakReference<DetailProblemActivity> mLeakActivityRef;

        public DetailProblemHandler(DetailProblemActivity leakActivity) {
            mLeakActivityRef = new WeakReference<DetailProblemActivity>(leakActivity);
        }

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            if (mLeakActivityRef.get() != null) {
                switch (msg.what){
                    case 50:
                        DB.getInstance().getProblemList(txtItems,bean);
                        detailProblemHandler.sendEmptyMessage(51);
                        break;
                    case 51:
                        itemAdapter.updata(txtItems);
                        break;
                    case 60:
                            DB.getInstance().getProStand(proStand,bean);
                            detailProblemHandler.sendEmptyMessage(61);
                        break;
                    case 61:
                        itme7.setText(proStand.get("startTime"));
                        itme8.setText(proStand.get("endTime"));
                        ImageLoader.getInstance().displayImage("file://"+proStand.get("staffLine"),item15);
                        ImageLoader.getInstance().displayImage("file://"+proStand.get("personLine"),item16);
                        ImageLoader.getInstance().displayImage("file://"+proStand.get("personPhoto"),item17);
                        break;
                    case 70:
                        DB.getInstance().updataStand(bean);
                        detailProblemHandler.sendEmptyMessage(71);
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
                }
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_problem_layout);
        Intent intent = getIntent();
        if(intent.hasExtra("obj")){
            bean = (ReserItemBean) intent.getSerializableExtra("obj");
        }
        takePhoto = getTakePhoto();
        sharedUtils = new SharedUtils(Common.config);
        detailProblemHandler = new DetailProblemHandler(DetailProblemActivity.this);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    public void takeSuccess(final TResult result) {
        super.takeSuccess(result);
        if(SELECT_INDEX==-2){
            ArrayList<TImage> imsge = result.getImages();
            if(imsge.size()!=0){
                DB.getInstance().prother("personPhoto",imsge.get(0).getCompressPath(),bean);
                detailProblemHandler.sendEmptyMessage(60);
            }
        }else {
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    ArrayList<TImage> imsge = result.getImages();
                    DB.getInstance().addDetailProImages(txtItems.get(SELECT_INDEX).getId(),bean,imsge,detailProblemHandler);
                }
            }.start();


        }
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }
}
