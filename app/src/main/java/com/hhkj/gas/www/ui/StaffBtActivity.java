package com.hhkj.gas.www.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.adapter.DetailBt_Item5Adapter;
import com.hhkj.gas.www.adapter.DetailBt_Item6Adapter;
import com.hhkj.gas.www.adapter.DetailBt_Item7Adapter;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.bean.DetailStaff;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffB;
import com.hhkj.gas.www.bean.StaffImageItem;
import com.hhkj.gas.www.bean.StaffQj;
import com.hhkj.gas.www.bean.StaffTxtItem;
import com.hhkj.gas.www.bluetooth.printer.PrintExecutor;
import com.hhkj.gas.www.bluetooth.printer.PrintSocketHolder;
import com.hhkj.gas.www.bluetooth.printer.StaffMark;
import com.hhkj.gas.www.bluetooth.printer.TestPrintDataMaker;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.db.DB;
import com.hhkj.gas.www.widget.InScrollListView;
import com.hhkj.gas.www.widget.NewToast;
import com.hhkj.gas.www.widget.PrintView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/8/17/017.
 */

public class StaffBtActivity extends BaseActivity  {

    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private StaffMark staffMark;
    private ReserItemBean bean;
    private TextView status,change_bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_bt_layout);
        staffBtHandler = new StaffBtHandler(this);
        status = (TextView) findViewById(R.id.status);
        Intent intent = getIntent();
        if(intent.hasExtra("obj")){
            bean = (ReserItemBean) intent.getSerializableExtra("obj");
        }
        if(bean.getStaffTag().equals("Y")){
            status.setText("安检结果:合格");
        }else if(bean.getStaffTag().equals("J")){
            status.setText("安检结果:隐患已解除");
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(mReceiver,filter);
    }

    private StaffBtHandler staffBtHandler;
    private class StaffBtHandler extends Handler {
        WeakReference<StaffBtActivity> mLeakActivityRef;

        public StaffBtHandler(StaffBtActivity leakActivity) {
            mLeakActivityRef = new WeakReference<StaffBtActivity>(leakActivity);
        }

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            if (mLeakActivityRef.get() != null) {
                switch (msg.what){
                    case 1:
                        staffMark = new StaffMark(StaffBtActivity.this,bean,staffImages,dss,staffBs,staffQjs);
                        item5Adapter.updata(dss);
                        item7Adapter.updata(staffQjs);
                        P.c("staffBs"+staffBs.size());
                        item6Adapter.updata(staffBs);
                        break;
                }
            }
        }
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

    private void load() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                DB.getInstance().updataStand(bean);
                DB.getInstance().updataStaffImageItem(staffImages, bean);
                DB.getInstance().updataStaffTxtItem(txtListMap, bean);
                parseTxtItem();
                DB.getInstance().updataStaffBs(staffBs,bean);
                DB.getInstance().updataStaffQj(staffQjs,bean);
                staffBtHandler.sendEmptyMessage(1);
            }
        }.start();
    }

    private TextView back,print;
    private InScrollListView item5,item7,item6;//安检项目
    private DetailBt_Item5Adapter item5Adapter;
    private DetailBt_Item7Adapter item7Adapter;
    private DetailBt_Item6Adapter item6Adapter;
    private TextView item0,item1,item2,item3,item4;
    private PrintView printView;
    @Override
    public void init() {
        item5 = (InScrollListView) findViewById(R.id.item5);
        item5Adapter = new DetailBt_Item5Adapter(StaffBtActivity.this,dss);
        item5.setAdapter(item5Adapter);
        change_bt = (TextView) findViewById(R.id.change_bt);
        item6 = (InScrollListView) findViewById(R.id.item6);
        item6Adapter = new DetailBt_Item6Adapter(StaffBtActivity.this,staffBs);
        item6.setAdapter(item6Adapter);

        item7Adapter = new DetailBt_Item7Adapter(StaffBtActivity.this,staffQjs);
        item7 = (InScrollListView) findViewById(R.id.item7)
        ;item7.setAdapter(item7Adapter);
        item0 = (TextView) findViewById(R.id.item0);
        item1 = (TextView) findViewById(R.id.item1);
        item2 = (TextView) findViewById(R.id.item2);
        item3 = (TextView) findViewById(R.id.item3);
        item4= (TextView) findViewById(R.id.item4);
        item0.setText("工作编号:"+bean.getNo());
        item1.setText("客户姓名:"+bean.getName());
        item2.setText("联系电话:"+bean.getTel());
        item3.setText("预约时间:"+bean.getTime());
        item4.setText("客户地址:"+bean.getAdd());
        print = (TextView) findViewById(R.id.print);


        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
                AppManager.getAppManager().finishActivity(StaffBtActivity.this);

            }
        });
        change_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goList();
            }
        });
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBluetooth();
            }
        });
        load();
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    P.c("连接成功00");
                    break;

                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_CONNECTED:
                            P.c("连接成功");

                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            P.c("连接中");
                            break;
                        case BluetoothAdapter.STATE_ON:

                            P.c("开启");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            P.c("断开");
                            break;
                        case BluetoothAdapter.STATE_OFF:

                            P.c("关闭");
                            break;
                    }
                    break;
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    /**
     * 检查蓝牙
     */
    private void checkBluetooth() {
        if (bluetoothAdapter == null) {
            NewToast.makeText(StaffBtActivity.this,"不支持蓝牙", Common.TTIME).show();
            return;
        }
        if (bluetoothAdapter.isEnabled()) {

            if(sharedUtils.getStringValue("bt_name").length()==0){
                //没有选择正确的蓝牙
                goList();
            }
            print();
            printView = new PrintView(StaffBtActivity.this);
            printView.showSheet();
            // 载入设备
//            showBluetoothTest();

        } else {
            goList();

        }
    }

    /**
     * 选择合适的蓝牙
     */
    private void goList(){
        Intent in = new Intent(StaffBtActivity.this,BluetoothListActivity.class);
        startActivity(in);
    }
    /**
     * 打印操作
     */
    private BluetoothDevice mDevice;
    private PrintExecutor executor;
    public static final int TYPE_80 = 80;// 纸宽80mm


    private BluetoothDevice getSelect(){
        Set<BluetoothDevice> set = bluetoothAdapter.getBondedDevices();
        Iterator it =  set.iterator();
        while(it.hasNext()){
            BluetoothDevice device = (BluetoothDevice) it.next();
            if(device.getName().equals(sharedUtils.getStringValue("bt_name"))){
                return device;
            }

        }
        return  null;
    }
    private void print(){
        mDevice = getSelect();
        if (mDevice == null)
            return;
        if (executor == null) {
            executor = new PrintExecutor(mDevice, TYPE_80);
            executor.setOnStateChangedListener(new PrintSocketHolder.OnStateChangedListener() {
                @Override
                public void onStateChanged(int state) {
                    P.c("state--"+state);
                }
            });
            executor.setOnPrintResultListener(new PrintExecutor.OnPrintResultListener() {
                @Override
                public void onResult(int errorCode) {
                    P.c("errorCode"+errorCode);
                }
            });
            executor.setOnPrintResultListener(new PrintExecutor.OnPrintResultListener() {
                @Override
                public void onResult(int errorCode) {
                      P.c("error"+errorCode);
                    if(errorCode==0){
                        if(printView!=null){
                             printView.cancle();
                        }
                    }
                }
            });
        }
        P.c("执行的是"+mDevice.getName());
       // executor.setDevice(mDevice);
        executor.doPrinterRequestAsync(staffMark);
    }

}
