package com.hemdenry.media;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.hemdenry.media.config.MediaConfig;
import com.hemdenry.media.ui.MediaActivity;

public class MediaPick {

    private static MediaPick mInstance;
    private MediaConfig mMediaConfig;

    private MediaPick() {
    }

    public static MediaPick getInstance() {
        if (mInstance == null) {
            synchronized (MediaPick.class) {
                if (mInstance == null) {
                    mInstance = new MediaPick();
                }
            }
        }
        return mInstance;
    }

    public void open(Activity activity) {
        if (mInstance.mMediaConfig == null) {
            return;
        }
        if (mInstance.mMediaConfig.getHandleListener() == null) {
            return;
        }
        if (TextUtils.isEmpty(mInstance.mMediaConfig.getProvider())) {
            return;
        }
        Intent intent = new Intent(activity, MediaActivity.class);
        activity.startActivity(intent);
    }

    public MediaPick setMediaConfig(MediaConfig mediaConfig) {
        this.mMediaConfig = mediaConfig;
        return this;
    }

    public MediaConfig getMediaConfig() {
        return mMediaConfig;
    }
}