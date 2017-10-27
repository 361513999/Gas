package com.hhkj.gas.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.CopyFile;
import com.hhkj.gas.www.db.DB;


public class CallTips {
    private Context context;
    private Handler handler;
    private IDialog dlg;

    private  LayoutInflater inflater;

    public CallTips(Context context) {
        this.context = context;
        inflater  = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public Dialog showSheet(final String phone) {
        dlg = new IDialog(context, R.style.load_pop_style);
        final LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.clear_merroy, null);
        TextView item0 = (TextView) layout.findViewById(R.id.item0);
        TextView item1 = (TextView) layout.findViewById(R.id.item1);
        TextView txt = (TextView) layout.findViewById(R.id.txt);
        txt.setText("是否拨打电话\n\n"+phone);
        item0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancle();
//                Common.copy();

            }
        });
        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清除数据
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone));
                context.startActivity(intent);
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
