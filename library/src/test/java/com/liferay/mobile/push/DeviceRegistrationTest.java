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
import com.liferay.mobile.push.util.GoogleServices;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

/**
 * @author Bruno Farache
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DeviceRegistrationTest extends BaseTest {

	@Test
	public void registerWithRegistrationId() throws Exception {
		final String registrationId = "123";

		push.onSuccess(new Push.OnSuccess() {

			@Override
			public void onSuccess(JSONObject device) {
				try {
					Assert.assertNotNull(device);
					Assert.assertEquals(Push.FIREBASE, device.getString("platform"));
					Assert.assertEquals(registrationId, device.getString("token"));
				} catch (JSONException je) {
					Assert.fail();
				}
			}
		}).onFailure(new Push.OnFailure() {

			@Override
			public void onFailure(Exception e) {
				Assert.fail(e.getMessage());
			}
		}).register(registrationId);

		executeAsyncTasks();
	}

	@Test
	public void registerWithSenderId() throws Exception {
		GoogleServices googleServices = Mockito.mock(GoogleServices.class);
		Context context = ShadowApplication.getInstance().getApplicationContext();
		String senderId = "senderId";

		final String registrationId = "123";

		Mockito.when(googleServices.getRegistrationId(context, senderId))
			.thenReturn(registrationId);

		push.setGoogleServices(googleServices);

		push.onSuccess(new Push.OnSuccess() {

			@Override
			public void onSuccess(JSONObject device) {
				try {
					Assert.assertNotNull(device);
					Assert.assertEquals(Push.FIREBASE, device.getString("platform"));
					Assert.assertEquals(registrationId, device.getString("token"));
				} catch (JSONException je) {
					Assert.fail();
				}
			}
		}).register(context, senderId);

		executeAsyncTasks();
	}

	@Test
	public void unregister() throws Exception {
		final String registrationId = "123";

		register(registrationId);

		push.onSuccess(new Push.OnSuccess() {

			@Override
			public void onSuccess(JSONObject device) {
				try {
					Assert.assertNotNull(device);
					Assert.assertEquals(registrationId, device.getString("token"));
				} catch (JSONException je) {
					Assert.fail();
				}
			}
		}).unregister(registrationId);

		executeAsyncTasks();
	}

	protected void register(String registrationId) throws Exception {
		push.onFailure(new Push.OnFailure() {

			@Override
			public void onFailure(Exception e) {
				Assert.fail(e.getMessage());
			}
		}).register(registrationId);

		ShadowApplication.runBackgroundTasks();
	}
}
