//This is the main activity where the user can access the activity of adding students grades.
//In addition the user can get a specific grade of a student, get all grades of a student, get average grade of all exercises
//and download the entire data base to a csv form.
package com.example.targil2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;

public class MainActivity extends Activity {

	private String[] page;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//read the initializing sheet to determine each sub category score. 
		InputStream is=getResources().openRawResource(R.raw.parag_weight);
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		StringBuilder total = new StringBuilder();
		String line;
		try {
			while ((line = r.readLine()) != null) {
				total.append(" "+line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String f=total.toString().trim();
		String[] adding=f.split(" ");
		page=adding;
		SharedPreferences settings = getSharedPreferences("MyPres", MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		StringBuilder builder = new StringBuilder();
		for(String s : adding) {
			builder.append(s+" ");
		}
		String tempPage = builder.toString();
		editor.putString("page", tempPage);
		editor.commit();
		boolean addCol=settings.getBoolean("addCol", true);
		if (addCol==true)
		{
			editor = settings.edit();
			editor.putBoolean("addCol", false);
			editor.commit();

			DBHandler dbh=new DBHandler(this);
			dbh.addColumn(page);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//open the activity that adds grades to the DB.
	public void clicked_add(View v)
	{
		Intent i = new Intent(this,Add_grades.class);
		i.putExtra("pageVals", page);
		startActivity(i);
	}

	//method to get the sub grades of a specific students exercise
	public void clicked_getSub(View v)
	{
		EditText etEx=(EditText)findViewById(R.id.editTextEX);
		EditText etId=(EditText)findViewById(R.id.editTextID);
		String temp=etEx.getText().toString().trim();
		String temp2=etId.getText().toString().trim();
		if(temp.equals("") || temp2.equals(""))
		{
			Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
			return;
		}
		int exNum=Integer.parseInt(temp);
		int idNum=Integer.parseInt(temp2);
		Intent i = new Intent(this,Sub_grades.class);
		//passing arguments to the activity
		i.putExtra("pageVals", page);
		i.putExtra("ex", exNum);
		i.putExtra("id", idNum);
		//starting the activity that shows the sub grades
		startActivity(i);

	}
	
	//method to get all the grades of a specific student by his id
	public void getById(View v)
	{
		EditText etId=(EditText)findViewById(R.id.editTextGetAllID);
		String temp=etId.getText().toString().trim();
		if(temp.equals(""))
		{
			Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
			return;
		}
		int id=Integer.parseInt(temp);
		//passing arguments to the activity
		Intent i=new Intent(this, ID_grades.class);
		i.putExtra("pageVals", page);
		i.putExtra("id", id);
		startActivity(i);
	}

	//method to get average grades for the entire class
	public void allExGrades(View v)
	{
		Intent i=new Intent(this, Avg_grades.class);
		i.putExtra("pageVals", page);
		startActivity(i);
	}

	//method to download the entire DB to a csv form. 
	public void downloadDb(View v)
	{

		DBHandler dbh = new DBHandler(this);
		File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);//place the file in devices storage.
		if (!exportDir.exists())
		{
			exportDir.mkdirs();
		}

		File file = new File(exportDir, "EntireDb.csv");
		int rows=(int)page.length/3+1;
		try
		{
			file.createNewFile();               
			CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
			dbh.temp(csvWrite,rows);
		}

		catch(FileNotFoundException e) {
			Log.e("File not found" , e.toString());
		}
		catch(Exception sqlEx)
		{
			Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
		}

		Toast.makeText(getApplicationContext(),"DB downloaded", Toast.LENGTH_SHORT).show();
	}

}
