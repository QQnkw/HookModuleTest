package com.mode.weibo;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {
    public static final String TAG = "TcpServer";
    // 定义侦听端口号
    private final int SERVER_PORT = 8090;

    public void startServer() {
        try {
            //ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            // 循环侦听客户端连接请求
            while (true) {
                Socket client = serverSocket.accept();

                try {
                    Log.e("hehheh", "有人来访:");
                    // 等待客户端发送打开网站的消息
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String str = in.readLine();
                    Log.d(TAG,"客户端消息:"+str);
                    OutputStream outputStream = client.getOutputStream();
                    Thread.sleep(10*1000);
                    outputStream.write("响应成功".getBytes());
                    Log.d(TAG,"服务端响应");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    client.close();
                }
                Thread.sleep(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
