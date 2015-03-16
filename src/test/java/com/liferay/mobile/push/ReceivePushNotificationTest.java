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

import android.content.BroadcastReceiver;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.liferay.mobile.push.receiver.GoogleCloudMessagingIntentService;
import com.liferay.mobile.push.receiver.GoogleCloudMessagingReceiver;
import com.liferay.mobile.push.util.GoogleServices;

import java.util.List;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowApplication.Wrapper;

/**
 * @author Bruno Farache
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "android/src/main/AndroidManifest.xml", emulateSdk = 18)
public class ReceivePushNotificationTest extends BaseTest {

	@Test
	public void isIntentRegistered() {
		ShadowApplication app = Robolectric.getShadowApplication();

		Intent intent = new Intent("com.google.android.c2dm.intent.RECEIVE");

		List<BroadcastReceiver> receivers = app.getReceiversForIntent(intent);

		Assert.assertEquals(1, receivers.size());

		BroadcastReceiver receiver = receivers.get(0);

		receiver.onReceive(app.getApplicationContext(), intent);

		Intent startedIntent = app.peekNextStartedService();
		String componentClassName = startedIntent.getComponent().getClassName();

		Assert.assertEquals(
			GoogleCloudMessagingIntentService.class.getCanonicalName(),
			componentClassName);
	}

	@Test
	public void isReceiverRegistered() throws Exception {
		ShadowApplication app = Robolectric.getShadowApplication();

		List<Wrapper> wrappers = app.getRegisteredReceivers();

		Assert.assertFalse(wrappers.isEmpty());

		BroadcastReceiver receiver = null;

		for (Wrapper wrapper : wrappers) {
			if (wrapper.broadcastReceiver instanceof
					GoogleCloudMessagingReceiver) {

				receiver = wrapper.broadcastReceiver;
			}
		}

		Assert.assertNotNull(receiver);
	}

	@Test
	public void receivePushNotification() throws JSONException {
		final String body = "body";
		final String message = "message";

		GoogleCloudMessagingIntentService service =
			new GoogleCloudMessagingIntentService();

		Intent intent = new Intent(
			Robolectric.application, GoogleCloudMessagingIntentService.class);

		JSONObject payload = new JSONObject();
		payload.put(body, message);

		intent.putExtra("payload", payload.toString());

		GoogleServices googleServices = Mockito.spy(GoogleServices.class);

		Mockito.when(
			googleServices.getMessageType(service, intent))
		.thenReturn(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE);

		service.setGoogleServices(googleServices);

		service.onCreate();

		push.onPushNotification(new Push.OnPushNotification() {

			@Override
			public void onPushNotification(JSONObject pushNotification) {
				try {
					Assert.assertEquals(
						message, pushNotification.getString(body));
				}
				catch (JSONException je) {
					Assert.fail(je.getMessage());
				}
			}

		});

		service.onHandleIntent(intent);
	}

}