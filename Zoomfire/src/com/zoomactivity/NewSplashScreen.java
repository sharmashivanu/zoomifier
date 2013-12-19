package com.zoomactivity;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class NewSplashScreen extends Activity{
	protected boolean _active = true;
	protected int _splashTime = 1000;
	
	Handler handler;
	Runnable runnable;
	Thread splashTread;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		handler=new Handler();
	
		final Intent in = new Intent(this,ZoomifierSplashScreen.class);
	
		runnable=new Runnable() {
			
			@Override
			public void run() {
				startActivity(in);
				finish();
			}
		};
		
	
	
		handler.postDelayed(runnable, 3000);
		
	}

	

}
