package com.example.babymonitor;



import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

//this class determines the sensitivity of the microphone and the time between actions
public class SettingsActivity extends Activity {

	private SeekBar volumeControl = null;
	private Intent i;
	private Spinner delayTime; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		i=new Intent();
		setResult(RESULT_OK,i);

		SharedPreferences set=getSharedPreferences("mySet", MODE_PRIVATE);
		int progTime=set.getInt("sharedSensitivity", 0);

		volumeControl = (SeekBar) findViewById(R.id.volume_bar);
		volumeControl.setProgress(progTime);
		TextView tv=(TextView)findViewById(R.id.textViewNumber);
		tv.setText(progTime+"");
		// seek bar controller
		volumeControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				progressChanged = progress;
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			//post and save value after releasing finger
			public void onStopTrackingTouch(SeekBar seekBar) {
				TextView tv=(TextView)findViewById(R.id.textViewNumber);
				tv.setText(progressChanged+"");
				i.putExtra("sensitivity", progressChanged);
			}
		});

		delayTime=(Spinner)findViewById(R.id.spinner1);
		//time selected by spinner
		delayTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				String timeChoice=delayTime.getSelectedItem().toString();
				i.putExtra("timeSelect",timeChoice );

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

}
