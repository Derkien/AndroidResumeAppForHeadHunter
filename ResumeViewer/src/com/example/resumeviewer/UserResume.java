package com.example.resumeviewer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.resumeviewer.DatePickerFragment.OnDatePickedListener;
import com.example.resumeviewer.FeedTablesContract.TableResumeAnsw;
import com.example.resumeviewer.FeedTablesContract.TableUserResume;

public class UserResume extends FragmentActivity implements OnDatePickedListener {
	
	static final int GET_ANSWER = 1;  // The request code
	
	public final static String EXTRA_NAME = "com.example.resumeviewer.NAME";
	public final static String EXTRA_DATEBIRTH = "com.example.resumeviewer.DATEBIRTH";
	public final static String EXTRA_SEX = "com.example.resumeviewer.SEX";
	public final static String EXTRA_JOB = "com.example.resumeviewer.JOB";
	public final static String EXTRA_SALARY = "com.example.resumeviewer.SALARY";
	public final static String EXTRA_PHONE = "com.example.resumeviewer.PHONE";
	public final static String EXTRA_EMAIL = "com.example.resumeviewer.EMAIL";
	public final static String EXTRA_EMPLOYER = "com.example.resumeviewer.EMPLOYER";
	
	ResumeViewerDbHelper dbHelper = new ResumeViewerDbHelper(UserResume.this);

