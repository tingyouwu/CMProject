package com.kw.app.commonlib.utils;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;

/**
 * @Description:Java汉字转换为拼音
 */
public class HanziToPinyinUtil {

	/**
	 * @Decription 获取汉字的首字母
	 **/
	public static String getShortPinyin(String src){
		String result = "";
		try {
			result = PinyinHelper.getShortPinyin(src);
		} catch (PinyinException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @Decription 获取汉字的首字母
	 **/
	public static String getPinyin(String src){
		String result = "";
		try {
			result = PinyinHelper.convertToPinyinString(src,"");
		} catch (PinyinException e) {
			e.printStackTrace();
		}
		return result;
	}

}
