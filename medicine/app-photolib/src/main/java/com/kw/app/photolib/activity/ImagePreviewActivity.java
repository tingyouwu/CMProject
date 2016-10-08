package com.kw.app.photolib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.kw.app.commonlib.base.AppConstant;
import com.kw.app.commonlib.mvp.presenter.BasePresenter;
import com.kw.app.photolib.R;
import com.kw.app.photolib.adapter.ImagePreviewPagerAdapter;
import com.kw.app.photolib.bean.ImageModel;
import com.kw.app.widget.activity.BaseActivity;
import com.kw.app.widget.view.ImageViewPager;

import java.util.ArrayList;
import java.util.List;


/**
 * @Decription 图片预览
 * @author wty
 */
public class ImagePreviewActivity extends BaseActivity {

    public static final String EXTRA_PREVIEW_LIST = "previewList";
    public static final String EXTRA_PREVIEW_SELECT_LIST = "previewSelectList";
    public static final String EXTRA_MAX_SELECT_NUM = "maxSelectNum";
    public static final String EXTRA_POSITION = "position";

    public static final String OUTPUT_LIST = "outputList";
    public static final String OUTPUT_ISDONE = "isDone";

    private ImageViewPager viewpager;
    private CheckBox ck_selected;

    private int position;//当前位置
    private int maxSelectNum;//最大选择数目
    private List<ImageModel> images = new ArrayList<>();
    private List<ImageModel> selectImages = new ArrayList<>();
    private ImagePreviewPagerAdapter adapter;

    public static void startPreview(Activity context, List<ImageModel> images, List<ImageModel> selectImages, int maxSelectNum, int position) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(EXTRA_PREVIEW_LIST, (ArrayList) images);
        intent.putExtra(EXTRA_PREVIEW_SELECT_LIST, (ArrayList) selectImages);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum);
        context.startActivityForResult(intent, AppConstant.ActivityResult.Request_Preview);
    }


    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_image_preview;
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {

        images = (List<ImageModel>) getIntent().getSerializableExtra(EXTRA_PREVIEW_LIST);
        selectImages = (List<ImageModel>) getIntent().getSerializableExtra(EXTRA_PREVIEW_SELECT_LIST);
        maxSelectNum = getIntent().getIntExtra(EXTRA_MAX_SELECT_NUM, 9);
        position = getIntent().getIntExtra(EXTRA_POSITION, 1);

        ck_selected = (CheckBox) findViewById(R.id.checkbox_select);
        viewpager = (ImageViewPager) findViewById(R.id.preview_pager);
        adapter = new ImagePreviewPagerAdapter(this);
        adapter.setDatas(images);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(position);
        viewpager.setOffscreenPageLimit(4);

        registerListener();
        onSelectNumChange();
        onImageSwitch(position);

    }

    private void registerListener() {
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getDefaultNavigation().setTitle(position + 1 + "/" + images.size());
                onImageSwitch(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ck_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ck_selected.isChecked();
                if (selectImages.size() >= maxSelectNum && isChecked) {
                    Toast.makeText(ImagePreviewActivity.this, getString(R.string.message_max_num, maxSelectNum), Toast.LENGTH_LONG).show();
                    ck_selected.setChecked(false);
                    return;
                }
                ImageModel image = images.get(viewpager.getCurrentItem());
                if (isChecked) {
                    selectImages.add(image);
                } else {
                    for (ImageModel media : selectImages) {
                        if (media.path.equals(image.path)) {
                            selectImages.remove(media);
                            break;
                        }
                    }
                }
                onSelectNumChange();
            }
        });

        getDefaultNavigation().setTitle(position + 1 + "/" + images.size());
        getDefaultNavigation().getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneClick(true);
            }
        });

        getDefaultNavigation().getLeftButton().setText("返回");
        getDefaultNavigation().getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneClick(false);
            }
        });
    }

    @Override
    protected boolean isEnableStatusBar() {
        return true;
    }

    /**
     * @Decription 选择数目变化后需要更新状态
     **/
    private void onSelectNumChange() {
        boolean enable = selectImages.size() != 0;
        getDefaultNavigation().getRightButton().setEnabled(enable ? true : false);
        if (enable) {
            getDefaultNavigation().getRightButton().setText(getString(R.string.done_num, selectImages.size(), maxSelectNum));
        } else {
            getDefaultNavigation().getRightButton().setText(getString(R.string.done));
        }
    }

    /**
     * @Decription 改变一下checkbox状态
     **/
    private void onImageSwitch(int position) {
        ck_selected.setChecked(isSelected(images.get(position)));
    }

    /**
     * @Decription 判断是否选择过
     **/
    private boolean isSelected(ImageModel image) {
        for (ImageModel media : selectImages) {
            if (media.path.equals(image.path)) {
                return true;
            }
        }
        return false;
    }

    private void onDoneClick(boolean isDone){
        Intent intent = new Intent();
        intent.putExtra(OUTPUT_LIST,(ArrayList)selectImages);
        intent.putExtra(OUTPUT_ISDONE,isDone);
        setResult(RESULT_OK,intent);
        finish();
    }
}
