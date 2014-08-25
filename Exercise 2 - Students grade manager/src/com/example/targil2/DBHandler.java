//this class is in charge of the Data Base, creates it and doe's all kind of stuff as needed
package com.example.targil2;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import au.com.bytecode.opencsv.CSVWriter;

public class DBHandler extends SQLiteOpenHelper implements BaseColumns {

	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "Grades";
	private static final String TABLE_NAME = "lab_grades";

	public DBHandler(Context context) {
		super(context,DATABASE_NAME, null, DATABASE_VERSION);

		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String temp="CREATE TABLE " + TABLE_NAME + "("
				+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, zehut INTEGER, targilNum INTEGER, currTimeDate DATETIME)";
		db.execSQL(temp);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		// Create tables again
		onCreate(db);

	}

	public void addColumn(String[] col)
	{
		String base="ALTER TABLE "+TABLE_NAME+" ADD ";
		//adding columns as much as needed depending on the initializing file.
		for(int i=0;i<col.length;i+=3)
		{
			String add=base+col[i]+" INTEGER";
			getWritableDatabase().execSQL(add);
		}

	}

	//method to add grades to the DB
	@SuppressLint("SimpleDateFormat")
	public int addGrades(List<Integer> ls,Context context)
	{
		SharedPreferences settings = context.getSharedPreferences("MyPres", Context.MODE_PRIVATE);
		String page = settings.getString("page", "");
		String[] arrPage=page.split(" ");

		SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		int counter=0;
		String strDate=sdf.format(new Date());//creates the date and time the data was added
		SQLiteDatabase db = getWritableDatabase();
		Iterator<Integer> it=ls.iterator();
		ContentValues values = new ContentValues();
		while(it.hasNext())
		{
			int value=(Integer)it.next();
			if (counter==0)
			{
				values.put("targilNum", value);
			}

			else if (counter==1)
			{
				values.put("zehut", value);
				values.put("currTimeDate", strDate); 
			}
			else
			{
				values.put(arrPage[(counter-2)*3].trim(), value);
			}
			counter++;
		}
		try{
			// Inserting Row
			db.insert(TABLE_NAME, null, values);
			db.close(); // Closing database connection
		}catch (SQLiteException e) {
			return 0;
		}


		return 1;
	}

	public int getCoursesCount() {
		String countQuery = "SELECT  * FROM " + TABLE_NAME;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int num=cursor.getCount();
		cursor.close();
		return num;
	}

	//creates a list with the sub grades of a specific exercise
	public List<String> getSubGrades(int id, int ex, int subNum)
	{
		String getSub="SELECT * FROM "+TABLE_NAME+" WHERE zehut="+id+" AND targilNum="+ex;
		Log.d(DATABASE_NAME, getSub);
		List<String> subGrades=new ArrayList<String>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(getSub, null);
		if (cursor.moveToFirst()) {
			String dt=cursor.getString(3);
			subGrades.add(dt);
			for(int i=0;i<subNum;i++)
			{
				String temp=cursor.getString(4+i);
				subGrades.add(temp);
			}
		}
		cursor.close();
		return subGrades;

	}
	
	//method to return all the grades that are in the DB that have the given id
	public List<List<String>> getGradesId(int id, int subNum)//id=students id. subNum=number of columns added dynamically
	{
		List<List<String>> myList = new ArrayList<List<String>>();
		String getSub="SELECT * FROM "+TABLE_NAME+" WHERE zehut="+id;
		Log.d(DATABASE_NAME, getSub);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(getSub, null);
		if (cursor.moveToFirst()) {
			do {
				List<String> subGrades=new ArrayList<String>();
				for(int i=1;i<4+subNum;i++)
				{
					String temp=cursor.getString(i);
					subGrades.add(temp);
				}
				myList.add(subGrades);

			} while (cursor.moveToNext());
		}

		return myList;
	}
	
	//method to return all the grades of a specific exercise that are in the DB 
	public List<List<String>> getGradesByEx(int subNum, int ex)//subNum=number of columns added dynamically. ex= exercise number
	{
		List<List<String>> myList = new ArrayList<List<String>>();
		String getSub="SELECT * FROM "+TABLE_NAME+" WHERE targilNum="+ex;
		Log.d(DATABASE_NAME, getSub);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(getSub, null);
		if (cursor.moveToFirst()) {
			do {
				List<String> subGrades=new ArrayList<String>();
				for(int i=1;i<4+subNum;i++)
				{
					String temp=cursor.getString(i);
					subGrades.add(temp);
				}
				myList.add(subGrades);

			} while (cursor.moveToNext());
		}

		return myList;
	}
	
	//method to return a list of all exercise numbers that exist in the DB
	public List<Integer> ex_numbers()
	{
		List<Integer> temp = new ArrayList<Integer>();
		String getEx="SELECT DISTINCT targilNum FROM "+TABLE_NAME+" ORDER BY targilNum ASC";
		Log.d("get ex", getEx);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(getEx, null);
		if (cursor.moveToFirst()) {
			do {
				int ex=cursor.getInt(0);
				temp.add(ex);
			} while (cursor.moveToNext());
		}
		return temp;
	}
	
	//method that writes the data from the DB to the csv file
	public void temp(CSVWriter csvWrite, int rows)
	{
		String getEx="SELECT * FROM lab_grades";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor curCSV = db.rawQuery(getEx, null);
		csvWrite.writeNext(curCSV.getColumnNames());
		if (curCSV.moveToFirst()) {
			do {
				//Which column you want to export
				String arrStr[] = new String[rows+4];
				for (int j=0;j<rows+4;j++)
					arrStr[j]=curCSV.getString(j);
				csvWrite.writeNext(arrStr);

			} while(curCSV.moveToNext());
		}
		try {
			csvWrite.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		curCSV.close();
	}

}
