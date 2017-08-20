package com.hhkj.gas.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.adapter.DetailDlgAdapter;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffImageItem;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.common.SharedUtils;
import com.hhkj.gas.www.db.DB;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class DetailImageDlg {
    private Context context;

    private IDialog dlg;

    private  LayoutInflater inflater;
    private SharedUtils sharedUtils;
    private ImageLoader imageLoader;
    private ReserItemBean bean;
    private StaffImageItem id;

    public DetailImageDlg(Context context, SharedUtils sharedUtils,ImageLoader imageLoader,StaffImageItem id,ReserItemBean bean ) {
        this.context = context;
        this.id = id;
        this.bean = bean;
        this.sharedUtils = sharedUtils;
            this.imageLoader = imageLoader;
       inflater  = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    private Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        switch (msg.what){
            case 1:
                dlgAdapter.updata(rbs);
                break;
        }
        }
    };
    private ArrayList<StaffImageItem> rbs = new ArrayList<>();
    private DetailDlgAdapter dlgAdapter;
    public Dialog showSheet() {
        dlg = new IDialog(context, R.style.head_pop_style);
        final LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.detail_images_dlg, null);
        final TextView title = (TextView) layout.findViewById(R.id.title);
        final GridView childs_list = (GridView) layout.findViewById(R.id.childs_list);
        title.setText(id.getTag());

        new Thread(){
            @Override
            public void run() {
                super.run();
                DB.getInstance().updataStaffImageDlgItem(rbs,id.getId(),bean);
                handler.sendEmptyMessage(1);
            }
        }.start();
        childs_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        dlg.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                // TODO Auto-generated method stub
                P.c("弹出框长度"+layout.getWidth());
                dlgAdapter = new DetailDlgAdapter(context,rbs,imageLoader, (layout.getWidth()-15)/3);
                childs_list.setAdapter(dlgAdapter);
            }
        });
        dlg.setCanceledOnTouchOutside(true);
        dlg.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface arg0) {
                // TODO Auto-generated method stub

            }
        });
        dlg.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {
                // TODO Auto-generated method stub

            }
        });
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }
    public void cancle(){
        if(dlg!=null){
            dlg.cancel();
            dlg = null;
        }
    }
}
