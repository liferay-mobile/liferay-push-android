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

package com.liferay.mobile.push.bus;

import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * @author Bruno Farache
 */
public class BusUtil {

	public static void post(Object event) {
		getInstance().post(event);
	}

	public static void post(Exception e) {
		getInstance().post(e);
	}

	public static void subscribe(Object object) {
		getInstance().register(object);
	}

	public static void unsubscribe(Object object) {
		try {
			getInstance().unregister(object);
		}
		catch (IllegalArgumentException iae) {
			Log.w(_TAG, "Could not unsubscribe.", iae);
		}
	}

	protected static Bus getInstance() {
		if (_bus == null) {
			_bus = new Bus(ThreadEnforcer.ANY);
		}

		return _bus;
	}

	private static final String _TAG = BusUtil.class.getSimpleName();

	private static Bus _bus;

}