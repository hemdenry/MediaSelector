package com.hemdenry.media.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import com.hemdenry.media.loader.FolderLoader;

public class Folder {

    public static final String FOLDER_ID_ALL = String.valueOf(-1);

    private String id;
    private String path;
    private String name;
    private long count;

    public Folder(String id, String path, String name, long count) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public static Folder valueOf(Cursor cursor) {
        return new Folder(
                cursor.getString(cursor.getColumnIndex("bucket_id")),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)),
                cursor.getString(cursor.getColumnIndex("bucket_display_name")),
                cursor.getLong(cursor.getColumnIndex(FolderLoader.COLUMN_COUNT))
        );
    }

    public boolean isAll() {
        return FOLDER_ID_ALL.equals(id);
    }
}