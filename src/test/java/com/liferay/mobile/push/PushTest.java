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

import android.content.Context;

import com.liferay.mobile.android.auth.Authentication;
import com.liferay.mobile.android.auth.basic.BasicAuthentication;
import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.service.SessionImpl;
import com.liferay.mobile.push.util.GoogleServices;

import org.json.JSONException;
import org.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.*;

/**
 * @author Bruno Farache
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "android/src/main/AndroidManifest.xml", emulateSdk = 18)
public class PushTest {

	@Before
	public void before() {
		Authentication auth = new BasicAuthentication(
			"test@liferay.com", "test");

		Session session = new SessionImpl("http://localhost:8080", auth);
		push = Push.with(session);
	}

	@Test
	public void registerWithRegistrationId() throws Exception {
		final String registrationId = "123";

		push.onSuccess(new Push.OnSuccess() {

			@Override
			public void onSuccess(JSONObject device) {
				try {
					assertNotNull(device);
					assertEquals("android", device.getString("platform"));
					assertEquals(registrationId, device.getString("token"));
				}
				catch (JSONException je) {
					fail();
				}
			}

		})
		.onFailure(new Push.OnFailure() {

			@Override
			public void onFailure(Exception e) {
				fail(e.getMessage());
			}

		})
		.register(registrationId);

		Robolectric.runBackgroundTasks();
	}

	@Test
	public void registerWithSenderId() throws Exception {
		GoogleServices googleServices = Mockito.mock(GoogleServices.class);
		Context context = Robolectric.application;
		String senderId = "senderId";

		final String registrationId = "123";

		Mockito.when(googleServices.getRegistrationId(context, senderId))
			.thenReturn(registrationId);

		push.setGoogleServices(googleServices);

		push.onSuccess(new Push.OnSuccess() {

			@Override
			public void onSuccess(JSONObject device) {
				try {
					assertNotNull(device);
					assertEquals("android", device.getString("platform"));
					assertEquals(registrationId, device.getString("token"));
				}
				catch (JSONException je) {
					fail();
				}
			}

		}).register(context, senderId);

		Robolectric.runBackgroundTasks();
	}

	@Test
	public void unregister() throws Exception {
		final String registrationId = "123";

		register(registrationId);

		push.onSuccess(new Push.OnSuccess() {

			@Override
			public void onSuccess(JSONObject device) {
				try {
					assertNotNull(device);
					assertEquals(registrationId, device.getString("token"));
				}
				catch (JSONException je) {
					fail();
				}
			}

		})
		.unregister(registrationId);

		Robolectric.runBackgroundTasks();
	}

	protected void register(String registrationId) throws Exception {
		push.onFailure(new Push.OnFailure() {

			@Override
			public void onFailure(Exception e) {
				fail(e.getMessage());
			}

		})
		.register(registrationId);

		Robolectric.runBackgroundTasks();
	}

	protected Push push;

}