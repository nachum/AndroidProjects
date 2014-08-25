//In the service the data is downloaded from the web site and displayed on the screen
package com.example.weatherwidget;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class weather_service extends Service {
	private Boolean cont=true; 
	private int mAppWidgetId;
	private ThreadDemo td;
	private int refTime=10;//in case time interval was not initializes it will be 10 seconds
	private String city="";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		td=new ThreadDemo();
		Toast.makeText(this,"Service Created",Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cont=false;
		td.interrupt();
		try {
			td.join();//destroy the thread
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stopSelf();
		Toast.makeText(this,"Service Destroy",Toast.LENGTH_SHORT).show();
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
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			String time=extras.getString("time");
			refTime=refreshTime(time);
			city=extras.getString("city");
		}
		td.start();
		return super.onStartCommand(intent, flags, startId);
	}

	//converts the string selection to an int of seconds
	public int refreshTime(String time)
	{
		if(time.trim().equals("Every 30 seconds"))
			return 30;
		else if(time.trim().equals("Every 1 minute"))
			return 60;
		else if(time.trim().equals("Every 30 minutes"))
			return 1800;
		else if(time.trim().equals("Every 1 hour"))
			return 3600;
		else if(time.trim().equals("Every 6 hours"))
			return 21600;
		else if(time.trim().equals("Every 12 hours"))
			return 43200;
		else if(time.trim().equals("Every 24 hours"))
			return 86400;
		return 5;
	}

	private class ThreadDemo extends Thread{
		public boolean isNetworkAvailable() {
			ConnectivityManager cm = (ConnectivityManager) 
					getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			// if no network is available networkInfo will be null
			// otherwise check if we are connected
			if (networkInfo != null && networkInfo.isConnected()) {
				return true;
			}
			return false;
		} 
		//reading the data from web site
		private String readStream(InputStream in) {
			BufferedReader reader = null;
			StringBuffer buffer = new StringBuffer();
			try {
				reader = new BufferedReader(new InputStreamReader(in));
				String line = "";
				while ((line = reader.readLine()) != null) {
					buffer.append(line + "\r\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			Log.i("info",buffer.toString() );
			return buffer.toString();
		} 
		@Override
		public void run() {
			super.run();
			while(cont)
			{
				try{
					sleep(refTime*1000);
					if (isNetworkAvailable())
						Log.i("NetworkAvailable", "true");
					else
						Log.i("NetworkAvailable", "false");

					AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
					RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widget_layout);
					ComponentName thisWidget = new ComponentName(getApplicationContext(), myWidget.class);


					String des="";
					String icon="";
					String temp="";
					String tempMin="";
					String tempMax="";
					String location="";
					try {
						String tempUrl="http://api.openweathermap.org/data/2.5/find?q="+city+"&units=metric";
						URL url = new URL(tempUrl);
						HttpURLConnection con = (HttpURLConnection) url.openConnection();
						String weatherJson=readStream(con.getInputStream());

						con.disconnect();

						int  indexDescription=weatherJson.indexOf("description");
						int  indexIcon=weatherJson.indexOf("icon");
						int  endIndexIcon=weatherJson.indexOf("}]}", indexIcon);
						int indexTemp=weatherJson.indexOf("temp");
						int indexTempMin=weatherJson.indexOf("temp_min");
						int indexTempMax=weatherJson.indexOf("temp_max");
						int indexLocation=weatherJson.indexOf("name");
						int endIndexLocation=weatherJson.indexOf(",",indexLocation);

						des=weatherJson.substring(indexDescription+14, indexIcon-3).trim();
						icon=weatherJson.substring(indexIcon+7,endIndexIcon-1).trim();
						temp=weatherJson.substring(indexTemp+6, indexTemp+11);
						float f = Float.valueOf(temp.replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
						temp=Float.toString(f);
						tempMin=weatherJson.substring(indexTempMin+10, indexTempMin+15);
						f = Float.valueOf(tempMin.replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
						tempMin=Float.toString(f);
						tempMax=weatherJson.substring(indexTempMax+10, indexTempMax+15);
						f = Float.valueOf(tempMax.replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
						tempMax=Float.toString(f);
						location=weatherJson.substring(indexLocation+7, endIndexLocation-1);


					}catch(Exception e){
						e.getMessage();
					}


					String iconUrl = "http://openweathermap.org/img/w/"+icon+".png";


					Date d1 = new Date();
					Calendar c1 = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
					c1.setTime(d1);
					String str = sdf.format(c1.getTime());
					//views.setTextViewText(R.id.textViewCity, String.valueOf(t));
					views.setTextViewText(R.id.textViewDescribe,des);
					views.setTextViewText(R.id.textViewTemper,temp+"c");
					views.setTextViewText(R.id.textViewMinTemper,"Min: "+tempMin);
					views.setTextViewText(R.id.textViewMaxTemper,"Max: "+tempMax);
					views.setTextViewText(R.id.textViewCity,location);

					views.setTextViewText(R.id.textViewUpdate, str);
					views.setImageViewBitmap(R.id.imageView1, getBitmapFromURL(iconUrl));
					appWidgetManager.updateAppWidget(thisWidget, views);
					appWidgetManager.updateAppWidget(mAppWidgetId, views);


				}catch(Exception e){
					e.getMessage();
				}
			}
		}
		//receiving the weather icon and connecting it to a bitmap
		public Bitmap getBitmapFromURL(String src) {
			try {
				Log.e("src",src);
				URL url = new URL(src);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				Log.e("Bitmap","returned");
				return myBitmap;
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("Exception",e.getMessage());
				return null;
			}
		}


	}


}

