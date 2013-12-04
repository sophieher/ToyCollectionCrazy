package com.soph1her.toycollectioncrazyscarygirledition;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


public class ToyView extends Activity {

	private static final String TAG = "ToyView";

	private long rowID;

	private ImageView img;
	private TextView name;
	private TextView descriptionSet;
	private TextView year;
	private CheckBox collectionOwn;
	private CheckBox collectionWant;
	private CheckBox collectionTrade;
	private boolean customToyFlag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "in ToyView.oncreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.toy_view);

		img = (ImageView) findViewById(R.id.imgView);
		name = (TextView) findViewById(R.id.nameTextView);
		descriptionSet = (TextView) findViewById(R.id.setTextView);
		year = (TextView) findViewById(R.id.yearTextView);
		collectionOwn = (CheckBox) findViewById(R.id.ownedTextView);
		collectionWant = (CheckBox) findViewById(R.id.wantedTextView);
		collectionTrade = (CheckBox) findViewById(R.id.tradeTextView);

		Bundle extras = getIntent().getExtras();
		rowID = extras.getLong(ToyListActivity.ROW_ID); 
	} 

	public void onCheckboxClicked(View view) {
		// Is the view now checked?
		boolean checked = ((CheckBox) view).isChecked();

		// If the box is now checked, set it to checked, else uncheck it
		if (checked){
			((CheckBox) view).setChecked(true);
			Log.d("Testing checkbox", "checked " +checked);
		}

		else{
			((CheckBox) view).setChecked(false);
			Log.d("Testing checkbox", "checked " +checked);
		}
	}

	// called when the activity is first created
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		// Load the single toy using an AsyncTask
		new LoadToyTask().execute(rowID);
	} 

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		AsyncTask<Object, Object, Object> editToyTask = new AsyncTask<Object, Object, Object>() {
			@Override
			protected Object doInBackground(Object... params) { 
				saveToy(); 
				return null;
			} 
			@Override
			protected void onPostExecute(Object result) { 
				finish(); 
			}
		};

		editToyTask.execute((Object[]) null);
	}

	// performs database query outside GUI thread
	private class LoadToyTask extends AsyncTask<Long, Object, Cursor> {

		DBConnector dbconnector = 
				new DBConnector(ToyView.this);

		// perform the database access
		@Override
		protected Cursor doInBackground(Long... params) {
			dbconnector.open();

			// get a cursor containing all data on given entry
			return dbconnector.getOneToy(params[0]);
		}


		// use the Cursor returned from the doInBackground method
		@Override
		protected void onPostExecute(Cursor result) {
			super.onPostExecute(result);

			result.moveToFirst(); 

			// get the column index for each data item
			int nameIndex = result.getColumnIndex("name");
			int setIndex = result.getColumnIndex("descriptionSet");
			int yearIndex = result.getColumnIndex("year");
			int customIndex = result.getColumnIndex("custom");
			int imgIndex = result.getColumnIndex("imgURL");
			int ownIndex = result.getColumnIndex("collectionOwn");
			int wantIndex = result.getColumnIndex("collectionWant");
			int tradeIndex = result.getColumnIndex("collectionTrade");

			// Update the views 
			name.setText(result.getString(nameIndex));
			descriptionSet.setText(result.getString(setIndex));
			year.setText(result.getString(yearIndex));

			if(result.getInt(customIndex) == 1){
				img.setImageBitmap(BitmapFactory.decodeFile(result.getString(imgIndex)) );
				customToyFlag = true;
			}
			else{
				Log.d(TAG, "IMGURL" +  result.getString(imgIndex));
				final String imurl = result.getString(imgIndex);
				customToyFlag = false;				
				new AsyncTask<Void, Void, Bitmap>() {
					URL url;
					
					Bitmap bmp;
					   @Override
					   protected Bitmap doInBackground(Void... params) {
						   try {
								url = new URL(imurl);
								bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						   return bmp;
					   }

					   @Override
					   protected void onPostExecute(Bitmap result) {
					      img.setImageBitmap(result);
					   }

					}.execute();
			}

			if(result.getString(ownIndex).equals("true")){
				collectionOwn.setChecked(true);
			}
			if(result.getString(wantIndex).equals("true"))
				collectionWant.setChecked(true);

			if(result.getString(tradeIndex).equals("true"))
				collectionTrade.setChecked(true);

			result.close();
			dbconnector.close(); 
		}	
	}

	// create the Activity's menu from a menu resource XML file
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.custom_toy, menu);
		return true;
	}


	// handle choice from options menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete_toy:
			deleteCustomToy();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	} 


	// Delete only a custom toy from the database
	private void deleteCustomToy() {
		if(customToyFlag){
		// create a new AlertDialog Builder
		AlertDialog.Builder builder = 
				new AlertDialog.Builder(ToyView.this);

		builder.setTitle(R.string.confirmTitle); 
		builder.setMessage(R.string.confirmMessage); 

		// provide an OK button that simply dismisses the dialog
		builder.setPositiveButton(R.string.button_delete,
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int button) {

				final DBConnector databaseConnector = 
						new DBConnector(ToyView.this);

				// create an AsyncTask to delete a toy
				AsyncTask<Long, Object, Object> deleteTask =
						new AsyncTask<Long, Object, Object>() {

					@Override
					protected Object doInBackground(Long... params) {
						databaseConnector.deleteToy(params[0]); 
						return null;
					} 

					@Override
					protected void onPostExecute(Object result) {
						finish(); 
					} 
				}; 

				deleteTask.execute(new Long[] { rowID });               
			}
		}); 

		builder.setNegativeButton(R.string.button_cancel, null);
		builder.show(); 
		}
		else{
			AlertDialog.Builder builder = 
					new AlertDialog.Builder(ToyView.this);

			builder.setTitle(R.string.denyDeleteTitle); 
			builder.setMessage(R.string.denyDeleteMessage); 
			builder.setPositiveButton(R.string.okButton, null);
 			builder.show();
		}
	} 

	private void saveToy() {
		// get DBConnector to interact with the SQLite database
		DBConnector dbConnector = new DBConnector(this);
		dbConnector.update(
				rowID,
				String.valueOf(collectionOwn.isChecked()), 
				String.valueOf(collectionWant.isChecked()),
				String.valueOf(collectionTrade.isChecked()));
		Log.d(TAG, "saveToy " + String.valueOf(collectionOwn.isChecked()) + " " + 
				String.valueOf(collectionWant.isChecked()) + " " + 
				String.valueOf(collectionTrade.isChecked())) ;
	}

}

