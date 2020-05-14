package com.hemdenry.media.bean;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.hemdenry.media.util.MediaUtil;

/**
 * Created by fanghui on 2019/10/8 10:23
 * Description:
 */
public class Media {

    private long id;
    private String name;
    private String mimeType;
    private long size;
    private long duration;
    private Uri uri;

    public Media(long id, String name, String mimeType, long size, long duration) {
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
        this.duration = duration;
        this.size = size;

        if (id >= 0) {
            Uri uri;
            if (isImage()) {
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if (isVideo()) {
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else {
                // unknown
                uri = MediaStore.Files.getContentUri("external");
            }
            this.uri = ContentUris.withAppendedId(uri, id);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        return id == bean.id;
    }

    @Override
    public int hashCode() {
        return (int) id * 37 + name.hashCode();
    }

    public static Media valueOf(Cursor cursor) {
        return new Media(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)),
                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)),
                cursor.getLong(cursor.getColumnIndexOrThrow("duration")));
    }

    public boolean isImage() {
        return MediaUtil.isImage(mimeType);
    }

    public boolean isVideo() {
        return MediaUtil.isVideo(mimeType);
    }

    @Override
    public String toString() {
        return "Media{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                ", uri=" + uri +
                '}';
    }
}