package com.threestudio.multi_downloader.db;

import com.threestudio.multi_downloader.bean.ThreadInfo;

import java.util.List;

/**
 * create by threestudio on 2018/8/21 09:27.
 * use for 数据访问接口
 */
public interface ThreadDAO2 {
    /**
     * 插入线程信息
     *
     * @param threadInfo 线程信息
     */
    void insertThread(ThreadInfo threadInfo);

    /**
     * 删除线程信息
     *
     * @param url 地址
     */
    void deleteThread(String url);


    /**
     * /**
     * 更新线程信息
     *
     * @param url       地址
     * @param thread_id id
     * @param finished  完成进度
     */
    void updateThread(String url, int thread_id, long finished);


    /**
     * 查询文件的线程信息
     *
     * @param url 地址
     * @return 信息
     */
    List<ThreadInfo> getThread(String url);


    /**
     * 判断是否存在
     *
     * @param url       地址
     * @param thread_id id
     * @return 是否存在
     */
    boolean isExists(String url, int thread_id);
}
