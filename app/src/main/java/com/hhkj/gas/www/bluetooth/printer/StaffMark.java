package com.hhkj.gas.www.bluetooth.printer;

import android.content.Context;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bluetooth.printer.PrintDataMaker;
import com.hhkj.gas.www.common.P;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/17/017.
 */

public class StaffMark implements PrintDataMaker {
    private PrinterWriter printerWriter;
    private String checkTxt = "□是 □否";

    @Override
    public List<byte[]> getPrintData(int type) {
        ArrayList<byte[]> data = new ArrayList<>();
        try {
            printerWriter =  new PrinterWriter80mm(255, 600);
            printerWriter.setAlignCenter();
            data.add(printerWriter.getDataAndReset());
            //打印logo
            ArrayList<byte[]> logo = printerWriter.getImageByte(context.getResources(), R.mipmap.logo);
            data.addAll(logo);

            printerWriter.setAlignLeft();
            printerWriter.printLine();
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            //开始文字
            printerWriter.setAlignCenter();
            printerWriter.setEmphasizedOn();
            printerWriter.print("珠海燃气");
            printerWriter.setEmphasizedOff();
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            printerWriter.setFontSize(1);
            printerWriter.print("户内安全检查");
            printerWriter.printLineFeed();
            printerWriter.setFontSize(0);
            printerWriter.print("工作编号");
            printerWriter.printLineFeed();
            printerWriter.setAlignRight();
            printerWriter.print("第一次打印");
            printerWriter.printLineFeed();
            printerWriter.setAlignLeft();
           // printerWriter.setLineHeight(10);
            printerWriter.printLineFeed();
            printerWriter.printInOneLine("测试",checkTxt,0);
           // printerWriter.print("发送的方法是范德萨范德萨发生的范德萨的是非得失鬼地方个");
            P.c("打印宽度"+printerWriter.getLineWidth());

            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
//            printerWriter.setAlignCenter();
//            printerWriter.setEmphasizedOn();
//            printerWriter.setFontSize(1);
//
//            printerWriter.print("我的餐厅");
//            printerWriter.printLineFeed();
//            printerWriter.setFontSize(0);
//            printerWriter.setEmphasizedOff();
            printerWriter.printLineFeed();

            data.add(printerWriter.getDataAndReset());

            printerWriter.printLineFeed();
            printerWriter.print("扫一扫，查看详情");
            printerWriter.printLineFeed();

            printerWriter.feedPaperCutPartial();
            data.add(printerWriter.getDataAndClose());
            return  data;
        } catch (IOException e) {
            return new ArrayList<>();
        }

    }
    private Context context;
    public StaffMark(Context context){
        this.context = context;
    }
}
