package com.mode.weibo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WeiBoHook implements IXposedHookLoadPackage {

    private Context      mContext;
    private Object       mOriginalComposerManager;
    private String       mFormClientData = null;
    private String       mEncodeUrl      = null;
    private Handler      mHandler;
    private ServerSocket mServer;

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
                            if (TextUtils.isEmpty(mFormClientData)) {
                                str = (String) param.args[0];
                            } else {
                                str = mFormClientData;
                                param.args[0] = str;
                            }
                            XposedBridge.log("源码执行前AAA" + str);
                            //                            printStackInfo();
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
                                        sb.append("和").append(short_url);
                                    }
                                }
                                mEncodeUrl = sb == null ? "无转换链接" : sb.toString();
                                XposedBridge.log("源码执行后VVV" + mEncodeUrl);
                                mHandler.obtainMessage(3, mEncodeUrl).sendToTarget();
                            }
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
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            mContext = (Context) param.thisObject;
                            mHandler = new Handler(mContext.getMainLooper()) {
                                @Override
                                public void handleMessage(@NonNull Message msg) {
                                    switch (msg.what) {
                                        case 0:
                                            if (mContext != null) {
                                                Toast.makeText(mContext, "服务器启动成功", Toast.LENGTH_LONG).show();
                                            }
                                            break;
                                        case 1:
                                            if (mContext != null) {
                                                Toast.makeText(mContext, mFormClientData, Toast.LENGTH_LONG).show();
                                            }
                                            break;
                                        case 2:
                                            if (mOriginalComposerManager != null) {
                                                //c()是构造发布数据之前的发布操作
                                                XposedHelpers.callMethod(mOriginalComposerManager, "c");
                                            }
                                            break;
                                        case 3:
                                            if (mContext != null) {
                                                Toast.makeText(mContext, (String) msg.obj, Toast.LENGTH_LONG).show();
                                            }
                                            break;
                                    }
                                }
                            };
                            startTcpServer();
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
                            if (mContext != null) {
                                mContext = null;
                            }
                            if (mOriginalComposerManager != null) {
                                mOriginalComposerManager = null;
                            }
                            if (mHandler != null) {
                                mHandler.removeCallbacksAndMessages(null);
                                mHandler = null;
                            }
                            try {
                                if (mServer != null) {
                                    if (!mServer.isClosed()) {
                                        mServer.close();
                                    }
                                    mServer = null;
                                }
                            } catch (IOException e) {
                                XposedBridge.log(e);
                            }
                        }
                    });
        }

    }

    private void startTcpServer() {
        new Thread(new Runnable() {
            private InputStream mInputStream;
            private OutputStream mOutputStream;
            private Socket mClient;

            @Override
            public void run() {
                try {
                    mServer = new ServerSocket(8090);
                    mHandler.sendEmptyMessage(0);
                    //提示服务器启动
                    while (true) {
                        //调用accept()方法开始监听，等待客户端的连接
                        mClient = mServer.accept();
                        try {
                            mEncodeUrl = null;
                            mFormClientData = null;
                            mInputStream = mClient.getInputStream();
                            byte[] arr = new byte[1024];
                            StringBuilder sb = new StringBuilder();
                            int length = 0;
                            if ((length = mInputStream.read(arr)) != -1) {
                                String str = new String(arr, 0, length);
                                sb.append(str);
                            }
                            mFormClientData = sb.toString();
                            mHandler.obtainMessage(1, mFormClientData).sendToTarget();
                            mHandler.sendEmptyMessage(2);
                            int waitTime = 0;
                            //获得和客户端相连的IO输出流
                            mOutputStream = mClient.getOutputStream();
                            while (true) {
                                if (TextUtils.isEmpty(mEncodeUrl)) {
                                    Thread.sleep(250);
                                    waitTime++;
                                    if (waitTime == 5) {
                                        mOutputStream.write("URL转换失败".getBytes());
                                        break;
                                    }
                                } else {
                                    mOutputStream.write(mEncodeUrl.getBytes());
                                    break;
                                }
                            }
                        } catch (IOException | InterruptedException e) {
                            XposedBridge.log(e);
                        } finally {
                            //关闭资源
                            try {
                                if (mClient != null) {
                                    if (!mClient.isClosed()) {
                                        mClient.close();
                                    }
                                    mClient = null;
                                }
                                if (mInputStream != null) {
                                    mInputStream.close();
                                    mInputStream = null;
                                }
                                if (mOutputStream != null) {
                                    mOutputStream.close();
                                    mOutputStream = null;
                                }
                            } catch (IOException e) {
                                XposedBridge.log(e);
                            }
                        }
                    }
                } catch (IOException e) {
                    XposedBridge.log(e);
                }
            }
        }).start();
    }
}
