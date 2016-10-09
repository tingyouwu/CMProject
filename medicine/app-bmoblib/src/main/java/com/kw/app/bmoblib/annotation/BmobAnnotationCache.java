package com.kw.app.bmoblib.annotation;

import java.util.HashMap;
import java.util.Map;

import cn.bmob.v3.BmobObject;

public class BmobAnnotationCache {

	private Map<String, BmobAnnotationTable> tableCache = new HashMap<String, BmobAnnotationTable>();

	public synchronized BmobAnnotationTable getTable(String tableName,Class<? extends BmobObject> clazz) {
		BmobAnnotationTable table = tableCache.get(tableName);
		
		try {
			if(table==null){
				table = new BmobAnnotationTable(tableName,clazz);
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
