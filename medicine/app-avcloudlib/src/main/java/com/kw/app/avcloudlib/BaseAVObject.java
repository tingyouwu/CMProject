package com.kw.app.avcloudlib;

import android.os.Parcel;

import com.avos.avoscloud.AVObject;
import com.kw.app.avcloudlib.annotation.AVAnnotationCache;
import com.kw.app.avcloudlib.annotation.AVAnnotationField;
import com.kw.app.avcloudlib.annotation.AVAnnotationTable;
import com.kw.app.ormlib.SqliteBaseDALEx;
import com.kw.app.ormlib.annotation.DatabaseField;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述：所有AVObject对象的基类
 * 注：本层只提供方法，不能定义任何成员变量
 **/
public abstract class BaseAVObject extends AVObject {

    //此处为我们的默认实现，当然你也可以自行实现
    public static final Creator CREATOR = AVObjectCreator.instance;

    public BaseAVObject(){
        super();
    }

    public BaseAVObject(Parcel in){
        super(in);
    }

    @Override
    public void put(String key, Object value) {
        super.put(key, value);
    }

    /**
     * 获取当前所有注解字段
     */
    public List<AVAnnotationField> getAVAnnotationField(){
        AVAnnotationCache cache = AVCloudModuleManager.getInstance().getAVAnnotationCache();
        AVAnnotationTable table = cache.getTable(this.getClass().getSimpleName(),this.getClass());
        return table.getFields();
    }

    /**
     * 把注解的值取出，并转换成字符串
     * @return Map 字段名 字段数值
     */
    public Map<String,String> getAnnotationFieldValue(){
        Map<String,String> values = new HashMap<String,String>();
        for(AVAnnotationField baf:getAVAnnotationField()){
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
        for(AVAnnotationField baf:getAVAnnotationField()){
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
        for(AVAnnotationField baf:getAVAnnotationField()){
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

        for(AVAnnotationField baf:getAVAnnotationField()){
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
                        if(baf.isLongType()){
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

}