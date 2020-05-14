package com.hemdenry.media.util;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import com.hemdenry.media.R;
import com.hemdenry.media.config.MediaConfig;
import com.yalantis.ucrop.UCrop;

public class UCropUtil {

    public static void start(AppCompatActivity activity, Uri sourceFileUri, Uri targetFileUri, MediaConfig config) {
        UCrop uCrop = UCrop.of(sourceFileUri, targetFileUri)
                .withAspectRatio(config.getAspectRatioX(), config.getAspectRatioY())
                .withMaxResultSize(config.getMaxWidth(), config.getMaxHeight());
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(activity.getResources().getColor(R.color.colorPrimary));
        options.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimary));
        options.setToolbarWidgetColor(activity.getResources().getColor(R.color.white));
        options.setFreeStyleCropEnabled(config.isFreeStyleEnable());
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(80);
        options.setShowCropFrame(config.isShowCropFrame());
        options.setCircleDimmedLayer(config.isCircleCrop());
        uCrop.withOptions(options);
        uCrop.start(activity);
    }
}