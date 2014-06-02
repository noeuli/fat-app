package com.noeuli.fatapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
    private static final boolean LOGD = true;
    private static final String TAG = "MainActivity";
    
    private TextView mTextMonthTitle;
    
    private class WeekArray {
        private ArrayList<LinearLayout> mWeekList;

        public WeekArray() {
            mWeekList = new ArrayList<LinearLayout>();
        }

        public void add(LinearLayout day) {
            mWeekList.add(day);
        }

//        public int size() {
//            return mWeekList.size();
//        }

        public void setDate(int week, int day, String strDate) {
            int resId = DAY_TEXTVIEW_ID[week][day];
            LinearLayout dayGroup = mWeekList.get(day);
            TextView txtDate = (TextView) dayGroup.findViewById(resId);
            if (strDate == null) strDate = "";
            txtDate.setText(strDate);
            txtDate.setTag(strDate);
            dayGroup.setTag(strDate);
        }
    }
    private ArrayList<WeekArray> mDayViewList;

    final int[][] DYAGROUP_ID = new int[][]{
            {R.id.day1group, R.id.day2group, R.id.day3group, R.id.day4group, R.id.day5group, R.id.day6group, R.id.day7group,},
            {R.id.day8group, R.id.day9group, R.id.day10group, R.id.day11group, R.id.day12group, R.id.day13group, R.id.day14group,},
            {R.id.day15group, R.id.day16group, R.id.day17group, R.id.day18group, R.id.day19group, R.id.day20group, R.id.day21group,},
            {R.id.day22group, R.id.day23group, R.id.day24group, R.id.day25group, R.id.day26group, R.id.day27group, R.id.day28group,},
            {R.id.day29group, R.id.day30group, R.id.day31group, R.id.day32group, R.id.day33group, R.id.day34group, R.id.day35group,},
            {R.id.day36group, R.id.day37group, R.id.day38group, R.id.day39group, R.id.day40group, R.id.day41group, R.id.day42group,},
    };

    final int[][] DAY_TEXTVIEW_ID = new int[][]{
            {R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7,},
            {R.id.day8, R.id.day9, R.id.day10, R.id.day11, R.id.day12, R.id.day13, R.id.day14,},
            {R.id.day15, R.id.day16, R.id.day17, R.id.day18, R.id.day19, R.id.day20, R.id.day21,},
            {R.id.day22, R.id.day23, R.id.day24, R.id.day25, R.id.day26, R.id.day27, R.id.day28,},
            {R.id.day29, R.id.day30, R.id.day31, R.id.day32, R.id.day33, R.id.day34, R.id.day35,},
            {R.id.day36, R.id.day37, R.id.day38, R.id.day39, R.id.day40, R.id.day41, R.id.day42,},
    };

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupViews();
	}
	
	private void setupCalendarList() {
        mDayViewList = new ArrayList<WeekArray>();

        LinearLayout day = null;

        for (int i = 0; i < 6; i++) {
            WeekArray weekArray = new WeekArray();
            for (int j = 0; j < 7; j++) {
                int resId = DYAGROUP_ID[i][j];
                day = (LinearLayout) findViewById(resId);
                weekArray.add(day);
            }
            mDayViewList.add(weekArray);
        }
    }

    private void setupViews() {
	    findViews();
	    // display this month
	    initMonth();
        initCalendarId();
        showMonth();
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

    private void initMonth() {
        // Get today and clear time of day
        mDisplayMonth = Calendar.getInstance();
        mDisplayMonth.set(Calendar.HOUR_OF_DAY, 0);   // clear() would not reset the hour of day
        mDisplayMonth.clear(Calendar.MINUTE);
        mDisplayMonth.clear(Calendar.SECOND);
        mDisplayMonth.clear(Calendar.MILLISECOND);
    }

    private void showPrevMonth() {
        moveToPrevMonth();
        showMonth();
    }

    private void showNextMonth() {
        moveToNextMonth();
        showMonth();
    }

    private void showMonth() {
        getMonthInfo();
        drawCalendar();
        showMonthTitle();
        showEvent();
    }
    
    private void showEvent() {
        getCalendarEventList();
        showCalendarEventList();
    }
    
    private void showMonthTitle() {
        mTextMonthTitle.setText(mDisplayMonth.get(Calendar.YEAR) + "년 "
                + (mDisplayMonth.get(Calendar.MONTH)+1) + "월");
    }

    private void moveToPrevMonth() {
        mDisplayMonth.add(Calendar.MONTH, -1);
    }

    private void moveToNextMonth() {
        mDisplayMonth.add(Calendar.MONTH, 1);
    }

    int mFirstDayOfWeek;
    int mLastDayOfWeek;

    private void getMonthInfo() {
        // Get start of this month in milliseconds
        mDisplayMonth.set(Calendar.DAY_OF_MONTH, 1);
        mFirstDate = mDisplayMonth.getTime();
        mFirstDayOfWeek = mDisplayMonth.get(Calendar.DAY_OF_WEEK);

        // Get end of this month
        mDisplayMonth.add(Calendar.MONTH, 1);
        mDisplayMonth.add(Calendar.DATE, -1);
        mLastDate = mDisplayMonth.getTime();
        mLastDayOfWeek = mDisplayMonth.get(Calendar.DAY_OF_WEEK);
    }

    private void drawCalendar() {
//        int weeks = mDayViewList.size();
        int weekOfMonth = mDisplayMonth.get(Calendar.WEEK_OF_MONTH);
        int day = 0;

        Log.d(TAG, "drawCalendar() date=" + mDisplayMonth + "\nmFirstDayOfWeek=" + mFirstDayOfWeek
                + " weekOfMonth=" + weekOfMonth + " viewArraySize=" + mDayViewList.size());

        for (int week=0; week<mDayViewList.size(); week++) {
            WeekArray oneWeek = mDayViewList.get(week);

            for (int dayIndex=0; dayIndex<7; dayIndex++) {
                String strDay = null;
                if (week==0) {
                    // First Week
                    if (dayIndex+1 >= mFirstDayOfWeek) {
                        strDay = Integer.toString(++day);
                    }
                } else if (week+1==weekOfMonth) {
                    // Last week
                    if (dayIndex < mLastDayOfWeek) {
                        strDay = Integer.toString(++day);
                    }
                } else if (week<weekOfMonth) {
                    strDay = Integer.toString(++day);
                }
                oneWeek.setDate(week, dayIndex, strDay);
            }
        }

    }

    // Find out calendar id and save into mCalendarId
    private void initCalendarId() {
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

            Log.w(TAG, "initCalendarId() count=" + rows + " cols=" + cols);

//            ArrayList<CalendarRecord> records = new ArrayList<CalendarRecord>();

            do {
                if (mCalendarId==INVALID_ID) mCalendarId = c.getInt(0);
                CalendarRecord r = new CalendarRecord(c.getInt(0), c.getString(1), c.getString(2),
                        c.getString(3), c.getString(4), c.getInt(5), c.getString(6));
                Log.d(TAG, "initCalendarId() [" + (i++) + "] record:\n" + r);
            } while (c.moveToNext());
        } catch (Exception e) {
            Log.e(TAG, "Error : Exception occurred on initCalendarId()." , e);
        } finally {
            c.close();
        }
    }
    
    private class CalendarEvent {
        private String mTitle;
        private long mStartTime;
        private long mEndTime;
        private String mTimeZone;
        
        public CalendarEvent(String title, long start, long end, String timeZone) {
            mTitle = title;
            mStartTime = start;
            mEndTime = end;
            mTimeZone = timeZone;
        }
        
        public String getTitle() {
             return mTitle;
        }
        
        public long getStartTime() {
            return mStartTime;
        }
        
        public long getEndTime() {
            return mEndTime;
        }
        
        public String toString() {
            return "[" + mTitle + "] " + getTime(mStartTime, mTimeZone) + "~" + getTime(mEndTime, mTimeZone);
        }
    }
    
    String getTime(long dateInMillis, String timeZone) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/M/d h:mm/z", Locale.KOREA); 
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        String dateString = formatter.format(new Date(dateInMillis));
        return dateString;
    }
    
    private ArrayList<CalendarEvent> mCalendarEventList = new ArrayList<CalendarEvent>();

    private void getCalendarEventList() {
        Log.d(TAG, "getCalendarEventList() id=" + mCalendarId);
        if (mCalendarId == INVALID_ID) return;
        
        if (mCalendarEventList == null) {
            mCalendarEventList = new ArrayList<CalendarEvent>();
        }
        mCalendarEventList.clear();

        Uri eventListUri = Uri.parse(CALENDAR_URI + "/events");

        String[] selection = new String[] {
                EventsEntity.TITLE,
                EventsEntity.DTSTART,
                EventsEntity.DTEND,
                EventsEntity.EVENT_TIMEZONE,
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
                String title = c.getString(0);
                long start = c.getLong(1);
                long end = c.getLong(2);
                String timeZone = c.getString(3);
                
                CalendarEvent event = new CalendarEvent(title, start, end, timeZone); 
                mCalendarEventList.add(event);

                Log.d(TAG, "getCalendarEventList() [" + (i++) + "] record:" + event);
            } while (c.moveToNext());
        } catch (Exception e) {

        } finally {
            c.close();
        }
    }

    private void showCalendarEventList() {
        if (mCalendarEventList == null || mCalendarEventList.size() == 0) {
            Log.w(TAG, "showCalendarEventList(): Nothing to show.");
            return;
        }

        if (LOGD) Log.d(TAG, "showCalendarEventList() size=" + mCalendarEventList.size() + " timezone=" + TimeZone.getDefault());
        
        for (int i=0; i<mCalendarEventList.size(); i++) {
            CalendarEvent event = mCalendarEventList.get(i);
            String title = event.getTitle();
            long sdate = event.getStartTime();
            long edate = event.getEndTime();
            
            Calendar cursorCalendar = Calendar.getInstance();
            cursorCalendar.setTimeZone(TimeZone.getDefault());
            cursorCalendar.set(mDisplayMonth.get(Calendar.YEAR),  mDisplayMonth.get(Calendar.MONTH), 1);
            cursorCalendar.set(Calendar.HOUR_OF_DAY, 0);   // clear() would not reset the hour of day
            cursorCalendar.clear(Calendar.MINUTE);
            cursorCalendar.clear(Calendar.SECOND);
            cursorCalendar.clear(Calendar.MILLISECOND);
            
            Date cursorDate = cursorCalendar.getTime();
            
            do {
                if (sdate <= cursorDate.getTime() && edate >= cursorDate.getTime()) {
                    Log.e(TAG, "mCalendarEventList.size() [" + i + "] s=" + sdate + " e=" + edate + " c=" + cursorDate.getTime());
                    addEvent(cursorCalendar.get(Calendar.DATE), title);
                } else {
                    Log.d(TAG, "mCalendarEventList.size() [" + i + "] s=" + sdate + " e=" + edate + " c=" + cursorDate.getTime());
                }
                cursorCalendar.add(Calendar.DATE, 1);
                cursorDate = cursorCalendar.getTime();
            } while (cursorDate.getTime() <= mLastDate.getTime());
        }
    }
    
    private void addEvent(int date, String title) {
        Log.d(TAG, "addEvent(" + date + ", " + title + ")");
        TextView tv = findTextView(date);
        if (tv != null) {
            tv.setText(title);
        } else {
            Log.e(TAG, "Error! Can't find text veiw for date " + date);
        }
    }
    
    private TextView findTextView(int date) {
        
        return null;
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

        Log.d(TAG, "onClick() id=" + id + " v=" + v);

        if (v instanceof LinearLayout) {
            Object tag = v.getTag();
            if (tag instanceof String) {
                String strDay = (String)tag;
                if (!"".equals(strDay)) showToast((String)tag);
            }
        } else if (id == R.id.txtPrevMonth) {
            showPrevMonth();

        } else if (id == R.id.txtNextMonth) {
            showNextMonth();
        } else {

        }
    }

    private Toast mToast = null;
    private void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        if (mToast != null) mToast.show();
    }

}
