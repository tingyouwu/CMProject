package com.kw.app.medicine.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kw.app.commonlib.utils.ImageLoaderUtil;
import com.kw.app.medicine.R;
import com.kw.app.medicine.activity.UserInfoActivity;
import com.kw.app.medicine.data.bmob.UserBmob;
import com.kw.app.widget.BaseRecyclerViewHolder;
import com.kw.app.widget.adapter.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * @Decription 搜索好友 适配器
 */
public class SearchUserAdapter extends BaseRecyclerViewAdapter<UserBmob> {
    public SearchUserAdapter(Context context, List data) {
        super(context, R.layout.item_search_user, data);
    }

    @Override
    protected void convert(BaseRecyclerViewHolder helper, final UserBmob item, int position) {
        TextView name = helper.getView(R.id.name);
        ImageView img = helper.getView(R.id.avatar);
        ImageLoaderUtil.loadCircle(mContext, item.getLogourl(), img);

        name.setText(item.getUsername());
        helper.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoActivity.startUserInfoActivity(mContext,item);
            }
        });

    }

}
