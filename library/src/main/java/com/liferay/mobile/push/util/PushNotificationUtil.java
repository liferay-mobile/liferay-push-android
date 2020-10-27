package com.liferay.mobile.push.util;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import com.liferay.mobile.push.exception.PushNotificationReceiverException;

import org.json.JSONException;
import org.json.JSONObject;
public class PushNotificationUtil {

	private static final String GCM_TYPE = "gcm";

	public static JSONObject getPushNotification(Context context, Intent intent)
			throws PushNotificationReceiverException {

		String messageType = intent.getStringExtra("message_type");

		if (messageType != null && !messageType.equals(GCM_TYPE)) {
			throw new PushNotificationReceiverException("Unknown message type" + messageType);
		}

		Bundle extras = intent.getExtras();

		if ((extras == null) || extras.isEmpty()) {
			throw new PushNotificationReceiverException("Push notification body is empty.");
		}

		String body = extras.getString("gcm.notification.body", null);

		String payload = extras.getString("payload", null);

		try {
			JSONObject jsonObject = payload == null ? new JSONObject() : new JSONObject(payload);

			jsonObject.put("body", body);

			return jsonObject;
		} catch (JSONException je) {
			throw new PushNotificationReceiverException(je);
		}
	}
}