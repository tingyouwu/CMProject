package com.kw.app.medicine.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kw.app.commonlib.utils.ImageLoaderUtil;
import com.kw.app.medicine.R;
import com.kw.app.medicine.activity.FriendInfoActivity;
import com.kw.app.medicine.bean.FriendBean;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.widget.BaseRecyclerViewHolder;
import com.kw.app.widget.adapter.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * @Decription 联系人 适配器
 */
public class ContactAdapter extends BaseRecyclerViewAdapter<FriendBean> {
    public ContactAdapter(Context context, List data) {
        super(context, R.layout.item_contact, data);
    }

    @Override
    protected void convert(BaseRecyclerViewHolder helper, final FriendBean item, int position) {
        TextView name = helper.getView(R.id.tv_name);
        final ImageView icon = helper.getView(R.id.iv_contact_header);
        TextView letter = helper.getView(R.id.tv_letter);
        ImageLoaderUtil.loadCircle(mContext,item.getUser().getLogourl(),R.mipmap.img_contact_default,icon);
        name.setText(item.getUser().getNickname());

        if(position==0){
            letter.setVisibility(View.VISIBLE);
            letter.setText(item.getSortkey());
        }else{
            String lastSortkey = mData.get(position-1).getSortkey();
            if(lastSortkey.equals(item.getSortkey())){
                letter.setVisibility(View.GONE);
            }else{
                letter.setVisibility(View.VISIBLE);
                letter.setText(item.getSortkey());
            }
        }

        helper.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendInfoActivity.startFriendInfoActivity(mContext,item.getUser());
            }
        });

    }

    /**
     * 获取其第一次出现首字母的位置
     */
    public int getPositionForSection(String sortkey) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mData.get(i).getSortkey();
            if (sortkey.equals(sortStr)) {
                return i;
            }
        }

        return -1;
    }

}
