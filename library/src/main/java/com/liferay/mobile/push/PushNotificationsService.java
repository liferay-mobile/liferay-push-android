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

package com.liferay.mobile.push;

import android.content.Intent;
import androidx.core.app.JobIntentService;
import com.liferay.mobile.push.Push.OnPushNotification;
import com.liferay.mobile.push.bus.BusUtil;
import com.liferay.mobile.push.exception.PushNotificationReceiverException;
import com.liferay.mobile.push.util.GoogleServices;
import org.json.JSONObject;

/**
 * @author Bruno Farache
 */
public class PushNotificationsService extends JobIntentService implements OnPushNotification {

	@Override
	protected void onHandleWork(Intent intent) {
		try {
			JSONObject pushNotification = _googleService.getPushNotification(this, intent);

			BusUtil.post(pushNotification);
			onPushNotification(pushNotification);
		} catch (PushNotificationReceiverException pnre) {
			BusUtil.post(pnre);
		}
	}

	@Override
	public void onPushNotification(JSONObject pushNotification) {
	}

	public void setGoogleServices(GoogleServices googleServices) {
		_googleService = googleServices;
	}

	private GoogleServices _googleService = new GoogleServices();
}