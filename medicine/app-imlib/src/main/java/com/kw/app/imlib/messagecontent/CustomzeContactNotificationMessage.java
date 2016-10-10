package com.kw.app.imlib.messagecontent;


import android.os.Parcel;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.common.RLog;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

/**
 * @author wty
 * 自定义  通知消息
 **/

@MessageTag(
		value = "app:customContactNtf",
		flag = MessageTag.NONE
		//发送的自定义消息不会在会话页面和会话列表中展示。
)
public class CustomzeContactNotificationMessage extends MessageContent {
	private static final String TAG = "CustomzeContactNotificationMessage";
	public static final String CONTACT_OPERATION_REQUEST = "Request";
	public static final String CONTACT_OPERATION_ACCEPT_RESPONSE = "AcceptResponse";
	public static final String CONTACT_OPERATION_REJECT_RESPONSE = "RejectResponse";
	private String operation;
	private String sourceUserId;
	private String targetUserId;
	private String message;
	private String extra;
	public static final Creator<CustomzeContactNotificationMessage> CREATOR = new Creator() {
		public CustomzeContactNotificationMessage createFromParcel(Parcel source) {
			return new CustomzeContactNotificationMessage(source);
		}

		public CustomzeContactNotificationMessage[] newArray(int size) {
			return new CustomzeContactNotificationMessage[size];
		}
	};

	public String getOperation() {
		return this.operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getSourceUserId() {
		return this.sourceUserId;
	}

	public void setSourceUserId(String sourceUserId) {
		this.sourceUserId = sourceUserId;
	}

	public String getTargetUserId() {
		return this.targetUserId;
	}

	public void setTargetUserId(String targetUserId) {
		this.targetUserId = targetUserId;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getExtra() {
		return this.extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public CustomzeContactNotificationMessage(Parcel in) {
		this.operation = ParcelUtils.readFromParcel(in);
		this.sourceUserId = ParcelUtils.readFromParcel(in);
		this.targetUserId = ParcelUtils.readFromParcel(in);
		this.message = ParcelUtils.readFromParcel(in);
		this.extra = ParcelUtils.readFromParcel(in);
		this.setUserInfo((UserInfo) ParcelUtils.readFromParcel(in, UserInfo.class));
	}

	public static CustomzeContactNotificationMessage obtain(String operation, String sourceUserId, String targetUserId, String message) {
		CustomzeContactNotificationMessage obj = new CustomzeContactNotificationMessage();
		obj.operation = operation;
		obj.sourceUserId = sourceUserId;
		obj.targetUserId = targetUserId;
		obj.message = message;
		return obj;
	}

	private CustomzeContactNotificationMessage() {
	}

	public byte[] encode() {
		JSONObject jsonObj = new JSONObject();

		try {
			jsonObj.putOpt("operation", this.operation);
			jsonObj.putOpt("sourceUserId", this.sourceUserId);
			jsonObj.putOpt("targetUserId", this.targetUserId);
			if(!TextUtils.isEmpty(this.message)) {
				jsonObj.putOpt("message", this.message);
			}

			if(!TextUtils.isEmpty(this.getExtra())) {
				jsonObj.putOpt("extra", this.getExtra());
			}

			if(this.getJSONUserInfo() != null) {
				jsonObj.putOpt("user", this.getJSONUserInfo());
			}
		} catch (JSONException var4) {
			RLog.e("ContactNotificationMessage", "JSONException " + var4.getMessage());
		}

		try {
			return jsonObj.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException var3) {
			var3.printStackTrace();
			return null;
		}
	}

	public CustomzeContactNotificationMessage(byte[] data) {
		String jsonStr = null;

		try {
			jsonStr = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException var5) {
			var5.printStackTrace();
		}

		try {
			JSONObject e = new JSONObject(jsonStr);
			this.setOperation(e.optString("operation"));
			this.setSourceUserId(e.optString("sourceUserId"));
			this.setTargetUserId(e.optString("targetUserId"));
			this.setMessage(e.optString("message"));
			this.setExtra(e.optString("extra"));
			if(e.has("user")) {
				this.setUserInfo(this.parseJsonToUserInfo(e.getJSONObject("user")));
			}
		} catch (JSONException var4) {
			RLog.e("ContactNotificationMessage", "JSONException " + var4.getMessage());
		}

	}

	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeToParcel(dest, this.operation);
		ParcelUtils.writeToParcel(dest, this.sourceUserId);
		ParcelUtils.writeToParcel(dest, this.targetUserId);
		ParcelUtils.writeToParcel(dest, this.message);
		ParcelUtils.writeToParcel(dest, this.extra);
		ParcelUtils.writeToParcel(dest, this.getUserInfo());
	}

	public int describeContents() {
		return 0;
	}
}
