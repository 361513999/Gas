package com.hhkj.gas.www.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.base.BaseActivity;
import com.zc.http.okhttp.request.RequestCall;

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
    private Button login;
    @Override
    public void init() {
        user = (EditText) findViewById(R.id.user);
        pass = (EditText) findViewById(R.id.pass);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    });
    }
    private RequestCall loginCall;
    private void login(){
        String userValue = user.getText().toString();
        String passValue = pass.getText().toString();

    }

}
