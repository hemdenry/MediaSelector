package com.hemdenry.media.util;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.hemdenry.media.bean.Media;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;

public class FileUtil {

    /**
     * 存入媒体文件：MediaStore.Images、MediaStore.Video、MediaStore.Audio
     */
    public static Media createFile(Context context, String directory, boolean isImage) {
        String fileName;
        if (isImage) {
            fileName = "img_" + getRandomFileName() + ".jpg";
            directory = directory + File.separator + "image";
        } else {
            fileName = "video_" + getRandomFileName() + ".mp4";
            directory = directory + File.separator + "video";
        }
        String mimeType = isImage ? "image/jpeg" : "video/mp4";
        String filePath = Environment.getExternalStorageDirectory() + File.separator + directory + File.separator + fileName;
        ContentValues values = new ContentValues(4);
        values.put(MediaStore.MediaColumns.TITLE, fileName);
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, isImage ? Environment.DIRECTORY_PICTURES : Environment.DIRECTORY_MOVIES);
        } else {
            values.put(MediaStore.MediaColumns.DATA, filePath);
        }
        Uri uri;
        if (isImage) {
            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        }
        Media media = new Media(0, fileName, mimeType, 0, 0);
        media.setUri(uri);
        return media;
    }

    public static Media getCropFile(Context context, String directory) {
        String fileName = "img_" + getRandomFileName() + ".jpg";
        String filePath = Environment.getExternalStorageDirectory() + File.separator + directory + File.separator + "image" + File.separator + fileName;
        ContentValues values = new ContentValues(4);
        values.put(MediaStore.MediaColumns.TITLE, fileName);
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        } else {
            values.put(MediaStore.MediaColumns.DATA, filePath);
        }
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Media media = new Media(0, fileName, "image/jpeg", 0, 0);
        media.setUri(uri);
        return media;
    }

    public static String getRandomFileName() {
        Random r = new Random();
        int randomNumber = r.nextInt(89999) + 10000;//获取随机的五位数
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(System.currentTimeMillis());
        return timeStamp + "_" + randomNumber;
    }
}