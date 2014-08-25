package com.example.babymonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

//catching the phone call and starting "MyPhoneStateListener" class 
public class ServiceReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		MyPhoneStateListener phoneListener=new MyPhoneStateListener(context);
		TelephonyManager telephony = (TelephonyManager) 
				context.getSystemService(Context.TELEPHONY_SERVICE);
		telephony.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);// invoke listen method for call
		
	}
}
