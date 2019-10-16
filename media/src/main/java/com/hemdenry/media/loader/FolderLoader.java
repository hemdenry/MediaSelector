package com.hemdenry.media.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.loader.content.CursorLoader;

import com.hemdenry.media.MediaPick;
import com.hemdenry.media.bean.Folder;

/**
 * Load all folders (grouped by bucket_id) into a single cursor.
 */
public class FolderLoader extends CursorLoader {

    private static String mDefaultFolderName = "";

    public static final String COLUMN_COUNT = "count";
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    private static final String[] COLUMN = {
            MediaStore.Files.FileColumns._ID,
            "bucket_id",
            "bucket_display_name",
            MediaStore.MediaColumns.DATA,
            COLUMN_COUNT
    };

    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            "bucket_id",
            "bucket_display_name",
            MediaStore.MediaColumns.DATA,
            "COUNT(*) AS " + COLUMN_COUNT
    };

    private static final String SELECTION_MEDIA_TYPE_SINGLE = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + " AND " +
            MediaStore.MediaColumns.SIZE + ">0" + ") GROUP BY (bucket_id";

    private static final String SELECTION_MEDIA_TYPE_ALL = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + " OR " +
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)" + " AND " + MediaStore.MediaColumns.SIZE + ">0" + ") GROUP BY (bucket_id";

    private static final String[] ARGS_ALL = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };

    private static final String ORDER_BY = "datetaken DESC";

    private FolderLoader(Context context, String selection, String[] args) {
        super(context, QUERY_URI, PROJECTION, selection, args, ORDER_BY);
    }

    public static CursorLoader newInstance(Context context, String defaultFolderName) {
        mDefaultFolderName = defaultFolderName;
        String selection;
        String[] args;
        if (MediaPick.getInstance().getMediaConfig().isShowOnlyImage()) {
            selection = SELECTION_MEDIA_TYPE_SINGLE;
            args = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)};
        } else if (MediaPick.getInstance().getMediaConfig().isShowOnlyVideo()) {
            selection = SELECTION_MEDIA_TYPE_SINGLE;
            args = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
        } else {
            selection = SELECTION_MEDIA_TYPE_ALL;
            args = ARGS_ALL;
        }
        return new FolderLoader(context, selection, args);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor defaultFolder = super.loadInBackground();
        MatrixCursor allFolder = new MatrixCursor(COLUMN);
        int totalCount = 0;
        String defaultFolderImage = "";
        if (defaultFolder != null) {
            while (defaultFolder.moveToNext()) {
                totalCount += defaultFolder.getInt(defaultFolder.getColumnIndex(COLUMN_COUNT));
            }
            if (defaultFolder.moveToFirst()) {
                defaultFolderImage = defaultFolder.getString(defaultFolder.getColumnIndex(MediaStore.MediaColumns.DATA));
            }
        }
        allFolder.addRow(new String[]{Folder.FOLDER_ID_ALL, Folder.FOLDER_ID_ALL, mDefaultFolderName, defaultFolderImage, String.valueOf(totalCount)});
        return new MergeCursor(new Cursor[]{allFolder, defaultFolder});
    }
}