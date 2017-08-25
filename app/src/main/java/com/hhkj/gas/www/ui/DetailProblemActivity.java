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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.adapter.DetailProItemAdapter;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffTxtItem;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.SharedUtils;
import com.hhkj.gas.www.db.DB;
import com.hhkj.gas.www.inter.PhotoSelect;
import com.hhkj.gas.www.inter.ProSelect;
import com.hhkj.gas.www.widget.CommonPhotoPop;
import com.hhkj.gas.www.widget.InScrollListView;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.LubanOptions;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

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
    private void init(){
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity(DetailProblemActivity.this);
            }
        });

        item0 = (TextView) findViewById(R.id.item0);
        item1 = (TextView) findViewById(R.id.item1);
        item2 = (TextView) findViewById(R.id.item2);
        item3 = (TextView) findViewById(R.id.item3);
        item4 = (TextView) findViewById(R.id.item4);
        item5 = (TextView) findViewById(R.id.item5);
        item6 = (TextView) findViewById(R.id.item6);
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
                detailProblemHandler.sendEmptyMessage(50);
            }
        }.start();
        item18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailProblemActivity.this,DetailProBtActivity.class);
                intent.putExtra("obj",txtItems);
                intent.putExtra("bean",bean);
                startActivity(intent);
            }
        });
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
                    case 6:

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
