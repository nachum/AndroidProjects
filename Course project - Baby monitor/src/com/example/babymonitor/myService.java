package com.example.babymonitor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

//this is where the magic happens.
//the service measures noise volume and invokes camera/sms/phone call by the user choice
public class myService extends Service {

	private final BroadcastReceiver myBroadcast = new ServiceReceiver();
	private boolean ServRun=true;
	private String phoneNum="", time="";
	private int sens;
	private Boolean sms = false, phone = false, photo = false;
	private Camera mCamera;
	//path for saving the audio file
	private String path = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/" +  System.currentTimeMillis()+ ".3gp";
	//path for saving the pictures
	String picPath = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/";
	private static MediaRecorder mRecorder;
	private SurfaceView sv;
	private Parameters parameters;


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//initializing the broadcast receiver to work only when the service is running
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.PHONE_STATE" );
		registerReceiver(myBroadcast, filter);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myBroadcast); //releasing the broadcast receiver
		ServRun=false;
		Toast.makeText(this,"Service Destroyed",Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Toast.makeText(this,"Service LowMemory",Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Toast.makeText(this,"Service start",Toast.LENGTH_SHORT).show();
		sv = new SurfaceView(getApplicationContext());//the surface for the camera


	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//getting data from main activity
		if(intent!=null)
		{
			sens=intent.getIntExtra("sens",0);
			phoneNum=intent.getStringExtra("phoNum");

			Log.i("service sensitivity", sens+"");
			sms = intent.getBooleanExtra("sms", sms);
			phone = intent.getBooleanExtra("phone", phone);
			photo = intent.getBooleanExtra("photo", photo);
			time = intent.getStringExtra("time");
			int delayTime=calculateTime(time);

			//starting the thread
			MyAsyncTask mat=new MyAsyncTask();
			mat.execute(delayTime);
		}
		//return START_NOT_STICKY;
		return super.onStartCommand(intent, flags, startId);

	}

	//calculate the string for real time
	private int calculateTime(String time)
	{
		Log.i("arrive", time);
		if(time.trim().equals("Every 1 minute"))
			return 6;
		else if(time.trim().equals("Every 2 minutes"))
			return 12;
		else if(time.trim().equals("Every 5 minutes"))
			return 30;
		else if(time.trim().equals("Every 10 minutes"))
			return 60;
		else if(time.trim().equals("Every 20 minutes"))
			return 120;
		else if(time.trim().equals("Every 30 minutes"))
			return 180;
		return 6;
	}



	private class MyAsyncTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			int delay=params[0];
			Log.i("delay", delay+"");
			// TODO Auto-generated method stub
			int[] times={0,0,0,0};
			int counter=delay;
			while(ServRun)
			{
				try{
					try {
						Thread.sleep(10*1000); //sleep for 10 seconds        
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					int lvl = mRecorder.getMaxAmplitude();
					Log.i("levelB4",lvl+"");
					lvl = (int) (lvl/327.67);
					times[counter%3]=lvl;
					int avg=(int)(times[0]+times[1]+times[2])/3;//calculating average of last 3 tests
					Log.i("level",lvl+"");
					Log.i("average level",avg+"");

					//if sensitivity is lower then the volume that was detected by device
					//and the delay time has passed
					if(sens<=avg && counter>=delay)
					{
						counter=-1;
						if(sms)
						{
							//sending SMS
							SmsManager smsManager = SmsManager.getDefault();
							smsManager.sendTextMessage(phoneNum, null, "Baby Alert!", null, null);
							Log.i("sms", "sms sent");
						}

						if(phone)
						{
							//making phone call
							Intent intent = new Intent(Intent.ACTION_CALL);

							intent.setData(Uri.parse("tel:" + phoneNum));
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
							startActivity(intent);
							Log.i("phoneCall", "phone called");

						}
						if (photo)
						{
							//taking picture
							try {
								mCamera.setPreviewDisplay(sv.getHolder());
								parameters = mCamera.getParameters();
								//set camera parameters
								mCamera.setParameters(parameters);
								mCamera.startPreview();

								mCamera.takePicture(shutterCallback, null, mCall);
								Log.i("got", "took picture");

							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}
				}
				catch(Exception e){
					e.getMessage();
				}
				counter++;
			}

			return null;
		}

		//closing the recorder and camera
		@Override
		protected void onPostExecute(Void result) {

			mRecorder.stop();
			Log.i("stop", "recorder stopped");
			try {
				mCamera.stopPreview();
			} catch (Exception e){
				Log.i("stop", "camera didn't stop");
			}
			Log.i("stop", "camera stopped");
			if(mCamera != null)
			{
				mCamera.release();
				mCamera=null;
				Log.i("Camera", "camera released");
			}
			MainActivity.finished = true;
		}

		//Initializing camera and recorder
		@Override
		protected void onPreExecute() {
			Log.i("got", "got here 1");
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setOutputFile(path);
			try {
				mRecorder.prepare();
			} catch (IllegalStateException e1) {
				// TODO Auto-generated catch block
				Log.i("shit", "illegal");
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Log.i("shit", "io");
				e1.printStackTrace();
			}
			mRecorder.start();
			mRecorder.getMaxAmplitude();
			mCamera = Camera.open();
			Log.i("got", "finished initializing");
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}

		ShutterCallback shutterCallback = new ShutterCallback() {
			public void onShutter() {
				Log.i("CAMERA", "onShutter'd");
			}
		};

		Camera.PictureCallback mCall = new Camera.PictureCallback()
		{

			public void onPictureTaken(byte[] data, Camera camera)
			{
				//decode the data obtained by the camera into a Bitmap

				FileOutputStream outStream = null;
				try{
					outStream = new FileOutputStream(picPath+System.currentTimeMillis()+ ".jpg");
					outStream.write(data);
					outStream.close();
				} catch (FileNotFoundException e){
					Log.d("CAMERA", e.getMessage());
				} catch (IOException e){
					Log.d("CAMERA", e.getMessage());
				}

			}
		};
	}

}
