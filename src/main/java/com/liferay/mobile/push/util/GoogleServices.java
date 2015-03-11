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

package com.liferay.mobile.push.util;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.liferay.mobile.push.exception.UnavailableGooglePlayServicesException;

import java.io.IOException;

/**
 * @author Bruno Farache
 */
public class GoogleServices {

	public String getRegistrationId(Context context, String senderId)
		throws IOException {

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

		return gcm.register(senderId);
	}

	public void isGooglePlayServicesAvailable(Context context)
		throws UnavailableGooglePlayServicesException {

		int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(
			context);

		if (result != ConnectionResult.SUCCESS) {
			String message = GooglePlayServicesUtil.getErrorString(result);

			throw new UnavailableGooglePlayServicesException(message);
		}
	}

}