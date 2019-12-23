package com.hemdenry.media.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hemdenry.media.MediaPick;
import com.hemdenry.media.R;
import com.hemdenry.media.adapter.FolderAdapter;
import com.hemdenry.media.adapter.MediaAdapter;
import com.hemdenry.media.bean.Folder;
import com.hemdenry.media.bean.Media;
import com.hemdenry.media.config.MediaConfig;
import com.hemdenry.media.loader.FolderLoader;
import com.hemdenry.media.loader.MediaLoader;
import com.hemdenry.media.util.FileUtil;
import com.hemdenry.media.util.UCropUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class MediaActivity extends AppCompatActivity implements View.OnClickListener, FolderAdapter.OnFolderItemClickListener, MediaAdapter.OnAdapterItemClickListener {

    private Context mContext;
    private FrameLayout mHeadLayout;
    private TextView tvFolderName;
    private ImageView ivClose;
    private ImageView ivConfirm;
    private RecyclerView mRecyclerView;

    private String mDefaultFolderName = "";
    private File mTempCameraFile;
    private File mTempCropFile;
    private Uri mCameraFileUri;
    private List<Media> mSelectList;

    private MediaConfig mMediaConfig;
    private FolderWindow mFolderWindow;
    private FolderAdapter mFolderAdapter;
    private MediaAdapter mMediaAdapter;

    private static final int REQUEST_CAMERA = 1001;

    private String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        mContext = this;
        setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        if (havePermission()) {
            init();
        } else {
            ActivityCompat.requestPermissions(this, permissions, 1000);
        }
    }

    private void setStatusBarColor(@ColorInt int color) {
        final Window window = getWindow();
        if (window != null) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    private boolean havePermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void init() {
        initData();
        initView();
        initFolder();
    }

    private void initData() {
        mMediaConfig = MediaPick.getInstance().getMediaConfig();
        if (!mMediaConfig.isMemorizeHistory()) {
            mMediaConfig.setSelectList(null);
        }
        mSelectList = mMediaConfig.getSelectList();
        if (mMediaConfig.isShowOnlyImage()) {
            mDefaultFolderName = getResources().getString(R.string.label_all_image);
        } else if (mMediaConfig.isShowOnlyVideo()) {
            mDefaultFolderName = getResources().getString(R.string.label_all_video);
        } else if (mMediaConfig.isShowAll()) {
            mDefaultFolderName = getResources().getString(R.string.label_all_item);
        }
    }

    private void initView() {
        mHeadLayout = findViewById(R.id.head_layout);
        tvFolderName = findViewById(R.id.tv_folder_name);
        ivClose = findViewById(R.id.iv_close);
        ivConfirm = findViewById(R.id.iv_confirm);
        mRecyclerView = findViewById(R.id.recycler_view);
        tvFolderName.setText(mDefaultFolderName);
        tvFolderName.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        ivConfirm.setOnClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mMediaAdapter = new MediaAdapter(this);
        mMediaAdapter.setAdapterItemClickListener(this);
        mRecyclerView.setAdapter(mMediaAdapter);

        mFolderAdapter = new FolderAdapter(this);
        mFolderAdapter.setOnFolderItemClickListener(this);
        mFolderWindow = new FolderWindow(this, mFolderAdapter);
    }

    private void initFolder() {
        LoaderManager.LoaderCallbacks<Cursor> loaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

            @NonNull
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return FolderLoader.newInstance(mContext, mDefaultFolderName);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
                mFolderAdapter.swapCursor(cursor);
                cursor.moveToFirst();
                Folder folder = Folder.valueOf(cursor);
                takeMedia(folder);
            }

            @Override
            public void onLoaderReset(@NonNull Loader<Cursor> loader) {

            }
        };
        LoaderManager.getInstance(this).initLoader(1, null, loaderCallback);
    }

    private void takeMedia(final Folder folder) {
        LoaderManager.LoaderCallbacks<Cursor> loaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

            @NonNull
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return MediaLoader.newInstance(mContext, folder);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
                mMediaAdapter.swapCursor(cursor);
            }

            @Override
            public void onLoaderReset(@NonNull Loader<Cursor> loader) {

            }
        };
        //restartLoader会在onResume时又重新加载数据，initLoader就不会。
        LoaderManager.getInstance(this).initLoader(2, null, loaderCallback);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_folder_name) {
            mFolderWindow.showPopupWindow(mHeadLayout);
        } else if (id == R.id.iv_close) {
            finish();
        } else if (id == R.id.iv_confirm) {
            mMediaConfig.getHandleListener().onSelectMedia(mMediaConfig.getSelectList());
            finish();
        }
    }

    @Override
    public void onFolderItemClick(Folder folder) {
        //使用initLoader不会重新加载数据，所以在切换Folder后不会刷新，所以要先destroy掉这个Loader（id为2的loader）
        LoaderManager.getInstance(this).destroyLoader(2);
        mFolderWindow.dismiss();
        tvFolderName.setText(folder.getName());
        takeMedia(folder);
    }

    @Override
    public void onOpenCamera() {
        Intent cameraIntent = null;
        boolean isImage = true;
        if (mMediaConfig.isShowOnlyImage()) {
            isImage = true;
            cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        } else if (mMediaConfig.isShowOnlyVideo()) {
            isImage = false;
            cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        }
        if (cameraIntent == null) {
            return;
        }
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            mTempCameraFile = FileUtil.createFile(this, mMediaConfig.getFilePath(), isImage);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mCameraFileUri = FileProvider.getUriForFile(this, mMediaConfig.getProvider(), mTempCameraFile);
            } else {
                mCameraFileUri = Uri.fromFile(mTempCameraFile);
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraFileUri);
            List<ResolveInfo> infoList = getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : infoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, mCameraFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(this, R.string.alert_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClickMedia(Media media) {
        if (mMediaConfig.getMaxSize() == 1) {
            if (mMediaConfig.isCrop() && media.isImage()) {
                mTempCameraFile = new File(mSelectList.get(0).getPath());
                mTempCropFile = FileUtil.getCropFile(mMediaConfig.getFilePath());
                UCropUtil.start(MediaActivity.this, mTempCameraFile, mTempCropFile, mMediaConfig);
                return;
            }
            mMediaConfig.getHandleListener().onSelectMedia(mSelectList);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.alert_no_storage_permission, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        } else if (grantResults[2] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.alert_no_camera_permission, Toast.LENGTH_SHORT).show();
            MediaPick.getInstance().getMediaConfig().setShowCamera(false);
            init();
        } else {
            init();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (mMediaConfig.isCrop() && mMediaConfig.isShowOnlyImage() && mMediaConfig.getMaxSize() == 1) {
                        mSelectList.clear();
                        mTempCropFile = FileUtil.getCropFile(mMediaConfig.getFilePath());
                        UCropUtil.start(this, mTempCameraFile, mTempCropFile, mMediaConfig);
                    } else {
                        String mimeType = getMimeType(mTempCameraFile.getAbsolutePath());
                        long duration;
                        if (mMediaConfig.isShowOnlyVideo()) {
                            duration = getVideoDuration(mTempCameraFile.getAbsolutePath());
                        } else {
                            duration = 0;
                        }
                        Media media = new Media(0, mTempCameraFile.getAbsolutePath(), mTempCameraFile.getName(), mimeType, mTempCameraFile.length(), System.currentTimeMillis(), duration);
                        media.setUri(mCameraFileUri);
                        mSelectList.add(media);
                        handleResult(mTempCameraFile);
                    }
                } else {
                    if (mTempCameraFile != null && mTempCameraFile.exists()) {
                        mTempCameraFile.delete();
                    }
                    Toast.makeText(this, R.string.alert_take_picture_fail, Toast.LENGTH_SHORT).show();
                }
                break;
            case UCrop.REQUEST_CROP:
                if (data == null) {
                    return;
                }
                Uri uri = UCrop.getOutput(data);
                if (mTempCameraFile != null && mTempCameraFile.exists()) {
                    mTempCameraFile.delete();
                }
                mSelectList.clear();
                String mimeType = getMimeType(mTempCropFile.getAbsolutePath());
                Media media = new Media(0, mTempCropFile.getAbsolutePath(), mTempCropFile.getName(), mimeType, mTempCropFile.length(), System.currentTimeMillis(), 0);
                media.setUri(uri);
                mSelectList.add(media);
                handleResult(mTempCropFile);
                break;
        }
    }

    private void handleResult(File file) {
        notifyMediaUpdate(file);
        if (mMediaConfig.getMaxSize() == 1) {
            mMediaConfig.getHandleListener().onSelectMedia(mSelectList);
            finish();
        }
    }

    private void notifyMediaUpdate(File file) {
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

//    private String getMimeType(String filePath) {
//        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//        String mime = "text/plain";
//        if (filePath != null) {
//            try {
//                mmr.setDataSource(filePath);
//                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
//            } catch (RuntimeException e) {
//                return mime;
//            }
//        }
//        return mime;
//    }

    private String getMimeType(String filePath) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public long getVideoDuration(String videoPath) {
        int duration;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoPath);
            duration = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return duration;
    }
}