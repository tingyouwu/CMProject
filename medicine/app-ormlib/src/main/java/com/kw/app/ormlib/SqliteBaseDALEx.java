package com.kw.app.ormlib;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.kw.app.ormlib.annotation.DatabaseField;
import com.kw.app.ormlib.annotation.SqliteAnnotationCache;
import com.kw.app.ormlib.annotation.SqliteAnnotationField;
import com.kw.app.ormlib.annotation.SqliteAnnotationTable;

import org.json.JSONObject;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Decription 数据表对象的基类
 **/
public abstract class SqliteBaseDALEx implements Serializable,Cloneable{
	
	private static final long serialVersionUID = 1L;
	protected String TABLE_NAME = "";
	protected String SQL_CREATETABLE = "";
	protected long indexId;

	public SqliteBaseDALEx() {
		if(TextUtils.isEmpty(TABLE_NAME)){
			TABLE_NAME = createTableName();
		}
	}

	/**
	 * @Decription 创建数据库某一张表表名
	 **/
	protected String createTableName(){
		return this.getClass().getSimpleName();
	}

	/**
	 * @Decription 获取数据表表名
	 * @return 表名
	 **/
	public String getTableName(){
		return TABLE_NAME;
	}

	/**
	 * @Decription 获取当前数据库对象
	 * @return 当前DB
	 **/
	public static BaseSqliteOpenHelper getDB(){
		return OrmModuleManager.getInstance().getSqliteHelper();
	}
	
	/**
	 * @Decription 得到构建表的sql语句
	 * @return 建表语句
	 */
	protected String SqlCreateTable(){
		if(!TextUtils.isEmpty(SQL_CREATETABLE))return SQL_CREATETABLE;
		//遍历带注解的字段
		List<SqliteAnnotationField> safs =  getSqliteAnnotationField();
        List<String> fieldsStr = new ArrayList<String>();
        fieldsStr.add("`id` integer primary key autoincrement");

		for(SqliteAnnotationField saf:safs){
            fieldsStr.add("`"+ saf.getColumnName() +"`" +" "+saf.getType()+(saf.isPrimaryKey()?" COLLATE NOCASE ":""));
		}
        //拼接初始化表的语句
        SQL_CREATETABLE = String.format("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( %s )", TextUtils.join(",",fieldsStr));
		return SQL_CREATETABLE;
		
	}

	/**
	 * @Decription 创建表
	 **/
	public void createTable(){
		BaseSqliteOpenHelper db = getDB();
		if (!db.isTableExits(TABLE_NAME)) {
			if(TextUtils.isEmpty(SQL_CREATETABLE)) SQL_CREATETABLE = SqlCreateTable();
			if(TextUtils.isEmpty(TABLE_NAME)) TABLE_NAME = createTableName();
		 	db.creatTable(SQL_CREATETABLE);
		}
	}

