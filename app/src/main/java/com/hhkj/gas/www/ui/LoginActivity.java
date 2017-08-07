package com.hhkj.gas.www.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.FileUtils;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.common.SharedUtils;
import com.hhkj.gas.www.common.U;
import com.hhkj.gas.www.widget.NewToast;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by cloor on 2017/8/6.
 */

public class LoginActivity extends BaseActivity{
    private LoginHandler handler;
    private class LoginHandler extends Handler {
        WeakReference<LoginActivity> mLeakActivityRef;

        public LoginHandler(LoginActivity leakActivity) {
            mLeakActivityRef = new WeakReference<LoginActivity>(leakActivity);
        }
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            if (mLeakActivityRef.get() != null){
                switch (msg.what){
                    case 0:
                        NewToast.makeText(LoginActivity.this,(String)msg.obj,2000).show();
                        break;
                    case 1:
                        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(intent);
                        AppManager.getAppManager().finishActivity(LoginActivity.this);
                        break;
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gas_login);
        handler = new LoginHandler(LoginActivity.this);
    }
    private EditText user,pass;
    private TextView login;
    @Override
    public void init() {
        user = (EditText) findViewById(R.id.user);
        pass = (EditText) findViewById(R.id.pass);
        login = (TextView) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            login();
        }
    });
    }
    private RequestCall loginCall;
    private void login(){
        String userValue = user.getText().toString().trim();
        String passValue = pass.getText().toString().trim();
        JSONObject object = new JSONObject();
        try {
            object.put("id",userValue);
            object.put("pwd",passValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loginCall = OkHttpUtils.postString().url(U.VISTER(U.BASE_URL)+U.LOGIN).mediaType(MediaType.parse("application/json; charset=utf-8")).content(object.toString()).build();
        P.c(U.VISTER(U.BASE_URL)+U.LOGIN);
        loginCall.execute(loginCallback);
    }
    //数据返回
    private StringCallback loginCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(String response, int id) {
            try {
                P.c(FileUtils.formatJson(response));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONObject jsonObject = new JSONObject( FileUtils.formatJson(response));
                if(jsonObject.getBoolean("Success")){
                    SharedUtils sharedUtils = new SharedUtils(Common.config);
                    sharedUtils.setStringValue("token",jsonObject.getString("Value"));
                    handler.sendEmptyMessage(1);
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
    };

}
