package com.kw.app.ormlib;

import android.content.Context;

import com.kw.app.commonlib.utils.CommonUtil;
import com.kw.app.ormlib.annotation.SqliteAnnotationCache;
import com.kw.app.ormlib.annotation.SqliteDao;

import java.util.HashMap;
import java.util.Map;

/**
 * @Decription 管理数据库对象以及缓存对象(改善由于反射造成的性能)
 * @author 吴廷优 on 2016/09/01
 **/
public class OrmModuleManager {

	private static volatile OrmModuleManager sInstance = null;
	private Context context;
	private SqliteAnnotationCache sqliteAnnotationCache;
	private Map<String,BaseSqliteOpenHelper> localDBMap = new HashMap<String,BaseSqliteOpenHelper>();
	private String xtion_dbname = "mydb.db"; //默认名字

	private OrmModuleManager(Context context){
		this.context = context;
	}

	/**
	 * @Decription 设置当前数据库名字(当前需要操作哪个数据库)
	 * @param dbName 数据库名字
	 **/
	public void setCurrentDBName(String dbName){
		this.xtion_dbname = dbName + ".db";
	}
	/**
	 * @Decription 初始化Orm缓存（必须在Application里面调用）
	 * @param context  ApplicationContext
	 **/
	public static void init(Context context){
		if (sInstance == null) {
			synchronized (OrmModuleManager.class) {
				if (sInstance == null) {
					sInstance = new OrmModuleManager(context);
				}
			}
		}
	}

	public static OrmModuleManager getInstance() {
		return sInstance;
	}

	/**
	 * @Decription 根据数据库名字来获取数据库,如果不存在就创建
	 * 				 并且数据库会切换到当前名字
	 **/
	public BaseSqliteOpenHelper getSqliteHelperByDBName(String dbname){
		this.xtion_dbname = dbname;
		return getSqliteHelper();
	}

	/**
	 * @Decription 关闭所有数据库(退出应用必须调用)
	 **/
	public void closeDB(){
		try {
			for(BaseSqliteOpenHelper db:localDBMap.values()){
				db.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		clearCache();
	}

	/**
	 * 功能描述：退出应用必须要调用该方法，用于清除所有缓存对象
	 **/
	private void clearCache(){
		if(sqliteAnnotationCache != null)sqliteAnnotationCache.clear();//清掉数据库缓存对象
		SqliteDao.clear();
	}

	SqliteAnnotationCache getSqliteAnnotationCache() {
		if(sqliteAnnotationCache == null){
			synchronized (this){
				if(sqliteAnnotationCache == null){
					sqliteAnnotationCache = new SqliteAnnotationCache();
				}
			}
		}
		return sqliteAnnotationCache;
	}

	BaseSqliteOpenHelper getSqliteHelper(){
		BaseSqliteOpenHelper db = localDBMap.get(xtion_dbname);
		if(db == null){
			synchronized (OrmModuleManager.class){
				if(db == null){
					db = new BaseSqliteOpenHelper(context, xtion_dbname, CommonUtil.getDbVersion(context));
					localDBMap.put(xtion_dbname, db);
				}
			}
		}
		return db;
	}
}
