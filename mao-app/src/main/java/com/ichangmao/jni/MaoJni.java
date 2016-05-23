package com.ichangmao.jni;

/**
 * Created by yangchangmao on 2016/4/25.
 */
public class MaoJni {
    public native int test(int num);

    public native long getFileSize(String path);

    /**
     * 计算文件或文件夹的大小
     *
     * @param path     要计算的路径
     * @param result   计算结果 result[0] 总大小， 1 文件夹个数， 2文件个数
     * @param calcType 计算方式 1:使用fstatat 2:使用lstat
     */
    public native void calcFileSize(String path, long[] result, int calcType);
}
