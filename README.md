![Liferay Mobile SDK logo](https://github.com/liferay/liferay-mobile-sdk/raw/master/logo.png)

# Liferay Push for Android

[![Build Status](https://travis-ci.org/brunofarache/liferay-push-android.svg?branch=master)](https://travis-ci.org/brunofarache/liferay-push-android)
[![Coverage Status](https://coveralls.io/repos/brunofarache/liferay-push-android/badge.svg?branch=master&t=1)](https://coveralls.io/r/brunofarache/liferay-push-android?branch=master)

* [Setup](#setup)
* [Use](#use)
	* [Registering a device](#registering-a-device)
	* [Receiving push notifications](#receiving-push-notifications)
	* [Sending push notifications](#sending-push-notifications)
	* [Unregistering a device](#unregistering-a-device)

## Setup

Add the library as a dependency to your project's build.gradle file:

```groovy
repositories {
	jcenter()
	mavenCentral()
}

dependencies {
	compile 'com.liferay.mobile:liferay-push:1.0.2'
}
```

## Use

### Registering a device

To receive push notifications, your app must register itself to the portal first. On the portal side, each device is tied to a user. Each user can have multiple registered devices. A device is represented by a device token string. Google calls this the `registrationId`.

Read [Android's documentation](http://developer.android.com/google/gcm/client.html) to learn how to get the `registrationId`.

It's easy to obtain a `registrationId` with *Liferay Push for Android*, you just have to call to the following method:

```java
import com.liferay.mobile.push.Push;

Session session = new SessionImpl("http://localhost:8080", new BasicAuthentication("test@liferay.com", "test"));

Push.with(session).register(this, SENDER_ID);
```

The `SENDER_ID` is the identifier of your GCM project, if you follow the official [Android's documentation](http://developer.android.com/google/gcm/client.html) it should be easy to obtain one.

Since all operations are asynchronous, you can set callbacks to check if the registration succeeded or an error occurred on the server side:

```java
Push.with(session)
	.onSuccess(new Push.OnSuccess() {

		@Override
		public void onSuccess(Object result) {
			System.out.println("Device was registered!");
		}

	})
	.onFailure(new Push.OnFailure() {

		@Override
		public void onFailure(Exception e) {
			System.out.println("Some error occurred!");
		}

	})
	.register(registrationId);
```

The `onSuccess` and `onFailure` callbacks are optional, but it's good practice to implement both. By doing so, your app can persist the device token or tell the user that an error occurred.

*Liferay Push for Android* is calling the *GCM server*, retrieving the results and storing your `registrationId` in the Liferay Portal instance for later use. If you obtain the token manually, you can register the device to the portal by calling the following method:

```java
Push.with(session).register(registrationId);
```

Now each time the portal wants to send a push notification to the user `test@liferay.com`, it looks up all registered devices for the user (including the one just registered) and sends the push notification for each `registrationId` found.

You should note that the [Push](src/main/java/com/liferay/mobile/push/Push.java) class is a wrapper for the Mobile SDK generated services. Internally, it calls the Mobile SDK's `PushNotificationsDeviceService` class. While you can still use `PushNotificationsDeviceService` directly, using the wrapper class is easier.

### Receiving push notifications

Once your device is registered, your app must be able to listen for notifications. 

There are several ways to do this:

* If you want to receive global notifications even if the application is not active at the moment you should implement a `BroadcastReceiver` instance in your app . [Android's developer documentation](http://developer.android.com/google/gcm/client.html#sample-receive) shows you how to do this.

* If you want to execute an action or show a notification only if the application is active, you could register a callback:

```java
Push.with(session).onPushNotification(new Push.OnPushNotification()

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
