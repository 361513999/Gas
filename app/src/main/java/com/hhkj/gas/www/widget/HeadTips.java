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
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffImageItem;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.db.DB;

import java.util.ArrayList;


public class HeadTips {
    private Context context;

    private IDialog dlg;

    private  LayoutInflater inflater;
    private Handler handler;
    private ArrayList<StaffImageItem> staffImageItems;
    private  ReserItemBean bean;
    public HeadTips(Context context, Handler handler, ArrayList<StaffImageItem> staffImageItems, ReserItemBean bean) {
        this.context = context;
        this.handler  = handler;
        this.bean = bean;
        this.staffImageItems = staffImageItems;
        inflater  = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public HeadTips(Context context, Handler handler,ReserItemBean bean) {
        this.context = context;
        this.bean = bean;
        this.handler  = handler;
         inflater  = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    private boolean isImage(){
        for(int i=-0;i<staffImageItems.size();i++){
           if(staffImageItems.get(i).getPath()==null){
               return  false;
           }
        }
        return  true;
    }
    public Dialog showSheet() {
        dlg = new IDialog(context, R.style.load_pop_style);
        final LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.head_to_layout, null);
        TextView item0 = (TextView) layout.findViewById(R.id.item0);
        TextView item1 = (TextView) layout.findViewById(R.id.item1);

        TextView txt = (TextView) layout.findViewById(R.id.txt);
//        txt.setText(type.equals("1")?"将刚领取的任务指派给组员":"将刚确认的任务指派给组员");
        txt.setText("是否立即提交到服务器");
        item0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //立即提交
                if(staffImageItems==null){
                    //是隐患单
                    if(DB.getInstance().canProStand(bean)){
                        handler.sendEmptyMessage(10);
                        cancle();
                    }else {
                        NewToast.makeText(context,"请签名及确定整改日期",Common.TTIME).show();
                    }

                }else{
                    if((bean.getStaffTag()!=null&&bean.getStaffTag().equals("Y"))||(bean.getStaffTag()!=null&&bean.getStaffTag().equals("J"))){
                        if(isImage()){

                            if( DB.getInstance().getCanSend()){
                                P.c("可以发送");
                                if(DB.getInstance().canLinePrint(bean)){
                                    handler.sendEmptyMessage(101);
                                    cancle();
                                }else{
                                    NewToast.makeText(context,"请签名",Common.TTIME).show();
                                }

                            }else {
                                NewToast.makeText(context,"请确认签名和安检单状态", Common.TTIME).show();
                            }
                        }else{
                            NewToast.makeText(context,"至少每项上传一张安检图片", Common.TTIME).show();
                        }
                    }else{
                        NewToast.makeText(context,"安检项目不合格", Common.TTIME).show();
                    }
                }



            }
        });
        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //本地更新
                handler.sendEmptyMessage(7);
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
