//This activity contains the surface of the circles and 2 buttons '+' and '-' to make the circles bigger and smaller
package com.example.targil1;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class MainActivity extends Activity implements Runnable {

	private SurfaceView surface;
	private SurfaceHolder holder;
	private boolean isRunning=false;
	private Thread thread;
	private int radius;		//circle radius
	private Paint paint=new Paint();
	static final int REQUEST_CODE_EXAMPLE=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		surface = (SurfaceView) findViewById(R.id.mysurface);
		holder = surface.getHolder();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	//to start the settings activity
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{
		case R.id.menu_setting:
			Intent i = new Intent(this,MenuActivity.class);
			startActivityForResult(i,REQUEST_CODE_EXAMPLE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	} 

	@Override
	protected void onPause(){
		super.onPause();
		pause();
	}

	//when paused the method saves the radius length
	private void pause() {
		SharedPreferences settings = getSharedPreferences("mySet", MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("radius", radius);
		editor.commit();
		isRunning = false;
		while (true)
		{
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		thread = null;
	}

	@Override
	protected void onResume(){
		super.onResume();
		resume();
	}


	private void resume() {
		SharedPreferences settings = getSharedPreferences("mySet", MODE_PRIVATE);
		radius = settings.getInt("radius",31);
		isRunning = true;
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (isRunning){
			if(!holder.getSurface().isValid())
				continue;
			Canvas canvas = holder.lockCanvas();

			drawCircles(canvas);

			holder.unlockCanvasAndPost(canvas);
		}
	}

	private void drawCircles(Canvas canvas) {
		Rect r = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
		changePaintColor("backColor");
		canvas.drawRect(r, paint);
		changePaintColor("circleColor");
		int numWidth=canvas.getWidth();
		int numHeight=canvas.getHeight();
		for (int i=2;i<numHeight;i+=(radius*2+5))
			for(int j=2;j<numWidth;j+=(radius*2+5))
			{
				canvas.drawCircle(j+radius, i+radius, radius, paint);
			}
	}

	public void clickedPlus(View v)
	{
		if (radius<146)
			radius+=1;
	}
	public void clickedMinus(View v)
	{
		if (radius>2)
			radius-=1;
	}


	//this method gets the users settings from the settings activity and saves them
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_CODE_EXAMPLE){
			if(resultCode==RESULT_OK)
			{
				String back=data.getStringExtra("backSelect");
				String circ=data.getStringExtra("circleSelect");
				SharedPreferences settings = getSharedPreferences("mySet", MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("backColor", back);
				editor.putString("circleColor", circ);
				editor.commit();
			}
		}
	}

	//changing colors
	public void changePaintColor(String color)
	{
		SharedPreferences settings = getSharedPreferences("mySet", MODE_PRIVATE);
		String changeColor; 
		if(color.equals("backColor"))
			changeColor= settings.getString(color, "Red");
		else
			changeColor= settings.getString(color, "Green");
		if(changeColor.equals("Red"))
			paint.setARGB(100, 255, 0, 0);
		else if(changeColor.equals("Blue"))
			paint.setARGB(100, 0, 0, 255);
		else if(changeColor.equals("Green"))
			paint.setARGB(100, 0, 255, 0);
	}


}