	protected EditText userName;
	protected EditText dateOfBirth;
	protected EditText userJob;
	protected EditText userSalary;
	protected EditText userPhone;
	protected EditText userEmail;
	protected Spinner sexSpinner;
	protected Long resumeId;
	protected Cursor dbCursor;
	protected Cursor resumeData;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_resume);

		sexSpinner = (Spinner) findViewById(R.id.user_resume_sex);
		ArrayAdapter<CharSequence> sexListAdapter = ArrayAdapter.createFromResource(this,
		        R.array.user_resume_sex, android.R.layout.simple_spinner_item);
		sexListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sexSpinner.setAdapter(sexListAdapter);

		userName = (EditText) findViewById(R.id.user_resume_name);
		dateOfBirth = (EditText) findViewById(R.id.user_resume_birth);
		userJob = (EditText) findViewById(R.id.user_resume_job);
		userSalary = (EditText) findViewById(R.id.user_resume_salary);
		userPhone = (EditText) findViewById(R.id.user_resume_phone);
		userEmail = (EditText) findViewById(R.id.user_resume_email);
		
		//get resume id
		Log.d("getCurUserResumeId", "start");
		dbCursor = getCurUserResumeId();
		if ( dbCursor != null && dbCursor.getCount() > 0 ){
			Log.d("dbCursor", "not null good");
			
			resumeId = dbCursor.getLong(dbCursor.getColumnIndexOrThrow(TableUserResume._ID));
			
			Log.d("dbCursor", "resumeId = " + resumeId);
			// check id
			if ( resumeId > 0 )
			{
				Log.d("resumeId", "resumeId not 0");
				// try to get data
				Log.d("getCurUserResumeById", "start");
				resumeData = getCurUserResumeById(resumeId);
				// try to fill form
				if( resumeData != null && resumeData.getCount() > 0 )
				{
					Log.d("resumeData", "data come");
					Log.d("setDataToFields", "start");
					setDataToFields();
				}
				
			}			
		}
		else
			resumeId = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_resume, menu);
		return true;
	}

	/*
	 * Send resume to employer
	 */
    public void sendResume(View v) {
    	// save before send
    	setDataToDb(v);
       	// Gets the data repository in write mode
       	SQLiteDatabase db = dbHelper.getWritableDatabase();
       	ContentValues values = new ContentValues();
       	values.put(TableUserResume.COLUMN_NAME_SENT, 1);
       	
       	if ( db.update(TableUserResume.TABLE_NAME, values, TableUserResume._ID + "="
                    + resumeId, null) > 0 )
        {
       		Intent intent = new Intent(UserResume.this, ViewUserResume.class);
       		
       		intent.putExtra("intentFrom", "UserResume");
       		intent.putExtra(EXTRA_NAME, userName.getText().toString());
       		intent.putExtra(EXTRA_DATEBIRTH, dateOfBirth.getText().toString());
       		intent.putExtra(EXTRA_SEX, sexSpinner.getSelectedItem().toString());
       		intent.putExtra(EXTRA_JOB, userJob.getText().toString());
       		intent.putExtra(EXTRA_SALARY, userSalary.getText().toString());
       		intent.putExtra(EXTRA_PHONE, userPhone.getText().toString());
       		intent.putExtra(EXTRA_PHONE, userPhone.getText().toString());
       		intent.putExtra(EXTRA_EMAIL, userEmail.getText().toString());
       		//imulation of random employer
            Random generator = new Random(new Date().getTime());
       		intent.putExtra(EXTRA_EMPLOYER, "employer"+generator.nextInt(500));
	
       		startActivityForResult(intent, GET_ANSWER);
        }
       	else
       	{
        	Toast toast = Toast.makeText(getApplicationContext(), 
 				   "Error!!", Toast.LENGTH_SHORT); 
        	toast.show();
       	}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == GET_ANSWER) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
            	//insert answer for db
            	ContentValues values = new ContentValues();
            	
            	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            	String dateForinsert = df.format(new GregorianCalendar().getTime());
            	values.put(TableResumeAnsw.COLUMN_NAME_ANSW_TIME, dateForinsert);
            	values.put(TableResumeAnsw.COLUMN_NAME_ANSW_CHECKED, 0);
            	values.put(TableResumeAnsw.COLUMN_RESUME_ID, resumeId);
            	values.put(TableResumeAnsw.COLUMN_NAME_ANSW_TEXT, data.getStringExtra(ViewUserResume.EXTRA_EMPLOYER_ANSWER));
            	values.put(TableResumeAnsw.COLUMN_NAME_ANSW_FROMORG, data.getStringExtra(ViewUserResume.EXTRA_EMPLOYER));
            	
            	// Gets the data repository in write mode
            	SQLiteDatabase db = dbHelper.getWritableDatabase();
            	
            	Long rowId = db.insert(
            			TableResumeAnsw.TABLE_NAME,
            	         null,
            	         values);
            	if(rowId > 0)
            	{
                    String title = "You have an answer for your resume!!!";
                    String message = data.getStringExtra("EMPLOYERANSWER");
                    String buttonString = "Yahooo!!!";
                    
                    AlertDialog.Builder ad = new AlertDialog.Builder(UserResume.this);
                    ad.setTitle(title);
                    ad.setMessage(message);
                    ad.setPositiveButton(buttonString, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            Toast.makeText(UserResume.this, "Go to list",
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(UserResume.this, ResumeAnsws.class);
                            startActivity(intent);
                        }
                    });
                    ad.create();
                    ad.show();
            	}
            	
            }
        }
    }
    
    /*
     *  Show calendar dialog
     */
    public void showDatePickerDialog(View v) {
    	DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    
    /*
     *  setselected date to textview (non-Javadoc)
     * @see com.example.resumeviewer.DatePickerFragment.OnDatePickedListener#setSelectedDate(int, int, int)
     */
    public void setSelectedDate(int year, int month, int day){
    	dateOfBirth.setText(month+"/"+day+"/"+year);
    }
    
    /*
     *  default date for calendar from textview (non-Javadoc)
     * @see com.example.resumeviewer.DatePickerFragment.OnDatePickedListener#getInputDate()
     */
    public String getInputDate(){
    	return dateOfBirth.getText().toString();
    }

    /* 
     * Fill ContentValues with data from form
     */
    public ContentValues createContentValues(){
    	// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		if( userName.getText().toString() != ""  )
			values.put(TableUserResume.COLUMN_NAME_USER_NAME, userName.getText().toString());
		if( dateOfBirth.getText().toString() != ""  )    		
			values.put(TableUserResume.COLUMN_NAME_USER_DATE_BIRTH, dateOfBirth.getText().toString());
		if( sexSpinner.getSelectedItem().toString() != ""  )    		
			values.put(TableUserResume.COLUMN_NAME_USER_SEX, sexSpinner.getSelectedItem().toString());
		if( userJob.getText().toString() != ""  )    		
			values.put(TableUserResume.COLUMN_NAME_USER_JOB, userJob.getText().toString());
		if( userSalary.getText().toString() != ""  )    		
			values.put(TableUserResume.COLUMN_NAME_USER_SALARY, userSalary.getText().toString());
		if( userPhone.getText().toString() != ""  )    		
			values.put(TableUserResume.COLUMN_NAME_USER_PHONE, userPhone.getText().toString());
		if( userEmail.getText().toString() != ""  )    		
			values.put(TableUserResume.COLUMN_NAME_USER_EMAIL, userEmail.getText().toString());
		
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    	String dateForinsert = df.format(new GregorianCalendar().getTime());

    	values.put(TableUserResume.COLUMN_NAME_TIME, dateForinsert);
    	values.put(TableUserResume.COLUMN_NAME_SENT, 1);

    	return values;
    }
    
    /*
     * Fill from with data from base
     */
    public void setDataToFields(){
		userName.setText(resumeData.getString(resumeData.getColumnIndexOrThrow(TableUserResume.COLUMN_NAME_USER_NAME)));
		dateOfBirth.setText(resumeData.getString(resumeData.getColumnIndexOrThrow(TableUserResume.COLUMN_NAME_USER_DATE_BIRTH)));
		userJob.setText(resumeData.getString(resumeData.getColumnIndexOrThrow(TableUserResume.COLUMN_NAME_USER_JOB)));
		userSalary.setText(resumeData.getString(resumeData.getColumnIndexOrThrow(TableUserResume.COLUMN_NAME_USER_SALARY)));
		userPhone.setText(resumeData.getString(resumeData.getColumnIndexOrThrow(TableUserResume.COLUMN_NAME_USER_PHONE)));
		userEmail.setText(resumeData.getString(resumeData.getColumnIndexOrThrow(TableUserResume.COLUMN_NAME_USER_EMAIL)));
		String sexValue = resumeData.getString(resumeData.getColumnIndexOrThrow(TableUserResume.COLUMN_NAME_USER_SEX));
		for (int i=0; i<sexSpinner.getCount();i++){
			String s = (String) sexSpinner.getItemAtPosition(i);
			if (s.equalsIgnoreCase(sexValue)){
				sexSpinner.setSelection(i);
			}
		}
    }
    
    /*
     * Update or Insert data
     */
    public boolean setDataToDb(View v){
    	Toast toast = Toast.makeText(getApplicationContext(), 
				   "Saving...", Toast.LENGTH_SHORT); 
    	toast.show();
    	final AlertDialog.Builder adb = new AlertDialog.Builder(this);
    	
    	// a bit of check's... like need fields
    	if( userName.getText().length() == 0 ){
    		adb.setMessage("Введите имя!!");
    		//adb.show();
    		adb.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	dialog.dismiss();
                }
            });
    		adb.create(); 
    		adb.show();
    		userName.requestFocus();
    		return false;	
    	}
    	if( dateOfBirth.getText().length() == 0  )
    	{
    		adb.setMessage("Введите Дату рождения!!");
    		adb.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
    		adb.create();
    		adb.show();
    		dateOfBirth.requestFocus();
    		return false;
    	}
    	if( userPhone.getText().length() == 0  )
    	{
    		adb.setMessage("Введите телефон!!");
    		adb.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();             
                }
            });
    		adb.create();
    		adb.show();
    		userPhone.requestFocus();
    		return false;
    	}
    	if( userEmail.getText().length() == 0  )
    	{
    		adb.setMessage("Введите почту!!");
    		adb.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();      
                }
            });
    		adb.create();
    		adb.show();
    		userEmail.requestFocus();
    		return false;
    	}
    	Log.d("setDataToDb", "вход");
		if ( resumeId != null && resumeId > 0 )
		{
			Log.d("setDataToDb", "Ключ есть, апдейтим");
			if(updateResume(resumeId))
				return true;
		}
		else
		{
			Log.d("setDataToDb", "Ключа нет, инсертим");
			resumeId = saveResume();
			if( resumeId > 0 )
				return true;
		}
		
		return false;
    }
    
    /*
     * Add resume
     */
    public Long saveResume(){
    	// Gets the data repository in write mode
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	Log.d("saveResume", "try to insert");
    	ContentValues values = createContentValues();
    	// Insert the new row, returning the primary key value of the new row
    	Long rowId = db.insert(
    			TableUserResume.TABLE_NAME,
    	         null,
    	         values);
    	if(rowId > 0)
    	{
    		Toast toast = Toast.makeText(getApplicationContext(), 
 				   "Resume Added!!", Toast.LENGTH_SHORT); 
    		toast.show();
    	}
    	return rowId; 
    }
    
    /*
     * Update with all data from form
     */
    public boolean updateResume(long rowId) {
    	// Gets the data repository in write mode
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	Log.d("updateResume", "start update");
        ContentValues updateValues = createContentValues();
         
        return db.update(TableUserResume.TABLE_NAME, updateValues, TableUserResume._ID + "="
                + rowId, null) > 0;
    }
   
    /*
     * Delete with return to main screen
     */
    public void deleteResumeWithReturn(View v) {
    	Toast toast = Toast.makeText(getApplicationContext(), 
				   "Пробуем удалить!", Toast.LENGTH_SHORT); 
		toast.show();
    	if (resumeId != null && resumeId > 0)
    	{
    		// check exit for main screen
    		boolean success = false;
    		// try to delete resume
    		if (deleteResume(resumeId))
	    	{
    	    	Toast toastR = Toast.makeText(getApplicationContext(), 
    					   "Resume deleted!!", Toast.LENGTH_SHORT); 
    	    	toastR.show();
    	    	
    	    	// enough for exit
    	    	success = true;
	    	}
    		// try to delete answers
    		if (deleteResumeAnsw(resumeId))
	    	{
    	    	Toast toastA = Toast.makeText(getApplicationContext(), 
    					   "Answers deleted!!", Toast.LENGTH_SHORT); 
    	    	toastA.show();
	    	}
    		
    		if (success){
    			Intent intent = new Intent(UserResume.this, UserAct.class);
            	startActivity(intent);    			
    		}
    		else{
    	    	Toast toastR = Toast.makeText(getApplicationContext(), 
 					   "Something wrong with deleting!!!", Toast.LENGTH_SHORT); 
    	    	toastR.show();
    		}

    	}
    }
    
    /*
     * Delete resume
     */
    public boolean deleteResume(long rowId) throws SQLException {
    	// Gets the data repository in write mode
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	return db.delete(TableUserResume.TABLE_NAME, TableUserResume._ID + "=" + rowId, null) > 0 ;
    }
    
    /*
     * Delete resume answers
     */
    public boolean deleteResumeAnsw(long rowId) throws SQLException {
    	// Gets the data repository in write mode
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	return db.delete(TableResumeAnsw.TABLE_NAME, TableResumeAnsw.COLUMN_RESUME_ID + "=" + rowId, null) > 0 ;
    }
    
    /* 
     * Get resume Id if able
     */
    public Cursor getCurUserResumeId() throws SQLException {
    	// Gets the data repository in read mode
		Log.d("getCurUserResumeId", "start");
    	SQLiteDatabase db = dbHelper.getReadableDatabase();

		String[] projection = {
		    TableUserResume._ID
		};

		String sortOrder =
				TableUserResume._ID + " DESC";

		Cursor resumeCursor = db.query( TableUserResume.TABLE_NAME, projection, null, null, null, null, sortOrder );
		Log.d("getCurUserResumeId", "executed");
		if (resumeCursor != null && resumeCursor.getCount() > 0) {
			Log.d("getCurUserResumeId", "resumeCursor not null && > 0 ");
			resumeCursor.moveToFirst();
		}
		return resumeCursor;
	}    

    /*
     *  Get resume data by rowId
     */
    public Cursor getCurUserResumeById(Long rowId) throws SQLException {
    	// Gets the data repository in read mode
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
    	Log.d("getCurUserResumeById", "readable");
        Cursor resumeCursor = db.query(true, TableUserResume.TABLE_NAME, null,
        		TableUserResume._ID + "=" + rowId, null, null, null, null, null);
        Log.d("getCurUserResumeById", "readable");
        if (resumeCursor != null && resumeCursor.getCount() > 0) {
        	Log.d("getCurUserResumeById", " not null && > 0 ");
        	resumeCursor.moveToFirst();
        }
        return resumeCursor;
	} 
}
