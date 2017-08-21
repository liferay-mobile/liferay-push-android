package com.nhpatt.pushexample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.liferay.mobile.android.auth.basic.BasicAuthentication;
import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.service.SessionImpl;
import com.liferay.mobile.push.Push;

import org.json.JSONObject;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Session session = new SessionImpl(
                "http://screens.liferay.org.es", new BasicAuthentication("push@liferay.com", "push"));

        try {
            Push push = Push.with(session);
            push.withPortalVersion(70);
            push.onSuccess(new Push.OnSuccess() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    Log.d("Registered!", jsonObject.toString());
                }
            }).onFailure(new Push.OnFailure() {
                @Override
                public void onFailure(Exception e) {
                    Log.e("Failure!", e.getMessage());
                }
            }).register(this, "862746724485");
        }
        catch (Exception e) {
            Log.e("Failure: ", e.getMessage());
        }

    }
}
