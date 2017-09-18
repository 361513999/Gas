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


public class ClearTips {
    private Context context;
    private Handler handler;
    private IDialog dlg;

    private  LayoutInflater inflater;

    public ClearTips(Context context,Handler handler) {
        this.context = context;
        this.handler = handler;

        inflater  = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public Dialog showSheet() {
        dlg = new IDialog(context, R.style.load_pop_style);
        final LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.clear_merroy, null);
        TextView item0 = (TextView) layout.findViewById(R.id.item0);
        TextView item1 = (TextView) layout.findViewById(R.id.item1);

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
                CopyFile copyFile = new CopyFile();
                copyFile.delFolder(Common.SD);
                DB.getInstance().clear();
                handler.sendEmptyMessage(0);
                NewToast.makeText(context,"清除完毕",2000).show();
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
