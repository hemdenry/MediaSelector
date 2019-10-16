package com.hemdenry.media.bean;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by fanghui on 2019/10/8 10:23
 * Description:
 */
public class Media {

    private long id;
    private String path;
    private String name;
    private String mimeType;
    private long size;
    private long modifyDate;
    private long duration;
    private Uri uri;

    public Media(long id, String path, String name, String mimeType, long size, long modifyDate, long duration) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.mimeType = mimeType;
        this.modifyDate = modifyDate;
        this.duration = duration;

        if (size < 0) {
            //某些设备获取size<0，直接计算
            size = new File(path).length() / 1024;
        }
        this.size = size;

        Uri uri;
        if (isImage()) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (isVideo()) {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            // ?
            uri = MediaStore.Files.getContentUri("external");
        }
        this.uri = ContentUris.withAppendedId(uri, id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(long modifyDate) {
        this.modifyDate = modifyDate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Media bean = (Media) o;
        return path.equals(bean.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    public static Media valueOf(Cursor cursor) {
        return new Media(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)),
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)),
                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)),
                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)),
                cursor.getLong(cursor.getColumnIndexOrThrow("duration")));
    }

    public boolean isImage() {
        if (mimeType == null) {
            return false;
        }
        return mimeType.startsWith("image");
    }

    public boolean isVideo() {
        if (mimeType == null) {
            return false;
        }
        return mimeType.startsWith("video");
    }

    @Override
    public String toString() {
        return "Media{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", size=" + size +
                ", modifyDate=" + modifyDate +
                ", duration=" + duration +
                ", uri=" + uri +
                '}';
    }
}