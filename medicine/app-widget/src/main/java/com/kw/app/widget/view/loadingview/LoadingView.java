package com.kw.app.widget.view.loadingview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kw.app.commonlib.utils.NetWorkUtils;
import com.kw.app.widget.R;

/**
 * 加载过程的view
 */
public class LoadingView extends FrameLayout {

    private Context mContext;
    LinearLayout ll_over;
    RelativeLayout ll_loading;
    TextView tv_loaded;
    TextView tv_loading;
    Button btn_loaded;
    ImageView iv_loading;
    ImageView iv_loaded;

    private String mLoadingText = "玩命加载中...";//加载中提示文字
    private int mLoadingIco = R.mipmap.icon_data_loading;//加载中显示的图标
    private String mLoaded_empty_text = "亲,生活好单调，刷新一下吧";//加载数据为空提示文字
    private int mEmptyIco = R.mipmap.icon_no_data;//加载数据为空时图标
    private String mLoaded_not_net_text = "网络不好使，请检查一下...";//没有网络提示
    private int mNoNetIco = R.mipmap.icon_data_error;//没有网络图标
    private String mLoaded_error_text = "没有内容,我家程序员跑路了！";//后台或者本地出现错误提示
    private int mErrorIco = R.mipmap.icon_data_error;//后台或者本地出现错误提示图标

    public boolean btn_empty_ennable = true;
    public boolean btn_error_ennable = true;
    public boolean btn_nonet_ennable = true;

    public String btn_empty_text = "刷新一下";
    public String btn_error_text = "刷新一下";
    public String btn_nonet_text = "刷新一下";

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    public LoadingView withLoadingIco(int resId) {
        mLoadingIco = resId;
        return this;
    }

    public LoadingView withEmptyIco(int resId) {
        mEmptyIco = resId;
        return this;
    }

    public LoadingView withNoNetIco(int resId) {
        mNoNetIco = resId;
        return this;
    }

    private OnRetryListener mOnRetryListener;
    private OnEmptyListener mOnEmptyListener;

    public LoadingView withOnRetryListener(OnRetryListener mOnRetryListener) {
        this.mOnRetryListener = mOnRetryListener;
        return this;
    }

    public LoadingView withOnEmptyListener(OnEmptyListener mOnEmptyListener){
        this.mOnEmptyListener = mOnEmptyListener;
        return this;
    }

    private LoadingState mState;

    public void setState(LoadingState state) {
        this.setVisibility(VISIBLE);
        if (mState == state) {
            return;
        } else if (state == LoadingState.STATE_LOADING) {
            ll_over.setVisibility(GONE);
            ll_loading.setVisibility(VISIBLE);
        } else if (state != LoadingState.STATE_LOADING) {
            ll_loading.setVisibility(GONE);
            ll_over.setVisibility(VISIBLE);
        }
        changeState(state);
    }

    public LoadingView withBtnNoNetEnnable(boolean ennable) {
        btn_nonet_ennable = ennable;
        return this;
    }

    public LoadingView withBtnErrorEnnable(boolean ennable) {
        btn_error_ennable = ennable;
        return this;
    }


    public LoadingView withBtnEmptyEnnable(boolean ennable) {
        btn_empty_ennable = ennable;
        return this;
    }

    public void updateLoadMsg(String msg){
        if(mState == LoadingState.STATE_LOADING){
            tv_loading.setText(msg);
        }
    }

