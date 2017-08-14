package com.zc.http.okhttp.callback;

import com.hhkj.gas.www.common.FileUtils;
import com.hhkj.gas.www.common.P;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by zhy on 15/12/14.
 */
public abstract class StringCallback extends Callback<String>
{
    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException
    {
        String result = response.body().string();

        try {
            P.c("返回"+ FileUtils.formatJson(result));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
