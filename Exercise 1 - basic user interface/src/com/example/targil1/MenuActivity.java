//In this activity the user can change background and circle color   
package com.example.targil1;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class MenuActivity extends Activity {

	private Spinner backSpinner, circleSpinner;
	private boolean backInit;
	private boolean circInit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		backSpinner = (Spinner) findViewById(R.id.spinner1);
		circleSpinner = (Spinner) findViewById(R.id.spinner2);
		backInit=true;
		circInit=true;
		final Intent i =getIntent();
		setResult(RESULT_OK, i);
		
		//listener for the background color selection
		backSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String backChoice=backSpinner.getSelectedItem().toString();
				i.putExtra("backSelect",backChoice );
			}
		});

		//listener for the circle color selection
		circleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String circleChoice=circleSpinner.getSelectedItem().toString();
				i.putExtra("circleSelect",circleChoice );
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

}
