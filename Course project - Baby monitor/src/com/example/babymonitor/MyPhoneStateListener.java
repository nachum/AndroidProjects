package com.example.babymonitor;

import android.content.Context;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

//this class listens to the phone call and doe's stuff according to phone state
public class MyPhoneStateListener extends PhoneStateListener {
	private Context con;
	private AudioManager audioManager;

	//initializing the context and the audio manager
	public MyPhoneStateListener(Context context)
	{
		this.con=context;
		audioManager = (AudioManager)con.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setMode(AudioManager.MODE_IN_CALL);
	}
	public void onCallStateChanged(int state,String incomingNumber){
		switch(state){
		case TelephonyManager.CALL_STATE_IDLE:// when phone is off or call finished
			Log.d("DEBUG", "IDLE");
			if(audioManager.isSpeakerphoneOn())
				audioManager.setSpeakerphoneOn(false);//turn speaker off
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK://phone call answered
			Log.d("DEBUG", "OFFHOOK");
			if(!audioManager.isSpeakerphoneOn())
				audioManager.setSpeakerphoneOn(true); //turn speaker on
			break;
		case TelephonyManager.CALL_STATE_RINGING://incoming call
			Log.d("DEBUG", "RINGING");
			break;
		}
	} 
}
