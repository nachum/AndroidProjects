package com.example.babymonitor;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

//this class lets the user pick a method, enter phone number and start/stop service
//there are more settings available by pressing the menu button.
public class MainActivity extends Activity {

	private Intent intent;
	private int sens=0;
	private String time="";
	static final int REQUEST_CODE_EXAMPLE=0;
	public static Boolean finished=true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SharedPreferences set=getSharedPreferences("mySet", MODE_PRIVATE);
		sens=set.getInt("sharedSensitivity", 0);
		time=set.getString("timeSelect", "Every 1 minute");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.menu, menu);
		return true;
	}
	//starting the service and passing it the relevant variables
	public void clickedStart(View v)
	{
		if (!finished) // if app did not finish closing camera and recorder it won't allow the user to start the service until every thing is closed  
			Toast.makeText(this,"Please wait until app is initialized",Toast.LENGTH_SHORT).show();
		else
		{
			EditText ph = (EditText)findViewById(R.id.editText1);
			String phoneNum=ph.getText().toString().trim();
			CheckBox b1 = (CheckBox)findViewById(R.id.checkBox1);
			boolean sms = false;
			boolean phone = false;
			boolean photo = false;
			if(b1.isChecked())
				sms = true;
			CheckBox b2 = (CheckBox)findViewById(R.id.checkBox2);
			if(b2.isChecked())
				phone = true;
			CheckBox b3 = (CheckBox)findViewById(R.id.checkBox3);
			if(b3.isChecked())
				photo = true;
			if(!photo && !sms && !phone) //checking if method was picked
				Toast.makeText(this,"Please pick a method",Toast.LENGTH_SHORT).show();
			else if (phoneNum.equals("")) // checking if a number was entered
				Toast.makeText(this,"Enter phone number",Toast.LENGTH_SHORT).show();
			else if(sms && phone)
				Toast.makeText(this,"please select SMS or phoneCall",Toast.LENGTH_SHORT).show();
			else
			{
				intent=new Intent(this,myService.class);
				intent.putExtra("phoNum", phoneNum);	//passing the phone number
				intent.putExtra("sens", sens);			//passing the sensitivity 	
				intent.putExtra("sms", sms);			//if "Send sms" was checked "true" value will be passed
				intent.putExtra("phone", phone);		//if call "Make phoneCall" checked "true" value will be passed
				intent.putExtra("photo", photo);		//if "Take picture" was checked "true" value will be passed
				intent.putExtra("time", time);			//passing the time to wait between each time the methods should be invoked
				finished = false;
				startService(intent);
			}
		}
	}
	//stop the service
	public void clickedStop(View v)
	{
		intent=new Intent(this,myService.class);
		stopService(intent);

		//startButton.setEnabled(true);
	}

	//starting the menu activity if it was selected
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{
		case R.id.menu_Settings:
			// Single menu item is selected do something
			Intent i= new Intent(this,SettingsActivity.class);
			startActivityForResult(i,REQUEST_CODE_EXAMPLE);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	} 

	//getting values from Settings activity(menu)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_CODE_EXAMPLE){
			if(resultCode==RESULT_OK)
			{
				sens=data.getIntExtra("sensitivity",0);
				time=data.getStringExtra("timeSelect");
				//saving data to phone memory
				SharedPreferences settings = getSharedPreferences("mySet", MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt("sharedSensitivity", sens);
				editor.putString("sharedTime", time);
				editor.commit();

			}
		}
	}

}
