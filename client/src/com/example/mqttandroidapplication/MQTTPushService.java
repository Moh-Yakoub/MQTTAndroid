package com.example.mqttandroidapplication;

import org.eclipse.paho.client.mqttv3.MqttCallback;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MQTTPushService extends Service {
	private MqttClient client;
	private customMqttCallback callback;
	private LocalBinder binder = new LocalBinder();

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return binder;
	}

	public void setup() {
		new MQTTtask().execute(MQTTtask.CONNECT + "");
	}

	public void subscribe(String topic) {
		Log.v("MQTTPushService:Subscribe", topic);
		new MQTTtask().execute(MQTTtask.SUBSCRIBE + "", topic);

	}

	public void unsubscribe(String topic) {
		new MQTTtask().execute(MQTTtask.UNSUBSCRIBE + "", topic);
	}

	public void disconnect() {
		new MQTTtask().execute(MQTTtask.DISCONNECT + "");
	}

	public class LocalBinder extends Binder {
		MQTTPushService getService() {
			return MQTTPushService.this;
		}
	}

	public class customMqttCallback implements MqttCallback {

		@Override
		public void connectionLost(Throwable arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void deliveryComplete(MqttDeliveryToken arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void messageArrived(MqttTopic arg0, MqttMessage arg1)
				throws Exception {
			// TODO Auto-generated method stub
			Bundle b = new Bundle();
			Intent i = new Intent(Constants.CALLBACK_INTENT);
			b.putString("msg", arg1 + "");
			i.putExtras(b);
			sendBroadcast(i);

		}
	}

	public class MQTTtask extends AsyncTask<String, String, Boolean> {
		public static final int CONNECT = 0;
		public static final int DISCONNECT = 1;
		public static final int SUBSCRIBE = 2;
		public static final int UNSUBSCRIBE = 3;

		@Override
		protected Boolean doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			int op = Integer.parseInt(arg0[0]);
			if (op == CONNECT) {
				return setup();
			} else if (op == DISCONNECT) {
				return disconnect();
			} else if (op == SUBSCRIBE) {
				Log.v("MQTTPushService:Subscribe_arg_length", arg0.length + ":"
						+ arg0[1]);
				return subscribe(arg0[1]);
			} else if (op == UNSUBSCRIBE) {
				return unsubscribe(arg0[1]);
			} else
				return false;

		}

		private boolean setup() {
			try {
				// use null MQTTPersistence , if not null it throws an exception
				client = new MqttClient("tcp://" + Constants.HOST + ":"
						+ Constants.PORT, "123456", null);
				client.connect();
				callback = new customMqttCallback();
				client.setCallback(callback);
				return true;
			} catch (Exception e) {
				Log.e("MQTTPushService:setup()", e + "");
				return false;
			}
		}

		private boolean subscribe(String topic) {
			try {
				client.subscribe(topic);
				return true;
			} catch (MqttSecurityException e) {
				// TODO Auto-generated catch block
				Log.e("MQTTPushService:subscribe", Log.getStackTraceString(e));
				return false;
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				Log.e("MQTTPushService:subscribe", e.getLocalizedMessage());
				return false;
			}
		}

		private boolean unsubscribe(String topic) {
			try {
				client.unsubscribe(topic);
				return true;
			} catch (MqttSecurityException e) {
				// TODO Auto-generated catch block
				Log.e("MQTTPushService:unsubscribe", e.getLocalizedMessage());
				return false;
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				Log.e("MQTTPushService:unsubscribe", e.getLocalizedMessage());
				return false;
			}
		}

		private boolean disconnect() {
			try {
				client.disconnect();
				return true;
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("MQTTPushService:disconnect", e.getLocalizedMessage());
				return false;
			}

		}

	}
}
