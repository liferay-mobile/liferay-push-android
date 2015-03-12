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

package com.liferay.mobile.push.receiver;

import android.app.IntentService;

import android.content.Intent;

import com.liferay.mobile.push.bus.BusUtil;
import com.liferay.mobile.push.exception.PushNotificationReceiverException;
import com.liferay.mobile.push.util.GoogleServices;

import org.json.JSONObject;

/**
 * @author Bruno Farache
 */
public class GoogleCloudMessagingIntentService extends IntentService {

	public GoogleCloudMessagingIntentService() {
		super(GoogleCloudMessagingIntentService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			JSONObject pushNotitification = _googleService.getPushNotification(
				this, intent);

			BusUtil.post(pushNotitification);

			GoogleCloudMessagingReceiver.completeWakefulIntent(intent);
		}
		catch (PushNotificationReceiverException pnre) {
			BusUtil.post(pnre);
		}
	}

	private GoogleServices _googleService = new GoogleServices();

}