package com.hemdenry.media.listener;

import android.content.Context;
import android.widget.ImageView;

import com.hemdenry.media.bean.Folder;
import com.hemdenry.media.bean.Media;

/**
 * Created by fanghui on 2019/10/16 13:37
 * Description:
 */
public interface MediaViewer {

    void onDisplayFolder(Context context, Folder folder, ImageView imageView);

    void onDisplayMedia(Context context, Media media, ImageView imageView);
}