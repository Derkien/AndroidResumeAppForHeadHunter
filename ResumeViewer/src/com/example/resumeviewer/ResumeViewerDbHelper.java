package com.example.resumeviewer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.resumeviewer.FeedTablesContract.TableResumeAnsw;
import com.example.resumeviewer.FeedTablesContract.TableUserResume;

public class ResumeViewerDbHelper extends SQLiteOpenHelper {
	private static final String TEXT_TYPE = " TEXT";
	private static final String INT_TYPE = " INTEGER";
	private static final String NN = " NOT NULL";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_USER_RESUME =
		"CREATE TABLE " + TableUserResume.TABLE_NAME + " (" +
		TableUserResume._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		TableUserResume.COLUMN_NAME_USER_NAME + TEXT_TYPE + NN + COMMA_SEP +
		TableUserResume.COLUMN_NAME_USER_DATE_BIRTH + TEXT_TYPE + NN + COMMA_SEP +
		TableUserResume.COLUMN_NAME_USER_SEX + TEXT_TYPE + NN + COMMA_SEP +
		TableUserResume.COLUMN_NAME_USER_JOB + TEXT_TYPE + NN + COMMA_SEP +
		TableUserResume.COLUMN_NAME_USER_SALARY + TEXT_TYPE + NN + COMMA_SEP +
		TableUserResume.COLUMN_NAME_USER_PHONE + TEXT_TYPE + NN + COMMA_SEP +
		TableUserResume.COLUMN_NAME_USER_EMAIL + TEXT_TYPE + NN + COMMA_SEP +
		TableUserResume.COLUMN_NAME_SENT + INT_TYPE + NN + COMMA_SEP +
		TableUserResume.COLUMN_NAME_TIME + TEXT_TYPE + NN + ")";
	private static final String SQL_CREATE_RESUME_ANSW =
		"CREATE TABLE " + TableResumeAnsw.TABLE_NAME + " (" +
		TableResumeAnsw._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		TableResumeAnsw.COLUMN_RESUME_ID + INT_TYPE + NN + COMMA_SEP +
		TableResumeAnsw.COLUMN_NAME_ANSW_TEXT + TEXT_TYPE + NN + COMMA_SEP +
//		TableResumeAnsw.COLUMN_NAME_ANSW_FROM + TEXT_TYPE + NN + COMMA_SEP +
		TableResumeAnsw.COLUMN_NAME_ANSW_FROMORG + TEXT_TYPE + NN + COMMA_SEP +
		TableResumeAnsw.COLUMN_NAME_ANSW_TIME + TEXT_TYPE + NN + COMMA_SEP +
		TableResumeAnsw.COLUMN_NAME_ANSW_CHECKED + INT_TYPE + NN + 
		")";

	private static final String SQL_DELETE_USER_RESUME =
		"DROP TABLE IF EXISTS " + TableUserResume.TABLE_NAME;
	private static final String SQL_DELETE_RESUME_ANSW =
		"DROP TABLE IF EXISTS " + TableResumeAnsw.TABLE_NAME;
	
	// If you change the database schema, you must increment the database version.
	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "ResumeViewer.db";

	public ResumeViewerDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_RESUME_ANSW);
		Log.d("createDb", "1st created");
		db.execSQL(SQL_CREATE_USER_RESUME);
		Log.d("createDb", "2nd created");
	}
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for online data, so its upgrade policy is
		// to simply to discard the data and start over
		db.execSQL(SQL_DELETE_USER_RESUME);
		db.execSQL(SQL_DELETE_RESUME_ANSW);
		onCreate(db);
	}
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}