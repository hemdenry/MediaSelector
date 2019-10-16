package com.hemdenry.media.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.loader.content.CursorLoader;

import com.hemdenry.media.MediaPick;
import com.hemdenry.media.bean.Folder;

public class MediaLoader extends CursorLoader {

    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,//路径
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_MODIFIED,
            "duration",//时长 long 单位ms(仅视频有)
    };

    private static final String SELECTION_MEDIA_TYPE_WITHOUT_FOLDER_SINGLE = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" +
            " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static final String SELECTION_MEDIA_TYPE_WITHOUT_FOLDER_ALL = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + " OR " +
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)" + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static final String[] ARGS_WITHOUT_FOLDER_ALL = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };

    private static final String SELECTION_MEDIA_TYPE_WITH_FOLDER_SINGLE = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" +
            " AND " + " bucket_id=?" + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static final String SELECTION_MEDIA_TYPE_WITH_FOLDER_ALL = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + " OR " +
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)" + " AND " + " bucket_id=?" + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static final String ORDER_BY = "datetaken DESC";

    private MediaLoader(Context context, String selection, String[] selectionArgs) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, ORDER_BY);
    }

    public static CursorLoader newInstance(Context context, Folder folder) {
        String selection;
        String[] args;
        if (folder.isAll()) {
            if (MediaPick.getInstance().getMediaConfig().isShowOnlyImage()) {
                selection = SELECTION_MEDIA_TYPE_WITHOUT_FOLDER_SINGLE;
                args = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)};
            } else if (MediaPick.getInstance().getMediaConfig().isShowOnlyVideo()) {
                selection = SELECTION_MEDIA_TYPE_WITHOUT_FOLDER_SINGLE;
                args = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
            } else {
                selection = SELECTION_MEDIA_TYPE_WITHOUT_FOLDER_ALL;
                args = ARGS_WITHOUT_FOLDER_ALL;
            }
        } else {
            if (MediaPick.getInstance().getMediaConfig().isShowOnlyImage()) {
                selection = SELECTION_MEDIA_TYPE_WITH_FOLDER_SINGLE;
                args = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), folder.getId()};
            } else if (MediaPick.getInstance().getMediaConfig().isShowOnlyVideo()) {
                selection = SELECTION_MEDIA_TYPE_WITH_FOLDER_SINGLE;
                args = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO), folder.getId()};
            } else {
                selection = SELECTION_MEDIA_TYPE_WITH_FOLDER_ALL;
                args = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO), folder.getId()};
            }
        }
        return new MediaLoader(context, selection, args);
    }

    @Override
    public Cursor loadInBackground() {
        return super.loadInBackground();
    }
}