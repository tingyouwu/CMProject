package com.kw.app.photolib.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kw.app.commonlib.base.AppConstant;
import com.kw.app.commonlib.mvp.presenter.BasePresenter;
import com.kw.app.commonlib.utils.FileUtils;
import com.kw.app.commonlib.utils.PhotoUtils;
import com.kw.app.commonlib.utils.ScreenUtil;
import com.kw.app.photolib.LocalMediaLoaderUtil;
import com.kw.app.photolib.R;
import com.kw.app.photolib.adapter.ImageFolderAdapter;
import com.kw.app.photolib.adapter.ImageListAdapter;
import com.kw.app.photolib.bean.FolderModel;
import com.kw.app.photolib.bean.ImageModel;
import com.kw.app.photolib.view.FolderWindow;
import com.kw.app.widget.activity.BaseActivity;
import com.kw.app.widget.view.GridSpacingItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Decrition 图片选择界面
 * @author wty
 */
public class ImageSelectorActivity extends BaseActivity {

    public final static int MODE_MULTIPLE = 1;
    public final static int MODE_SINGLE = 2;
    public final static String BUNDLE_CAMERA_PATH = "CameraPath";//拍照存储路径
    public final static String REQUEST_OUTPUT = "outputList";
    public final static String EXTRA_SELECT_MODE = "SelectMode";//选择模式 单选  多选
    public final static String EXTRA_SHOW_CAMERA = "ShowCamera";//是否显示拍照
    public final static String EXTRA_ENABLE_PREVIEW = "EnablePreview";//是否可以预览
    public final static String EXTRA_ENABLE_CROP = "EnableCrop";//是否可以裁剪
    public final static String EXTRA_MAX_SELECT_NUM = "MaxSelectNum";//最大选择数目
    public static final String EXTRA_SELECT_LIST = "previewSelectList";
    public static final String OUTPUT_ISCAMERA = "isCamera";

    TextView previewText;
    RecyclerView recyclerView;
    LinearLayout folderLayout;
    TextView folderName;
    View toolbar;

    private ImageListAdapter imageAdapter;
    private FolderWindow folderWindow;
    private String cameraPath;//拍照图片路径
    private int maxSelectNum = 9;
    private int selectMode = MODE_MULTIPLE;
    private boolean showCamera = true;
    private boolean enablePreview = true;
    private boolean enableCrop = false;
    private int spanCount = 4;
    private List<ImageModel> selectImages = new ArrayList<>();

