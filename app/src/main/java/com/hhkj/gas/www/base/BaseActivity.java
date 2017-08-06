package com.hhkj.gas.www.base;


import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public abstract class BaseActivity extends Activity{
    /**
     * 返回键调用
     */
    public void backActivity(){
        AppManager.getAppManager().finishActivity(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        AppManager.getAppManager().addActivity(this);
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    public abstract void  init();
}
