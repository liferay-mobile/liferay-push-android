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

import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.util.PortalVersion;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Bruno Farache
 */
public class PushNotificationsDeviceServiceWrapper {

	public PushNotificationsDeviceServiceWrapper(
		Session session, int portalVersion) {

		if (portalVersion == PortalVersion.V_6_2) {
			service62 = new com.liferay.mobile.android.v62.pushnotificationsdevice.PushNotificationsDeviceService(session);
		}
		else {
			service7 = new com.liferay.mobile.android.v70.pushnotificationsdevice.PushNotificationsDeviceService(session);
		}
	}

	public JSONObject addPushNotificationsDevice(String token, String platform)
		throws Exception {

		if (service62 != null) {
			return service62.addPushNotificationsDevice(token, platform);
		}

		return service7.addPushNotificationsDevice(token, platform);
	}

	public JSONObject deletePushNotificationsDevice(String token)
		throws Exception {

		if (service62 != null) {
			return service62.deletePushNotificationsDevice(token);
		}

		return service7.deletePushNotificationsDevice(token);
	}

	public void sendPushNotification(JSONArray toUserIds, String payload)
		throws Exception {

		if (service62 != null) {
			service62.sendPushNotification(toUserIds, payload);
			return;
		}

		service7.sendPushNotification(toUserIds, payload);
	}

	protected com.liferay.mobile.android.v70.pushnotificationsdevice.PushNotificationsDeviceService service7;
	protected com.liferay.mobile.android.v62.pushnotificationsdevice.PushNotificationsDeviceService service62;

}