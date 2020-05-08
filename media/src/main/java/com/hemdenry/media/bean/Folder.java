package com.hemdenry.media.bean;

import android.database.Cursor;
import android.net.Uri;

import com.hemdenry.media.loader.FolderLoader;

public class Folder {

    public static final String FOLDER_ID_ALL = String.valueOf(-1);

    private String id;
    private String name;
    private Uri uri;
    private long count;

    public Folder(String id, String name, Uri uri, long count) {
        this.id = id;
        this.name = name;
        this.uri = uri;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public static Folder valueOf(Cursor cursor) {
        String uri = cursor.getString(cursor.getColumnIndex(FolderLoader.COLUMN_URI));
        return new Folder(
                cursor.getString(cursor.getColumnIndex(FolderLoader.COLUMN_BUCKET_ID)),
                cursor.getString(cursor.getColumnIndex(FolderLoader.COLUMN_BUCKET_DISPLAY_NAME)),
                Uri.parse(uri),
                cursor.getLong(cursor.getColumnIndex(FolderLoader.COLUMN_COUNT))
        );
    }

    public boolean isAll() {
        return FOLDER_ID_ALL.equals(id);
    }
}