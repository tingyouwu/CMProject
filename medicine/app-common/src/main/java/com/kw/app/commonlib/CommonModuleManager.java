package com.kw.app.commonlib;

import android.content.Context;

import com.kw.app.commonlib.base.AppConstant;
import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.commonlib.utils.CommonUtil;
import com.kw.app.commonlib.utils.PreferenceUtil;

/**
 * common模块管理器
 */
public class CommonModuleManager {

    public static void init(Context applicationContext){
        PreferenceUtil.init(applicationContext);
        AppLogUtil.init(CommonUtil.getApplicationName(applicationContext));
        AppConstant.init(applicationContext);
    }

}
