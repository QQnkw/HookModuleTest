package com.mode.jinritoutiao;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;

import com.google.gson.JsonObject;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JinRiTouTiaoHook implements IXposedHookLoadPackage {
    private Context mContext;
    private String mData;
    private String mLoginName = "游客";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.ss.android.article.news")) {
            XposedBridge.log("模块加载成功");
            XposedHelpers.findAndHookMethod(ContextThemeWrapper.class, "attachBaseContext", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log("attachBaseContext源码执行前AAA");
                    if (mContext == null) {
                        mContext = (Context) param.args[0];
                    } else {
                        if (!TextUtils.isEmpty(mData)) {
                            mData = null;
                        }
                    }
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("attachBaseContext源码执行后VVV");
                }
            });
            //点赞按钮触发
            XposedHelpers.findAndHookMethod("com.ss.android.detail.feature.detail2.article.g",
                    lpparam.classLoader,
                    "d",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("d源码执行前AAA");
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedBridge.log("d源码执行后VVV");
                            if (mContext != null && !TextUtils.isEmpty(mData)) {
                                XposedBridge.log("d:mContext不为空");
                                Intent intent = new Intent();
                                intent.setAction("com.hook.hookmoduletest.articleInfo");
                                intent.setComponent(new ComponentName("com.hook.hookmoduletest",
                                        "com.mode.jinritoutiao.DataReceiver"));
                                intent.putExtra("DataBean", mData);
                                mContext.sendBroadcast(intent);
                            }
                            //                            printStackInfo();
                            /*Gson gson = new Gson();
                            Class<?> clzss = Class.forName("com.ss.android.detail.feature.detail2.article.d");
                            Method af = clzss.getDeclaredMethod("af");
                            af.setAccessible(true);
                            Object articleInfo = af.invoke(param.thisObject);
                            if (articleInfo!=null) {
                                String s = gson.toJson(articleInfo);
                                XposedBridge.log("articleInfo--->"+s);
                            }
                            Method ag = clzss.getDeclaredMethod("ag");
                            ag.setAccessible(true);
                            Object article = ag.invoke(param.thisObject);
                            if (article!=null) {
                                String s = gson.toJson(article);
                                int length = s.length();
                                int i = length / 8;
                                XposedBridge.log("article1--->"+s.substring(0,i)+"\n    \n");
                                XposedBridge.log("article2--->"+s.substring(i,i*2)+"\n    \n");
                                XposedBridge.log("article3--->"+s.substring(i*2,i*3)+"\n    \n");
                                XposedBridge.log("article4--->"+s.substring(i*3,i*4)+"\n    \n");
                                XposedBridge.log("article5--->"+s.substring(i*4,i*5)+"\n    \n");
                                XposedBridge.log("article6--->"+s.substring(i*5,i*6)+"\n    \n");
                                XposedBridge.log("article7--->"+s.substring(i*6,i*7)+"\n    \n");
                                XposedBridge.log("article8--->"+s.substring(i*7)+"\n    \n");
                            }*/
                        }
                    });

            XposedHelpers.findAndHookMethod("com.ss.android.detail.feature.detail2.article.d",
                    lpparam.classLoader,
                    "ag",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("ag源码执行前AAA");
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedBridge.log("ag源码执行后VVV");
                            //                            printStackInfo();
                            if (!TextUtils.isEmpty(mData)) {
                                return;
                            }
                            Object article = param.getResult();
                            if (article != null) {
                                //文章类型
                                Class spipeItemClzss = Class.forName("com.ss.android.model" +
                                        ".SpipeItem");
                                Field tagField = spipeItemClzss.getDeclaredField("tag");
                                tagField.setAccessible(true);
                                String tag = (String) tagField.get(article);

                                Class articleEntityClzss = Class.forName("com.bytedance.android" +
                                        ".ttdocker.article.ArticleEntity");
                                //文章标题
                                Field titleField = articleEntityClzss.getDeclaredField("title");
                                titleField.setAccessible(true);
                                String title = (String) titleField.get(article);
                                //文章URL
                                Field displayUrlField = articleEntityClzss.getDeclaredField("displayUrl");
                                displayUrlField.setAccessible(true);
                                String displayUrl = (String) displayUrlField.get(article);

                                //文章封面
                                Class articleClzss = Class.forName("com.bytedance.android.ttdocker.article.Article");
                                String coverUrl = "";
                                Field mMiddleImageField = articleClzss.getDeclaredField("mMiddleImage");
                                mMiddleImageField.setAccessible(true);
                                Object mMiddleImage = mMiddleImageField.get(article);
                                Class<?> ImageInfoClzss = Class.forName("com.ss.android.image.model.ImageInfo");
                                Field mUrlListField = ImageInfoClzss.getDeclaredField("mUrlList");
                                mUrlListField.setAccessible(true);
                                String mUrlList = (String) mUrlListField.get(mMiddleImage);
                                if (!TextUtils.isEmpty(mUrlList)) {
                                    int startIndex = mUrlList.indexOf(":") + 2;
                                    int endIndex = mUrlList.indexOf("}") - 1;
                                    coverUrl = mUrlList.substring(startIndex, endIndex);
                                }
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("tag", mLoginName);
                                jsonObject.addProperty("title", title);
                                jsonObject.addProperty("url", displayUrl);
                                jsonObject.addProperty("cover", coverUrl);
                                jsonObject.addProperty("remark", tag);
                                jsonObject.addProperty("platform", "toutiao");
                                mData = jsonObject.toString();
                                XposedBridge.log(mData);
                            }
                        }
                    });
            XposedHelpers.findAndHookMethod("com.ss.android.account.SpipeData",
                    lpparam.classLoader,
                    "getUserName",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            String loginName = (String) param.getResult();
                            if (TextUtils.equals(loginName, mLoginName)) {
                                return;
                            }
                            if (TextUtils.isEmpty(loginName)) {
                                mLoginName = "游客";
                                XposedBridge.log("用户名称:无");
                            } else {
                                mLoginName = loginName;
                                XposedBridge.log("用户名称:" + loginName);
                            }
                            //                            printStackInfo();
                        }
                    });
        }
    }

    public void printStackInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            String path = "类名:" + className + "----方法名：" + methodName;
            XposedBridge.log(path);
            //                        Log.e("gaokuanghua", path);
        }
    }
}
