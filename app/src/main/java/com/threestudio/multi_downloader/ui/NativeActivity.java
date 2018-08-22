package com.threestudio.multi_downloader.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.threestudio.multi_downloader.R;
import com.threestudio.multi_downloader.bean.FileInfo;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NativeActivity extends AppCompatActivity implements View.OnClickListener {

    static {
        System.loadLibrary("downloader-lib");
    }

    private native void startNativeDownload();
    private native void pauseNativeDownload();

    private TextView mName;
    private ProgressBar mProgressBar;
    private TextView mPro;
    private Button mStartButton;
    private Button mPauseButton;
    private MyHandler mHandler;
    private MyThread mThread;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_main);
        mName = (TextView)findViewById(R.id.name);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mPro = (TextView)findViewById(R.id.pro_text);
        mStartButton = (Button)findViewById(R.id.start);
        mPauseButton = (Button)findViewById(R.id.pause);
        mStartButton.setOnClickListener(this);
        mPauseButton.setOnClickListener(this);

        init();
        mHandler = new MyHandler();
        mThread = new MyThread();
    }


    private void init() {
        mPro.setVisibility(TextView.VISIBLE);
        mProgressBar.setMax(100);
    }

    //Call From JNI
    private void updateProgressBar(int finished)
    {
        Log.d("HTTP_DOWNLOADER","-----updateProgressBar-------finished =  " + finished);
        Message msg = new Message();
        Bundle b = new Bundle();// 存放数据
        b.putInt("progressValue",finished);
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start:
                if(mThread != null  && mThread.isAlive())
                {
                    mThread.resumeThread();
                }else{
                    mThread.start();
                }
                break;
            case R.id.pause:
                pauseNativeDownload();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class MyHandler extends  Handler{
        public MyHandler() {
        }

        public MyHandler(Looper L) {
            super(L);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle b = msg.getData();
            int progressValue = b.getInt("progressValue",0);
            mProgressBar.setProgress(progressValue);
            Log.d("HTTP_DOWNLOADER","-----updateProgressBar-------handleMessage =  " + progressValue);
            mPro.setText(new StringBuffer().append(progressValue).append("%"));
            if(progressValue == 100){
                mThread.interrupt();
                Log.d("HTTP_DOWNLOADER","-----updateProgressBar-------interrupt");
            }

        }
    }


    private class MyThread extends Thread {
        private final Object lock = new Object();
        private boolean pause = false;

        /**
         * 调用这个方法实现暂停线程
         */
        void pauseThread() {
            pause = true;
            Log.d("HTTP_DOWNLOADER","-----MyThread-------pauseThread");
        }

        /**
         * 调用这个方法实现恢复线程的运行
         */
        void resumeThread() {
            Log.d("HTTP_DOWNLOADER","-----MyThread-------resumeThread");
            pause = false;
            synchronized (lock) {
                lock.notifyAll();
            }
        }

        /**
         * 注意：这个方法只能在run方法里调用，不然会阻塞主线程，导致页面无响应
         */
        void onPause() {
            Log.d("HTTP_DOWNLOADER","-----MyThread-------onPause");
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        public void run() {
            super.run();
            try {
                startNativeDownload();
//                while (true) {
//                    // 让线程处于暂停等待状态
//                    while (pause) {
//
//                        onPause();
//                    }
//                    Log.d("HTTP_DOWNLOADER","-----MyThread-------run");
//
//                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
