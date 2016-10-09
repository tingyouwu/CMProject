package com.kw.app.bmoblib;

import android.content.Context;

import com.kw.app.bmoblib.annotation.BmobAnnotationCache;
import com.kw.app.bmoblib.annotation.BmobObjectDao;

import cn.bmob.v3.Bmob;

/**
 * @Decription 管理数据库对象以及缓存对象(改善由于反射造成的性能)
 * @author 吴廷优 on 2016/09/01
 **/
public class BmobModuleManager {

	public static final String Bmob_ApplicationId = "a0cdfdcda61923319f69dd6ee0a409c4";
	private static volatile BmobModuleManager sInstance = null;

	/** Bmob对象中字段 缓存，注入时用到 */
	private BmobAnnotationCache bmobAnnotationCache;

	public static BmobModuleManager getInstance() {
		if (sInstance == null) {
			synchronized (BmobModuleManager.class) {
				if (sInstance == null) {
					sInstance = new BmobModuleManager();
				}
			}
		}
		return sInstance;
	}

	public static void init(Context applicationContext){
		Bmob.initialize(applicationContext,Bmob_ApplicationId);
	}

	/**
	 * 功能描述：退出应用调用该方法，用于清除所有缓存对象
	 **/
	public void clearCache(){
		if(bmobAnnotationCache != null)bmobAnnotationCache.clear();//清除掉bmob缓存对象
		BmobObjectDao.clear();
	}

	public BmobAnnotationCache getBmobAnnotationCache() {
		if(bmobAnnotationCache == null)
			synchronized (this){
				if(bmobAnnotationCache == null){
					bmobAnnotationCache = new BmobAnnotationCache();
				}
			}
		return bmobAnnotationCache;
	}

}
