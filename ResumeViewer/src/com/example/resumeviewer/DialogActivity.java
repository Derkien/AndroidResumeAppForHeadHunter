package com.example.resumeviewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DialogActivity extends Activity {
	String message;
	String messageTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);

		// Get the message from the intent
		Intent intent = getIntent();
		
		if (message == ""  || message == null) {
			message = intent.getStringExtra(EmployerAct.EXTRA_MESSAGE);
		}
		if (message == ""  || message == null) {
			message = intent.getStringExtra(ResumeAnsws.EXTRA_MESSAGE);
		}
			
		messageTitle = intent.getStringExtra(ResumeAnsws.EXTRA_MESSAGE_TITLE);
		if(messageTitle != "")
			setTitle(messageTitle);
		// Create the text view
		TextView textView = (TextView)findViewById(R.id.dialog_text);
		textView.setTextSize(20);
		textView.setText(message);
		//setContentView(textView);	
		
     
	}

    public void finishDialog(View v) {
        DialogActivity.this.finish();
    }
}
