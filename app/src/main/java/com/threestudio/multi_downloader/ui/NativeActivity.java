package com.threestudio.multi_downloader.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.threestudio.multi_downloader.R;


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
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start:
                startNativeDownload();
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
}
