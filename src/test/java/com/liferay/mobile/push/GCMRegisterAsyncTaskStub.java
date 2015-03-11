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

import com.liferay.mobile.push.exception.UnavailableGooglePlayServicesException;
import com.liferay.mobile.push.task.GCMRegisterAsyncTask;

/**
 * @author Bruno Farache
 */
public class GCMRegisterAsyncTaskStub extends GCMRegisterAsyncTask {

	public GCMRegisterAsyncTaskStub(
			Context context, String senderId, String registrationId)
		throws UnavailableGooglePlayServicesException {

		super(context, senderId);

		this.registrationId = registrationId;
	}

	@Override
	public String doInBackground(Object... params) {
		return registrationId;
	}

	@Override
	public void isGooglePlayServicesAvailable(Context context)
		throws UnavailableGooglePlayServicesException {

		return;
	}

	protected String registrationId;

}