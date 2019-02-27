package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.example.circlieprogress.CircleProgress;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private MyHandler handler;
    private int value = 1;
    private CircleProgress circlieProgress;


    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> mActivity;
        public MyHandler(MainActivity activity){
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mActivity != null && mActivity.get() != null){
                mActivity.get().circlieProgress.setProgress(msg.what);
                Message message = Message.obtain();
                mActivity.get().value++;
                if(mActivity.get().value > 100){
                    return;
                }
                message.what = mActivity.get().value;
                mActivity.get().handler.sendMessageDelayed(message,100);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new MyHandler(this);
        circlieProgress = ((CircleProgress) findViewById(R.id.circle_progress));
        Message message = Message.obtain();
        message.what = value;
        handler.sendMessage(message);
    }
}
