package com.mode.weibo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeiBoHook implements IXposedHookLoadPackage {

    private Context mContext;
    private DataReceived mDataReceived;
    private Object mOriginalComposerManager;
    private String mUrlListData;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.sina.weibo")) {
            XposedBridge.log("模块加载成功");
            /*
             * com.sina.weibo.jobqueue.send.c.a(,)数据一切准备好,开始发布
             * com.sina.weibo.composerinde.manager.BaseMessageComposerManager.c()点击发布按钮后,实际开始发布
             * com.sina.weibo.composerinde.OriginalComposerActivity.a可能是发布结果返回
             * */
            // 点击发布按钮后的调用的方法
            /*XposedHelpers.findAndHookMethod("com.sina.weibo.composerinde.WeiboBaseComposerActivity"
                    , lpparam.classLoader
                    , "handleTitleBarEvent"
                    , int.class
                    , new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("源码执行前AAA");
                            //参数是0发布微博
                            //                            printStackInfo();
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedBridge.log("源码执行后VVV");
                        }
                    });*/
            //构建发布的数据
            /*XposedHelpers.findAndHookMethod("com.sina.weibo.composerinde.manager.e"
                    , lpparam.classLoader
                    , "z"
                    , new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("源码执行前AAA");
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            // com.sina.weibo.composer.model.Draft实际发布的对象
                            XposedBridge.log("源码执行后VVV");
                            //                            printStackInfo();
                        }
                    });*/
            // TODO: 2020/5/23  用户输入的参数
            XposedHelpers.findAndHookMethod("com.sina.weibo.requestmodels.hc"
                    , lpparam.classLoader
                    , "l"
                    , String.class
                    , new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            //用户输入的参数
                            String str = null;
                            if (TextUtils.isEmpty(mUrlListData)) {
                                str = (String) param.args[0];
                            } else {
                                String[] urlArr = mUrlListData.split(",");
                                StringBuilder sb = new StringBuilder();
                                for (String url : urlArr) {
                                    sb.append(url).append("和");
                                }
                                str = sb.toString();
                                param.args[0] = str;
                            }
                            XposedBridge.log("源码执行前AAA" + str);
                            //                            printStackInfo();
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedBridge.log("源码执行后VVV");
                        }
                    });
           /* XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    ClassLoader cl = ((Context)param.args[0]).getClassLoader();
                    Class<?> hookclass = null;
                    try {
                        hookclass = cl.loadClass("xxx.xxx.xxx");
                    } catch (Exception e) {
                        XposedBridge.log("寻找类报错"+e);
                        return;
                    }
                    XposedHelpers.findAndHookMethod(hookclass, "xxx", new XC_MethodHook(){
                        //进行hook操作
                    });
                }
            });*/
            // TODO: 2020/5/23 抓取关键字段成功
            final Class<?> statusClzss = lpparam.classLoader.loadClass("com.sina.weibo.models.Status");
            final Class<?> mblogCardClzss = lpparam.classLoader.loadClass("com.sina.weibo.models.MblogCard");
            XposedHelpers.findAndHookMethod(statusClzss
                    , "parseUrls"
                    , JSONArray.class
                    , new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("源码执行前AAA");
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            if (mContext != null) {
                                //发布成功的数据
                                Object object = param.thisObject;
                                Field url_structField = statusClzss.getDeclaredField("url_struct");
                                url_structField.setAccessible(true);
                                List url_struct = (List) url_structField.get(object);
                                StringBuilder sb = null;
                                if (url_struct != null && !url_struct.isEmpty()) {
                                    //short_url=http://t.cn/A62LIU1Z
                                    Field short_urlField = mblogCardClzss.getDeclaredField("short_url");
                                    sb = new StringBuilder();
                                    for (Object o : url_struct) {
                                        String short_url = (String) short_urlField.get(o);
                                        sb.append("\n").append(short_url);
                                    }
                                }
                                final String encodeUrl = sb == null ? "无转换链接" : sb.toString();
                                XposedBridge.log("源码执行后VVV" + encodeUrl);

                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, encodeUrl, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            //                            printStackInfo();
                            //                            createNetRequest();
                        }
                    });
            //带有发布数据的发布方法
            /*Class<?> clzss = Class.forName("com.sina.weibo.composer.model.Draft");
            XposedHelpers.findAndHookMethod("com.sina.weibo.composerinde.manager.OriginalComposerManager"
                    , lpparam.classLoader
                    , "p"
                    , clzss
                    , new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("源码执行前AAA");
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Object object = param.thisObject;
                            Class<?> aClass = Class.forName("com.sina.weibo.composerinde.manager.e");
                            Field field = aClass.getDeclaredField("o");
                            field.setAccessible(true);
                            Object o = field.get(object);
                            XposedBridge.log("源码执行后VVV--->" + o.getClass().getName());
                            printStackInfo();
                        }
                    });*/
            // TODO: 2020/5/23  没有跳转的主动发布操作,测试成功;实际执行发布方法的对象
            XposedHelpers.findAndHookConstructor("com.sina.weibo.composerinde.manager.OriginalComposerManager",
                    lpparam.classLoader,
                    Activity.class,
                    int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedBridge.log("源码执行后VVV");
                            //                            printStackInfo();
                            mOriginalComposerManager = param.thisObject;
                        }
                    });
            //发布结果的解析流程
            /*XposedHelpers.findAndHookMethod("com.sina.weibo.models.Status"
                    , lpparam.classLoader
                    , "initFromJsonObject"
                    , JSONObject.class
                    , new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("源码执行前AAA");
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedBridge.log("源码执行后VVV");
                            printStackInfo();
                        }
                    });*/
            //获取context,并注册数据接收广播
            XposedHelpers.findAndHookMethod("com.sina.weibo.composerinde.WeiboBaseComposerActivity"
                    , lpparam.classLoader
                    , "onCreate"
                    , Bundle.class
                    , new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("源码执行前AAA");
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedBridge.log("源码执行后VVV");
                            mContext = (Context) param.thisObject;
                            if (mContext != null) {
                                mDataReceived = new DataReceived();
                                IntentFilter intentFilter = new IntentFilter();
                                intentFilter.addAction("com.wb.data.url");
                                mContext.registerReceiver(mDataReceived, intentFilter);
                            }
                        }
                    });
            //注销mContext,注销数据广播接收器
            XposedHelpers.findAndHookMethod("com.sina.weibo.composerinde.WeiboBaseComposerActivity"
                    , lpparam.classLoader
                    , "onDestroy"
                    , new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("源码执行前AAA");
                            if (mContext != null) {
                                if (mDataReceived != null) {
                                    mContext.unregisterReceiver(mDataReceived);
                                }
                                mDataReceived = null;
                                mContext = null;
                                mOriginalComposerManager = null;
                            }
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedBridge.log("源码执行后VVV");
                        }
                    });
        }

    }

    private void createNetRequest() {
        String url = "http://wwww.baidu.com";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XposedBridge.log("onFailure:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                XposedBridge.log("onResponse:" + response.body().string());
            }
        });
    }

    private class DataReceived extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mUrlListData = intent.getStringExtra("UrlListData");
            if (!TextUtils.isEmpty(mUrlListData)) {
                try {
                    //c()是构造发布数据之前的发布操作
                    XposedHelpers.callMethod(mOriginalComposerManager, "c");
                    XposedBridge.log("成功了");
                } catch (Exception e) {
                    XposedBridge.log("出错了:" + e.getMessage());
                }
            }

        }
    }

    private void printStackInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            String path = "类名:" + className + "----方法名：" + methodName;
            XposedBridge.log(path);
        }
    }
}
