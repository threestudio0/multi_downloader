package com.threestudio.multi_downloader.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.threestudio.multi_downloader.bean.FileInfo;
import com.threestudio.multi_downloader.bean.ThreadInfo;
import com.threestudio.multi_downloader.db.ThreadDAOImpl;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SingleDownloadTask {

    private final Context mContext;
    private FileInfo mFileInfo = null;
    private long mFinished = 0;
    public boolean isPause = false;
    private ThreadDAOImpl mThreadDAO = null;

    public SingleDownloadTask(Context mContext, FileInfo mFileInfo) {
        this.mContext = mContext;
        this.mFileInfo = mFileInfo;
        mThreadDAO = new ThreadDAOImpl(mContext);
    }

    public void download() {
        //读取数据库的线程信息
        List<ThreadInfo> threadInfos = mThreadDAO.getThread(mFileInfo.getUrl());
        Log.e("threadsize==", threadInfos.size() + "");
        ThreadInfo info;
        if (threadInfos.size() == 0) {
            //初始化线程信息
            info = new ThreadInfo(0, mFileInfo.getUrl(), 0, mFileInfo.getLength(), 0);
        } else {
            info = threadInfos.get(0);
        }
        //创建子线程进行下载
        new DownloadThread(info).start();
    }

    private class DownloadThread extends Thread {

        private ThreadInfo threadInfo;

        public DownloadThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {
            //向数据库插入线程信息
            Log.e("isExists==", mThreadDAO.isExists(threadInfo.getUrl(), threadInfo.getId()) + "");
            if (!mThreadDAO.isExists(threadInfo.getUrl(), threadInfo.getId())) {
                mThreadDAO.insertThread(threadInfo);
            }
            HttpURLConnection connection;
            RandomAccessFile raf;
            InputStream is;
            try {
                URL url = new URL(threadInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                //设置下载位置
                long start = threadInfo.getStart() + threadInfo.getFinish();
                connection.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnd());
                //设置文件写入位置
                File file = new File(SingleService.DOWNLOAD_PATH, mFileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);

                Intent intent = new Intent(SingleService.ACTION_UPDATE);
                mFinished += threadInfo.getFinish();
                Log.e("threadInfo.getFinish==", threadInfo.getFinish() + "");
                Log.e("getResponseCode ===", connection.getResponseCode() + "");
                //开始下载
                if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                    Log.e("getContentLength==", connection.getContentLength() + "");
                    //读取数据
                    is = connection.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = is.read(buffer)) != -1) {

                        //下载暂停时，保存进度
                        if (isPause) {
                            Log.e("mfinished==", mFinished + "");
                            mThreadDAO.updateThread(mFileInfo.getUrl(), mFileInfo.getId(), mFinished);
                            return;
                        }

                        //写入文件
                        raf.write(buffer, 0, len);
                        //把下载进度发送广播给Activity
                        mFinished += len;
                        if (System.currentTimeMillis() - time > 1000) {//减少UI负载
                            time = System.currentTimeMillis();
                            intent.putExtra("finished", (int)(mFinished * 100 / mFileInfo.getLength()));
                            mContext.sendBroadcast(intent);
                            Log.e(" mFinished percent===", mFinished * 100 / mFileInfo.getLength() + "");
                        }

                    }

                    intent.putExtra("finished",100);
                    mContext.sendBroadcast(intent);
                    //删除线程信息
                    mThreadDAO.deleteThread(mFileInfo.getUrl(), mFileInfo.getId());
                    is.close();
                }
                raf.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
