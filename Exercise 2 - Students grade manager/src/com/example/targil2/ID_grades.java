//this activity presents a students grade sheet 
package com.example.targil2;

import java.util.ArrayList;
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
import android.view.Gravity;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ID_grades extends Activity {

	private int rows=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_id_grades);
		List<Double> GradesForAvg=new ArrayList<Double>();
		Intent in=getIntent();
		String[] pageVal=in.getStringArrayExtra("pageVals");
		int id=in.getIntExtra("id", 0);
		rows=(int)pageVal.length/3+1;

		DBHandler dbh=new DBHandler(this);
		List<List<String>> subGrades=dbh.getGradesId(id, rows);

		//this part sets the headline with the chosen students id 
		LinearLayout parentLayout=(LinearLayout)findViewById(R.id.parentIdLinearLayout);
		LinearLayout[] lLayout=new LinearLayout[subGrades.size()+2];
		lLayout[0]=new LinearLayout(this);
		lLayout[0].setId(0);
		lLayout[0].setOrientation(LinearLayout.HORIZONTAL);
		lLayout[0].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		TextView tvId=new TextView(this);
		tvId.setText("ID:\t"+id);
		tvId.setTextColor(Color.GREEN);
		tvId.setTextSize(20);
		lLayout[0].addView(tvId);
		parentLayout.addView(lLayout[0]);

		for(int i=1;i<=subGrades.size();i++)
		{
			//this part creates the 'father' layout for each grade
			lLayout[i]=new LinearLayout(this);
			lLayout[i].setId(0);
			lLayout[i].setOrientation(LinearLayout.VERTICAL);
			lLayout[i].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));

			List<String> temp=subGrades.get(i-1);
			double grade=calculateGrade(temp, pageVal, rows);
			GradesForAvg.add(grade);

			//this part creates the 2 sub layouts that are in the 'fathers' layout
			LinearLayout[] subLayout=new LinearLayout[2];
			//first one includes: exercise number and grade
			subLayout[0]=new LinearLayout(this);
			subLayout[0].setId(i+2+subGrades.size());
			subLayout[0].setOrientation(LinearLayout.HORIZONTAL);
			subLayout[0].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,Gravity.CENTER ));
			TextView tvEx=new TextView(this);
			String exNum=temp.get(1);
			tvEx.setText("EX:\t"+exNum+"\t\t");
			TextView tvGrade=new TextView(this);
			tvGrade.setText("Grade:\t"+grade);
			subLayout[0].addView(tvEx);
			subLayout[0].addView(tvGrade);
			lLayout[i].addView(subLayout[0]);
			//second sub layout includes: date and time
			subLayout[1]=new LinearLayout(this);
			subLayout[1].setId(i+2+subGrades.size());
			subLayout[1].setOrientation(LinearLayout.HORIZONTAL);
			subLayout[1].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			String dAndT=temp.get(2);
			String[] dateAndTime=dAndT.split(" ");
			TextView tvDate=new TextView(this);
			tvDate.setText("Date:\t"+dateAndTime[0]+"\t\t");
			TextView tvTime=new TextView(this);
			tvTime.setText("Time:\t"+dateAndTime[1]);
			subLayout[1].addView(tvDate);
			subLayout[1].addView(tvTime);
			lLayout[i].addView(subLayout[1]);
			if(i%2==0)
				lLayout[i].setBackgroundColor(Color.YELLOW);
			else
				lLayout[i].setBackgroundColor(Color.GRAY);
			parentLayout.addView(lLayout[i]);
		}
		
		//this part writes underneath the grades there total average
		double avg=0;
		Iterator<Double> it=GradesForAvg.iterator();
		while(it.hasNext())
		{
			avg+=(Double)it.next();
		}
		avg=(double)avg/subGrades.size();
		lLayout[subGrades.size()+1]=new LinearLayout(this);
		lLayout[subGrades.size()+1].setId(0);
		lLayout[subGrades.size()+1].setOrientation(LinearLayout.HORIZONTAL);
		lLayout[subGrades.size()+1].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		TextView tvTotal=new TextView(this);
		tvTotal.setText("Total average:\t"+avg);
		tvTotal.setTextSize(18);
		tvTotal.setTextColor(Color.rgb( 84, 223, 205));
		lLayout[subGrades.size()+1].addView(tvTotal);
		parentLayout.addView(lLayout[subGrades.size()+1]);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.id_grades, menu);
		return true;
	}
	
	//method to calculate the total grade out of the sub grades
	private double calculateGrade(List<String> sub, String[] pageVal,int rows)
	{
		double grade=0;
		int counter=1;
		for (int i=0;i<rows;i++)
		{
			String value=(String)sub.get(i+3);
			double temp=(double)Integer.parseInt(value)/10;
			int valFromTxt=Integer.parseInt(pageVal[counter]);
			Log.d("paragWeight", valFromTxt+", "+temp+", "+value);
			grade+=temp*valFromTxt;
			counter+=3;
		}

		return grade;

	}

}
