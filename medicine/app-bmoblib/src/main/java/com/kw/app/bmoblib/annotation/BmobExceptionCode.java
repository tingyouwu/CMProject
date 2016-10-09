package com.kw.app.bmoblib.annotation;

/**
 * @Decription bmob错误码以及对应的提示信息
 **/
public enum  BmobExceptionCode{

	UsernameOrPswError(101,"登录用户名或密码不正确"),
	UsernameTaken(202,"该昵称已经注册过"),
	EmailTake(203,"该邮箱已经注册过"),
	EmailNotExist(205,"没有找到此邮件的用户"),
	PhoneTaken(209,"该手机号码已经存在"),
	PswInCorrect(210,"旧密码不正确"),
	EmailError(301,"邮箱格式不正确"),

	UpLoadFileError(9003,"上传文件出错"),
	UpLoadFileFailure(9004,"上传文件失败"),
	FileSize10M(9007,"文件大小超过10M"),
	FileNotExist(9008,"上传文件不存在"),
	TimeOut(9010,"网络超时"),
	DataNull(9018,"输入数据为空");

	public int code;
	public String msg;

	BmobExceptionCode(int code,String msg){
		this.code = code;
		this.msg = msg;
	}

	public static String match(int code){
		for(BmobExceptionCode item:values()){
			if(code == item.code)
				return item.msg;
		}
		return "其他错误";
	}

}
