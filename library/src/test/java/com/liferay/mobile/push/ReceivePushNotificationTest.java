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
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.liferay.mobile.push.Push.OnPushNotification;
import com.liferay.mobile.push.util.GoogleServices;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * @author Bruno Farache
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ReceivePushNotificationTest extends BaseTest {

	@Test
	public void receivePushNotification() throws JSONException {
		final String body = "body";
		final String message = "message";

		PushNotificationsService service = Robolectric.setupService(PushNotificationsService.class);

		Intent intent = new Intent(RuntimeEnvironment.application.getApplicationContext(),
			PushNotificationsService.class);

		JSONObject payload = new JSONObject();
		payload.put(body, message);

		intent.putExtra("payload", payload.toString());

		GoogleServices googleServices = Mockito.spy(GoogleServices.class);

		Mockito.when(googleServices.getMessageType(service, intent))
			.thenReturn(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE);

		service.setGoogleServices(googleServices);

		service.onCreate();

		push.onPushNotification(new OnPushNotification() {

			@Override
			public void onPushNotification(JSONObject pushNotification) {
				try {
					Assert.assertEquals(message, pushNotification.getString(body));
				} catch (JSONException je) {
					Assert.fail(je.getMessage());
				}
			}
		});

		service.onHandleWork(intent);
	}
}