    /**
     * @param maxSelectNum  图片最大数量
     * @param mode  单选还是多选
     * @param isShowCamera 是否显示camera
     * @param enablePreview 是否可以预览
     * @param enableCrop 是否支持裁剪
     **/
    public static void start(Activity activity, int maxSelectNum, int mode, boolean isShowCamera, boolean enablePreview, boolean enableCrop,List<ImageModel> selectImages) {
        Intent intent = new Intent(activity, ImageSelectorActivity.class);
        intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum);
        intent.putExtra(EXTRA_SELECT_MODE, mode);
        intent.putExtra(EXTRA_SHOW_CAMERA, isShowCamera);
        intent.putExtra(EXTRA_ENABLE_PREVIEW, enablePreview);
        intent.putExtra(EXTRA_ENABLE_CROP, enableCrop);
        intent.putExtra(EXTRA_SELECT_LIST,(ArrayList)selectImages);
        activity.startActivityForResult(intent, AppConstant.ActivityResult.Request_Image);
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {

        maxSelectNum = getIntent().getIntExtra(EXTRA_MAX_SELECT_NUM, 9);
        selectMode = getIntent().getIntExtra(EXTRA_SELECT_MODE, MODE_MULTIPLE);
        showCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        enablePreview = getIntent().getBooleanExtra(EXTRA_ENABLE_PREVIEW, true);
        enableCrop = getIntent().getBooleanExtra(EXTRA_ENABLE_CROP, false);
        selectImages = (List<ImageModel>) getIntent().getSerializableExtra(EXTRA_SELECT_LIST);

        if (selectMode == MODE_MULTIPLE) {
            enableCrop = false;
        } else {
            enablePreview = false;
        }
        if (savedInstanceState != null) {
            cameraPath = savedInstanceState.getString(BUNDLE_CAMERA_PATH);
        }

        previewText = (TextView)findViewById(R.id.tv_preview_text);
        recyclerView = (RecyclerView) findViewById(R.id.folder_list);
        folderLayout = (LinearLayout) findViewById(R.id.layout_folder);
        folderName = (TextView) findViewById(R.id.tv_folder_name);
        toolbar = findViewById(R.id.toolbar);

        initTitle();

        folderWindow = new FolderWindow(this);
        previewText.setVisibility(enablePreview ? View.VISIBLE : View.GONE);

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, ScreenUtil.dp2px(this, 2), false));
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
        imageAdapter = new ImageListAdapter(this, maxSelectNum, selectMode, showCamera,enablePreview);
        if(selectImages != null && selectImages.size()!=0){
            imageAdapter.setSelectedImages(selectImages);
            updateState(selectImages.size());
        }
        recyclerView.setAdapter(imageAdapter);

        new LocalMediaLoaderUtil(this).loadAllImage(new LocalMediaLoaderUtil.LocalMediaLoadListener() {

            @Override
            public void loadComplete(List<FolderModel> folders) {
                folderWindow.bindFolder(folders);
                imageAdapter.bindImages(folders.get(0).getImages());
            }
        });
        registerListener();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_imageselector;
    }

    @Override
    protected boolean isEnableStatusBar() {
        return true;
    }

    private void initTitle(){
        getDefaultNavigation().setTitle("图片选择");
        getDefaultNavigation().setRightButton("完成", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(folderWindow.isShowing()){
                    //弹窗有出现 不做任何操作
                }else{
                    onSelectDone(imageAdapter.getSelectedImages());
                }
            }
        });
        getDefaultNavigation().getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(folderWindow.isShowing()){
                    //弹窗有出现 不做任何操作
                }else{
                    finish();
                }
            }
        });
    }

    /**
     * @Decription 注册各种监听事件
     **/
    private void registerListener() {
        imageAdapter.setOnImageSelectChangedListener(new ImageListAdapter.OnImageSelectChangedListener() {
            @Override
            public void onChange(List<ImageModel> selectImages) {
                updateState(selectImages.size());
            }

            @Override
            public void onTakePhoto() {
                if(!FileUtils.hasEnoughMemory()){
                    ImageSelectorActivity.this.showFailed("SD卡不够内存空间");
                    return;
                }
                startCamera();
            }

            @Override
            public void onPictureClick(ImageModel model, int position) {
                if (enablePreview) {
                    startPreview(imageAdapter.getData(), position);
                } else if (enableCrop) {
                    startCrop(model.path);
                } else {
                    onSelectDone(model.path);
                }
            }
        });

        folderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (folderWindow.isShowing()) {
                    folderWindow.dismiss();
                } else {
                    folderWindow.showAsDropDown(toolbar);
                }
            }
        });

        folderWindow.setOnItemClickListener(new ImageFolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String name, List<ImageModel> images) {
                folderWindow.dismiss();
                imageAdapter.bindImages(images);
                folderName.setText(name);
            }
        });
        previewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPreview(imageAdapter.getSelectedImages(), 0);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {//拍照成功返回
            if (requestCode == AppConstant.ActivityResult.Request_Camera) {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(cameraPath))));
                if (enableCrop) {
                    startCrop(cameraPath);
                } else {
                    onSelectDone(cameraPath);
                }
            }
            else if (requestCode == AppConstant.ActivityResult.Request_Preview) {//预览图片返回
                boolean isDone = data.getBooleanExtra(ImagePreviewActivity.OUTPUT_ISDONE, false);
                List<ImageModel> images = (List<ImageModel>) data.getSerializableExtra(ImagePreviewActivity.OUTPUT_LIST);
                if (isDone) {
                    onSelectDone(images);
                }else{
                    imageAdapter.setSelectedImages(images);
                    updateState(images.size());
                }
            }
            else if (requestCode == AppConstant.ActivityResult.Request_Crop) {//裁剪图片返回
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                File tempfile = FileUtils.createCropFile(ImageSelectorActivity.this);
                if(PhotoUtils.saveBitmap2file(bitmap,tempfile.getAbsolutePath())){
                    //保存成功
                    onSelectDone(tempfile.getAbsolutePath());
                }else{
                    showFailed("截图保存失败,请重试!");
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(BUNDLE_CAMERA_PATH, cameraPath);
    }

    private void updateState(int size){
        boolean enable = size != 0;
        getDefaultNavigation().getRightButton().setEnabled(enable ? true : false);
        previewText.setEnabled(enable ? true : false);
        if (enable) {
            getDefaultNavigation().getRightButton().setText(getString(R.string.done_num, size, maxSelectNum));
            previewText.setText(getString(R.string.preview_num, size));
        } else {
            getDefaultNavigation().getRightButton().setText(getString(R.string.done));
            previewText.setText(R.string.preview);
        }
    }

    /**
     * @Decription 拍照
     */
    private void startCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File cameraFile = FileUtils.createCameraFile(this);
            cameraPath = cameraFile.getAbsolutePath();
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
            startActivityForResult(cameraIntent, AppConstant.ActivityResult.Request_Camera);
        }
    }

    /**
     * @Decription 预览
     **/
    private void startPreview(List<ImageModel> previewImages, int position) {
        ImagePreviewActivity.startPreview(this, previewImages, imageAdapter.getSelectedImages(), maxSelectNum, position);
    }

    /**
     * @Decription 开始裁剪 调用系统截图工具
     **/
    public void startCrop(String path) {
        Uri uri = Uri.fromFile(new File(path));
        PhotoUtils.cropSmallImage(ImageSelectorActivity.this,uri,AppConstant.ActivityResult.Request_Crop);
    }

    /**
     * @param models
     * @Decription 选择多张图片返回
     */
    private void onSelectDone(List<ImageModel> models) {
        ArrayList<String> images = new ArrayList<>();
        for (ImageModel model : models) {
            images.add(model.path);
        }
        onResult(images,false);
    }

    /**
     * @param path
     * @Decription 选择单张图片返回
     **/
    private void onSelectDone(String path) {
        ArrayList<String> images = new ArrayList<>();
        images.add(path);
        onResult(images,true);
    }

    public void onResult(ArrayList<String> images,boolean isCamera) {
        Intent intent = new Intent();
        intent.putExtra(REQUEST_OUTPUT,(ArrayList)images);
        intent.putExtra(OUTPUT_ISCAMERA,isCamera);
        setResult(RESULT_OK,intent);
        finish();
    }
}
