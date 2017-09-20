package com.hhkj.gas.www.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.adapter.ImageAdapter;
import com.hhkj.gas.www.base.AppManager;
import com.hhkj.gas.www.base.BaseActivity;
import com.hhkj.gas.www.widget.HackyViewPager;
import com.jph.takephoto.model.TImage;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/20/020.
 */

public class ShowImageActivity extends BaseActivity {
    private HackyViewPager im;
    private ImageAdapter imageAdapter;
    @Override
    public void init() {
        im = (HackyViewPager) findViewById(R.id.images);
        imageAdapter = new ImageAdapter(ShowImageActivity.this,images);
        im.setAdapter(imageAdapter);
        im.setCurrentItem(current);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity(ShowImageActivity.this);
            }
        });
    }
    ArrayList<String> images;
    int current = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image_layout);
        Intent intent = getIntent();
        if(intent.hasExtra("list")){
            images = (ArrayList<String>) intent.getSerializableExtra("list");

        }else{
            images= new ArrayList<>();
        }
        if(intent.hasExtra("index")){
            current = intent.getIntExtra("index",0);
        }
    }
}
