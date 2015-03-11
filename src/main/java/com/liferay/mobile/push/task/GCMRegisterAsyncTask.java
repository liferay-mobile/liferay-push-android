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

package com.liferay.mobile.push.task;

import android.content.Context;

import android.os.AsyncTask;

import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.liferay.mobile.push.bus.BusUtil;
import com.liferay.mobile.push.exception.UnavailableGooglePlayServicesException;

/**
 * @author Bruno Farache
 */
public class GCMRegisterAsyncTask extends AsyncTask<Object, Void, String> {

	public GCMRegisterAsyncTask(Context context, String senderId)
		throws UnavailableGooglePlayServicesException {

		_context = context.getApplicationContext();
		_senderId = senderId;

		isGooglePlayServicesAvailable(context);
	}

	public String doInBackground(Object... params) {
		String registrationId = null;

		try {
			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(
				_context);

			registrationId = gcm.register(_senderId);
		}
		catch (Exception e) {
			Log.e(_TAG, "Could not retrieve request token.", e);
			_exception = e;
			cancel(true);
		}

		return registrationId;
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

	@Override
	protected void onCancelled() {
		BusUtil.post(_exception);
	}

	@Override
	protected void onPostExecute(String registrationId) {
		BusUtil.post(registrationId);
	}

	private static final String _TAG =
		GCMRegisterAsyncTask.class.getSimpleName();

	private Context _context;
	private Exception _exception;
	private String _senderId;

}