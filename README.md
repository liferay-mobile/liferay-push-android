![Liferay Mobile SDK logo](https://github.com/liferay/liferay-mobile-sdk/raw/master/logo.png)

# Liferay Push for Android

[![Build Status](https://travis-ci.org/liferay-mobile/liferay-push-android.svg?branch=master)](https://travis-ci.org/liferay-mobile/liferay-push-android)
[![Coverage Status](https://coveralls.io/repos/liferay-mobile/liferay-push-android/badge.svg?branch=master&t=1)](https://coveralls.io/r/liferay-mobile/liferay-push-android?branch=master)

* [Setup](#setup)
* [Use](#use)
	* [Registering a device](#registering-a-device)
	* [Receiving push notifications](#receiving-push-notifications)
	* [Sending push notifications](#sending-push-notifications)
	* [Unregistering a device](#unregistering-a-device)

## Setup

Add the library as a dependency to your project's build.gradle file:

```groovy
dependencies {
	compile 'com.liferay.mobile:liferay-push:1.1.0'
}
```

## Use

### Registering a device

To receive push notifications, your app must register itself to the portal first. On the portal side, each
device is tied to a user. Each user can have multiple registered devices. A device is represented by a device token string. Google calls this the `registrationId`.

To register a device, we need a `SENDER_ID`, the id of our project in firebase. Read [Firebase's documentation](https://firebase.google.com/docs/cloud-messaging/) to learn how to get the `SENDER_ID`.

The `SENDER_ID` is available, after creating a firebase project, in the *Cloud Messaging* tab under the project settings:

<img src="docs/images/Firebase Console Sender Id.png">

After obtaining the `SENDER_ID` it's easy to register a device with *Liferay Push for Android*, you just have to call to the following method:

```java
import com.liferay.mobile.push.Push;

Session session = new SessionImpl("http://localhost:8080", new BasicAuthentication("test@liferay.com", "test"));

Push.with(session).register(this, SENDER_ID);
```

If you want to use Liferay 7.x you should manually specify the version with a call like this:

```java
push.withPortalVersion(70)
```

Since all operations are asynchronous, you can set callbacks to check if the registration succeeded or an error occurred on the server side:

```java
Push.with(session)
	.onSuccess(new Push.OnSuccess() {

		@Override
		public void onSuccess(JSONObject jsonObject) {
			System.out.println("Device was registered!");
		}

	})
	.onFailure(new Push.OnFailure() {

		@Override
		public void onFailure(Exception e) {
			System.out.println("Some error occurred!");
		}

	})
	.register(SENDER_ID);
```

The `onSuccess` and `onFailure` callbacks are optional, but it's good practice to implement both. By doing so, your app can persist the device token or tell the user that an error occurred.

*Liferay Push for Android* is calling the *GCM server*, retrieving the results and storing your `registrationId` in the Liferay Portal instance for later use.

All set! If everything went well, you should see a new device registered under the *Push Notifications* menu in *Configuration*. Next step is [Receiving push notifications](#receiving-push-notifications)


#### Using the registrationId directly without registering against Liferay Portal

If you obtain the token manually, you can register the device to the portal by calling the following method:

```java
Push.with(session).register(registrationId);
```

Now each time the portal wants to send a push notification to the user `test@liferay.com`, it looks up all registered devices for the user (including the one just registered) and sends the push notification for each `registrationId` found.

You should note that the [Push](src/main/java/com/liferay/mobile/push/Push.java) class is a wrapper for the Mobile SDK generated services. Internally, it calls the Mobile SDK's `PushNotificationsDeviceService` class. While you can still use `PushNotificationsDeviceService` directly, using the wrapper class is easier.

### Receiving push notifications

Once your device is registered, you have to configure both the server and the client to be able to receive push messages.

To send notifications from Liferay you should configure the `API_KEY` inside *System Settings*, *Other* and *Android Push Notifications Sender*. To obtain the `API_KEY` you should, again, access your firebase project settings and under *Cloud Messaging*, use the **Legacy Server Key**. 

<img src="docs/images/Firebase Console Server Key.png">
 
Then you have to configure your project to be able to listen for notifications:

* You should implement a `BroadcastReceiver` instance in your app. [Android's developer documentation](http://developer.android.com/google/gcm/client.html#sample-receive) shows you how to do this. Specifically, you should:

	* Register a `<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />` in your *AndroidManifest.xml* file.
	* Register a WAKE_LOCK permission and Internet if you had not used those permissions before:

		```xml
		<uses-permission android:name="android.permission.INTERNET" />
		<uses-permission android:name="android.permission.WAKE_LOCK" />
		```
	
	* Add a *BroadcastReceiver* and a *IntentService* to your project:

		```xml
		<receiver
	        android:name=".PushReceiver"
	        android:permission="com.google.android.c2dm.permission.SEND">
	        <intent-filter>
	            <action android:name="com.google.android.c2dm.intent.RECEIVE" />
	            <category android:name="com.liferay.mobile.push" />
	        </intent-filter>
	    </receiver>
	
	   <service android:name=".PushService" />
		```
	
	* The code implementing those classes is really simple:

		```java
		public class PushReceiver extends PushNotificationsReceiver {
		    @Override
		    public String getServiceClassName() {
		        return PushService.class.getName();
		    }
		}
		
		public class PushService extends PushNotificationsService {
			 @Override
		    public void onPushNotification(JSONObject jsonObject) {
		        super.onPushNotification(jsonObject);
		        
		        // Your own code to deal with the push notification
		    }
		}
		```

* If you want to execute an action or show a notification only if the application is active, you could register a callback:

```java
Push.with(session).onPushNotification(new Push.OnPushNotification() {

	@Override
	public void onPushNotification(JSONObject jsonObject) {
	}

});
```
	
This method only works if you have already registered against Liferay Portal using the previous instructions.

### Sending push notifications

There are many ways to send push notifications from Liferay Portal. See the [Liferay Push documentation](../README.md) for more details. Alternatively, you can send push notifications from your Android app. Just make sure the user has the proper permissions in the portal to send push notifications.

```java
JSONObject notification = new JSONObject();
notification.put("message", "Hello!");

Push.with(session).send(toUserId, notification);
```

In this code, the push notification is sent to the user specified by `toUserId`. Upon receiving the notification, the portal looks up all the user's registered devices (both Android and iOS devices) and sends `notification` as the body of the push notification.

### Unregistering a device

If you want to stop receiving push notifications on a device, you can unregister it from from the portal with the following code:

```java
Push.with(session).unregister(registrationId);
```

Users can only unregister devices they own.
