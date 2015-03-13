/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mobile.push.util;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.liferay.mobile.push.exception.PushNotificationReceiverException;
import com.liferay.mobile.push.exception.UnavailableGooglePlayServicesException;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Bruno Farache
 */
public class GoogleServices {

	public JSONObject getPushNotification(Context context, Intent intent)
		throws PushNotificationReceiverException {

		String messageType = getInstance(context).getMessageType(intent);
		Bundle extras = intent.getExtras();

		if (extras.isEmpty()) {
			throw new PushNotificationReceiverException(
				"Push notification body is empty.");
		}

		if (!GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
			throw new PushNotificationReceiverException(
				"Unknown message type" + messageType);
		}

		try {
			JSONObject pushNotification = new JSONObject(
				extras.getString("payload"));

			return pushNotification;
		}
		catch (JSONException je) {
			throw new PushNotificationReceiverException(je);
		}
	}

	public String getRegistrationId(Context context, String senderId)
		throws IOException {

		return getInstance(context).register(senderId);
	}

	public void isGooglePlayServicesAvailable(Context context)
		throws UnavailableGooglePlayServicesException {

		int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(
			context);

		if (result != ConnectionResult.SUCCESS) {
			String message = GooglePlayServicesUtil.getErrorString(result);

			throw new UnavailableGooglePlayServicesException(message);
		}
	}

	protected GoogleCloudMessaging getInstance(Context context) {
		return GoogleCloudMessaging.getInstance(context);
	}

}