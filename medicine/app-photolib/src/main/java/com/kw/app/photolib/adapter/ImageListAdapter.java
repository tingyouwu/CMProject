package com.kw.app.photolib.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kw.app.commonlib.utils.ImageLoaderUtil;
import com.kw.app.photolib.R;
import com.kw.app.photolib.activity.ImageSelectorActivity;
import com.kw.app.photolib.bean.ImageModel;
import com.kw.app.widget.BaseRecyclerViewHolder;
import com.kw.app.widget.adapter.BaseRecyclerViewMultiItemAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Decription 图片选择器适配器
 * @author wty
 **/
public class ImageListAdapter extends BaseRecyclerViewMultiItemAdapter<ImageModel> {

	private boolean showCamera = true;//是否显示拍照item
	private boolean enablePreview = true;//是否使能预览功能
	private int maxSelectNum;//选择最大个数
	private int selectMode = ImageSelectorActivity.MODE_MULTIPLE;//选择模式  单选 多选

	private List<ImageModel> selectImages = new ArrayList<>();//已经选择的图片
	private OnImageSelectChangedListener imageSelectChangedListener;

	public ImageListAdapter(Context context, int maxSelectNum, int mode, boolean showCamera, boolean enablePreview) {
		super(context, null);
		this.maxSelectNum = maxSelectNum;
		this.selectMode = mode;
		this.showCamera = showCamera;
		this.enablePreview = enablePreview;
		addItemType(ImageModel.TYPE_CAMERA, R.layout.item_camera);
		addItemType(ImageModel.TYPE_PICTURE, R.layout.item_picture);
		init();
	}

	/**
	 * @Decription 初始化需要添加  删除功能
	 **/
	private void init(){
		this.mData.clear();
		if(showCamera){
			this.mData.add(new ImageModel(ImageModel.TYPE_CAMERA));
		}
	}

	/**
	 * @Decription 绑定图片数据
	 **/
	public void bindImages(List<ImageModel> data){
		this.mData.clear();
		if(showCamera){
			this.mData.add(new ImageModel(ImageModel.TYPE_CAMERA));
		}
		super.addData(data);
	}

	/**
	 * @Decription 绑定已经选择图片数据
	 **/
	public void bindSelectedImages(List<ImageModel> data){
		this.mData.clear();
		super.addData(data);
	}

	@Override
	protected void bindView(BaseRecyclerViewHolder helper, ImageModel item, int position) {
		switch (helper.getItemViewType()){
			case ImageModel.TYPE_CAMERA:
				setCameraItem(helper);
				break;
			case ImageModel.TYPE_PICTURE:
				setPictureItem(helper,item,position);
				break;
			default:
				break;
		}
	}

	@Override
	protected int getItemMultiViewType(int position) {
		return getItem(position).type;
	}

	/**
	 * @Decription 设置通用的图片
	 **/
	private void setPictureItem(final BaseRecyclerViewHolder helper, final ImageModel item, final int position) {
		ImageView img = helper.getView(R.id.picture);
		ImageLoaderUtil.load(mContext,new File(item.path),img);

		if(selectMode == ImageSelectorActivity.MODE_SINGLE){
			helper.getView(R.id.check).setVisibility(View.GONE);
		}

		selectImage(helper,isSelected(item));

		if(enablePreview){
			helper.getView(R.id.check).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					changeCheckBoxState(helper,item);
				}
			});
		}

		helper.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if((selectMode == ImageSelectorActivity.MODE_SINGLE || enablePreview) && imageSelectChangedListener != null){
					imageSelectChangedListener.onPictureClick(item,position);
				}else{
					changeCheckBoxState(helper,item);
				}
			}
		});
	}

	/**
	 * @Decription 设置拍照
	 **/
	private void setCameraItem(BaseRecyclerViewHolder helper) {
		helper.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (imageSelectChangedListener != null) {
					imageSelectChangedListener.onTakePhoto();
				}
			}
		});
	}

	/**
	 * @Decrpition 改变checkbox状态
	 **/
	private void changeCheckBoxState(BaseRecyclerViewHolder holder,ImageModel model){
		ImageView checkbox = holder.getView(R.id.check);
		boolean isChecked = checkbox.isSelected();
		if(selectImages.size()>=maxSelectNum && !isChecked){
			Toast.makeText(mContext,String.format("你最多可以选择%s张图片",maxSelectNum),Toast.LENGTH_LONG).show();
			return;
		}

		if(isChecked){
			for (ImageModel temp:selectImages){
				if(temp.path.equals(model.path)){
					selectImages.remove(temp);
					break;
				}
			}
		}else{
			selectImages.add(model);
		}

		selectImage(holder, !isChecked);
		if(imageSelectChangedListener != null){
			imageSelectChangedListener.onChange(selectImages);
		}
	}

	/**
	 * @Decription 判断是否已经选择
	 **/
	private boolean isSelected(ImageModel image) {
		for (ImageModel media : selectImages) {
			if (media.path.equals(image.path)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @Decription  设置选择图片的样式
	 **/
	private void selectImage(BaseRecyclerViewHolder holder, boolean isChecked) {
		holder.getView(R.id.check).setSelected(isChecked);
		ImageView img = holder.getView(R.id.picture);
		if (isChecked) {
			img.setColorFilter(mContext.getResources().getColor(R.color.image_overlay2), PorterDuff.Mode.SRC_ATOP);
		} else {
			img.setColorFilter(mContext.getResources().getColor(R.color.image_overlay), PorterDuff.Mode.SRC_ATOP);
		}
	}

	/**
	 * @Decription 返回选择的图片
	 **/
	public List<ImageModel> getSelectedImages() {
		return selectImages;
	}

	/**
	 * @Decription 返回选择的图片
	 **/
	public void setSelectedImages(List<ImageModel> data) {
		this.selectImages.clear();
		this.selectImages.addAll(data);
		notifyDataSetChanged();
	}

	public interface OnImageSelectChangedListener {
		/**图片数目变化**/
		void onChange(List<ImageModel> selectImages);
		/**点击拍照**/
		void onTakePhoto();
		/**点击图片**/
		void onPictureClick(ImageModel model, int position);
	}

	public void setOnImageSelectChangedListener(OnImageSelectChangedListener imageSelectChangedListener) {
		this.imageSelectChangedListener = imageSelectChangedListener;
	}

}
