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

package com.liferay.mobile.push.receiver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * @author Bruno Farache
 */
public class GoogleCloudMessagingReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ComponentName component = new ComponentName(
			context.getPackageName(),
			GoogleCloudMessagingIntentService.class.getName());

		intent.setComponent(component);

		startWakefulService(context, intent);
	}

}