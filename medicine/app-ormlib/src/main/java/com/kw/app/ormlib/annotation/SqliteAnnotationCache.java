package com.kw.app.ormlib.annotation;

import com.kw.app.ormlib.SqliteBaseDALEx;

import java.util.HashMap;
import java.util.Map;

public class SqliteAnnotationCache {

	private Map<String, SqliteAnnotationTable> tableCache = new HashMap<String, SqliteAnnotationTable>();

	public synchronized SqliteAnnotationTable getTable(String tableName,Class<? extends SqliteBaseDALEx> clazz) {
		SqliteAnnotationTable table = tableCache.get(tableName);
		
		try {
			if(table==null){
				table = new SqliteAnnotationTable(tableName,clazz);
				tableCache.put(table.getTableName(), table);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return table;
	}
	
	public void clear(){
		tableCache.clear();
	}
}
