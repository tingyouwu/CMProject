package com.kw.app.avcloudlib.annotation;

import com.avos.avoscloud.AVObject;

import java.util.HashMap;
import java.util.Map;


public class AVAnnotationCache {

	private Map<String, AVAnnotationTable> tableCache = new HashMap<String, AVAnnotationTable>();

	public synchronized AVAnnotationTable getTable(String tableName,Class<? extends AVObject> clazz) {
		AVAnnotationTable table = tableCache.get(tableName);
		
		try {
			if(table==null){
				table = new AVAnnotationTable(tableName,clazz);
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
