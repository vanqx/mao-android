package com.ichangmao.commons;

import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yangchangmao on 2016/3/10.
 */
public class IOUtils {

    static MaoLog log = MaoLog.getLogger(IOUtils.class.getSimpleName());

    public static void save(String path, byte[] data) {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            File p = file.getParentFile();
            if (!p.exists()) {
                p.mkdirs();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            log.d("File not found: " + e.getMessage());
        } catch (IOException e) {
            log.d("Error accessing file: " + e.getMessage());
        }
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(new File(getOutputMediaFilePath(type)));
    }

    /**
     * Create a File for saving an image or video
     */
    public static String getOutputMediaFilePath(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "vanq");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                log.d("failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path;
        if (type == MEDIA_TYPE_IMAGE) {
            path = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
        } else if (type == MEDIA_TYPE_VIDEO) {
            path = mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4";
        } else {
            return null;
        }

        return path;
    }

    public static boolean copyFileByChannel(File src, File dest) {
        try {
            int length;
            FileInputStream in = new FileInputStream(src);
            FileOutputStream out = new FileOutputStream(dest);
            FileChannel inC = in.getChannel();
            FileChannel outC = out.getChannel();
            while (true) {
                if (inC.position() == inC.size()) {
                    inC.close();
                    outC.close();
                    return true;
                }
                if ((inC.size() - inC.position()) < 20971520)
                    length = (int) (inC.size() - inC.position());
                else
                    length = 20971520;
                inC.transferTo(inC.position(), length, outC);
                inC.position(inC.position() + length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long getFileSize(String path) {
        File file = new File(path);
        if (file.isFile()) {
            return file.length();
        } else if (file.isDirectory()) {
            String parent = path;
            if (!parent.endsWith("/")) {
                parent += "/";
            }
            String[] files = file.list();
            long size = 0;
            for (String f : files) {
                size += getFileSize(parent + f);
            }
            return size;
        } else {
            return 0;
        }
    }
}
