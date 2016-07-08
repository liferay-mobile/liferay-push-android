package com.liferay.mobile.android;

import org.json.JSONArray;
import org.json.JSONObject;

public interface PushNotificationsDevice {

	JSONObject addPushNotificationsDevice(String token, String platform) throws Exception;

	JSONObject deletePushNotificationsDevice(String token) throws Exception;

	void sendPushNotification(JSONArray toUserIds, String payload) throws Exception;

	void sendPushNotification(String platform, JSONArray tokens, String payload) throws Exception;
}
