package com.mode.weibo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    public static final String            TAG = "MainActivity";
    private             AppCompatButton   mBtnStartServer;
    private             AppCompatButton   mBtnStopServer;
    private             AppCompatActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_main);

        mBtnStartServer = findViewById(R.id.btn_start_server);
        mBtnStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTcpServer();
            }
        });
        mBtnStopServer = findViewById(R.id.btn_stop_server);
        mBtnStopServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mServer != null) {
                        if (!mServer.isClosed()) {
                            mServer.close();
                        }
                        mServer = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String       mEncodeUrl    = "1234567890";
    private ServerSocket mServer;

    private void startTcpServer() {
        new Thread(new Runnable() {
            private InputStream mInputStream;
            private OutputStream mOutputStream;
            private Socket mClient;

            @Override
            public void run() {
                try {
                    mServer = new ServerSocket(8090);
                    Log.d(TAG, "服务器启动");
                    //提示服务器启动
                    while (true) {
                        //调用accept()方法开始监听，等待客户端的连接
                        mClient = mServer.accept();
                        try {
                            mInputStream = mClient.getInputStream();
                            byte[] arr = new byte[1024];
                            StringBuilder sb = new StringBuilder();
                            int length = 0;
                            if ((length = mInputStream.read(arr)) != -1) {
                                String str = new String(arr, 0, length);
                                sb.append(str);
                            }
                            String text = "客服端数据:" + sb.toString();
                            Log.d(TAG, text);
                            int waitTime = 0;
                            //获得和客户端相连的IO输出流
                            mOutputStream = mClient.getOutputStream();
                            while (true) {
                                if (TextUtils.isEmpty(mEncodeUrl)) {
                                    Thread.sleep(250);
                                    waitTime++;
                                    if (waitTime == 5) {
                                        mOutputStream.write(mEncodeUrl.getBytes());
                                        break;
                                    }
                                } else {
                                    mOutputStream.write(mEncodeUrl.getBytes());
                                    break;
                                }
                            }
                        } catch (IOException | InterruptedException e) {
                            Log.d(TAG, e.getMessage());
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
                                Log.d(TAG, e.getMessage());
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }).start();
    }
}
