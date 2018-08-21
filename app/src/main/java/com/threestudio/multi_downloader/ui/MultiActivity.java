package com.threestudio.multi_downloader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;


import com.threestudio.multi_downloader.R;
import com.threestudio.multi_downloader.adapter.FileListAdapter;
import com.threestudio.multi_downloader.bean.FileInfo;
import com.threestudio.multi_downloader.service.MultiDownloadService;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MultiActivity extends AppCompatActivity {
    private static final String TAG = "MultiActivity";
    private ListView mList;
    private List<FileInfo> fileList;
    private FileListAdapter listAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_main);
        mList = (ListView)findViewById(R.id.list);


        initData();
        initSetup();
        initRegister();

    }

    private void initRegister() {
        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(MultiDownloadService.ACTION_UPDATE);
        filter.addAction(MultiDownloadService.ACTION_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    private void initSetup() {
        //创建适配器
        listAdapter = new FileListAdapter(this, fileList);
        //给listview设置适配器
        mList.setAdapter(listAdapter);
    }

    private void initData() {
        fileList = new ArrayList<>();
        FileInfo fileInfo1 = new FileInfo(0, "http://dldir1.qq.com/weixin/android/weixin672android1340.apk",
                "weixin.apk", 0, 0);
        FileInfo fileInfo2 = new FileInfo(1, "http://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk",
                "qq.apk", 0, 0);
        FileInfo fileInfo3 = new FileInfo(2, "http://t.alipayobjects.com/L1/71/100/and/alipay_wap_main.apk",
                "ailipay.apk", 0, 0);
        FileInfo fileInfo4 = new FileInfo(3, "http://pkg.zhimg.com/zhihu/bang-futureve-mobile-apk-release-5.22.4(804).apk",
                "zhihu.exe", 0, 0);
        fileList.add(fileInfo1);
        fileList.add(fileInfo2);
        fileList.add(fileInfo3);
        fileList.add(fileInfo4);
    }

    /**
     * 更新UI的广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MultiDownloadService.ACTION_UPDATE.equals(intent.getAction())) {
                long finished = intent.getLongExtra("finished", 0);
                int id = intent.getIntExtra("id", 0);
                Log.e(TAG, "finished==" + finished);
                Log.e(TAG, "id==" + id);
                listAdapter.updateProgress(id, finished);
            } else if (MultiDownloadService.ACTION_FINISHED.equals(intent.getAction())) {
                FileInfo fileinfo = (FileInfo) intent.getSerializableExtra("fileinfo");
                //更新进度为100
                listAdapter.updateProgress(fileinfo.getId(), 100);
                Toast.makeText(
                        MultiActivity.this,
                        fileinfo.getFileName() + "下载完成",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
