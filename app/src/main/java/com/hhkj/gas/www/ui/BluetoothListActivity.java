package com.hhkj.gas.www.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.adapter.BlueThAdapter;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.widget.NewToast;
import com.hhkj.gas.www.widget.PullToRefreshView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 2017/8/17/017.
 */

public class BluetoothListActivity extends BaseActivity {
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private TextView connect_bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_layout);
    }

    private ListView gas_list;
    private BlueThAdapter thAdapter;
    private ArrayList<BluetoothDevice> bds = new ArrayList<>();
    private TextView ok,back;
    @Override
    public void init() {
        connect_bt = (TextView) findViewById(R.id.connect_bt);
        ok = (TextView) findViewById(R.id.ok);
        back = (TextView) findViewById(R.id.back);
        gas_list = (ListView) findViewById(R.id.gas_list);
        thAdapter = new BlueThAdapter(BluetoothListActivity.this, bds);
        gas_list.setAdapter(thAdapter);
        checkBluetooth();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity(BluetoothListActivity.this);
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(thAdapter.getDevice()!=null){
                    BluetoothDevice device = thAdapter.getDevice();
                    Intent intent = new Intent();
                    setResult(10000,intent);
                    sharedUtils.setStringValue("bt_name",device.getName());
                    sharedUtils.setStringValue("bt_add",device.getAddress());

                    AppManager.getAppManager().finishActivity(BluetoothListActivity.this);
                }else{
                    NewToast.makeText(BluetoothListActivity.this,"请确定已连接的打印机",200).show();
                }

            }
        });
        connect_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSetting();
            }
        });
        gas_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                thAdapter.select(i);
            }
        });
    }
    private void openSetting(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try{
            startActivity(intent);
        } catch(ActivityNotFoundException ex){
            ex.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private AlertDialog dlgBluetoothOpen;

    private void showForceTurnOnBluetoothDialog() {
        if (dlgBluetoothOpen == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("进入系统连接蓝牙");
            builder.setNegativeButton("拒绝", null);
            builder.setPositiveButton("好的",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //查询蓝牙情况
                            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                            startActivity(intent);
                        }
                    });
            dlgBluetoothOpen = builder.create();
        }
        dlgBluetoothOpen.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Set<BluetoothDevice> set = bluetoothAdapter.getBondedDevices();
        Iterator it = set.iterator();
        bds.clear();
        while (it.hasNext()) {
            BluetoothDevice device = (BluetoothDevice) it.next();
            bds.add(device);
        }
        thAdapter.updata(bds);

    }

    /**
     * 检查蓝牙
     */
    private void checkBluetooth() {
        if (bluetoothAdapter == null) {
            NewToast.makeText(BluetoothListActivity.this, "不支持蓝牙", Common.TTIME).show();
            return;
        }
        if (bluetoothAdapter.isEnabled()) {
            if (dlgBluetoothOpen != null && dlgBluetoothOpen.isShowing()) {
                dlgBluetoothOpen.dismiss();
            }

            // 载入设备
//            showBluetoothTest();

        } else {
            showForceTurnOnBluetoothDialog();
        }
    }

}
