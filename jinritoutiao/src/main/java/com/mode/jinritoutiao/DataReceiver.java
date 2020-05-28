package com.mode.jinritoutiao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class DataReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String dataBean = intent.getStringExtra("DataBean");
        if (TextUtils.isEmpty(dataBean)) {
            return;
        }
        /*String url = "xxxxxxx";
        OkGo.<String>post(url)//
                .tag(this)
                .upJson(dataBean)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Toast.makeText(App.sContext, "已收藏", Toast.LENGTH_LONG).show();
                    }
                });*/
    }
}
