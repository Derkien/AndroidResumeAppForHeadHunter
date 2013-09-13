package com.example.resumeviewer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.resumeviewer.FeedTablesContract.TableUserResume;

public class EmployerAct extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.resumeviewer.EXTRA_MESSAGE";
	public final static String EXTRA_MESSAGE_RESUME_ID = "com.example.resumeviewer.EXTRA_MESSAGE_RESUME_ID";

	Cursor resumeCursor;
	Cursor listCursor;
	SimpleCursorAdapter dbAdapter;
	
	ResumeViewerDbHelper dbHelper = new ResumeViewerDbHelper(EmployerAct.this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_employer);
		
		setDataList();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.employer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.employer_ctxt_menu, menu);
	}
	
	/*
	 * Fill list with data
	 */
	public void setDataList(){
		
		listCursor = fetchAllResume();
		
		String[] columns = new String[] {
			TableUserResume.COLUMN_NAME_TIME,
			TableUserResume.COLUMN_NAME_USER_EMAIL,
			TableUserResume.COLUMN_NAME_USER_NAME,
		};

		int[] to = new int[] { 
			R.id.resume_time,
			R.id.resume_email,
			R.id.resume_from
		};

		ListView listView = (ListView) findViewById(R.id.listView1);
		dbAdapter = new SimpleCursorAdapter(this, R.layout.resume_info, listCursor, columns, to, 0);

		listView.setAdapter(dbAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Get the cursor, positioned to the corresponding row in the result set
				Cursor cursor = (Cursor) parent.getItemAtPosition(position);
				Long resumeId = 
					cursor.getLong(cursor.getColumnIndexOrThrow(TableUserResume._ID));
				
				Intent intent = new Intent(EmployerAct.this, ViewUserResume.class);
				intent.putExtra("intentFrom", "EmployerAct");
				intent.putExtra(EXTRA_MESSAGE_RESUME_ID, resumeId);
				startActivity(intent);
			}
		});		
	}

	/*
	 * Get all resume... for now its only one...
	 */
	public Cursor fetchAllResume() throws SQLException {
		Log.d("fetchAllResume", "start");
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		String[] fields = {
			TableUserResume._ID,
			TableUserResume.COLUMN_NAME_TIME,
			TableUserResume.COLUMN_NAME_USER_EMAIL,
			TableUserResume.COLUMN_NAME_USER_NAME
		};

		String sortOrder =
				TableUserResume.COLUMN_NAME_TIME + " DESC";

		// get data 
		resumeCursor = db.query( TableUserResume.TABLE_NAME, fields, null, null, null, null, sortOrder );
		Log.d("fetchAllResume", "executed");
		if (resumeCursor != null && resumeCursor.getCount() > 0) {
			Log.d("fetchAllResume", "resumeCursor not null && > 0 ");
			resumeCursor.moveToFirst();
		}
		return resumeCursor;
	}
	

}
