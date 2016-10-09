package com.kw.app.bmoblib.annotation;

import android.database.Cursor;
import android.text.TextUtils;

import com.kw.app.ormlib.annotation.DatabaseField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobObject;

public class BmobAnnotationTable {

	private String tableName;
	private Class<? extends BmobObject> clazz;
	private List<BmobAnnotationField> fields;
	private Map<String,BmobAnnotationField> fieldMaps;

	private String primaryKey;

	public BmobAnnotationTable(String tableName, Class<? extends BmobObject> clazz) {
		this.tableName = tableName;
		this.clazz = clazz;
	}

	public String getTableName(){
		return tableName;
	}

	public BmobAnnotationField getField(String name){
		if(fieldMaps==null)fieldMaps = new HashMap<String,BmobAnnotationField>();
		return fieldMaps.get(name);
	}

	public synchronized List<BmobAnnotationField> getFields(){

		if(fields==null){
			if(fieldMaps==null)fieldMaps = new HashMap<String,BmobAnnotationField>();
			fields = new ArrayList<BmobAnnotationField>();

			for(Field f:clazz.getDeclaredFields()){
				DatabaseField dbf = f.getAnnotation(DatabaseField.class);
				if(dbf!=null){
					BmobAnnotationField baf = new BmobAnnotationField(f,dbf);
					fields.add(baf);
					fieldMaps.put(baf.getColumnName(),baf);
					if(baf.isPrimaryKey())this.primaryKey = baf.getColumnName();
				}
			}
		}
		return fields;
	}
	
	public synchronized Map<String,Integer> getCursorIndex(Cursor cursor){
		
		Map<String,Integer> index = new HashMap<String,Integer>();
		for (int i = 0; i < cursor.getColumnCount(); i++) {
			String name = cursor.getColumnName(i);
			index.put(name, i);
		}
		return index;
	}

	public String getPrimaryKey() {
		if(TextUtils.isEmpty(primaryKey)){
			getFields();
		}
		return primaryKey;
	}
	
}