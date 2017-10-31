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
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.FileUtils;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.common.SharedUtils;
import com.hhkj.gas.www.common.U;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.MediaType;


public class NoTips {
    private Context context;
    private IDialog dlg;

    private  LayoutInflater inflater;
    private SharedUtils sharedUtils;
    private ReserItemBean rb;
    private Handler handler;
    public NoTips(Context context,SharedUtils sharedUtils,ReserItemBean rb,Handler handler) {
        this.context = context;
        this.rb = rb;
        this.handler = handler;
        this.sharedUtils = sharedUtils;
        inflater  = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public Dialog showSheet(final int back) {
        dlg = new IDialog(context, R.style.load_pop_style);
        final LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.clear_merroy, null);
        final TextView item0 = (TextView) layout.findViewById(R.id.item0);
        final TextView item1 = (TextView) layout.findViewById(R.id.item1);
        TextView txt = (TextView) layout.findViewById(R.id.txt);
        if(sharedUtils.getBooleanValue("head")){
            //组长
            if(back==1){
                txt.setText("审批放弃申请");
                item0.setText("同意");
                item1.setText("拒绝");
            }
        }else{
            if(back==1){
                txt.setText("取消放弃申请");
                item0.setText("确定");
                item1.setText("取消");
            }else if(back==0){
                txt.setText("发起放弃申请");
                item0.setText("确定");
                item1.setText("取消");
            }
        }

        item0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item0.getText().toString().equals("确定")){
                    if(back==1){
                        process("CancelGiveUp");
                    }else if (back==0){
                        process("GiveUpOrder");
                    }

                }else if(item0.getText().toString().equals("同意")){
                    process("ConfirmGiveUp");
                }
            }
        });
        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item1.getText().toString().equals("取消")){
                    cancle();
                }else if(item1.getText().toString().equals("拒绝")){
                    process("CancelGiveUp");
                }
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
    private void process(String method){
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("cls","Gas.SecurityOrder");
            jsonObject.put("method",method);
            jsonObject.put("toKen", sharedUtils.getStringValue("token"));
            JSONObject object  = new JSONObject();
            object.put("OrderId",rb.getId());
            jsonObject.put("param",object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils.postString().url(U.VISTER(U.BASE_URL)+U.LIST).mediaType(MediaType.parse("application/json; charset=utf-8")).content(jsonObject.toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject( FileUtils.formatJson(response));
                    P.c(jsonObject.toString());
                    if(jsonObject.getBoolean("Success")){
                        handler.sendEmptyMessage(-8);
                        cancle();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


}
