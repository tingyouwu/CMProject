package com.kw.app.medicine.base;

import com.kw.app.medicine.avcloud.AVFileManager;
import com.kw.app.medicine.avcloud.AVFriendManager;
import com.kw.app.medicine.avcloud.AVUserManager;

/**
 * @author :wty
 * 管理第三方模块
 */
public class CloudManager {

    private IFriendManager friendManager;
    private IFileManager fileManager;
    private IUserManager userManager;

    private static volatile CloudManager sInstance = new CloudManager();
    public static CloudManager getInstance(){
        return sInstance;
    }

    private CloudManager(){
        friendManager = AVFriendManager.getInstance();
        fileManager = AVFileManager.getInstance();
        userManager = AVUserManager.getInstance();
    }

    public IFriendManager getFriendManager() {
        return friendManager;
    }

    public void setFriendManager(IFriendManager friendManager) {
        this.friendManager = friendManager;
    }

    public IFileManager getFileManager() {
        return fileManager;
    }

    public void setFileManager(IFileManager fileManager) {
        this.fileManager = fileManager;
    }

    public IUserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }
}
