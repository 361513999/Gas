package com.hhkj.gas.www.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.DetailStaff;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffTxtItem;
import com.hhkj.gas.www.common.FileUtils;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.db.DB;
import com.hhkj.gas.www.inter.ProSelect;
import com.hhkj.gas.www.ui.DetailActivity;
import com.hhkj.gas.www.widget.auto.AutoFlowLayout;

import java.util.ArrayList;


/**
 * Created by cloor on 2017/8/9.
 */

public class DetailProItemAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private  ArrayList<StaffTxtItem>  rbs;
    private ProSelect proSelect;
    public DetailProItemAdapter(Context context, ArrayList<StaffTxtItem>  rbs, ProSelect proSelect){
        this.context = context;
        this.rbs = rbs;
        this.proSelect = proSelect;
        inflater = LayoutInflater.from(context);
    }
    private int size = 0;
    public void updata( ArrayList<StaffTxtItem>  rbs,int size){
        this.size = size;
        notifyDataSetChanged();
    }
    public void updata( ArrayList<StaffTxtItem>  rbs){
        this.size = rbs.size();
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return size;
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

        TextView item0;
        ImageView item2;
        GridView item3;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.mipmap.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.detail_pro_item, null);
            viewHolder.item0 = (TextView) convertView.findViewById(R.id.item0);
            viewHolder.item2 = (ImageView) convertView.findViewById(R.id.item2);
            viewHolder.item3 = (GridView) convertView.findViewById(R.id.item3);

            convertView.setTag(R.mipmap.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.mipmap.ic_launcher
                    + position);
        }


        final StaffTxtItem it = rbs.get(position);
        int index = position+1;
        int imageLen = it.getImageItems().size();
        viewHolder. item3.setNumColumns(imageLen);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(FileUtils.dip2px(context, 90) * imageLen, LinearLayout.LayoutParams.WRAP_CONTENT);
        viewHolder.item3.setLayoutParams(params);
        DetailProImageAdapter adapter = new DetailProImageAdapter(context,it.getImageItems());
        viewHolder.item3.setAdapter(adapter);
        viewHolder.item0.setText(index+"、"+it.getTxt());
        viewHolder.item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proSelect.select(position);

            }
        });
        return  convertView;
    }


}
