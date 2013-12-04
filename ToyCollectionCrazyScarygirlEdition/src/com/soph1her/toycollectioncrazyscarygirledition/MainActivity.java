// First screen
// has the title of the app
// a button to go to the toys
// a button to sign-in/authenticate with google
//TODO: an about dialog action bar setting 


package com.soph1her.toycollectioncrazyscarygirledition;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button mToyLauncher;
	private Button mLoginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mToyLauncher = (Button) findViewById(R.id.enter_button);
		mToyLauncher.setEnabled(true);
		mToyLauncher.setOnClickListener(new ButtonClickListener());

		mLoginButton = (Button) findViewById(R.id.login);
		mLoginButton.setEnabled(true);
		mLoginButton.setOnClickListener(new ButtonClickListener());
	}


	private class ButtonClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if(mToyLauncher.getId() == ((Button)v).getId())
				startActivity(new Intent(getBaseContext(), ToyListActivity.class));
			else
				startActivity(new Intent(getBaseContext(), LoginActivity.class));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) { 
		case R.id.about: 
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setMessage(R.string.about_message);
			builder.setTitle(R.string.about_title);
			builder.setPositiveButton(R.string.okButton, null);
			AlertDialog dialog = builder.create();
			dialog.setView(MainActivity.this.getLayoutInflater().inflate(R.layout.dialog, null));
			builder.show();
			return true; 
		}
		return false;
	}

}


