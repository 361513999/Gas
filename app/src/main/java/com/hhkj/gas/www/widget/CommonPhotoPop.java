package com.hhkj.gas.www.widget;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.inter.PhotoSelect;

public class CommonPhotoPop {


    private CommonPhotoPop() {

    }

    public static Dialog showSheet(Context context, final PhotoSelect select, final int index) {
        final Dialog dlg = new Dialog(context, R.style.delete_pop_style);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.common_photo_pop, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        TextView camer = (TextView) layout.findViewById(R.id.camer);
        TextView photo = (TextView) layout.findViewById(R.id.photo);
        TextView mCancel = (TextView) layout.findViewById(R.id.cancel);
        camer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                select.camcer(index);
                dlg.dismiss();
            }
        });
        photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                select.photos(index);
                dlg.dismiss();
            }
        });

        mCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//				actionSheetSelected.onClick(object);
                dlg.dismiss();
            }
        });

        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(false);
        dlg.setContentView(layout);
        dlg.show();

        return dlg;
    }

    public void cancle() {

    }

}
