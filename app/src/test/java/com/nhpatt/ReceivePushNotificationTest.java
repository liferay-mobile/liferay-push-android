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

package com.nhpatt;

import android.content.BroadcastReceiver;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.liferay.mobile.push.BuildConfig;
import com.liferay.mobile.push.Push.OnPushNotification;
import com.liferay.mobile.push.PushNotificationsReceiver;
import com.liferay.mobile.push.PushNotificationsService;
import com.liferay.mobile.push.util.GoogleServices;
import com.nhpatt.pushexample.PushService;

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
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowApplication.Wrapper;

import java.util.List;

/**
 * @author Bruno Farache
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ReceivePushNotificationTest {

	@Test
	public void isIntentRegistered() {
		ShadowApplication app = ShadowApplication.getInstance();

		Intent intent = new Intent("com.google.android.c2dm.intent.RECEIVE");

		List<BroadcastReceiver> receivers = app.getReceiversForIntent(intent);

		Assert.assertEquals(1, receivers.size());

		BroadcastReceiver receiver = receivers.get(0);

		receiver.onReceive(app.getApplicationContext(), intent);

		Intent startedIntent = app.peekNextStartedService();
		String componentClassName = startedIntent.getComponent().getClassName();

		Assert.assertEquals(
			PushService.class.getCanonicalName(),
			componentClassName);
	}

	@Test
	public void isReceiverRegistered() throws Exception {
		ShadowApplication app = ShadowApplication.getInstance();

		List<Wrapper> wrappers = app.getRegisteredReceivers();

		Assert.assertFalse(wrappers.isEmpty());

		BroadcastReceiver receiver = null;

		for (Wrapper wrapper : wrappers) {
			if (wrapper.broadcastReceiver instanceof
				PushNotificationsReceiver) {

				receiver = wrapper.broadcastReceiver;
			}
		}

		Assert.assertNotNull(receiver);
	}

}
