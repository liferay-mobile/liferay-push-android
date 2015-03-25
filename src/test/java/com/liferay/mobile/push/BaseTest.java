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

import org.junit.Before;
import org.robolectric.Robolectric;

/**
 * @author Bruno Farache
 */
public abstract class BaseTest {

	@Before
	public void setup() {
		Context context = Robolectric.application;

		String username = context.getString(R.string.username);
		String password = context.getString(R.string.password);
		String server = context.getString(R.string.server);

		Authentication auth = new BasicAuthentication(username, password);
		Session session = new SessionImpl(server, auth);

		push = Push.with(session);
	}

	protected Push push;

}