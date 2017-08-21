package com.hhkj.gas.www.base;


import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.SharedUtils;
import com.hhkj.gas.www.widget.CommonLogin;
import com.hhkj.gas.www.widget.LoadView;
import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class BaseActivity extends Activity{
    /**
     * 返回键调用
     */

    public SharedUtils sharedUtils;
    public LayoutInflater inflater;

    public void backActivity(){
        AppManager.getAppManager().finishActivity(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        sharedUtils = new SharedUtils(Common.config);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        AppManager.getAppManager().addActivity(this);
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        AppManager.getAppManager().finishActivity(this);
    }

    /**
     * 重新登录
     */
    public void reLogin(){
        CommonLogin login = new CommonLogin(this,sharedUtils);
        login.showSheet();
    }
    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    public abstract void  init();
}
