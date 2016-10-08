package com.kw.app.photolib;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;


import com.kw.app.photolib.bean.FolderModel;
import com.kw.app.photolib.bean.ImageModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Decription 加载本地多媒体文件工具类
 */
public class LocalMediaLoaderUtil {

    private final static String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID};

    private FragmentActivity activity;

    public LocalMediaLoaderUtil(FragmentActivity activity) {
        this.activity = activity;
    }

    /**
     * @Decription 加载图片文件
     **/
    public void loadAllImage(final LocalMediaLoadListener imageLoadListener) {
        activity.getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                CursorLoader cursorLoader = new CursorLoader(
                            activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            IMAGE_PROJECTION, MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?",
                            new String[]{"image/jpeg", "image/png"}, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                ArrayList<FolderModel> imageFolders = new ArrayList<FolderModel>();
                FolderModel allImageFolder = new FolderModel();
                List<ImageModel> allImages = new ArrayList<ImageModel>();

                if (data != null) {
                    int count = data.getCount();
                    if (count > 0) {
                        data.moveToFirst();
                        do {
                            String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                            // 如原图路径不存在或者路径存在但文件不存在,就结束当前循环
                            if (TextUtils.isEmpty(path) || !new File(path).exists()) {
                                continue;
                            }
                            String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                            ImageModel image = new ImageModel(path,name);

                            FolderModel folder = getImageFolder(path,imageFolders);

                            folder.getImages().add(image);
                            folder.setImageNum(folder.getImageNum() + 1);

                            allImages.add(image);
                            allImageFolder.setImageNum(allImageFolder.getImageNum()+1);
                        } while (data.moveToNext());

                        allImageFolder.setFirstImagePath(allImages.get(0).path);
                        allImageFolder.setName("全部图片");
                        allImageFolder.setImages(allImages);

                        imageFolders.add(allImageFolder);
                        sortFolder(imageFolders);
                        imageLoadListener.loadComplete(imageFolders);
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
            }
        });
    }

    private void sortFolder(List<FolderModel> imageFolders) {
        // 文件夹按图片数量排序
        Collections.sort(imageFolders, new Comparator<FolderModel>() {
            @Override
            public int compare(FolderModel lhs, FolderModel rhs) {
                if (lhs.getImages() == null || rhs.getImages() == null) {
                    return 0;
                }
                int lsize = lhs.getImageNum();
                int rsize = rhs.getImageNum();
                return lsize == rsize ? 0 : (lsize < rsize ? 1 : -1);
            }
        });
    }

    /**
     * @Decription 根据文件当前路径  返回对应的文件夹路径
     **/
    private FolderModel getImageFolder(String path,List<FolderModel> imageFolders) {
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();

        for (FolderModel folder : imageFolders) {
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        FolderModel newFolder = new FolderModel();
        newFolder.setName(folderFile.getName());
        newFolder.setPath(folderFile.getAbsolutePath());
        newFolder.setFirstImagePath(path);
        imageFolders.add(newFolder);
        return newFolder;
    }

    public interface LocalMediaLoadListener {
        void loadComplete(List<FolderModel> folders);
    }

}
