package com.hhkj.gas.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.FileUtils;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.common.SharedUtils;
import com.hhkj.gas.www.common.U;
import com.hhkj.gas.www.inter.TimeSelect;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import library.NumberPickerView;
import library.view.GregorianLunarCalendarView;
import okhttp3.Call;
import okhttp3.MediaType;

public class ChangeTime {
    private Context context;

    private IDialog dlg;

    private  LayoutInflater inflater;
    private SharedUtils sharedUtils;
    private ReserItemBean it;

    public ChangeTime(Context context, SharedUtils sharedUtils, ReserItemBean it) {
        this.context = context;
        this.it = it;
        this.sharedUtils = sharedUtils;
       inflater  = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    private Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        switch (msg.what){
            case 1:

                break;
        }
        }
    };


    public Dialog showSheet() {
        dlg = new IDialog(context, R.style.head_pop_style);
        final LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.change_time, null);
        TextView sure = (TextView) layout.findViewById(R.id.sure);
        TextView cancle = (TextView) layout.findViewById(R.id.cancle);
        final GregorianLunarCalendarView calendarView = (GregorianLunarCalendarView) layout.findViewById(R.id.calendar_view_start);
        calendarView.init();


        final NumberPickerView picker_minute = (NumberPickerView) layout.findViewById(R.id.picker_minute);
        final NumberPickerView picker_hour = (NumberPickerView) layout.findViewById(R.id.picker_hour);

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GregorianLunarCalendarView.CalendarData calendarData0 = calendarView.getCalendarData();
                final Calendar calendar0 = calendarData0.getCalendar();
                final String show = calendar0.get(Calendar.YEAR) + "-"
                        + (calendar0.get(Calendar.MONTH) + 1) + "-"
                        + calendar0.get(Calendar.DAY_OF_MONTH)+"  "+picker_hour.getContentByCurrValue()+":"+picker_minute.getContentByCurrValue();

            change(show);

            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancle();
            }
        });
        dlg.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                // TODO Auto-generated method stub

            }
        });
        dlg.setCanceledOnTouchOutside(true);
        dlg.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface arg0) {
                // TODO Auto-generated method stub

            }
        });
        dlg.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {
                // TODO Auto-generated method stub

            }
        });
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }
    private TimeSelect select;
    public void setI(TimeSelect select){
        this.select = select;
    }
    private void change(final String time){
        final JSONObject object = new JSONObject();
        try {
            object.put("cls","Gas.SecurityOrder");
            object.put("method","UpdatePlanOrderDate");
            object.put("toKen",sharedUtils.getStringValue("token"));
            JSONObject obj = new JSONObject();
            obj.put("OrderId",it.getId());

            obj.put("Date",time);
            object.put("param",obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString().url(U.VISTER(U.BASE_URL)+U.LIST).mediaType(MediaType.parse("application/json; charset=utf-8")).content(object.toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                P.c("错误了"+e.getLocalizedMessage());

            }

            @Override
            public void onResponse(String response, int id) {

                try {
                    JSONObject jsonObject = new JSONObject( FileUtils.formatJson(response));
                    if(jsonObject.getBoolean("Success")){
                        it.setTime(time);
                        select.success(time);
                        cancle();
                    }else{
                        NewToast.makeText(context,jsonObject.getString("Error"), Common.TTIME).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
    public void cancle(){
        if(dlg!=null){
            dlg.cancel();
            dlg = null;
        }
    }
}
