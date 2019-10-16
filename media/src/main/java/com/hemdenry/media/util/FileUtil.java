package com.hemdenry.media.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtil {

    private final static String PATTERN = "yyyyMMddHHmmss";

    /**
     * 创建文件
     *
     * @param context  context
     * @param filePath 文件路径
     * @return file
     */
    public static File createFile(Context context, String filePath, boolean isImage) {
        String timeStamp = new SimpleDateFormat(PATTERN, Locale.CHINA).format(new Date());
        String externalStorageState = Environment.getExternalStorageState();
        if (isImage) {
            timeStamp = "IMG_" + timeStamp;
            filePath = filePath + File.separator + "image";
        } else {
            timeStamp = "VIDEO_" + timeStamp;
            filePath = filePath + File.separator + "video";
        }
        File dir = new File(Environment.getExternalStorageDirectory() + filePath);
        String extension = isImage ? ".jpg" : ".mp4";
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return new File(dir, timeStamp + extension);
        } else {
            File cacheDir = context.getCacheDir();
            return new File(cacheDir, timeStamp + extension);
        }
    }

    /**
     * 创建初始文件夹，保存拍摄图片和裁剪后的图片
     *
     * @param filePath 文件夹路径
     */
    public static void createFile(String filePath) {
        String externalStorageState = Environment.getExternalStorageState();
        File cropFile = new File(Environment.getExternalStorageDirectory() + filePath + "/crop");
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            if (!cropFile.exists()) {
                cropFile.mkdirs();
            }
            File file = new File(cropFile, ".nomedia");//创建忽视文件。有该文件，系统将检索不到此文件夹下的图片。
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getFilePath(Context context) {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getPath();
        } else {
            return context.getCacheDir().getAbsolutePath();
        }
    }

    /**
     * @param filePath 文件夹路径
     * @return 截图完成的 file
     */
    public static File getCropFile(String filePath) {
        String timeStamp = new SimpleDateFormat(PATTERN, Locale.CHINA).format(new Date());
        return new File(Environment.getExternalStorageDirectory() + filePath + "/crop/" + timeStamp + ".jpg");
    }
}