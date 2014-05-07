package com.noeuli.fatapp;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
    private static final boolean LOGD = true;
    private static final String TAG = "MainActivity";
    
    private TextView mTextMonthTitle;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		setupViews();
	}
	
	private void setupViews() {
	    measureScreenSize();
	    setupMonthTitle();
	    setupDayOfWeekTitle();
	    setupMonthCalendar();
	}
	
	private int mScreenWidth;
	private int mScreenHeight;
	private int mWindowWidth;
	private int mWindowHeight;
	private int mCalendarWidth;
	private int mCalendarHeight;
	private int mDayWidth;
	private int mDayHeight;
	
	private void measureScreenSize() {
	    Display disp = getWindowManager().getDefaultDisplay();
	    
	    View v = getWindow().getDecorView();
	    mWindowWidth = v.getWidth();
	    mWindowHeight = v.getHeight();
	    
	    Rect r = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
	    Log.d(TAG, "measureScreenSize() baseLayout=" + mWindowWidth + "x" + mWindowHeight + " r=" + r);
	}
	
	private void setupMonthTitle() {
        mTextMonthTitle = (TextView) findViewById(R.id.txtMonthTitle);
        String dateTime = DateFormat.getDateInstance().format(new Date());
        mTextMonthTitle.setText(dateTime);
	}
	
	private void setupDayOfWeekTitle() {
	    Calendar calendar = Calendar.getInstance();
	    LinearLayout mDayOfWeekLayout = (LinearLayout) findViewById(R.id.day_of_week_title);
	    
	    for (int i=0; i<7; i++) {
	        TextView tv = new TextView(this);
	        tv.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.KOREA));
	        mDayOfWeekLayout.addView(tv);
	    }
	}
	
	private void setupMonthCalendar() {
	    
	}
	
	private void datePrintTest() {
        DateFormat[] formats = new DateFormat[] {
            DateFormat.getDateInstance(),
            DateFormat.getDateTimeInstance(),
            DateFormat.getTimeInstance(),
        };
        for (DateFormat df : formats) {
            System.out.println(df.format(new Date()));
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            System.out.println(df.format(new Date()));
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    @Override
    public void onClick(View v) {
        if (v==null) return;
        int id = v.getId();
        Log.d(TAG, "onClick() view=" + v + " id=" + id);
    }

}
