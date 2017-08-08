package com.hhkj.gas.www.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.adapter.Start0Adapter;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.widget.PullToRefreshView;

/**
 * Created by Administrator on 2017/8/8/008.
 */

public class Start0Activity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.start0_layout);
    }
    private ListView gas_list;
    private TextView back;
    private Start0Adapter start0Adapter;
    private PullToRefreshView pull_to_refresh_list;
    private PullToRefreshView.OnHeaderRefreshListener listHeadListener = new PullToRefreshView.OnHeaderRefreshListener(){

        @Override
        public void onHeaderRefresh(PullToRefreshView view) {
            pull_to_refresh_list.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pull_to_refresh_list.onHeaderRefreshComplete();

                }
            },runTime);
        }
    };
    private PullToRefreshView.OnFooterRefreshListener listFootListener = new PullToRefreshView.OnFooterRefreshListener() {
        @Override
        public void onFooterRefresh(PullToRefreshView view) {
            pull_to_refresh_list.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pull_to_refresh_list.onFooterRefreshComplete();
                }
            },runTime);
        }
    };
private final int runTime = 1000;

    @Override
    public void init() {
        pull_to_refresh_list = (PullToRefreshView) findViewById(R.id.pull_to_refresh_list);
        pull_to_refresh_list.setOnHeaderRefreshListener(listHeadListener);
        pull_to_refresh_list.setOnFooterRefreshListener(listFootListener);
        gas_list = (ListView) findViewById(R.id.gas_list);
        start0Adapter = new Start0Adapter(Start0Activity.this);
        gas_list.setAdapter(start0Adapter);
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity(Start0Activity.this);
            }
        });
    }
}
