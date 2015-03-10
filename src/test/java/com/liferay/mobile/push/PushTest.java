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

import com.liferay.mobile.android.auth.Authentication;
import com.liferay.mobile.android.auth.basic.BasicAuthentication;
import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.service.SessionImpl;
import com.liferay.mobile.push.exception.UnavailableGooglePlayServicesException;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * @author Bruno Farache
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "android/src/main/AndroidManifest.xml", emulateSdk = 18)
public class PushTest {

	@Test(expected = UnavailableGooglePlayServicesException.class)
	public void register() throws Exception {
		Authentication auth = new BasicAuthentication(
			"test@liferay.com", "test");

		Session session = new SessionImpl("http://localhost:8080", auth);
		Push.with(session).register(Robolectric.application, "");
	}

}