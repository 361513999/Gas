package com.hhkj.gas.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.AreaBean;

import java.util.ArrayList;

/**
 * Created by cloor on 2017/8/9.
 */

public class AreasAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<AreaBean> rbs;
    public AreasAdapter(Context context, ArrayList<AreaBean> rbs){
        this.context = context;
        this.rbs = rbs;
        inflater = LayoutInflater.from(context);
    }
    public void updata(ArrayList<AreaBean> rbs){
        this.rbs = rbs;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return  20;
//        return rbs.size();
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
        TextView txt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.mipmap.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.area_item, null);
            viewHolder.txt = (TextView) convertView.findViewById(R.id.txt);
            convertView.setTag(R.mipmap.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.mipmap.ic_launcher
                    + position);
        }
       // AreaBean it = rbs.get(position);
//        viewHolder.txt.setText(it.getName());
        viewHolder.txt.setText("测试");
        return  convertView;
    }
}
