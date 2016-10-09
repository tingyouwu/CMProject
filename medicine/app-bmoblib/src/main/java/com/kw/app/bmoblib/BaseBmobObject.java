package com.kw.app.bmoblib;

import com.kw.app.bmoblib.annotation.BmobAnnotationCache;
import com.kw.app.bmoblib.annotation.BmobAnnotationField;
import com.kw.app.bmoblib.annotation.BmobAnnotationTable;
import com.kw.app.ormlib.SqliteBaseDALEx;
import com.kw.app.ormlib.annotation.DatabaseField;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobObject;

/**
 * 功能描述：所有Bmob对象的基类
 * 注：本层只提供方法，不能定义任何成员变量
 **/
public abstract class BaseBmobObject extends BmobObject {

    public BaseBmobObject(){
        //由于限制  类名不能超过20个字
        setTableName(this.getClass().getSimpleName());
    }

    /**
     * 获取当前所有注解字段
     */
    public List<BmobAnnotationField> getBmobAnnotationField(){
        BmobAnnotationCache cache = BmobManager.getInstance().getBmobAnnotationCache();
        BmobAnnotationTable table = cache.getTable(getTableName(),this.getClass());
        return table.getFields();
    }

    /**
     * 把注解的值取出，并转换成字符串
     * @return Map 字段名 字段数值
     */
    public Map<String,String> getAnnotationFieldValue(){
        Map<String,String> values = new HashMap<String,String>();
        for(BmobAnnotationField baf:getBmobAnnotationField()){
            Field f = baf.getField();
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
     * 获取对象中所有注解的值
     * @return Map 字段名  对象
     */
    public Map<String,Object> getAnnotationFieldObject(){
        Map<String,Object> values = new HashMap<String,Object>();
        for(BmobAnnotationField baf:getBmobAnnotationField()){
            Field f = baf.getField();
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
     * 获取注解值并转换成JSON对象
     */
    public JSONObject getJsonAnnotationFieldValue(){
        JSONObject jb = new JSONObject();
        for(BmobAnnotationField baf:getBmobAnnotationField()){
            Field f = baf.getField();
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
     * 把map中的值填充到注解字段中
     */
    public void setAnnotationField(SqliteBaseDALEx dalex){
        setAnnotationField(dalex.getAnnotationFieldValue());
    }

    /**
     * 把map中的值填充到注解字段中
     */
    protected void setAnnotationField(Map<String,String> values){

        for(BmobAnnotationField baf:getBmobAnnotationField()){
            try {
                Field f = baf.getField();
                DatabaseField.FieldType type = baf.getType();

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
                        f.set(this, Integer.valueOf(value));
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
}