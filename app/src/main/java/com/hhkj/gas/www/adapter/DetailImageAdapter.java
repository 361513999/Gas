package com.hhkj.gas.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.AreaBean;
import com.hhkj.gas.www.bean.StaffImageItem;

import java.util.ArrayList;

/**
 * Created by cloor on 2017/8/9.
 */

public class DetailImageAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<StaffImageItem> rbs;
    public DetailImageAdapter(Context context, ArrayList<StaffImageItem> rbs){
        this.context = context;
        this.rbs = rbs;
        inflater = LayoutInflater.from(context);
    }
    public void updata(ArrayList<StaffImageItem> rbs){
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
        ImageView item0;
        TextView txt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.mipmap.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.detail_image_item, null);
            viewHolder.item0 = (ImageView) convertView.findViewById(R.id.item0);
            viewHolder.txt = (TextView) convertView.findViewById(R.id.item1);
            convertView.setTag(R.mipmap.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.mipmap.ic_launcher
                    + position);
        }
        StaffImageItem it = rbs.get(position);
      if(it.getPath()==null){
          viewHolder.item0.setImageResource(R.mipmap.btn_add);
      }
        viewHolder.txt.setText(it.getTag());
        return  convertView;
    }
}
