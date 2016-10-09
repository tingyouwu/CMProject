package com.kw.app.widget.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.kw.app.widget.BaseViewHolder;

import java.util.List;

/**
 * 功能描述：通用 adapter（多布局）
 * @remark getItemViewType不能等于getViewTypeCount
 */
public abstract class BaseViewMultiItemCommonAdapter<T> extends BaseViewCommonAdapter<T> {

    /**
     * Map用于存储  类型以及 对应的布局文件  调用addItemType吧少年
     */
    private SparseArray<Integer> layouts;

    public BaseViewMultiItemCommonAdapter(Context context, List<T> data) {
        super(context,data);
    }

    @Override
    public int getItemViewType(int position) {
        return getItemMultiViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        //这是一个注意点，需要特别注意重写,避免java.lang.ArrayIndexOutOfBoundsException: (数组越界)
        return layouts.size();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(View convertView, ViewGroup parent, int position) {
        return createBaseViewHolder(convertView, parent, getLayoutId(getItemViewType(position)));
    }

    private int getLayoutId(int viewType) {
        return layouts.get(viewType);
    }

    protected void addItemType(int type, int layoutResId) {
        if (layouts == null) {
            layouts = new SparseArray<>();
        }
        layouts.put(type, layoutResId);
    }


    @Override
    protected void convert(BaseViewHolder helper, T item) {
        convert(helper, item);
    }

    /**
     * @Decription 给view绑定数据
     **/
    protected abstract void bindView(BaseViewHolder helper, T item);

    /**
     * @Description 布局类型
     **/
    protected abstract int getItemMultiViewType(int position);

}


