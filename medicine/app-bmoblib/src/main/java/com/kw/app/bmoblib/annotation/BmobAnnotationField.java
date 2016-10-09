package com.kw.app.bmoblib.annotation;

import android.text.TextUtils;

import com.kw.app.ormlib.annotation.DatabaseField;

import java.lang.reflect.Field;

public class BmobAnnotationField {
	/** 对象字段 */
	private Field field;

	/** 字段的注解 */
	private DatabaseField annotation;

	/** 字段注解中声明的类型 */
	private DatabaseField.FieldType type;

	/** 字段是否为表的主键 */
	private boolean primaryKey;

	/** 字段在数据库表中的列名 */
	private String columnName;

	public BmobAnnotationField(Field field, DatabaseField annotation) {
		this.annotation = annotation;
		this.field = field;
		this.type = annotation.Type();
		
		this.columnName = annotation.fieldName();
		if(TextUtils.isEmpty(this.columnName)){
			this.columnName = field.getName();
		}
		this.primaryKey = annotation.primaryKey();
	}
	
	public DatabaseField.FieldType getType() {
		return type;
	}

	public Field getField() {
		return field;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public DatabaseField getAnnotation() {
		return annotation;
	}
	
	/**
	 * @return 获取该字段在表中的列名
	 */
	public String getColumnName(){
		return columnName;
	}
	
}