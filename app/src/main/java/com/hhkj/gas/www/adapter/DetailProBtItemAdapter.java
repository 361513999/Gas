package com.hhkj.gas.www.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.StaffImageItem;
import com.hhkj.gas.www.bean.StaffTxtItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by cloor on 2017/8/9.
 */

public class DetailProBtItemAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<StaffTxtItem> rbs;
    private int HEIGHT ;
    private Handler handler;
    public DetailProBtItemAdapter(Context context, ArrayList<StaffTxtItem> rbs ){
        this.context = context;
        this.rbs = rbs;
        this.HEIGHT = HEIGHT;
        this.handler = handler;
        inflater = LayoutInflater.from(context);
    }
    public void updata(ArrayList<StaffTxtItem> rbs){
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
        TextView item0;

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
            convertView = inflater.inflate(R.layout.detailpr_bt_item, null);

            viewHolder.item0 = (TextView) convertView.findViewById(R.id.item0);

            convertView.setTag(R.mipmap.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.mipmap.ic_launcher
                    + position);
        }
        viewHolder.item0.setText((position+1)+"、"+rbs.get(position).getTxt());
        return  convertView;
    }
}
