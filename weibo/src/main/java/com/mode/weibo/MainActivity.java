package com.mode.weibo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppCompatEditText mEt;
    private AppCompatButton   mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEt = findViewById(R.id.et);
        mBtn = findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mBtn == null) {
                            return;
                        }
                        Toast.makeText(MainActivity.this,"开始转换",Toast.LENGTH_LONG).show();
                        String string = mEt.getText().toString();
                        if (!TextUtils.isEmpty(string)) {
                            Intent intent = new Intent();
                            intent.putExtra("UrlListData", string);
                            intent.setAction("com.wb.data.url");
                            MainActivity.this.sendBroadcast(intent);
                        }
                    }
                }, 30 * 1000);
            }
        });
    }
}
