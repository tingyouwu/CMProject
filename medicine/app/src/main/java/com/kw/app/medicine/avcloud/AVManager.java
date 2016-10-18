package com.kw.app.medicine.avcloud;

import android.content.Context;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.kw.app.avcloudlib.AVCloudModuleManager;
import com.kw.app.medicine.data.avcloud.FriendAVCloud;
import com.kw.app.medicine.data.avcloud.UserAVCloud;

/**
 * @author :wty
 * 做一些初始化工作管理
 */
public class AVManager{
    public static void init(Context applicationContext){

        /**
         *要实现子类化，需要下面几个步骤：
         * 首先声明一个子类继承自 AVObject；
         * 添加 @AVClassName 注解。它的值必须是一个字符串，也就是你过去传入 AVObject 构造函数的类名。这样以来，后续就不需要再在代码中出现这个字符串类名；
         * 确保你的子类有一个 public 的默认（参数个数为 0）的构造函数。切记不要在构造函数里修改任何 AVObject 的字段；
         * 在你的应用初始化的地方，在调用 AVOSCloud.initialize() 之前注册子类 AVObject.registerSubclass(YourClass.class)。
         **/

        AVUser.alwaysUseSubUserClass(UserAVCloud.class);
        AVObject.registerSubclass(FriendAVCloud.class);
        AVCloudModuleManager.init(applicationContext);
    }
}
