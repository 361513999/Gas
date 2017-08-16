package com.hhkj.gas.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.DetailBItem;
import com.hhkj.gas.www.bean.StaffImageItem;

import java.util.ArrayList;

/**
 * Created by cloor on 2017/8/9.
 */

public class DetailBAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<DetailBItem> rbs;
    public DetailBAdapter(Context context, ArrayList<DetailBItem> rbs){
        this.context = context;
        this.rbs = rbs;
        inflater = LayoutInflater.from(context);
    }
    public void updata(ArrayList<DetailBItem> rbs){
        this.rbs = rbs;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return rbs.size();
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
            ImageView item3;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.mipmap.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.detail_b_item, null);
            viewHolder.item3 = (ImageView) convertView.findViewById(R.id.item3);
            convertView.setTag(R.mipmap.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.mipmap.ic_launcher
                    + position);
        }
        viewHolder.item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbs.remove(position);
                notifyDataSetChanged();
            }
        });
        return  convertView;
    }
}
