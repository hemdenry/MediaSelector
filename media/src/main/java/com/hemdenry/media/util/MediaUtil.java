package com.hemdenry.media.util;

/**
 * Created by hemdenry on 2020/5/8 17:37
 */
public class MediaUtil {

    private MediaUtil() {
    }

    public static boolean isImage(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        return mimeType.startsWith("image");
    }

    public static boolean isVideo(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        return mimeType.startsWith("video");
    }
}