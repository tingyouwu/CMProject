package com.kw.app.bmoblib;

import com.kw.app.bmoblib.annotation.BmobAnnotationCache;
import com.kw.app.bmoblib.annotation.BmobObjectDao;

/**
 * @Decription 管理数据库对象以及缓存对象(改善由于反射造成的性能)
 * @author 吴廷优 on 2016/09/01
 **/
public class BmobManager {

	private static volatile BmobManager sInstance = null;

	/** Bmob对象中字段 缓存，注入时用到 */
	private BmobAnnotationCache bmobAnnotationCache;

	public static BmobManager getInstance() {
		if (sInstance == null) {
			synchronized (BmobManager.class) {
				if (sInstance == null) {
					sInstance = new BmobManager();
				}
			}
		}
		return sInstance;
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
