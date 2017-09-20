package com.hhkj.gas.www.adapter;

import android.content.Context;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.StaffImageItem;
import com.hhkj.gas.www.common.P;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by cloor on 2017/8/9.
 */

public class DetailDlgAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<StaffImageItem> rbs;
    private int HEIGHT ;
    private Handler handler;
    public DetailDlgAdapter(Context context, ArrayList<StaffImageItem> rbs, int HEIGHT,Handler handler ){
        this.context = context;
        this.rbs = rbs;
        this.HEIGHT = HEIGHT;
        this.handler = handler;
        inflater = LayoutInflater.from(context);
    }
    public void updata(ArrayList<StaffImageItem> rbs){
        this.rbs = rbs;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return rbs.size()+1;
    }

    @Override
    public Object getItem(int position) {
        return rbs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private class ViewHolder {
        ImageView item0;
        TextView txt;
        RelativeLayout layout;
    }
    private boolean show =false;
    public void change(boolean show){
        this.show = show;
        notifyDataSetChanged();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.mipmap.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.detail_image_item_dlg, null);
            viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.layout);
            viewHolder.item0 = (ImageView) convertView.findViewById(R.id.item0);
            viewHolder.txt = (TextView) convertView.findViewById(R.id.item1);
            convertView.setTag(R.mipmap.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.mipmap.ic_launcher
                    + position);
        }
        viewHolder.layout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,HEIGHT));
        if(position==0){
//            viewHolder.item0.setImageResource(R.mipmap.btn_add);
//            viewHolder.item0.setScaleType(ImageView.ScaleType.FIT_XY);
            ImageLoader.getInstance().displayImage("drawable://"+R.mipmap.btn_add,viewHolder.item0);
            viewHolder.txt.setVisibility(View.GONE);
            viewHolder.item0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  handler.sendEmptyMessage(3);
                }
            });
        }else{
            final StaffImageItem it = rbs.get(position-1);
            ImageLoader.getInstance().displayImage("file://"+it.getPath(),viewHolder.item0);
            viewHolder.txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message msg = new Message();
                    msg.what = 2;
                    msg.arg1 = it.getI();
                    handler.sendMessage(msg);
                }
            });
            viewHolder.item0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message msg = new Message();
                    msg.what = 4;
                    msg.arg1 = position-1;
                    handler.sendMessage(msg);
                }
            });
        }

        return  convertView;
    }
}
