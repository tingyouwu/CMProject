package com.kw.app.commonlib.utils;

import com.orhanobut.logger.Logger;

/**
 * @Decription log工具类
 * @author wty
 */
public class AppLogUtil {

    /**
     * initialize the logger.
     */
    public static void init(String Tag) {
        Logger.init(Tag);
    }

    /**
     * log.i
     * @param msg
     */
    public static void i(String msg) {
            Logger.i(msg);
    }

    /**
     * log.d
     * @param msg
     */
    public static void d(String msg) {
            Logger.d(msg);
    }

    /**
     * log.w
     * @param msg
     */
    public static void w(String msg) {
            Logger.w(msg);
    }

    /**
     * log.e
     * @param msg
     */
    public static void e(String msg) {
        Logger.e(msg);
    }

    public static void e(Throwable e) {
        Logger.e(e, "");
    }
}
