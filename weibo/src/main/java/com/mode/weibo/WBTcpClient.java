package com.mode.weibo;

import java.io.*;
import java.net.Socket;

/**
 * 1关闭防火墙才能使用;
 * 不关,会报拒绝接入端口错误
 * 2模拟器先重连ADB,第一次跑会出错,多跑几次后会正常
 */
public class WBTcpClient {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8080;
    public static final String adb_path = "E:\\Software\\Nox\\bin\\nox_adb";//adb所在路径
    private Socket mClient;
    private OutputStream outputStream;
    private InputStream inputStream;
    private String mSrcUrl = "https://www.baidu.com/";

    public WBTcpClient(String srcUrl) {
        mSrcUrl = srcUrl;
        try {
            mClient = new Socket(HOST, PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToServer() {
        try {
            //把虚拟机的8090端口绑定到PC本机的8080端口，这样当PC向8080发送数据时实际上是发到虚拟机的8090端口
            //Runtime.getRuntime().exec(adb_path + " –s emulator-5554 forward tcp:8080 tcp:8090");这个方法不好用
            Runtime.getRuntime().exec(adb_path + " forward tcp:8080 tcp:8090");//这个好用
            System.out.println("已经将虚拟机端口8090绑定到PC端口8080 " + adb_path);
            outputStream = mClient.getOutputStream();
            outputStream.write(mSrcUrl.getBytes());
            inputStream = mClient.getInputStream();
            int length = 0;
            byte[] arr = new byte[1024];
            StringBuilder sb = new StringBuilder();
            if ((length = inputStream.read(arr)) != -1) {
                String str = new String(arr, 0, length);
                sb.append(str);
            }
            System.out.println("服务端数据:" + sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            try {
                if (mClient != null) {
                    mClient.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
