package com.hhkj.gas.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.CopyFile;
import com.hhkj.gas.www.db.DB;


public class DetailTips {
    private Context context;
    private Handler handler;
    private IDialog dlg;
    private int index;
    private  LayoutInflater inflater;
    private String txt;
    public DetailTips(Context context, Handler handler,String txt,int index) {
        this.context = context;
        this.handler = handler;
        this.txt = txt;
        this.index = index;
        inflater  = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public Dialog showSheet() {
        dlg = new IDialog(context, R.style.load_pop_style);
        final LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.clear_merroy, null);
        TextView item0 = (TextView) layout.findViewById(R.id.item0);
        TextView item1 = (TextView) layout.findViewById(R.id.item1);
        TextView tt = (TextView) layout.findViewById(R.id.txt);
        tt.setText(txt);
        item0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancle();


            }
        });
        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清除数据
            handler.sendEmptyMessage(index);
                cancle();
            }
        });


        dlg.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                // TODO Auto-generated method stub

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
