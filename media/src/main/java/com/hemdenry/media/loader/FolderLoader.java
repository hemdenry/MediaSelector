package com.hemdenry.media.loader;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.loader.content.CursorLoader;

import com.hemdenry.media.MediaPick;
import com.hemdenry.media.bean.Folder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Load all folders (grouped by bucket_id) into a single cursor.
 */
public class FolderLoader extends CursorLoader {

    private static String mDefaultFolderName = "";
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    public static final String COLUMN_BUCKET_ID = "bucket_id";
    public static final String COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name";
    public static final String COLUMN_URI = "uri";
    public static final String COLUMN_COUNT = "count";

    private static final String[] COLUMN = {
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            COLUMN_URI,
            COLUMN_COUNT
    };

    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE
    };

    private static final String SELECTION_MEDIA_TYPE_SINGLE = MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " +
            MediaStore.MediaColumns.SIZE + ">0";

    private static final String SELECTION_MEDIA_TYPE_ALL = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? OR " +
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?) AND " + MediaStore.MediaColumns.SIZE + ">0";

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
        //拿到所有的文件夹，每一个文件都有一个文件夹，所以如果一个文件夹有三个文件，那么会有三个相同的文件夹
        Cursor allFolderCursor = super.loadInBackground();

        //所有文件的文件夹
        MatrixCursor allFileFolder = new MatrixCursor(COLUMN);

        //所有文件的数量
        int totalCount = 0;

        //所有文件的文件夹封面uri
        Uri allFileFolderUri = null;

        //所有文件的文件夹封面mimeType
        String allFileFolderMimeType = "";

        //根据bucketId计算每个文件夹名出现的次数，最终次数表示的就是该文件夹内有多少个文件，把每个文件夹出现的次数都保存在map内
        Map<Long, Long> countMap = new HashMap<>();
        if (allFolderCursor != null) {
            while (allFolderCursor.moveToNext()) {
                long bucketId = allFolderCursor.getLong(allFolderCursor.getColumnIndex(COLUMN_BUCKET_ID));
                Long count = countMap.get(bucketId);
                if (count == null) {
                    count = 1L;
                } else {
                    count++;
                }
                countMap.put(bucketId, count);
            }
        }

        MatrixCursor folderList = new MatrixCursor(COLUMN);
        if (allFolderCursor != null) {
            if (allFolderCursor.moveToFirst()) {
                allFileFolderUri = getUri(allFolderCursor);
                allFileFolderMimeType = allFolderCursor.getString(allFolderCursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
                Set<Long> done = new HashSet<>();//记录已经处理过的bucketId
                do {
                    long bucketId = allFolderCursor.getLong(allFolderCursor.getColumnIndex(COLUMN_BUCKET_ID));
                    if (done.contains(bucketId)) {//如果bucketId已经出现过，说明该文件夹已经处理过
                        continue;
                    }

                    long fileId = allFolderCursor.getLong(allFolderCursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                    String bucketDisplayName = allFolderCursor.getString(allFolderCursor.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME));
                    String mimeType = allFolderCursor.getString(allFolderCursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
                    Uri uri = getUri(allFolderCursor);
                    long count = countMap.get(bucketId);

                    folderList.addRow(new String[]{
                            String.valueOf(fileId),
                            String.valueOf(bucketId),
                            bucketDisplayName,
                            mimeType,
                            uri.toString(),
                            String.valueOf(count),
                    });
                    totalCount += count;
                    done.add(bucketId);//已经处理过的文件夹就记录下来
                } while (allFolderCursor.moveToNext());
            }
        }

        allFileFolder.addRow(new String[]{
                Folder.FOLDER_ID_ALL,
                Folder.FOLDER_ID_ALL,
                mDefaultFolderName,
                allFileFolderMimeType,
                allFileFolderUri == null ? null : allFileFolderUri.toString(),
                String.valueOf(totalCount)
        });
        return new MergeCursor(new Cursor[]{allFileFolder, folderList});
    }

    private Uri getUri(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
        String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
        Uri contentUri;
        if (isImage(mimeType)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (isVideo(mimeType)) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            // unknown
            contentUri = MediaStore.Files.getContentUri("external");
        }
        return ContentUris.withAppendedId(contentUri, id);
    }

    private boolean isImage(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        return mimeType.startsWith("image");
    }

    private boolean isVideo(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        return mimeType.startsWith("video");
    }
}