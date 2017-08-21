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

import com.liferay.mobile.android.callback.typed.JSONObjectCallback;
import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.service.SessionImpl;
import com.liferay.mobile.android.util.PortalVersion;
import com.liferay.mobile.push.task.GoogleCloudMessagingAsyncTask;
import com.liferay.mobile.push.util.GoogleServices;

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
		this.onFailure = onFailure;

		return this;
	}

	public Push onPushNotification(OnPushNotification onPushNotification) {
		_subscriber.subscribe();

		this.onPushNotification = onPushNotification;

		return this;
	}

	public Push onSuccess(OnSuccess onSuccess) {
		this.onSuccess = onSuccess;

		return this;
	}

	public void register(Context context, String senderId) throws Exception {
		try {
			_subscriber.subscribe();

			AsyncTask task = new GoogleCloudMessagingAsyncTask(
				context, senderId, _googleServices);

			task.execute();
		}
		catch (Exception e) {
			_subscriber.unsubscribe();

			throw e;
		}
	}

	public void register(String registrationId) throws Exception {
		getService().addPushNotificationsDevice(registrationId, ANDROID);
	}

	public void send(List<Long> toUserIds, JSONObject pushNotification)
		throws Exception {

		JSONArray toUserIdsJSONArray = new JSONArray();

		for (long toUserId : toUserIds) {
			toUserIdsJSONArray.put(toUserId);
		}

		getService().sendPushNotification(
			toUserIdsJSONArray, pushNotification.toString());
	}

	public void send(long toUserId, JSONObject pushNotification)
		throws Exception {

		List<Long> toUserIds = new ArrayList<Long>();
		toUserIds.add(toUserId);

		send(toUserIds, pushNotification);
	}

	public void unregister(String registrationId) throws Exception {
		getService().deletePushNotificationsDevice(registrationId);
	}

	public Push withPortalVersion(int portalVersion) {
		_portalVersion = portalVersion;
		return this;
	}

	public interface OnFailure {

		void onFailure(Exception e);

	}

	public interface OnPushNotification {

		void onPushNotification(JSONObject pushNotification);

	}

	public interface OnSuccess {

		void onSuccess(JSONObject jsonObject);

	}

	protected Push(Session session) {
		_session = new SessionImpl(session);
		_subscriber = new PushSubscriber(this);
		_session.setCallback(new JSONObjectCallback() {

			@Override
			public void onFailure(Exception e) {
				if (onFailure != null) {
					onFailure.onFailure(e);
				}
			}

			@Override
			public void onSuccess(JSONObject jsonObject) {
				if (onSuccess != null) {
					onSuccess.onSuccess(jsonObject);
				}
			}

		});
	}

	protected PushNotificationsDeviceServiceWrapper getService() {
		return new PushNotificationsDeviceServiceWrapper(
			_session, _portalVersion);
	}

	protected void setGoogleServices(GoogleServices googleServices) {
		_googleServices = googleServices;
	}

	protected OnFailure onFailure;
	protected OnPushNotification onPushNotification;
	protected OnSuccess onSuccess;

	private GoogleServices _googleServices = new GoogleServices();
	private int _portalVersion = PortalVersion.V_6_2;
	private Session _session;
	private PushSubscriber _subscriber;

}