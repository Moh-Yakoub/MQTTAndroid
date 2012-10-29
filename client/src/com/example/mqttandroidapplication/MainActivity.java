package com.example.mqttandroidapplication;

import java.io.PrintWriter;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.example.mqttandroidapplication.MQTTPushService.LocalBinder;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private MQTTPushService service;
	private MQTTBroadCastReciever broadcastreciever;

	boolean mbound = false;

	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			mbound = false;
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			// TODO Auto-generated method stub
			LocalBinder binder = (LocalBinder) arg1;
			service = binder.getService();
			mbound = true;
			service.setup();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		broadcastreciever = new MQTTBroadCastReciever(this);
		IntentFilter filter = new IntentFilter(Constants.CALLBACK_INTENT);
		registerReceiver(broadcastreciever, filter);

		Intent intent = new Intent(this, MQTTPushService.class);
		if (!mbound)
			bindService(intent, connection, Context.BIND_AUTO_CREATE);
		final EditText subscribe = (EditText) findViewById(R.id.topic_subscribe);
		final EditText unsubscribe = (EditText) findViewById(R.id.topic_unsubscribe);

		Button b = (Button) findViewById(R.id.button);
		b.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mbound) {
					service.subscribe(subscribe.getText().toString());
				} else {
					Toast.makeText(MainActivity.this, "Service not ready",
							Toast.LENGTH_LONG).show();
				}

			}
		});

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		destroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		destroy();
	}

	private void destroy() {
		service.disconnect();
		if (mbound)
			unbindService(connection);

		unregisterReceiver(broadcastreciever);

	}

}
