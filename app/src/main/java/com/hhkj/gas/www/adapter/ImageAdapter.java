package com.hhkj.gas.www.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.hhkj.gas.www.R;
import com.jph.takephoto.model.TImage;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2017/9/20/020.
 */

public class ImageAdapter extends PagerAdapter {
    private ArrayList<String> images;
    private Context context;
    public ImageAdapter( Context context,ArrayList<String> images){
        this.images = images;
        this.context = context;
    }
    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(context);
        String path = images.get(position);
        ImageLoader.getInstance().displayImage("file://"+ path,photoView);
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
