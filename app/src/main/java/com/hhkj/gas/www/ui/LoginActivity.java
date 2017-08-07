package com.hhkj.gas.www.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.FileUtils;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.common.U;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by cloor on 2017/8/6.
 */

public class LoginActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gas_login);
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
    private StringCallback loginCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(String response, int id) {
            try {
                JSONObject jsonObject = new JSONObject( FileUtils.formatJson(response));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

}
