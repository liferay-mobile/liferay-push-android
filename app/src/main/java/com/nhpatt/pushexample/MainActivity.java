package com.nhpatt.pushexample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.liferay.mobile.android.auth.basic.BasicAuthentication;
import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.service.SessionImpl;
import com.liferay.mobile.push.Push;
import org.json.JSONObject;

public class MainActivity extends Activity implements View.OnClickListener {

	public static final String TAG = "LiferayPushDemoApp";

	private Push push;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.push_send).setOnClickListener(this);

		Session session = new SessionImpl("http://10.0.2.2:8080",
			new BasicAuthentication("test@liferay.com", "test"));

		try {
			push = Push.with(session);
			push.withPortalVersion(70);

			push.onSuccess(success -> {
				Log.d(TAG, success.toString());
				push.onPushNotification(jsonObject -> runOnUiThread(() -> showToast(jsonObject)));
			}).onFailure(this::logError).register();
		} catch (Exception e) {
			logError(e);
		}
	}

	@Override
	public void onClick(View v) {
		try {
			EditText pushMessageView = findViewById(R.id.push_message);
			String pushMessage = pushMessageView.getText().toString();

			JSONObject pushNotification = new JSONObject();
			pushNotification.put("body", pushMessage);

			push.send(54205, pushNotification);
		} catch (Exception e) {
			logError(e);
		}
	}

	private void showToast(JSONObject jsonObject) {
		Toast.makeText(this, "Push received: " + jsonObject.toString(), Toast.LENGTH_LONG).show();
	}

	private int logError(Exception e) {
		return Log.e(TAG, e.getMessage());
	}
}
