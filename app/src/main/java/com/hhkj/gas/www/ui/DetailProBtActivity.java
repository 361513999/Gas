package com.hhkj.gas.www.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffTxtItem;
import com.hhkj.gas.www.bluetooth.printer.DetailProMark;
import com.hhkj.gas.www.bluetooth.printer.PrintExecutor;
import com.hhkj.gas.www.bluetooth.printer.PrintSocketHolder;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.widget.NewToast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by cloor on 2017/8/25.
 */

public class DetailProBtActivity extends BaseActivity {
    private TextView back;
    private TextView print;
    private DetailProMark detailProMark;
    private ArrayList<StaffTxtItem> staffTxtItems;
    private ReserItemBean bean;
    @Override
    public void init() {
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity(DetailProBtActivity.this);
            }
        });
        print = (TextView) findViewById(R.id.print);
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打印
                checkBluetooth();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_pro_layout);
        Intent intent = getIntent();
        if(intent.hasExtra("obj")){
            staffTxtItems = (ArrayList<StaffTxtItem>) intent.getSerializableExtra("obj");
            bean = (ReserItemBean) intent.getSerializableExtra("bean");
            detailProMark = new DetailProMark(DetailProBtActivity.this,bean,staffTxtItems);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(mReceiver,filter);
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
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private void checkBluetooth() {
        if (bluetoothAdapter == null) {
            NewToast.makeText(DetailProBtActivity.this,"不支持蓝牙", Common.TTIME).show();
            return;
        }
        if (bluetoothAdapter.isEnabled()) {

            if(sharedUtils.getStringValue("bt_name").length()==0){
                //没有选择正确的蓝牙
                goList();
            }
            print();
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
        Intent in = new Intent(DetailProBtActivity.this,BluetoothListActivity.class);
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

                }
            });
        }
        P.c("执行的是"+mDevice.getName());
        // executor.setDevice(mDevice);
        executor.doPrinterRequestAsync(detailProMark);
    }
}
