package com.kw.app.widget.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.kw.app.widget.BaseRecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述：通用recyclerview adapter
 * @author wty
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context mContext;
    protected int mLayoutResId;//布局文件id
    protected List<T> mData;
    private View mContentView;
    private boolean isScrolling = false;

    public BaseRecyclerViewAdapter(Context context) {
        this(context,0, null);
    }

    public BaseRecyclerViewAdapter(Context context,List<T> data) {
        this(context,0, data);
    }

    public BaseRecyclerViewAdapter(Context context,int layoutResId) {
        this(context,layoutResId,null);
    }

    public BaseRecyclerViewAdapter(Context context,View contentView, List<T> data) {
        this(context,0, data);
        mContentView = contentView;
    }

    public BaseRecyclerViewAdapter(Context context,int layoutResId, List<T> data) {
        this.mContext = context;
        this.mData = data == null ? new ArrayList<T>() : data;
        if (layoutResId != 0) {
            this.mLayoutResId = layoutResId;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder BaseRecyclerViewHolder = onCreateDefViewHolder(parent, viewType);
        return BaseRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        convert((BaseRecyclerViewHolder)holder,mData.get(position),position);
    }

    protected BaseRecyclerViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createBaseViewHolder(parent, mLayoutResId);
    }

    protected BaseRecyclerViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        if (mContentView == null) {
            return BaseRecyclerViewHolder.get(mContext,parent,layoutResId);
        }
        return new BaseRecyclerViewHolder(mContentView);
    }

    public void remove(int position) {
        /**
         * 先remove,再notifyItemRemoved， 最后再notifyItemRangeChanged
         remove：把数据从list中remove掉，
         notifyItemRemoved：显示动画效果
         notifyItemRangeChanged：对于被删掉的位置及其后range大小范围内的view进行重新onBindViewHolder
         **/
        mData.remove(position);
        notifyItemRemoved(position);
        if(mData.size()>1)
            notifyItemRangeChanged(position,mData.size()-position);

    }

    public void addOne(T item) {
        mData.add(item);
        notifyItemInserted(mData.size());
    }

    public void addOne(int position, T item) {
        mData.add(position, item);
        notifyItemInserted(position);
    }

    public void addData(List<T> data) {
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(int position,List<T> data) {
        this.mData.addAll(position,data);
        notifyDataSetChanged();
    }

    public void retsetData(List<T> data){
        this.mData.clear();
        addData(data);
    }

    public void clearData(){
        this.mData.clear();
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return mData;
    }

    public T getItem(int position) {
        return mData.get(position);
    }

    /**
     * 功能描述：绑定数据
     **/
    protected abstract void convert(BaseRecyclerViewHolder helper, T item,int position);

    public void setScrolling(boolean isScrolling){
        this.isScrolling = isScrolling;
    }

    public boolean getScrollingStatus(){
        return this.isScrolling;
    }
}
