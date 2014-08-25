//This is the settings activity, the user can reach this activity by touching the widget.
//In this activity the user can select the city he wants on the widget and the time between each update.
//To start the widget the user must press 'Start service' and to stop it press 'Stop service' 
package com.example.weatherwidget;



import android.os.Bundle;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Spinner;

public class Settings extends Activity {

	private Intent intent;
	private int mAppWidgetId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID, 
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	//starting the service and collecting the settings that were selected
	public void clicked_start(View v)
	{
		Spinner spTime=(Spinner) findViewById(R.id.spinnerTime);//time interval
		Spinner spCity=(Spinner) findViewById(R.id.spinnerCity);//city
		String time =String.valueOf(spTime.getSelectedItem());
		String city =String.valueOf(spCity.getSelectedItem());
		intent=new Intent(this,weather_service.class);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("time", time);
		intent.putExtra("city", city);
		startService(intent);


	}
	//stopping the service
	public void clicked_stop(View v)
	{
		Intent intent=new Intent(this,weather_service.class);
		stopService(intent);

	}


}
