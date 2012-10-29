package com.example.mqttandroidapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MQTTBroadCastReciever extends BroadcastReceiver {
	private Context c;

	public MQTTBroadCastReciever(Context c) {
		// TODO Auto-generated constructor stub
		this.c = c;
	}

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Bundle b = arg1.getExtras();
		if (b != null) {
			Toast.makeText(c, b.getString("msg"), Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(c, "recieved Message", Toast.LENGTH_LONG).show();
		}
	}

}
