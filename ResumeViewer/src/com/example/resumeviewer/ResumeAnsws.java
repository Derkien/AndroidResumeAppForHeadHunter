package com.example.resumeviewer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.resumeviewer.FeedTablesContract.TableResumeAnsw;

public class ResumeAnsws extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.resumeviewer.EXTRA_MESSAGE";
	public final static String EXTRA_MESSAGE_TITLE = "com.example.resumeviewer.EXTRA_MESSAGE_TITLE";
	
	ResumeViewerDbHelper dbHelper = new ResumeViewerDbHelper(ResumeAnsws.this);
	
	SimpleCursorAdapter dbAdapter;
	Cursor listCursor;
	Long resumeId;
	Cursor resumeCursor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resume_answs);

		setDataList();
	}
	/*
	 * Fill with data
	 */
	public boolean setDataList(){
		
		listCursor = fetchAllAnswers();
		
		if(listCursor.getCount() <= 0){
        	Toast toast = Toast.makeText(getApplicationContext(), 
 				   "No answers yet...", Toast.LENGTH_SHORT); 
        	toast.show();
			finish();
			return false;
		}
		
		resumeId = listCursor.getLong(listCursor.getColumnIndexOrThrow(TableResumeAnsw.COLUMN_RESUME_ID));
		
		String[] columns = new String[] {
			TableResumeAnsw.COLUMN_NAME_ANSW_FROMORG,
			TableResumeAnsw.COLUMN_NAME_ANSW_TIME
		};

		int[] to = new int[] { 
			R.id.answer_from,
			R.id.answer_time
		};

		ListView listView = (ListView) findViewById(R.id.listView1);
		dbAdapter = new SimpleCursorAdapter(this, R.layout.answer_info, listCursor, columns, to, 0);

		listView.setAdapter(dbAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Get the cursor, positioned to the corresponding row in the result set
				Cursor cursor = (Cursor) parent.getItemAtPosition(position);
				String answerText = 
					cursor.getString(cursor.getColumnIndexOrThrow(TableResumeAnsw.COLUMN_NAME_ANSW_TEXT));
				String answerEmpl = 
						cursor.getString(cursor.getColumnIndexOrThrow(TableResumeAnsw.COLUMN_NAME_ANSW_FROMORG));
				Intent intent = new Intent(ResumeAnsws.this, DialogActivity.class);
				intent.putExtra(EXTRA_MESSAGE, answerText);
				intent.putExtra(EXTRA_MESSAGE_TITLE, "Ответ от "+answerEmpl);
				startActivity(intent);
			}
		});		
		registerForContextMenu(listView);
		return true;
	}
	/*
	 * My context
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.resume_answs_ctxt, menu);
	}	
	/*
	 * Get all answers
	 */
	public Cursor fetchAllAnswers() throws SQLException {
		Log.d("fetchAllAnswers", "start");
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		String[] fields = {
			TableResumeAnsw._ID,
			TableResumeAnsw.COLUMN_RESUME_ID,
			TableResumeAnsw.COLUMN_NAME_ANSW_FROMORG,
			TableResumeAnsw.COLUMN_NAME_ANSW_CHECKED,
			TableResumeAnsw.COLUMN_NAME_ANSW_TIME,
			TableResumeAnsw.COLUMN_NAME_ANSW_TEXT
		};

		String sortOrder =
				TableResumeAnsw.COLUMN_NAME_ANSW_TIME + " DESC";

		// get data 
		resumeCursor = db.query( TableResumeAnsw.TABLE_NAME, fields, null, null, null, null, sortOrder );
		Log.d("fetchAllAnswers", "executed");
		if (resumeCursor != null && resumeCursor.getCount() > 0) {
			Log.d("fetchAllAnswers", "resumeCursor not null && > 0 ");
			resumeCursor.moveToFirst();
		}
		return resumeCursor;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.resume_answs, menu);
		return true;
	}

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	// get list item info for context
    	AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
    	
        switch(item.getItemId()) {
	        case R.id.menu_delete:
	        	Toast toast = Toast.makeText(getApplicationContext(), 
	 				   "Trying to delete andswer...", Toast.LENGTH_SHORT); 
	        	toast.show();
	            if(deleteAnsw(acmi.id)){
	    	    	Toast toastA = Toast.makeText(getApplicationContext(), 
	    					   "Answer deleted!!", Toast.LENGTH_SHORT); 
	    	    	toastA.show();
	            	// refresh list
	    	    	setDataList();
	            }
	            return true;
	        case R.id.menu_view:
	        	Cursor answCursor = getAnswerDataById(acmi.id);

	        	if(setAnswerAsChecked(acmi.id))
	        	{
	        		Toast toastA = Toast.makeText(getApplicationContext(), 
	    					   "Answer marked as read!!", Toast.LENGTH_SHORT); 
	    	    	toastA.show();
	        	}
	        	
	        	String answerText = 
	        			answCursor.getString(answCursor.getColumnIndexOrThrow(TableResumeAnsw.COLUMN_NAME_ANSW_TEXT));
				String answerEmpl = 
						answCursor.getString(answCursor.getColumnIndexOrThrow(TableResumeAnsw.COLUMN_NAME_ANSW_FROMORG));
	        	Intent intent = new Intent(ResumeAnsws.this, DialogActivity.class);
				intent.putExtra(EXTRA_MESSAGE, answerText);
				intent.putExtra(EXTRA_MESSAGE_TITLE, "Ответ от "+answerEmpl);
				startActivity(intent);
				
	            return true;
        }
        return super.onContextItemSelected(item);
    }
    
    /*
     * Delete answer by rowId
     */
    public boolean deleteAnsw(long rowId) throws SQLException {
    	// Gets the data repository in write mode
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	return db.delete(TableResumeAnsw.TABLE_NAME, TableResumeAnsw._ID + "=" + rowId, null) > 0 ;
    }   
    
    /*
     *  Get answer data by rowId
     */
    public Cursor getAnswerDataById(Long rowId) throws SQLException {
    	// Gets the data repository in read mode
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
    	Log.d("getAnswerDataById", "readable");
        Cursor answCursor = db.query(true, TableResumeAnsw.TABLE_NAME, null,
        		TableResumeAnsw._ID + "=" + rowId, null, null, null, null, null);
        if (answCursor != null && answCursor.getCount() > 0) {
        	Log.d("getAnswerDataById", " not null && > 0 ");
        	answCursor.moveToFirst();
        }
        return answCursor;
	}
    
    /*
     *  Set checked answer
     */
    public boolean setAnswerAsChecked(Long rowId) throws SQLException {
    	// Gets the data repository in write mode
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	Log.d("updateResume", "start update");
    	
    	ContentValues values = new ContentValues();
		values.put(TableResumeAnsw.COLUMN_NAME_ANSW_CHECKED, "1");
         
        return db.update(TableResumeAnsw.TABLE_NAME, values, TableResumeAnsw._ID + "="
                + rowId, null) > 0;
	}
}
