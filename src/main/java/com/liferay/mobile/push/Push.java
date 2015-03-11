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

import android.os.AsyncTask;

import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.service.SessionImpl;
import com.liferay.mobile.android.task.callback.typed.JSONObjectAsyncTaskCallback;
import com.liferay.mobile.android.v62.pushnotificationsdevice.PushNotificationsDeviceService;
import com.liferay.mobile.push.bus.BusUtil;
import com.liferay.mobile.push.task.GCMRegisterAsyncTask;
import com.liferay.mobile.push.util.GoogleServices;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Bruno Farache
 */
public class Push {

	public static final String ANDROID = "android";

	public static Push with(Session session) {
		return new Push(session);
	}

	@Subscribe
	public void onFailure(Exception e) {
		BusUtil.unregister(this);

		if (_onFailure != null) {
			_onFailure.onFailure(e);
		}
	}

	public Push onFailure(OnFailure onFailure) {
		_onFailure = onFailure;

		return this;
	}

	public Push onSuccess(OnSuccess onSuccess) {
		_onSuccess = onSuccess;

		return this;
	}

	public void register(Context context, String senderId) throws Exception {
		register(context, senderId, new GoogleServices());
	}

	@Subscribe
	public void register(String registrationId) throws Exception {
		getService().addPushNotificationsDevice(registrationId, ANDROID);
	}

	public void send(List<Long> toUserIds, JSONObject notification)
		throws Exception {

		JSONArray toUserIdsJSONArray = new JSONArray();

		for (long toUserId : toUserIds) {
			toUserIdsJSONArray.put(toUserId);
		}

		getService().sendPushNotification(
			toUserIdsJSONArray, notification.toString());
	}

	public void send(long toUserId, JSONObject notification) throws Exception {
		List<Long> toUserIds = new ArrayList<Long>();
		toUserIds.add(toUserId);

		send(toUserIds, notification);
	}

	public void unregister(String registrationId) throws Exception {
		getService().deletePushNotificationsDevice(registrationId);
	}

	public interface OnFailure {

		public void onFailure(Exception e);

	}

	public interface OnSuccess {

		public void onSuccess(JSONObject jsonObject);

	}

	protected Push(Session session) {
		_session = new SessionImpl(session);
		_session.setCallback(new JSONObjectAsyncTaskCallback() {

			@Override
			public void onFailure(Exception e) {
				Push.this.onFailure(e);
			}

			@Override
			public void onSuccess(JSONObject jsonObject) {
				if (_onSuccess != null) {
					_onSuccess.onSuccess(jsonObject);
				}
			}

		});
	}

	protected PushNotificationsDeviceService getService() {
		return new PushNotificationsDeviceService(_session);
	}

	protected void register(
			Context context, String senderId, GoogleServices googleServices)
		throws Exception {

		try {
			BusUtil.register(this);

			AsyncTask task = new GCMRegisterAsyncTask(
				context, senderId, googleServices);

			task.execute();
		}
		catch (Exception e) {
			BusUtil.unregister(this);

			throw e;
		}
	}

	private OnFailure _onFailure;
	private OnSuccess _onSuccess;
	private Session _session;

}