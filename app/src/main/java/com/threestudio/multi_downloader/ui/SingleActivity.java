package com.threestudio.multi_downloader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.threestudio.multi_downloader.R;
import com.threestudio.multi_downloader.bean.FileInfo;
import com.threestudio.multi_downloader.service.SingleService;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SingleActivity extends AppCompatActivity implements View.OnClickListener{
    private String TAG ="MULTI_DOWNLOADER";
    private TextView mName;
    private ProgressBar mProgressBar;
    private TextView mPro;
    private Button mStartButton;
    private Button mPauseButton;
    private String mUrl;
    private FileInfo mFileInfo;



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

        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(SingleService.ACTION_UPDATE);
        registerReceiver(mReceiver, filter);


    }

    private void init() {
        mPro.setVisibility(TextView.VISIBLE);
        mProgressBar.setMax(100);
        //创建文件信息对象
        mUrl = "http://dldir1.qq.com/weixin/android/weixin672android1340.apk";
        mFileInfo = new FileInfo(0, mUrl, "WeChat", 0, 0);
        mName.setText(mFileInfo.getFileName());
    }

    /**
     * 更新UI的广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SingleService.ACTION_UPDATE.equals(intent.getAction())) {
                int finished = intent.getIntExtra("finished", 0);
                mProgressBar.setProgress(finished);
                mPro.setText(new StringBuffer().append(finished).append("%"));
            }
        }
    };


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(SingleActivity.this, SingleService.class);
        switch (view.getId()){
            case R.id.start:
                Log.d(TAG,"SingleService start button");
                intent.setAction(SingleService.ACTION_START);
                intent.putExtra("fileinfo", mFileInfo);
                startService(intent);
                break;
            case R.id.pause:
                Log.d(TAG,"SingleService pause button");
                intent.setAction(SingleService.ACTION_PAUSE);
                intent.putExtra("fileinfo", mFileInfo);
                startService(intent);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
