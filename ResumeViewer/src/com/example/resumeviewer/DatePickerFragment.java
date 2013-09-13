package com.example.resumeviewer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment
implements DatePickerDialog.OnDateSetListener {
	
	
	public interface OnDatePickedListener {
		public void setSelectedDate(int year, int month, int day);
		public String getInputDate();
	}
	    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		OnDatePickedListener userResume = (OnDatePickedListener) getActivity();
		String dates = userResume.getInputDate();
		final Calendar c = Calendar.getInstance();
		
		SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());    

		// Use the current date as the default date in the picker
		if (dates != null){
			//Log.d("dateBefore",dates);
			try {
				c.setTime(df.parse(dates));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Log.d("dateAfter",dates);
		}
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);			

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		OnDatePickedListener setDate = (OnDatePickedListener) getActivity();
			setDate.setSelectedDate(year, month, day);

	}
}