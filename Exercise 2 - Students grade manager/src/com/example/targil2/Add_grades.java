//this activity allows the user to enter grades to the DB
package com.example.targil2;

import java.util.ArrayList;
import java.util.List;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Add_grades extends Activity {

	private int rows=0;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint({ "NewApi", "InlinedApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_grades);
		Intent in=getIntent();
		String[] pageVal=in.getStringArrayExtra("pageVals");
		rows=(int)pageVal.length/3+1;//determining how many sub grades are needed to be filled in
		//the sub grades amount is dynamic so the text fields are added now
		LinearLayout parentLayout=(LinearLayout)findViewById(R.id.parentLinearLayout);
		LinearLayout[] lLayout=new LinearLayout[rows];
		for (int j=0;j<rows;j++)
		{
			lLayout[j]=new LinearLayout(this);
			lLayout[j].setId(j);
			lLayout[j].setOrientation(LinearLayout.HORIZONTAL);
			lLayout[j].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			if(j%2==0)
				lLayout[j].setBackgroundColor(Color.GREEN);
			else
				lLayout[j].setBackgroundColor(Color.RED);
			TextView tv=new TextView(this);
			tv.setText(pageVal[j*3]+": ");
			EditText et=new EditText(this);
			et.setId(rows+j);
			et.setEms(4);
			et.setInputType(InputType.TYPE_CLASS_NUMBER);
			lLayout[j].addView(tv);
			lLayout[j].addView(et);
			parentLayout.addView(lLayout[j]);
		}


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_grades, menu);
		return true;
	}

	//method to collect the data that was filled in and updating the DB.
	public void addGrades(View v)
	{
		List<Integer> ls=new ArrayList<Integer>();//list for saving the data until it's uploaded to the DB
		EditText edNum=(EditText)findViewById(R.id.editTextEx);
		EditText edId=(EditText)findViewById(R.id.editTextId);
		String temp=edNum.getText().toString().trim();
		String temp2=edId.getText().toString().trim();
		if (temp.equals("") || temp2.equals(""))
		{
			Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
			return;
		}
		int exNum=Integer.parseInt(temp);
		int id=Integer.parseInt(temp2);
		ls.add(exNum);
		ls.add(id);
		for (int i=0;i<rows;i++)
		{
			EditText edTemp=(EditText)findViewById(rows+i);
			temp=edTemp.getText().toString().trim();
			if (temp.equals(""))
			{
				Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
				return;
			}
			int tempNum=Integer.parseInt(temp);
			if ( tempNum<1 || tempNum>10 )
			{
				Toast.makeText(getApplicationContext(), "Please enter grades between 1-10", Toast.LENGTH_SHORT).show();
				return;
			}
			ls.add(tempNum);
		}
		DBHandler dbh=new DBHandler(this);
		int ok=dbh.addGrades(ls,getApplicationContext());
		if(ok==1)
			Toast.makeText(getApplicationContext(), "DataBase updated", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(getApplicationContext(), "DataBase failed to update", Toast.LENGTH_SHORT).show();
	}

}
