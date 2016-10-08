package com.kw.app.photolib.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kw.app.commonlib.utils.ImageLoaderUtil;
import com.kw.app.photolib.R;
import com.kw.app.photolib.bean.FolderModel;
import com.kw.app.photolib.bean.ImageModel;
import com.kw.app.widget.BaseRecyclerViewHolder;
import com.kw.app.widget.adapter.BaseRecyclerViewAdapter;

import java.io.File;
import java.util.List;

/**
 * @Decription 图片文件夹适配器
 */
public class ImageFolderAdapter extends BaseRecyclerViewAdapter<FolderModel> {

    private int checkedIndex = 0;//记录已经选择的路径
    private OnItemClickListener onItemClickListener;

    public ImageFolderAdapter(Context context) {
        super(context, R.layout.item_folder, null);
    }

    @Override
    protected void convert(BaseRecyclerViewHolder helper, final FolderModel item, final int position) {
        ImageView img_icon = helper.getView(R.id.first_image);
        TextView tv_foldername = helper.getView(R.id.folder_name);
        TextView tv_imagenum = helper.getView(R.id.image_num);
        ImageView img_selected = helper.getView(R.id.is_selected);

        ImageLoaderUtil.load(mContext,new File(item.getFirstImagePath()),img_icon);
        tv_foldername.setText(item.getName());
        tv_imagenum.setText(mContext.getString(R.string.num_postfix,item.getImageNum()));
        img_selected.setVisibility(checkedIndex == position ? View.VISIBLE : View.GONE);

        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    checkedIndex = position;//记录最新路径
                    notifyDataSetChanged();
                    onItemClickListener.onItemClick(item.getName(), item.getImages());
                }
            }
        });

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener{
        void onItemClick(String folderName, List<ImageModel> images);
    }
}
