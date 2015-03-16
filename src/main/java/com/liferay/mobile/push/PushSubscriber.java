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

import com.liferay.mobile.push.bus.BusUtil;

import com.squareup.otto.Subscribe;

import org.json.JSONObject;

/**
 * @author Bruno Farache
 */
public class PushSubscriber {

	public PushSubscriber(Push push) {
		this.push = push;
	}

	@Subscribe
	public void onFailure(Exception e) {
		unsubscribe();

		if (push.onFailure != null) {
			push.onFailure.onFailure(e);
		}
	}

	@Subscribe
	public void onPushNotification(JSONObject pushNotification) {
		if (push.onPushNotification != null) {
			push.onPushNotification.onPushNotification(pushNotification);
		}
	}

	@Subscribe
	public void register(String registrationId) throws Exception {
		unsubscribe();

		push.register(registrationId);
	}

	public void subscribe() {
		BusUtil.subscribe(this);
	}

	public void unsubscribe() {
		BusUtil.unsubscribe(this);
	}

	protected Push push;

}