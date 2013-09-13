package com.example.resumeviewer;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import com.example.resumeviewer.FeedTablesContract.TableResumeAnsw;
import com.example.resumeviewer.FeedTablesContract.TableUserResume;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ViewUserResume extends Activity {

	
	public final static String EXTRA_EMPLOYER_ANSWER = "com.example.resumeviewer.EMPLOYERANSWER";
	public final static String EXTRA_EMPLOYER = "com.example.resumeviewer.EMPLOYER";
	
	private Long resumeId;
	private String employer;
	private Cursor resumeData;
	
	private TextView userName;
	private TextView userSex;
	private TextView userBirth;
	private TextView userJob;
	private TextView userSalary;
	private TextView userPhone;
	private TextView userEmail;
	private EditText employerAnswer;
	
	private String from;

	ResumeViewerDbHelper dbHelper = new ResumeViewerDbHelper(ViewUserResume.this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_user_resume);
	
		userName = (TextView) findViewById(R.id.user_name);
		userBirth = (TextView) findViewById(R.id.user_birth);
		userJob = (TextView) findViewById(R.id.user_job);
		userSalary = (TextView) findViewById(R.id.user_money);
		userPhone = (TextView) findViewById(R.id.user_phone);
		userEmail = (TextView) findViewById(R.id.user_email);
		userSex = (TextView) findViewById(R.id.user_sex);
		employerAnswer = (EditText) findViewById(R.id.employer_answer);
		
		Intent intent = getIntent();
		from = intent.getStringExtra("intentFrom");
		
		if(from.equals("UserResume")){
			userName.setText(intent.getStringExtra(UserResume.EXTRA_NAME));
			userBirth.setText(intent.getStringExtra(UserResume.EXTRA_DATEBIRTH));
			userJob.setText(intent.getStringExtra(UserResume.EXTRA_JOB));
			userSalary.setText(intent.getStringExtra(UserResume.EXTRA_SALARY));
			userPhone.setText(intent.getStringExtra(UserResume.EXTRA_PHONE));
			userEmail.setText(intent.getStringExtra(UserResume.EXTRA_EMAIL));
			userSex.setText(intent.getStringExtra(UserResume.EXTRA_SEX));
			employer = intent.getStringExtra(UserResume.EXTRA_EMPLOYER);
		}

		if(from.equals("EmployerAct")){
    		resumeId = intent.getLongExtra(EmployerAct.EXTRA_MESSAGE_RESUME_ID, 0);
			if(resumeId > 0)
			{
				resumeData = getCurUserResumeById(resumeId);
				userName.setText(resumeData.getString(resumeData.getColumnIndexOrThrow(TableUserResume.COLUMN_NAME_USER_NAME)));
				userBirth.setText(resumeData.getString(resumeData.getColumnIndexOrThrow(TableUserResume.COLUMN_NAME_USER_DATE_BIRTH)));
				userJob.setText(resumeData.getString(resumeData.getColumnIndexOrThrow(TableUserResume.COLUMN_NAME_USER_JOB)));
				userSalary.setText(resumeData.getString(resumeData.getColumnIndexOrThrow(TableUserResume.COLUMN_NAME_USER_SALARY)));
				userPhone.setText(resumeData.getString(resumeData.getColumnIndexOrThrow(TableUserResume.COLUMN_NAME_USER_PHONE)));
				userEmail.setText(resumeData.getString(resumeData.getColumnIndexOrThrow(TableUserResume.COLUMN_NAME_USER_EMAIL)));
				userSex.setText(resumeData.getString(resumeData.getColumnIndexOrThrow(TableUserResume.COLUMN_NAME_USER_SEX)));
				//imulation of random employer
	            Random generator = new Random(new Date().getTime());
	            employer = "employer"+generator.nextInt(500);
			}
			else
			{
	    		Toast toast = Toast.makeText(getApplicationContext(), 
	  				   "Error!!! Can't get data for resume by rowID!!", Toast.LENGTH_SHORT); 
	     		toast.show();
			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_user_resume, menu);
		return true;
	}

	/*
	 * Ответ на резюме
	 */
	public void answToResume(View v){

	 	final AlertDialog.Builder ad = new AlertDialog.Builder(this);
 	
	 	// a bit of checks
	 	if( employerAnswer.getText().length() == 0 ){
	 		ad.setMessage("Без ответа не отправится!!!");
	 		ad.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	dialog.dismiss();     
                }
            });
	 		ad.create(); 
	 		ad.show();
	 		employerAnswer.requestFocus();
	 	}
	 	else if(from.equals("UserResume")){
			Intent intent = new Intent();
			intent.putExtra(EXTRA_EMPLOYER_ANSWER, employerAnswer.getText().toString());
			intent.putExtra(EXTRA_EMPLOYER, employer);
			setResult(RESULT_OK, intent);
			finish();
	 	}
	 	else if(from.equals("EmployerAct")){
	 		//insert answer for db
        	ContentValues values = new ContentValues();
        	
        	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        	String dateForinsert = df.format(new GregorianCalendar().getTime());
        	values.put(TableResumeAnsw.COLUMN_NAME_ANSW_TIME, dateForinsert);
        	values.put(TableResumeAnsw.COLUMN_NAME_ANSW_CHECKED, 0);
        	values.put(TableResumeAnsw.COLUMN_RESUME_ID, resumeId);
        	values.put(TableResumeAnsw.COLUMN_NAME_ANSW_TEXT, employerAnswer.getText().toString());
        	values.put(TableResumeAnsw.COLUMN_NAME_ANSW_FROMORG, employer);
        	
        	// Gets the data repository in write mode
        	SQLiteDatabase db = dbHelper.getWritableDatabase();
        	
        	Long rowId = db.insert(
        			TableResumeAnsw.TABLE_NAME,
        	         null,
        	         values);
        	if(rowId > 0)
        	{
	    		Toast toast = Toast.makeText(getApplicationContext(), 
		  				   "Answer sent", Toast.LENGTH_SHORT); 
		     	toast.show();
        	}
        	finish();
        }
 		
	}
	
	public void callMe(View v){
		String tel = userPhone.getText().toString();
		tel = PhoneNumberUtils.formatNumber(tel);
		Intent sIntent = new Intent(Intent.ACTION_CALL, Uri
		   .parse("tel:"+userPhone.getText().toString()));
		startActivity(sIntent);
	}
	public void mailMe(View v){
		String email = userEmail.getText().toString();
		if(email != ""){
			String uriText =
				    "mailto:" + email + 
				    "?subject=" + Uri.encode("My request for resume") + 
				    "&body=" + Uri.encode("That's how i'll answer for your resume. "+employerAnswer.getText().toString());

				Uri uri = Uri.parse(uriText);

			Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
			sendIntent.setData(uri);
			startActivity(Intent.createChooser(sendIntent, "Send email"));	
			//or that way... or just from XML layout with autolink
//			Intent msg=new Intent(Intent.ACTION_SEND);
//			String[] to={email};
//			String[] copie={"u2@to.com"};
//	
//			msg.putExtra(Intent.EXTRA_EMAIL, to);
//			msg.putExtra(Intent.EXTRA_CC, copie);
//			msg.putExtra(Intent.EXTRA_TEXT, "Ответ на ваше резюме");
//			msg.putExtra(Intent.EXTRA_SUBJECT, "Резюме");
//			msg.setType("message/rfc822");
//			startActivity(Intent.createChooser(msg, "Отправить "));
		}
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

        if (resumeCursor != null && resumeCursor.getCount() > 0) {
        	Log.d("getCurUserResumeById", " not null && > 0 ");
        	resumeCursor.moveToFirst();
        }
        return resumeCursor;
	} 
}