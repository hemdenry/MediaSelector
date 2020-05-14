package com.hemdenry.media.config;

import com.hemdenry.media.bean.Media;
import com.hemdenry.media.listener.MediaViewer;
import com.hemdenry.media.listener.SelectMediaListener;

import java.util.ArrayList;
import java.util.List;

public class MediaConfig {

    private int maxSize = 9;//配置开启多选时，最大可选择的图片数量，默认9
    private int maxWidth = 1080;//最大的裁剪值，默认1080
    private int maxHeight = 2160;//最大的裁剪值，默认2160
    private boolean isShowCamera = true;//是否开启相机，默认true
    private boolean isOpenCamera = false;//是否直接开启相机，默认false
    private boolean isCrop = false;//是否开启裁剪，默认关闭
    private boolean isCircleCrop;//是否圆形裁剪
    private boolean isFreeStyleEnable;//自由尺寸是否可用
    private boolean isShowCropFrame;//裁剪边框是否显示
    private boolean isMemorizeHistory = true;//是否记录之前已选中的列表
    private boolean isShowOnlyImage = true;//是否只显示图片
    private boolean isShowOnlyVideo = false;//是否只显示视频
    private boolean isShowAll = false;//是否显示所有（图片和视频）
    private float aspectRatioX = 1;//裁剪比，默认1：1
    private float aspectRatioY = 1;//裁剪比，默认1：1
    private String provider;//兼容android 7.0设置
    private String directory = "/media";//拍照以及截图后存放的位置，默认：/media
    private MediaViewer mediaViewer;//加载器，显示图片和视频
    private List<Media> selectList;//已选择的照片
    private SelectMediaListener mSelectMediaListener;

    public MediaConfig provider(String provider) {
        this.provider = provider;
        return this;
    }

    public MediaConfig crop(boolean isCrop) {
        this.isCrop = isCrop;
        return this;
    }

    public MediaConfig crop(boolean isCrop, float aspectRatioX, float aspectRatioY, int maxWidth, int maxHeight) {
        this.isCrop = isCrop;
        this.aspectRatioX = aspectRatioX;
        this.aspectRatioY = aspectRatioY;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        return this;
    }

    public MediaConfig handleListener(SelectMediaListener selectMediaListener) {
        this.mSelectMediaListener = selectMediaListener;
        return this;
    }

    public MediaConfig setCircleCrop(boolean circleCrop) {
        this.isCircleCrop = circleCrop;
        return this;
    }

    public MediaConfig maxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public MediaConfig isShowCamera(boolean isShowCamera) {
        this.isShowCamera = isShowCamera;
        return this;
    }

    public MediaConfig directory(String directory) {
        this.directory = directory;
        return this;
    }

    public MediaConfig isOpenCamera(boolean isOpenCamera) {
        this.isOpenCamera = isOpenCamera;
        return this;
    }

    public MediaConfig isShowOnlyImage(boolean isShowOnlyImage) {
        this.isShowOnlyImage = isShowOnlyImage;
        if (isShowOnlyImage) {
            this.isShowOnlyVideo = false;
            this.isShowAll = false;
        }
        return this;
    }

    public MediaConfig isShowOnlyVideo(boolean isShowOnlyVideo) {
        this.isShowOnlyVideo = isShowOnlyVideo;
        if (isShowOnlyVideo) {
            this.isShowOnlyImage = false;
            this.isShowAll = false;
        }
        return this;
    }

    public MediaConfig isShowAll(boolean isShowAll) {
        this.isShowAll = isShowAll;
        if (isShowAll) {
            this.isShowOnlyImage = false;
            this.isShowOnlyVideo = false;
        }
        return this;
    }

    public MediaConfig freeStyleEnable(boolean freeStyleEnable) {
        this.isFreeStyleEnable = freeStyleEnable;
        return this;
    }

    public MediaConfig showCropFrame(boolean showCropFrame) {
        this.isShowCropFrame = showCropFrame;
        return this;
    }

    public MediaConfig memorizeHistory(boolean memorizeHistory) {
        this.isMemorizeHistory = memorizeHistory;
        return this;
    }

    public MediaConfig mediaViewer(MediaViewer mediaViewer) {
        this.mediaViewer = mediaViewer;
        return this;
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

    public String getDirectory() {
        return directory;
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
}