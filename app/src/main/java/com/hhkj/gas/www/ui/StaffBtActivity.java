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
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.bluetooth.printer.PrintExecutor;
import com.hhkj.gas.www.bluetooth.printer.PrintSocketHolder;
import com.hhkj.gas.www.bluetooth.printer.StaffMark;
import com.hhkj.gas.www.bluetooth.printer.TestPrintDataMaker;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.widget.NewToast;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/8/17/017.
 */

public class StaffBtActivity extends BaseActivity  {

    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private StaffMark staffMark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_bt_layout);
        staffMark = new StaffMark(StaffBtActivity.this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(mReceiver,filter);
    }
    private TextView back;
    @Override
    public void init() {
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
                checkBluetooth();
            }
        });
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

                }
            });
        }
        P.c("执行的是"+mDevice.getName());
       // executor.setDevice(mDevice);
        executor.doPrinterRequestAsync(staffMark);
    }

}
