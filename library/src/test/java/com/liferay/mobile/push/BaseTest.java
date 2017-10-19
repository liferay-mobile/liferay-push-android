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
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowLooper;

/**
 * @author Bruno Farache
 */
public abstract class BaseTest {

	@Before
	public void setup() {
		Context context = RuntimeEnvironment.application;

		String username = System.getenv("PUSH_USERNAME");

		if (username == null) {
			username = context.getString(R.string.username);
		}

		String password = System.getenv("PUSH_PASSWORD");

		if (password == null) {
			password = context.getString(R.string.password);
		}

		String server = System.getenv("PUSH_SERVER");

		if (server == null) {
			server = context.getString(R.string.server);
		}

		Authentication auth = new BasicAuthentication(username, password);
		Session session = new SessionImpl(server, auth);
		push = Push.with(session).withPortalVersion(70);
	}

	protected void executeAsyncTasks() {
		try {
			ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
			Thread.sleep(1000);
			ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	protected Push push;

}
