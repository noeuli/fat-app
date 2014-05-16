package com.noeuli.fatapp;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.CalendarEntity;
import android.provider.CalendarContract.EventsEntity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity implements OnClickListener {
    private static final boolean LOGD = true;
    private static final String TAG = "MainActivity";
    
    private TextView mTextMonthTitle;
    private ArrayList<Object> mDayViewList;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupViews();
	}
	
	private void setupCalendarList() {
        mDayViewList = new ArrayList<Object>();

        ArrayList<LinearLayout> weekList = null;
        LinearLayout day = null;
        int[][] firstDayIdArray = new int[][]{
                {R.id.day1group, R.id.day2group, R.id.day3group, R.id.day4group, R.id.day5group, R.id.day6group, R.id.day7group,},
                {R.id.day8group, R.id.day9group, R.id.day10group, R.id.day11group, R.id.day12group, R.id.day13group, R.id.day14group,},
                {R.id.day15group, R.id.day16group, R.id.day17group, R.id.day18group, R.id.day19group, R.id.day20group, R.id.day21group,},
                {R.id.day22group, R.id.day23group, R.id.day24group, R.id.day25group, R.id.day26group, R.id.day27group, R.id.day28group,},
                {R.id.day29group, R.id.day30group, R.id.day31group, R.id.day32group, R.id.day33group, R.id.day34group, R.id.day35group,},
                {R.id.day36group, R.id.day37group, R.id.day38group, R.id.day39group, R.id.day40group, R.id.day41group, R.id.day42group,},
        };

        for (int i = 0; i < 6; i++) {
            weekList = new ArrayList<LinearLayout>();
            for (int j = 0; j < 7; j++) {
                int resId = firstDayIdArray[i][j];
                day = (LinearLayout) findViewById(resId);
                weekList.add(day);
            }
            mDayViewList.add(weekList);
        }
    }

    private void setupViews() {
	    findViews();

	    // display this month
        setupDisplayMonth();
        getMonthInfo();
        drawCalendar();

        // display event list
        //getCalendarTest();
        //getCalendarEventList();
	}
	
	private void findViews() {
	    // Month Title
	    mTextMonthTitle = (TextView) findViewById(R.id.txtMonthTitle);
	    
	    // all days for LinearLayout
	    setupCalendarList();
	}

    class CalendarRecord {
        int _id;
        String accName;
        String accType;
        String syncId;
        String displayName;
        int accessLevel;
        String ownerAccount;

        public CalendarRecord(int id, String an, String at, String sid, String dn, int al,
                              String oa) {
            _id = id;
            accName = an;
            accType = at;
            syncId = sid;
            displayName = dn;
            accessLevel = al;
            ownerAccount = oa;
        }

        public String toString() {
            return "_id=" + _id + "\naccName=" + accName + "\naccType=" + accType
                    + "\nsyncId=" + syncId + "\ndisplayName=" + displayName
                    + "\n accessLevel=" + accessLevel + "\nownerAccount=" + ownerAccount;
        }
    }

    private final String CALENDAR_URI = "content://com.android.calendar";
    private final int INVALID_ID = -1;
    private int mCalendarId = INVALID_ID;

    private Calendar mDisplayMonth;
    private Date mFirstDate;
    private Date mLastDate;

    private void setupDisplayMonth() {
        // Get today and clear time of day
        mDisplayMonth = Calendar.getInstance();
        mDisplayMonth.set(Calendar.HOUR_OF_DAY, 0);   // clear() would not reset the hour of day
        mDisplayMonth.clear(Calendar.MINUTE);
        mDisplayMonth.clear(Calendar.SECOND);
        mDisplayMonth.clear(Calendar.MILLISECOND);
    }

    private void moveToPrevMonth() {
        mDisplayMonth.add(Calendar.MONTH, -1);
    }

    private void moveToNextMonth() {
        mDisplayMonth.add(Calendar.MONTH, 1);
    }

    int mDayOfWeek;

    private void getMonthInfo() {
        // Get start of this month in milliseconds
        mDisplayMonth.set(Calendar.DAY_OF_MONTH, 1);
        mFirstDate = mDisplayMonth.getTime();
        mDayOfWeek = mDisplayMonth.get(Calendar.DAY_OF_WEEK);

        // Get end of this month
        mDisplayMonth.add(Calendar.MONTH, 1);
        mDisplayMonth.add(Calendar.DATE, -1);
        mLastDate = mDisplayMonth.getTime();
    }

    private void drawCalendar() {
        int weeks = mDayViewList.size();
        Log.d(TAG, "drawCalendar() date=" + mDisplayMonth + " mDayOfWeek=" + mDayOfWeek
                + " viewArraySize=" + mDayViewList.size());

        for (int week=0; week<mDayViewList.size(); week++) {
            ArrayList<LinearLayout> oneWeek = mDayViewList.get(week);
        }

    }

    // Find out calendar id
    private void getCalendarTest() {
        Uri calendarUri = Uri.parse(CALENDAR_URI + "/calendars");
        String[] selection = new String[] {
                CalendarEntity._ID,
                CalendarEntity.ACCOUNT_NAME,
                CalendarEntity.ACCOUNT_TYPE,
                CalendarEntity._SYNC_ID,
                CalendarEntity.CALENDAR_DISPLAY_NAME,
                CalendarEntity.CALENDAR_ACCESS_LEVEL,
                CalendarEntity.OWNER_ACCOUNT,
        };
        String[] condition = new String[] {
                "600",
                CalendarContract.ACCOUNT_TYPE_LOCAL,
        };
        Cursor c = getContentResolver().query(
                calendarUri, selection,
                "calendar_access_level>=? and account_type<>?", condition, null);
        if (c==null || !c.moveToFirst()) {
            // System does not have any calendars.
            Log.e(TAG, "No Calenders found.");
            return;
        }

        try {
            int i=0;
            int rows = c.getCount();
            int cols = c.getColumnCount();

            Log.w(TAG, "getCalendarTest() count=" + rows + " cols=" + cols);

            ArrayList<CalendarRecord> records = new ArrayList<CalendarRecord>();

            do {
                if (mCalendarId==INVALID_ID) mCalendarId = c.getInt(0);
                CalendarRecord r = new CalendarRecord(c.getInt(0), c.getString(1), c.getString(2),
                        c.getString(3), c.getString(4), c.getInt(5), c.getString(6));
                Log.d(TAG, "getCalendarTest() [" + (i++) + "] record:\n" + r);
            } while (c.moveToNext());
        } catch (Exception e) {
            Log.e(TAG, "Error : Exception occurred on getCalendarTest()." , e);
        } finally {
            c.close();
        }
    }

    private void getCalendarEventList() {
        Log.d(TAG, "getCalendarEventList() id=" + mCalendarId);
        if (mCalendarId == INVALID_ID) return;

        Uri eventListUri = Uri.parse(CALENDAR_URI + "/events");

        String[] selection = new String[] {
                EventsEntity.TITLE,
                EventsEntity.DTSTART,
                EventsEntity.DTEND,
        };

        StringBuilder where = new StringBuilder("calendar_id=?");
        where.append("and (dtstart>=? or dtend>=?)");
        where.append("and (dtstart<=? or dtend<=?)");

        String[] condition = new String[] {
                String.valueOf(mCalendarId),
                String.valueOf(mFirstDate.getTime()),
                String.valueOf(mFirstDate.getTime()),
                String.valueOf(mLastDate.getTime()),
                String.valueOf(mLastDate.getTime()),
        };

        Cursor c = getContentResolver().query(
                eventListUri, selection,
                where.toString(), condition, null);

        if (c==null || !c.moveToFirst()) {
            // Calendar does not have any events.
            Log.d(TAG, "No events foud.");
            return;
        }

        try {
            int i = 0;
            int rows = c.getCount();
            int cols = c.getColumnCount();

            Log.w(TAG, "getCalendarEventList() count=" + rows + " cols=" + cols);

            do {
                StringBuilder record =
                        new StringBuilder("getCalendarEventList() [" + (i++) + "] record:\n");
                for (int col=0; col<cols; col++) {
                    record.append("\t[" + col + "][" + c.getColumnName(col) + "]=["
                            + c.getString(col) + "]\n");
                }
                Log.d(TAG, record.toString());

            } while (c.moveToNext());
        } catch (Exception e) {

        } finally {
            c.close();
        }
    }

    // Query events list using given calender id
    private void queryEventList(String calendarId) {
        Uri eventsUri = Uri.parse(CALENDAR_URI + "/events");

    }
	
	private void setupMonthTitle() {
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
