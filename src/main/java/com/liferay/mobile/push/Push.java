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

import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.service.SessionImpl;
import com.liferay.mobile.android.task.callback.typed.GenericAsyncTaskCallback;
import com.liferay.mobile.android.v62.pushnotificationsdevice.PushNotificationsDeviceService;
import com.liferay.mobile.push.bus.BusUtil;
import com.liferay.mobile.push.task.GCMRegisterAsyncTask;

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

	public Push onFailure(OnFailure onFailure) {
		_onFailure = onFailure;

		return this;
	}

	public Push onSuccess(OnSuccess onSuccess) {
		_onSuccess = onSuccess;

		return this;
	}

	public void register(Context context, String senderId) {
		try {
			BusUtil.register(this);

			GCMRegisterAsyncTask task = new GCMRegisterAsyncTask(
				context, senderId);

			task.execute();
		}
		catch (Exception e) {
			onFailure(e);
		}
	}

	@Subscribe
	public void register(String registrationId) {
		try {
			getService().addPushNotificationsDevice(registrationId, ANDROID);
		}
		catch (Exception e) {
			onFailure(e);
		}
	}

	public void send(List<Long> toUserIds, JSONObject notification) {
		try {
			JSONArray toUserIdsJSONArray = new JSONArray();

			for (long toUserId : toUserIds) {
				toUserIdsJSONArray.put(toUserId);
			}

			getService().sendPushNotification(
				toUserIdsJSONArray, notification.toString());
		}
		catch (Exception e) {
			onFailure(e);
		}
	}

	public void send(long toUserId, JSONObject notification) {
		try {
			List<Long> toUserIds = new ArrayList<Long>();
			toUserIds.add(toUserId);

			send(toUserIds, notification);
		}
		catch (Exception e) {
			onFailure(e);
		}
	}

	public void unregister(String registrationId) {
		try {
			getService().deletePushNotificationsDevice(registrationId);
		}
		catch (Exception e) {
			onFailure(e);
		}
	}

	public interface OnFailure {

		public void onFailure(Exception e);

	}

	public interface OnSuccess {

		public void onSuccess(Object result);

	}

	protected Push(Session session) {
		_session = new SessionImpl(session);
		_session.setCallback(new GenericAsyncTaskCallback() {

			@Override
			public void onFailure(Exception e) {
				Push.this.onFailure(e);
			}

			@Override
			public void onSuccess(Object result) {
				if (_onSuccess != null) {
					_onSuccess.onSuccess(result);
				}
			}

			@Override
			public Object transform(Object obj) throws Exception {
				return obj;
			}

		});
	}

	protected PushNotificationsDeviceService getService() {
		return new PushNotificationsDeviceService(_session);
	}

	@Subscribe
	protected void onFailure(Exception e) {
		BusUtil.unregister(this);

		if (_onFailure != null) {
			_onFailure.onFailure(e);
		}
	}

	private OnFailure _onFailure;
	private OnSuccess _onSuccess;
	private Session _session;

}