//this activity presents the sub grades of a specific exercise that was chosen by the user
package com.example.targil2;

import java.util.Iterator;
import java.util.List;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Sub_grades extends Activity {

	private int rows=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sub_grades);
		Intent in=getIntent();
		String[] pageVal=in.getStringArrayExtra("pageVals");

		//getting ex and id number from main activity
		int ex=in.getIntExtra("ex", 0);
		int id=in.getIntExtra("id", 0);
		rows=(int)pageVal.length/3+1;

		DBHandler dbh=new DBHandler(this);
		List<String> subGrades=dbh.getSubGrades(id, ex, rows);//receives a list containing the sub grades of a specific exercise 
		Iterator<String> it=subGrades.iterator();
		String temp="";
		while(it.hasNext())
		{
			String value=(String)it.next();
			temp+=value+" ";
		}
		String[] fromDB=temp.split(" ");
		if(fromDB.length<2)
		{
			Toast.makeText(getApplicationContext(), "Values do not exist in DB", Toast.LENGTH_SHORT).show();
			Intent inte = new Intent(this,MainActivity.class);
			startActivity(inte);
			return;
		}

		//the first 4 rows of the layout are fixed with data that changes by what the user asked to see
		LinearLayout parentLayout=(LinearLayout)findViewById(R.id.parentSubLinearLayout);
		LinearLayout[] lLayout=new LinearLayout[rows+4];
		
		lLayout[0]=new LinearLayout(this);
		lLayout[0].setId(0);
		lLayout[0].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		TextView tvEx=new TextView(this);
		tvEx.setText("Excercise number: "+ex);
		lLayout[0].addView(tvEx);
		parentLayout.addView(lLayout[0]);
		
		lLayout[1]=new LinearLayout(this);
		lLayout[1].setId(1);
		lLayout[1].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		TextView tvId=new TextView(this);
		tvId.setText("ID: "+id);
		lLayout[1].addView(tvId);
		parentLayout.addView(lLayout[1]);
		
		lLayout[2]=new LinearLayout(this);
		lLayout[2].setId(2);
		lLayout[2].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		TextView tvDate=new TextView(this);
		tvDate.setText("Updated date: "+fromDB[0]);
		lLayout[2].addView(tvDate);
		parentLayout.addView(lLayout[2]);
		
		lLayout[3]=new LinearLayout(this);
		lLayout[3].setId(3);
		lLayout[3].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		TextView tvTime=new TextView(this);
		tvTime.setText("Updated time: "+fromDB[1]);
		lLayout[3].addView(tvTime);
		parentLayout.addView(lLayout[3]);
		
		//this is were the dynamic data is added to the layout
		for (int i=0;i<rows;i++)
		{
			lLayout[i+4]=new LinearLayout(this);
			lLayout[i+4].setId(i+4);
			lLayout[i+4].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			TextView tvTemp=new TextView(this);
			tvTemp.setText(pageVal[i*3]+": "+fromDB[i+2]);
			lLayout[i+4].addView(tvTemp);
			parentLayout.addView(lLayout[i+4]);
		}
		//painting the rows
		for(int j=0;j<rows+4;j++)
		{
			if(j%2==0)
				lLayout[j].setBackgroundColor(Color.GREEN);
			else
				lLayout[j].setBackgroundColor(Color.RED);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sub_grades, menu);
		return true;
	}

}
