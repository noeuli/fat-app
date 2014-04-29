package com.noeuli.fatapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CalendarView;

public class MainActivity extends Activity {
    
    private CalendarView mCalendarView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mCalendarView = (CalendarView) findViewById(R.id.calendar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
