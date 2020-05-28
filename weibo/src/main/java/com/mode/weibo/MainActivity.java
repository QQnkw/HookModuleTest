package com.mode.weibo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    public static final String            TAG = "MainActivity";
    private             AppCompatEditText mEt;
    private             AppCompatButton   mBtnCoverUrl;
    private             AppCompatButton   mBtnStartServer;
    private             AppCompatButton   mBtnStopServer;
    private             AppCompatActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_main);
        mEt = findViewById(R.id.et);
        mBtnCoverUrl = findViewById(R.id.btn_covert_url);
        mBtnCoverUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mBtnCoverUrl == null) {
                            return;
                        }
                        Toast.makeText(mActivity, "开始转换", Toast.LENGTH_LONG).show();
                        String string = mEt.getText().toString();
                        if (!TextUtils.isEmpty(string)) {
                            Intent intent = new Intent();
                            intent.putExtra("UrlListData", string);
                            intent.setAction("com.wb.data.url");
                            mActivity.sendBroadcast(intent);
                        }
                    }
                }, 30 * 1000);
            }
        });

        mBtnStartServer = findViewById(R.id.btn_start_server);
        mBtnStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "服务器启动", Toast.LENGTH_LONG).show();
            }
        });
        mBtnStopServer = findViewById(R.id.btn_stop_server);
        mBtnStopServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "服务器停止", Toast.LENGTH_LONG).show();
            }
        });
    }
}