    private void changeState(LoadingState state) {
        switch (state) {
            case STATE_LOADING:
                mState = LoadingState.STATE_LOADING;
                iv_loading.setImageResource(mLoadingIco);
                tv_loading.setText(mLoadingText);
                break;
            case STATE_EMPTY:
                mState = LoadingState.STATE_EMPTY;
                iv_loaded.setImageResource(mEmptyIco);
                tv_loaded.setText(mLoaded_empty_text);
                if (btn_empty_ennable) {
                    btn_loaded.setVisibility(VISIBLE);
                    btn_loaded.setText(btn_empty_text);
                    btn_loaded.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnEmptyListener != null) {
                                setState(LoadingState.STATE_LOADING);
                                mOnEmptyListener.onClick();
                            }
                        }
                    });
                } else {
                    btn_loaded.setVisibility(GONE);
                }
                break;
            case STATE_ERROR:
                mState = LoadingState.STATE_ERROR;
                iv_loaded.setImageResource(mErrorIco);
                tv_loaded.setText(mLoaded_error_text);
                if (btn_error_ennable) {
                    btn_loaded.setVisibility(VISIBLE);
                    btn_loaded.setText(btn_error_text);
                    btn_loaded.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnRetryListener != null) {
                                setState(LoadingState.STATE_LOADING);
                                mOnRetryListener.onRetry();
                            }
                        }
                    });
                } else {
                    btn_loaded.setVisibility(GONE);
                }
                break;
            case STATE_NO_NET:
                mState = LoadingState.STATE_NO_NET;
                iv_loaded.setImageResource(mNoNetIco);
                tv_loaded.setText(mLoaded_not_net_text);
                if (btn_nonet_ennable) {
                    btn_loaded.setVisibility(VISIBLE);
                    btn_loaded.setText(btn_nonet_text);
                    btn_loaded.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnRetryListener != null) {
                                setState(LoadingState.STATE_LOADING);
                                mOnRetryListener.onRetry();
                            }
                        }
                    });
                } else {
                    btn_loaded.setVisibility(GONE);
                }
                break;
        }

    }


    public LoadingView withErrorIco(int resId) {
        mErrorIco = resId;
        return this;
    }

    public LoadingView withLoadedEmptyText(int resId) {
        mLoaded_empty_text = getResources().getString(resId);
        return this;
    }

    public LoadingView withLoadedEmptyText(String mLoadedemptyText) {
        this.mLoaded_empty_text = mLoadedemptyText;
        return this;
    }

    public LoadingView withLoadedNoNetText(int resId) {
        mLoaded_not_net_text = getResources().getString(resId);
        return this;
    }

    public LoadingView withbtnEmptyText(String text) {
        this.btn_empty_text = text;
        return this;
    }

    public LoadingView withbtnErrorText(String text) {
        this.btn_error_text = text;
        return this;
    }

    public LoadingView withbtnNoNetText(String text) {
        this.btn_nonet_text = text;
        return this;
    }


    public LoadingView withLoadedNoNetText(String mLoadedNoNetText) {
        this.mLoaded_not_net_text = mLoadedNoNetText;
        return this;
    }

    public LoadingView withLoadedErrorText(int resId) {
        mLoaded_error_text = getResources().getString(resId);
        return this;
    }

    public LoadingView withLoadedErrorText(String mLoadedErrorText) {
        this.mLoaded_error_text = mLoadedErrorText;
        return this;
    }

    public LoadingView withLoadingText(int resId) {
        mLoadingText = getResources().getString(resId);
        return this;
    }

    public LoadingView withLoadingText(String mLoadingText) {
        this.mLoadingText = mLoadingText;
        return this;
    }


    public void build() {

        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.loading, this);

        ll_over = (LinearLayout) view.findViewById(R.id.ll_over);
        ll_loading = (RelativeLayout) view.findViewById(R.id.ll_loading);
        tv_loaded = (TextView) view.findViewById(R.id.tv_loaded);
        tv_loading=(TextView) view.findViewById(R.id.tv_loading);
        btn_loaded = (Button) view.findViewById(R.id.btn_loaded);
        iv_loading = (ImageView) view.findViewById(R.id.iv_loading);
        iv_loaded = (ImageView) view.findViewById(R.id.iv_loaded);

        if (NetWorkUtils.isNetworkConnected(mContext)) {
            setState(LoadingState.STATE_LOADING);
        } else {
            setState(LoadingState.STATE_NO_NET);
        }

        /**
         * 禁止手指滑动事件被底层view接收
         **/
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }
}
