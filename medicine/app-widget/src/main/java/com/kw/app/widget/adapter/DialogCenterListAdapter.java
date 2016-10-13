package com.kw.app.widget.adapter;

import android.content.Context;
import android.widget.TextView;

import com.kw.app.widget.BaseViewHolder;
import com.kw.app.widget.R;

import java.util.List;

/**
 * @Decription 主页界面 适配器
 */
public class DialogCenterListAdapter extends BaseViewCommonAdapter<String> {
    public DialogCenterListAdapter(Context context, List data) {
        super(context, R.layout.item_center_dialog_text, data);

    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        TextView tv = helper.getView(R.id.tv_content);
        tv.setText(item);
    }

}
