package com.kw.app.medicine.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kw.app.commonlib.utils.ImageLoaderUtil;
import com.kw.app.medicine.R;
import com.kw.app.medicine.activity.FriendInfoActivity;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.widget.BaseRecyclerViewHolder;
import com.kw.app.widget.adapter.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * @Decription 联系人 适配器
 */
public class ContactAdapter extends BaseRecyclerViewAdapter<UserDALEx> {
    public ContactAdapter(Context context, List data) {
        super(context, R.layout.item_contact, data);
    }

    @Override
    protected void convert(BaseRecyclerViewHolder helper, final UserDALEx item, int position) {
        TextView name = helper.getView(R.id.tv_name);
        final ImageView icon = helper.getView(R.id.iv_contact_header);
        TextView letter = helper.getView(R.id.tv_letter);
        ImageLoaderUtil.loadCircle(mContext,item.getLogourl(),R.mipmap.img_contact_default,icon);
        name.setText(item.getNickname());

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(section)){
            letter.setVisibility(View.VISIBLE);
            letter.setText(item.getPinyin().substring(0, 1).toUpperCase());
        }else{
            letter.setVisibility(View.GONE);
        }

        helper.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendInfoActivity.startFriendInfoActivity(mContext,item);
            }
        });

    }

    /**
     * 根据当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        String sortString = mData.get(position).getPinyin().substring(0,1).toUpperCase();

        // 正则表达式，判断首字母是否是英文字母
        if(sortString.matches("[A-Z]")){
            return sortString.charAt(0);
        }else{
            return "#".charAt(0);
        }
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mData.get(i).getPinyin();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

}
