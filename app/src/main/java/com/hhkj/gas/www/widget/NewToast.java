package com.hhkj.gas.www.widget;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by cloor on 2017/8/7.
 */

public class NewToast extends Toast {

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public NewToast(Context context) {
        super(context);
    }

}
