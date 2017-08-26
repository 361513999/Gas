package com.hhkj.gas.www.bluetooth.printer;

import android.content.Context;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.DetailStaff;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffB;
import com.hhkj.gas.www.bean.StaffImageItem;
import com.hhkj.gas.www.bean.StaffQj;
import com.hhkj.gas.www.bean.StaffTxtItem;
import com.hhkj.gas.www.common.P;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/17/017.
 */

/**
 * 安检打印
 */
public class DetailProMark implements PrintDataMaker {
    private PrinterWriter printerWriter;


    @Override
    public List<byte[]> getPrintData(int type) {
        ArrayList<byte[]> data = new ArrayList<>();
        try {
            printerWriter =  new PrinterWriter80mm(255, 600);
            printerWriter.setAlignCenter();
            data.add(printerWriter.getDataAndReset());
            //打印logo
            ArrayList<byte[]> logo = printerWriter.getImageByte(context.getResources(), R.mipmap.logo);
            //data.addAll(logo);

            printerWriter.setAlignLeft();
            printerWriter.printLine();
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            //开始文字
            printerWriter.setAlignCenter();
            printerWriter.setEmphasizedOn();
            printerWriter.print(context.getString(R.string.staff_view_item0));
            printerWriter.setEmphasizedOff();
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            printerWriter.setFontSize(1);
            printerWriter.print(context.getString(R.string.staff_view_item4));
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            printerWriter.setFontSize(0);
            printerWriter.print("工作编号:"+bean.getNo());
            printerWriter.printLineFeed();
            printerWriter.setAlignRight();
            printerWriter.print("第一次打印");
            printerWriter.printLineFeed();
            printerWriter.setAlignLeft();
            //开始写客户
            printerWriter.writeH(70);
            printerWriter.printL(context.getString(R.string.staff_view_item6," ",bean.getName()," "));

            printerWriter.print( context.getString(R.string.staff_view_item5," "));
            printerWriter.printLineFeed();
            printerWriter.setEmphasizedOn();
            for(int i=0;i<staffTxtItems.size();i++){
                printerWriter.printL((i+1)+"、"+staffTxtItems.get(i).getTxt());
            }
            printerWriter.setEmphasizedOff();
            printerWriter.printLineFeed();
            printerWriter.print("停气日期:2017-08-08");
            printerWriter.printLineFeed();
            printerWriter.print("并在:2017-08-08前整改完毕，特此通知。");
            printerWriter.printLineFeed();
            printerWriter.print("联系电话："+bean.getTel());
            printerWriter.printLineFeed();
            printerWriter.print("家庭地址："+bean.getAdd());
            printerWriter.printLineFeed();
            printerWriter.print("客户签名：");
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            printerWriter.print("检查人："+bean.getStaffName());
            printerWriter.printLineFeed();
            printerWriter.setAlignRight();
            printerWriter.print("填发日期：2017-08-08");
            printerWriter.printLineFeed();
            printerWriter.setAlignCenter();
            printerWriter.printLineFeed();
            printerWriter.print("服务热线：18813955321");

            printerWriter.printLineFeed();

            printerWriter.printLine();
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            data.add(printerWriter.getDataAndReset());
            printerWriter.feedPaperCutPartial();
            data.add(printerWriter.getDataAndClose());
            return  data;
        } catch (IOException e) {
            return new ArrayList<>();
        }

    }
    private Context context;
    private ReserItemBean bean;
    private ArrayList<StaffTxtItem> staffTxtItems;

    public DetailProMark(Context context, ReserItemBean bean, ArrayList<StaffTxtItem> staffTxtItems  ){
        this.context = context;
        this.bean = bean;
        this.staffTxtItems = staffTxtItems;

    }
}
