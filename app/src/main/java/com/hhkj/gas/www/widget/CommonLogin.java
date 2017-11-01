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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hhkj.gas.www.R;
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

public class CommonLogin {
    private Context context;

    private IDialog dlg;
    private  LayoutInflater inflater;
    private SharedUtils sharedUtils;
    public CommonLogin(Context context,SharedUtils sharedUtils) {
        this.context = context;
        this.sharedUtils = sharedUtils;
       inflater  = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    private boolean falg = true;
    private int what = 0;
    private Handler detailHandler;
    public void setResult(boolean flag,int what,Handler detailHandler){
        this.falg = flag;
        this.what = what;
        this.detailHandler = detailHandler;
    }

    public void setResult(Handler detailHandler){
        this.detailHandler = detailHandler;
    }
    private Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        switch (msg.what){
            case 0:
                NewToast.makeText(context,(String)msg.obj, Common.TTIME).show();
                break;
            case 1:
                NewToast.makeText(context,"登录成功", Common.TTIME).show();

                break;
        }
        cancle();
        }
    };
    TextView title ;
    public Dialog showSheet() {
        dlg = new IDialog(context, R.style.head_pop_style);
        final LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.common_login, null);
        TextView sure = (TextView) layout.findViewById(R.id.sure);
        final TextView cancle = (TextView) layout.findViewById(R.id.cancle);
        final EditText user = (EditText) layout.findViewById(R.id.user);
        final EditText pass = (EditText) layout.findViewById(R.id.pass);
        title = (TextView) layout.findViewById(R.id.title);
        if(falg){
            title.setText("重新登录");
        }else{
            title.setText("员工校验");
        }

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userValue = user.getText().toString().trim();
                String passValue = pass.getText().toString().trim();
                JSONObject object = new JSONObject();
                try {
                    object.put("id",userValue);
                    object.put("pwd",passValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                OkHttpUtils.postString().url(U.VISTER(U.BASE_URL)+U.LOGIN).mediaType(MediaType.parse("application/json; charset=utf-8")).content(object.toString()).build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        P.c("错误了"+e.getLocalizedMessage());

                    }

                    @Override
                    public void onResponse(String response, int id) {

                        try {
                            JSONObject jsonObject = new JSONObject( FileUtils.formatJson(response));
                            if(jsonObject.getBoolean("Success")){
                                if(falg){
                                    sharedUtils.setStringValue("token",jsonObject.getString("Value"));
                                    sharedUtils.setBooleanValue("head",(jsonObject.getInt("Error")==0)?false:true);
                                    String result = jsonObject.getString("Result");
                                    JSONObject obj = new JSONObject(result);
                                    sharedUtils.setStringValue("userid",obj.getString("Id"));
                                    sharedUtils.setStringValue("name",obj.getString("StaffName"));
                                    sharedUtils.setStringValue("last",user.getText().toString().trim());
                                    if(detailHandler!=null){
                                        detailHandler.sendEmptyMessage(-8);
                                    }
                                    handler.sendEmptyMessage(1);

                                }else {
                                    P.c("校验正常");
                                    detailHandler.sendEmptyMessage(what);
                                    cancle();
                                }

                            }else{
                                Message msg = new Message();
                                msg.what = 0;
                                msg.obj = jsonObject.getString("Error");
                                handler.sendMessage(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
