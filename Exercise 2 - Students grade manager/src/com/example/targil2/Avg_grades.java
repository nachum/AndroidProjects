//This activity presents for each exercise the average of all the students and the amount of students that have grades. 
package com.example.targil2;

import java.util.Iterator;
import java.util.List;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Avg_grades extends Activity {

	private int rows=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_avg_grades);
		Intent in=getIntent();
		String[] pageVal=in.getStringArrayExtra("pageVals");
		rows=(int)pageVal.length/3+1;//determining amount of sub grades.
		DBHandler dbh=new DBHandler(this);
		List<Integer> exNums=dbh.ex_numbers();//returns a list of all exercise numbers that exist in the DB
		Iterator<Integer> it=exNums.iterator();
		while(it.hasNext())
		{
			int value=(int)it.next();
			List<List<String>> all=dbh.getGradesByEx(rows, value);
			Iterator<List<String>> itAll=all.iterator();
			double grade=0;
			while(itAll.hasNext())
			{
				List<String> temp=itAll.next();
				grade+=calculateGrade(temp, pageVal, rows);//calculates the subGrades to one grade
			}
			int amount=all.size();
			double avg=(double)grade/amount;
			addToTable(value,avg,amount);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.avg_grades, menu);
		return true;
	}

	//create a new row in the table that shows exercise number, average score and number of students that took the test
	private void  addToTable(int value, double avg, int amount)
	{
		TableLayout tl = (TableLayout) findViewById(R.id.tableLayout1);

		TableRow row= new TableRow(this);
		TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
		row.setLayoutParams(lp);

		TextView tvEx=new TextView(this);
		tvEx.setText("  "+value+"");
		tvEx.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
		tvEx.setBackgroundResource(R.drawable.cellborder);

		TextView tvAvg=new TextView(this);
		tvAvg.setText("  "+avg+"");
		tvAvg.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
		tvAvg.setBackgroundResource(R.drawable.cellborder);

		TextView tvSt=new TextView(this);
		tvSt.setText("  "+amount+"");
		tvSt.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
		tvSt.setBackgroundResource(R.drawable.cellborder);

		row.addView(tvEx);
		row.addView(tvAvg);
		row.addView(tvSt);
		tl.addView(row);
	}

	//calculating total grade from the sub grades
	private double calculateGrade(List<String> sub, String[] pageVal,int rows)
	{
		double grade=0;
		int counter=1;
		for (int i=0;i<rows;i++)
		{
			String value=(String)sub.get(i+3);
			double temp=(double)Integer.parseInt(value)/10;
			int valFromTxt=Integer.parseInt(pageVal[counter]);//value from initializing file
			Log.d("paragWeight", valFromTxt+", "+temp+", "+value);
			grade+=temp*valFromTxt;
			counter+=3;
		}
		return grade;
	}

}
