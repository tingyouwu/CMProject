package com.kw.app.avcloudlib.annotation;

import android.database.Cursor;
import android.text.TextUtils;

import com.avos.avoscloud.AVObject;
import com.kw.app.ormlib.annotation.DatabaseField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AVAnnotationTable {

	private String tableName;
	private Class<? extends AVObject> clazz;
	private List<AVAnnotationField> fields;
	private Map<String,AVAnnotationField> fieldMaps;

	private String primaryKey;

	public AVAnnotationTable(String tableName, Class<? extends AVObject> clazz) {
		this.tableName = tableName;
		this.clazz = clazz;
	}

	public String getTableName(){
		return tableName;
	}

	public AVAnnotationField getField(String name){
		if(fieldMaps==null)fieldMaps = new HashMap<String,AVAnnotationField>();
		return fieldMaps.get(name);
	}

	public synchronized List<AVAnnotationField> getFields(){

		if(fields==null){
			if(fieldMaps==null)fieldMaps = new HashMap<String,AVAnnotationField>();
			fields = new ArrayList<AVAnnotationField>();

			for(Field f:clazz.getDeclaredFields()){
				DatabaseField dbf = f.getAnnotation(DatabaseField.class);
				if(dbf!=null){
					AVAnnotationField baf = new AVAnnotationField(f,dbf);
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