	/**
	 * @Decription 判断表是否没有任何数据
	 **/
	public boolean isTableEmpty(){
		long count = 0;
		Cursor cursor = null;
        try {
			BaseSqliteOpenHelper db = getDB();
            if (db.isTableExits(TABLE_NAME)) {
                cursor = db.find("select count(*) from "+ TABLE_NAME,new String[] {});
                if (cursor != null && cursor.moveToNext()) {
                	count = cursor.getLong(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
				cursor = null;
            }
        }
		return count == 0;
    }
	
	/**
	 * @Decription 获得所有的注解字段，把注解的值取出，并转换成字符串
	 * @return Map-K:String V:String
	 */
	public Map<String,String> getAnnotationFieldValue(){
		Map<String,String> values = new HashMap<String,String>();
		
		for(SqliteAnnotationField saf:getSqliteAnnotationField()){
			Field f = saf.getField();
			try {
				f.setAccessible(true);
				Object value = f.get(this);
				if (value != null)
					values.put(f.getName(), value.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return values;
	}
	
	/**
	 * @Decripton 获得所有的注解字段，把注解的值取出，并转换成Map<String,Object>
	 * @return Map-K:String V:Object
	 */
	public Map<String,Object> getAnnotationFieldObject(){
		Map<String,Object> values = new HashMap<String,Object>();
		
		for(SqliteAnnotationField saf:getSqliteAnnotationField()){
			Field f = saf.getField();
			try {
				f.setAccessible(true);
				Object value = f.get(this);
				if (value != null)
					values.put(f.getName(), value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return values;
	}
	
	/**
	 * @Decription 获得所有的注解字段，把注解的值取出，并转换成JSon键值对
	 * @return JsonObject
	 */
	public JSONObject getJsonAnnotationFieldValue(){
		JSONObject jb = new JSONObject();
		for(SqliteAnnotationField saf:getSqliteAnnotationField()){
			Field f = saf.getField();
			try {
				f.setAccessible(true);
				Object value = f.get(this);
				if (value != null)
					jb.put(f.getName(), value.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return jb;
	}
	
	/**
	 * @Decription 把map中的值填充到字段中
	 * @param values 入参 Map
	 */
	public void setAnnotationField(Map<String,String> values){
		
		for(SqliteAnnotationField af:getSqliteAnnotationField()){
			try {
				Field f = af.getField();
				DatabaseField.FieldType type = af.getType();

				f.setAccessible(true);
				if (!values.containsKey(f.getName())) {
					continue;
				}
				String value = values.get(f.getName());
				if (value == null)
					value = "";

				if (type == DatabaseField.FieldType.INT) {
					if (value.equals("")) {
						f.set(this, 0);
					} else {
						if(af.isLongType()){
							f.set(this, Long.valueOf(value));
						}else{
							f.set(this, Integer.valueOf(value));
						}
					}
				} else if (type == DatabaseField.FieldType.VARCHAR) {
					f.set(this, value);
				} else if (type == DatabaseField.FieldType.REAL) {
					if (value.equals("")) {
						f.set(this, 0);
					} else {
						f.set(this, Float.valueOf(value));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @Decription 设置对象完毕后可以加入一些额外操作
	 **/
    protected void onSetCursorValueComplete(Cursor cursor){}

    /**
     * @Decription 通过游标赋值
     */
	public final void setAnnotationField(Cursor cursor){
		setAnnotationField(cursor,null);
	}

    /**
     * @Decription 通过游标赋值
	 * @param cursor 游标
	 * @param cursorIndex 索引(查询出多条数据的时候建议使用getCursorIndex方法转换,尽量不要为null)
     */
    private final void setAnnotationField(Cursor cursor,Map<String, Integer> cursorIndex){
    	try {
			SqliteAnnotationCache cache = OrmModuleManager.getInstance().getSqliteAnnotationCache();
    		SqliteAnnotationTable table = cache.getTable(TABLE_NAME, this.getClass());

			if(cursorIndex == null){
				cursorIndex = table.getCursorIndex(cursor);
			}

			// 当前对象的索引id
            int indexId_InCursor = cursorIndex.get("id");

            if(indexId_InCursor!= -1 ){
                this.indexId = cursor.getLong(indexId_InCursor);
            }

			for (Map.Entry<String, Integer> entry : cursorIndex.entrySet()) {
				if(entry.getKey().equals("id"))continue;
				SqliteAnnotationField saf = table.getField(entry.getKey());
				DatabaseField.FieldType t = saf.getType();
				Field f = saf.getField();
				f.setAccessible(true);
				try {
					if (t == DatabaseField.FieldType.VARCHAR) {
						f.set(this, cursor.getString(entry.getValue()));
					} else if (t == DatabaseField.FieldType.INT) {
						if(saf.isLongType()){
							f.set(this, cursor.getLong(entry.getValue()));
						}else{
							f.set(this, cursor.getInt(entry.getValue()));
						}
					} else if (t == DatabaseField.FieldType.REAL) {
						f.set(this, cursor.getFloat(entry.getValue()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			onSetCursorValueComplete(cursor);

	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
    }

    /**
     * @Decription 把用户对象的属性转换成键值对
     */
    protected ContentValues tranform2Values() {
		
        ContentValues values = new ContentValues();
		for(SqliteAnnotationField saf:getSqliteAnnotationField()){
			try {
				Field f = saf.getField();
				f.setAccessible(true);
				Object v = f.get(this);
				if (v != null){
					String value = v.toString();
					values.put(saf.getColumnName(), value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
        return values;
    }

	/**
	 * @Decription 判断是否存在某个主键id记录
	 **/
    public boolean isExist(String id){
    	return isExist(getPrimaryKey(), id);
    }

	/**
	 *  @Decription 判断是否存在某个主键id记录
	 **/
	protected boolean isExist(String key,String id){
    	 boolean result = false;
    	 Cursor cursor = null;
         try {
			 BaseSqliteOpenHelper db = getDB();
			 if (db.isTableExits(TABLE_NAME)) {
                 cursor = db.find("select "+ key +" from "+ TABLE_NAME +" where "+key +" =? ",new String[] {id});
                 if (cursor != null && cursor.moveToNext()) {
                    result = true;
                 }
             }
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             if (cursor != null && !cursor.isClosed()) {
                 cursor.close();
             }
         }
         return result;
    }

	/**
	 * @Decription 获取所有注解字段
	 */
	public List<SqliteAnnotationField> getSqliteAnnotationField(){
		SqliteAnnotationCache cache = OrmModuleManager.getInstance().getSqliteAnnotationCache();
		SqliteAnnotationTable table = cache.getTable(TABLE_NAME,this.getClass());
		return table.getFields();
	}

	/**
	 * @Decription 获取表主键
	 */
	public String getPrimaryKey(){
		SqliteAnnotationCache cache = OrmModuleManager.getInstance().getSqliteAnnotationCache();
		SqliteAnnotationTable table = cache.getTable(TABLE_NAME,this.getClass());
		return table.getPrimaryKey();
	}

	/**
	 * @Decription 根据列名找到对应的字段
	 */
	public SqliteAnnotationField getSqliteAnnotationField(String columnname){
		SqliteAnnotationCache cache = OrmModuleManager.getInstance().getSqliteAnnotationCache();
		SqliteAnnotationTable table = cache.getTable(TABLE_NAME,this.getClass());
		return table.getField(columnname);
	}
	
	/**
	 * @Decription 通过事务操作，效率高
	 */
	private boolean operatorWithTransaction(OnTransactionListener listener){
		BaseSqliteOpenHelper db = null;
        try {
            db = getDB();
            if(db.getConnection().isOpen()){
                db.getConnection().beginTransaction();
                return listener.onTransaction(db);
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null && db.getConnection().isOpen()) {
                db.getConnection().setTransactionSuccessful();
                db.getConnection().endTransaction();
            }
        }
	}

	/**
	 * @Decription 获取索引id
	 **/
    public long getIndexId() {
        return indexId;
    }

	/**
	 * @Decription 创建一个对象
	 * @return
	 */
	private SqliteBaseDALEx newDALExInstance(){
		try {
			return this.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} 
	}

	/**
	 * @Decription 获取当前对象的主键id值
	 * @return 主键id
	 */
	public String getPrimaryId(){
		String result = "";
		String key = getPrimaryKey();
		if(TextUtils.isEmpty(key))return null;
		SqliteAnnotationCache cache = OrmModuleManager.getInstance().getSqliteAnnotationCache();
		SqliteAnnotationTable table = cache.getTable(TABLE_NAME,this.getClass());
		SqliteAnnotationField field = table.getField(key);
		
		Field f = field.getField();
		f.setAccessible(true);
		try {
			result = (String) f.get(this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @Decription 设置主键ID的值
	 * @param id 主键id
	 **/
	public void setPrimaryId(String id){
		String key = getPrimaryKey();
		if(TextUtils.isEmpty(key))return;
		SqliteAnnotationCache cache = OrmModuleManager.getInstance().getSqliteAnnotationCache();
		SqliteAnnotationTable table = cache.getTable(TABLE_NAME,this.getClass());
		SqliteAnnotationField field = table.getField(key);

		//主键字段
		Field f = field.getField();
		f.setAccessible(true);
		try {
			f.set(this, id);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Decription 根据主键id 搜索出一个对象
	 * @param id 主键id
	 **/
	public <T extends SqliteBaseDALEx> T findById(String id){
		return (T)findById(id, null);
	}

	/**
	 * @Decription 根据主键id 搜索出一个对象
	 * @param id 主键id
	 * @param listener 回调
	 **/
	public <T extends SqliteBaseDALEx> T findById(String id,OnQueryListener listener){
		String sql = "select * from "+TABLE_NAME+" where "+getPrimaryKey()+" = ?";
		return findOne(sql,new String[]{id}, listener);
	}

	/**
	 * @Decription 根据主键id 搜索出一个对象
	 * @param sql sql语句
	 * @param listener 回调
	 **/
	public <T extends SqliteBaseDALEx> T findOne(String sql, OnQueryListener listener){
		return findOne(sql, new String[]{}, listener);
	}

	/**
	 * @Decription 根据主键id 搜索出一个对象
	 * @param sql sql语句
	 * @param params 语句中的参数
	 **/
	public <T extends SqliteBaseDALEx> T findOne(String sql, String[] params){
		return findOne(sql, params, null);
	}

	/**
	 * @Decription  根据sql语句查找一个对象
	 * @param sql sql语句
	 * @param params 语句中的参数
	 * @param listener 查找完之后回调
	 **/
	public <T extends SqliteBaseDALEx> T findOne(String sql, String[] params, OnQueryListener listener){
        SqliteBaseDALEx dalex;
        Cursor cursor = null;
        try {
			BaseSqliteOpenHelper db = getDB();
            if (db.isTableExits(TABLE_NAME)) {
                cursor = db.find(sql,params);
                if (cursor != null && cursor.moveToNext()) {
                    dalex = newDALExInstance();
                    dalex.setAnnotationField(cursor);
                    if(listener!=null)listener.onResult(cursor,dalex);
                    return (T)dalex;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(listener!=null)listener.onException(e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        return null;
    }

	/**
	 * @Decription 搜索出当前表的所有记录（全部列）
	 **/
	public  <T extends SqliteBaseDALEx> List<T> findAll(){
		return findList("select * from "+TABLE_NAME, new String[]{});
	}

	/**
	 * @Decription 搜索出当前表的所有记录（全部列）
	 **/
	public  <T extends SqliteBaseDALEx> List<T> findList(String sql){
		return findList(sql, new String[]{});
	}

	/**
	 * @Decription 根据sql语句 搜索出列表
	 * @param sql sql语句
	 * @param params 参数
	 **/
	public  <T extends SqliteBaseDALEx> List<T> findList(String sql,String[] params){
		return findList(sql, params, null);
	}

	/**
	 * @Decription 根据sql语句 搜索出列表
	 * @param sql sql语句
	 * @param params 参数
	 * @param listener 回调
	 **/
	public  <T extends SqliteBaseDALEx> List<T> findList(String sql,String[] params,OnQueryListener listener){
		List<SqliteBaseDALEx> list = new ArrayList<SqliteBaseDALEx>();
		Cursor cursor = null;
		SqliteBaseDALEx baseDalex = null;
        try {
			BaseSqliteOpenHelper db = getDB();
            if (db.isTableExits(TABLE_NAME)) {

                cursor = db.find(sql,params);
        		Map<String, Integer> cursorIndex = getCursorIndex(cursor);
                while (cursor != null && cursor.moveToNext()) {
                	if(baseDalex==null){
                		baseDalex = newDALExInstance();
                	}
                    SqliteBaseDALEx dalex = (SqliteBaseDALEx) baseDalex.clone();
                	dalex.setAnnotationField(cursor,cursorIndex);

                	list.add(dalex);
					if(listener!=null)listener.onResult(cursor,null);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            if(listener!=null)listener.onException(e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
		
		return (List<T>)list;
	}

	/**
	 * @Decription 获取当前cursor 列对应的索引
	 **/
	protected final Map<String, Integer> getCursorIndex(Cursor cursor){
		SqliteAnnotationCache cache = OrmModuleManager.getInstance().getSqliteAnnotationCache();
		SqliteAnnotationTable table = cache.getTable(TABLE_NAME,this.getClass());
		return table.getCursorIndex(cursor);
	}

	/**
	 * @Decription 根据主键Id删除掉一个记录
	 * @param id 主键id
	 **/
	public void deleteById(String id){
		BaseSqliteOpenHelper db;
    	try {
			db = getDB();
			db.delete(TABLE_NAME, getPrimaryKey() +"=?", new String[]{id});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * @Decription 保存或者更新列表
	 * @param dalex 对象列表
	 */
    public <T extends SqliteBaseDALEx> void saveOrUpdate(final T[] dalex){
		List<T> list = new ArrayList<T>();
		for(T item:dalex){
			list.add(item);
		}
		saveOrUpdate(list);
    }

	/**
	 * @Decription 保存或者更新列表
	 * @param dalex 对象列表
	 **/
	public <T extends SqliteBaseDALEx> void saveOrUpdate(final List<T> dalex){
		createTable();
		operatorWithTransaction(new OnTransactionListener() {

			@Override
			public boolean onTransaction(BaseSqliteOpenHelper db) {
				for(T model:dalex){
					String id = model.getPrimaryId();
					if(TextUtils.isEmpty(id))continue;
					ContentValues values = model.tranform2Values();
					if (!TextUtils.isEmpty(id) && isExist(id)) {
						db.update(TABLE_NAME, values, getPrimaryKey() + "=?", new String[]{id});
					} else {
						db.save(TABLE_NAME, values);
					}
				}
				return true;
			}
		});
	}

	/**
	 * @Decription 保存或者刷新单个对象
	 **/
	public void saveOrUpdate(){
		BaseSqliteOpenHelper db = getDB();
        String id = getPrimaryId();
        if(TextUtils.isEmpty(id))return;
        createTable();
        ContentValues values = tranform2Values();
        if(!TextUtils.isEmpty(id) && isExist(id)){
            db.update(TABLE_NAME, values, getPrimaryKey()+"=?", new String[]{id});
        }else{
            db.save(TABLE_NAME, values);
        }

	}


    /**
	 * @Decription 根据sql语句来计算数目
	 * @param sql sql语句
	 * @param params 参数
	 **/
    public int count(String sql,String[] params){
        int result = 0;
        Cursor cursor = null;
        try {
			BaseSqliteOpenHelper db = getDB();
            if (db.isTableExits(TABLE_NAME)) {
                cursor = db.find(sql,params);
                if (cursor != null && cursor.moveToNext()) {
                    result = cursor.getInt(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return result;
    }

	protected interface OnTransactionListener{
		boolean onTransaction(BaseSqliteOpenHelper db);
	}

	protected interface OnQueryListener{
		<T extends SqliteBaseDALEx> void onResult(Cursor cursor, T t);
		void onException(Exception e);
	}

}