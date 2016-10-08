package com.kw.app.photolib.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kw.app.photolib.R;
import com.kw.app.photolib.bean.ImageModel;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * @Decription 图片浏览 适配器
 * @author wty
 */
public class ImagePreviewPagerAdapter extends PagerAdapter {

    private List<ImageModel> datas = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;

    public ImagePreviewPagerAdapter(Context context){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(datas == null) return 0;
        return datas.size();
    }


    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View view = inflater.inflate(R.layout.item_pager_image, container, false);
        if(view != null){
            final PhotoView imageView = (PhotoView) view.findViewById(R.id.image);

            imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                }
            });

            final String imgurl = datas.get(position).path;

            Glide.with(context)
                    .load(imgurl)
                    .diskCacheStrategy( DiskCacheStrategy.NONE )
                    .error(R.mipmap.img_error_fail)
                    .into(imageView);

            container.addView(view, 0);
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public void setDatas(List<ImageModel> datas) {
        if(datas != null )
            this.datas = datas;
    }

}
