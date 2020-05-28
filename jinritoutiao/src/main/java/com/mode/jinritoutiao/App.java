package com.mode.jinritoutiao;

import android.app.Application;

import com.lzy.okgo.OkGo;

public class App extends Application {

    public static App sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        OkGo.getInstance().init(this);
    }
}
