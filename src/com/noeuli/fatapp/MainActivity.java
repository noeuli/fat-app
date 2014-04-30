package com.noeuli.fatapp;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CalendarView;

public class MainActivity extends Activity implements OnClickListener {
    private static final boolean LOGD = true;
    private static final String TAG = "MainActivity";
    
    private CalendarView mCalendarView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initCalendar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void initCalendar() {
        mCalendarView = (CalendarView) findViewById(R.id.calendar);
        mCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        mCalendarView.setFocusedMonthDateColor(Color.BLACK);
        mCalendarView.setUnfocusedMonthDateColor(Color.GRAY);
        mCalendarView.setSelectedWeekBackgroundColor(Color.CYAN);
        mCalendarView.setShowWeekNumber(false);
        mCalendarView.setWeekSeparatorLineColor(Color.RED);
        mCalendarView.setOnClickListener(this);
	}

    @Override
    public void onClick(View v) {
        if (v==null) return;
        int id = v.getId();
        Log.d(TAG, "onClick() view=" + v + " id=" + id);
        
        if (id == R.id.calendar) {
            long selectedDate = mCalendarView.getDate();
            Log.d(TAG, "onClick(): date=" + new Date(selectedDate));
        }
    }

}
