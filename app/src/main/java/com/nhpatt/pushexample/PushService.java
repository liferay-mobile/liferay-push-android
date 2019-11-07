package com.nhpatt.pushexample;

import android.util.Log;
import com.liferay.mobile.push.PushNotificationsService;
import org.json.JSONObject;

public class PushService extends PushNotificationsService {

	@Override
	public void onPushNotification(JSONObject jsonObject) {
		super.onPushNotification(jsonObject);
		Log.d("LiferayPushDemoApp", "Push notification received!" + jsonObject.toString());
	}
}
