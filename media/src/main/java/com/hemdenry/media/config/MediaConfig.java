package com.hemdenry.media.config;

import com.hemdenry.media.bean.Media;
import com.hemdenry.media.listener.MediaViewer;
import com.hemdenry.media.listener.SelectMediaListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MediaConfig {

    private Builder builder;

    private int maxWidth;//最大的裁剪值，默认500
    private int maxHeight;//最大的裁剪值，默认500
    private int maxSize;//配置开启多选时，最大可选择的图片数量，默认9

    private boolean isShowCamera;//是否开启相机，默认true
    private boolean isOpenCamera;//是否直接开启相机，默认false
    private boolean isCrop;//是否开启裁剪，默认关闭
    private boolean isCircleCrop;//是否圆形裁剪
    private boolean isFreeStyleEnable;//自由尺寸是否可用
    private boolean isShowCropFrame;//裁剪边框是否显示
    private boolean isMemorizeHistory;//是否记录之前已选中的列表
    private boolean isShowOnlyImage = false;//是否只显示图片
    private boolean isShowOnlyVideo = false;//是否只显示视频
    private boolean isShowAll = false;//是否显示所有（图片和视频）
    private float aspectRatioX;//裁剪比，默认1：1
    private float aspectRatioY;//裁剪比，默认1：1
    private String provider;//兼容android7.0设置
    private String filePath;//拍照以及截图后存放的位置，默认：/media
    private MediaViewer mediaViewer;//加载器，显示图片和视频
    private List<Media> selectList;//已选择的照片
    private SelectMediaListener mSelectMediaListener;

    private MediaConfig(Builder builder) {
        setBuilder(builder);
    }

    private void setBuilder(Builder builder) {
        this.builder = builder;
        this.maxSize = builder.maxSize;
        this.isShowCamera = builder.isShowCamera;
        this.filePath = builder.filePath;
        this.isOpenCamera = builder.isOpenCamera;
        this.isCrop = builder.isCrop;
        this.aspectRatioX = builder.aspectRatioX;
        this.aspectRatioY = builder.aspectRatioY;
        this.maxWidth = builder.maxWidth;
        this.maxHeight = builder.maxHeight;
        this.provider = builder.provider;
        this.isFreeStyleEnable = builder.isFreeStyleEnable;
        this.isShowCropFrame = builder.isShowCropFrame;
        this.isCircleCrop = builder.isCircleCrop;
        this.isMemorizeHistory = builder.isMemorizeHistory;
        this.isShowOnlyImage = builder.isShowOnlyImage;
        this.isShowOnlyVideo = builder.isShowOnlyVideo;
        this.isShowAll = builder.isShowAll;
        this.mediaViewer = builder.mediaViewer;
        this.mSelectMediaListener = builder.mSelectMediaListener;
    }

    public boolean isCircleCrop() {
        return isCircleCrop;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public boolean isShowCamera() {
        return isShowCamera;
    }

    public void setShowCamera(boolean showCamera) {
        isShowCamera = showCamera;
    }

    public void resetShowCamera() {
        isShowCamera = builder.isShowCamera;
    }

    public String getFilePath() {
        return filePath;
    }

    public SelectMediaListener getHandleListener() {
        return mSelectMediaListener;
    }

    public boolean isOpenCamera() {
        return isOpenCamera;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public boolean isCrop() {
        return isCrop;
    }

    public float getAspectRatioX() {
        return aspectRatioX;
    }

    public float getAspectRatioY() {
        return aspectRatioY;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public String getProvider() {
        return provider;
    }

    public boolean isFreeStyleEnable() {
        return isFreeStyleEnable;
    }

    public boolean isShowCropFrame() {
        return isShowCropFrame;
    }

    public List<Media> getSelectList() {
        if (selectList == null) {
            selectList = new ArrayList<>();
        }
        return selectList;
    }

    public void setSelectList(List<Media> selectList) {
        this.selectList = selectList;
    }

    public boolean isMemorizeHistory() {
        return isMemorizeHistory;
    }

    public boolean isShowOnlyImage() {
        return isShowOnlyImage;
    }

    public boolean isShowOnlyVideo() {
        return isShowOnlyVideo;
    }

    public boolean isShowAll() {
        return isShowAll;
    }

    public MediaViewer getMediaViewer() {
        return mediaViewer;
    }

    public static class Builder implements Serializable {

        private static MediaConfig mMediaConfig;
        private SelectMediaListener mSelectMediaListener;

        private boolean isShowCamera = true;
        private boolean isFreeStyleEnable;
        private boolean isShowCropFrame;
        private boolean isCrop = false;
        private boolean isCircleCrop;
        private boolean isOpenCamera = false;
        private boolean isMemorizeHistory = true;
        private boolean isShowOnlyImage = true;
        private boolean isShowOnlyVideo = false;
        private boolean isShowAll = false;
        private int maxSize = 9;
        private int maxWidth = 500;
        private int maxHeight = 500;
        private float aspectRatioX = 1;
        private float aspectRatioY = 1;
        private String filePath = "/media";
        private String provider;
        private MediaViewer mediaViewer;

        public Builder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public Builder crop(boolean isCrop) {
            this.isCrop = isCrop;
            return this;
        }

        public Builder crop(boolean isCrop, float aspectRatioX, float aspectRatioY, int maxWidth, int maxHeight) {
            this.isCrop = isCrop;
            this.aspectRatioX = aspectRatioX;
            this.aspectRatioY = aspectRatioY;
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            return this;
        }

        public Builder handleListener(SelectMediaListener selectMediaListener) {
            this.mSelectMediaListener = selectMediaListener;
            return this;
        }

        public Builder setCircleCrop(boolean circleCrop) {
            this.isCircleCrop = circleCrop;
            return this;
        }

        public Builder maxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Builder isShowCamera(boolean isShowCamera) {
            this.isShowCamera = isShowCamera;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder isOpenCamera(boolean isOpenCamera) {
            this.isOpenCamera = isOpenCamera;
            return this;
        }

        public Builder isShowOnlyImage(boolean isShowOnlyImage) {
            this.isShowOnlyImage = isShowOnlyImage;
            if (isShowOnlyImage) {
                this.isShowOnlyVideo = false;
                this.isShowAll = false;
            }
            return this;
        }

        public Builder isShowOnlyVideo(boolean isShowOnlyVideo) {
            this.isShowOnlyVideo = isShowOnlyVideo;
            if (isShowOnlyVideo) {
                this.isShowOnlyImage = false;
                this.isShowAll = false;
            }
            return this;
        }

        public Builder isShowAll(boolean isShowAll) {
            this.isShowAll = isShowAll;
            if (isShowAll) {
                this.isShowOnlyImage = false;
                this.isShowOnlyVideo = false;
            }
            return this;
        }

        public Builder freeStyleEnable(boolean freeStyleEnable) {
            this.isFreeStyleEnable = freeStyleEnable;
            return this;
        }

        public Builder showCropFrame(boolean showCropFrame) {
            this.isShowCropFrame = showCropFrame;
            return this;
        }

        public Builder memorizeHistory(boolean memorizeHistory) {
            this.isMemorizeHistory = memorizeHistory;
            return this;
        }

        public Builder mediaViewer(MediaViewer mediaViewer) {
            this.mediaViewer = mediaViewer;
            return this;
        }

        public MediaConfig build() {
            if (mMediaConfig == null) {
                mMediaConfig = new MediaConfig(this);
            } else {
                mMediaConfig.setBuilder(this);
            }
            return mMediaConfig;
        }
    }
}