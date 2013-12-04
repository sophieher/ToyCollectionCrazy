// Class for Adding a custom toy
// has a layout for adding the toy
// the layout is connected to fields for the database.

package com.soph1her.toycollectioncrazyscarygirledition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

public class CustomToyActivity extends Activity {
	
	private static final String TAG = "CustomToy";

	private static final int MEDIA_TYPE_IMAGE = 0;

	private long rowID;

	private ImageButton addImg;
	private Uri fileUri;
	private File imgFile;
	private Bitmap mImageBitmap;
	private EditText name;
	private EditText descriptionSet;
	private EditText year;
	private CheckBox collectionOwn;
	private CheckBox collectionWant;
	private CheckBox collectionTrade;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_toy);
		// Show the Up button in the action bar.
		setupActionBar();

		addImg = (ImageButton) findViewById(R.id.imageButton1);
		addImg.setOnClickListener(takePhotoClicked);

		name = (EditText) findViewById(R.id.editName);
		descriptionSet = (EditText) findViewById(R.id.editSet);
		year = (EditText) findViewById(R.id.editYear);
		collectionOwn = (CheckBox) findViewById(R.id.checkOwn);
		collectionWant = (CheckBox) findViewById(R.id.checkWant);
		collectionTrade = (CheckBox) findViewById(R.id.checkTrade);

		Bundle extras = getIntent().getExtras();

		// if there are extras, use them to populate the EditTexts
		if (extras != null) {
			rowID = extras.getLong("row_id");
			name.setText(extras.getString("name")); 
			descriptionSet.setText(extras.getString("descriptionSet"));
			year.setText(extras.getString("year"));
			// TODO: find out how to get extra for setting check box
			//checkBox.setChecked(false);
		}
		
		

		// set event listener for the Add Button
		Button addToCollection = 
				(Button) findViewById(R.id.addToCollection);
		addToCollection.setOnClickListener(addToCollectionButtonClicked);
	}

	/** Create a file Uri for saving an image or video */
	private Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image */
	private File getOutputMediaFile(int type) {
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "TCC! ScaryGirl Edition");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (!mediaStorageDir.exists()){
	        if (!mediaStorageDir.mkdirs()){
	            Log.d("@string/app_name", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name. Code from android docs
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	OnClickListener takePhotoClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
			startActivityForResult(takePictureIntent, 2);
		}
	};

	@SuppressLint("NewApi")
	@Override
	public void onActivityResult(int request, int result, Intent intent) {
		if(request == 2 && result == RESULT_OK){
//			Bundle extras = intent.getExtras();
//			mImageBitmap = (Bitmap) extras.get("data");
//			addImg.setImageBitmap(mImageBitmap);
		    Drawable d;// = Drawable.createFromPath(f.getAbsolutePath());
			
			try {
			    InputStream inputStream = getContentResolver().openInputStream(fileUri);
			    d = Drawable.createFromStream(inputStream, fileUri.toString() );
			} catch (FileNotFoundException e) {
			    d = getResources().getDrawable(R.drawable.ic_launcher);
			}
			addImg.setBackground(d);
			addImg.setEnabled(false);
		}	
	}

	// responds to event generated when user clicks the done Button
	OnClickListener addToCollectionButtonClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if ((name.getText().length() != 0) && year.getText().length() != 0 && descriptionSet.getText().length() != 0) {
				AsyncTask<Object, Object, Object> addToyTask = new AsyncTask<Object, Object, Object>() {
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
				
				addToyTask.execute((Object[]) null); 
			}
			else {
				// create a new AlertDialog Builder
				AlertDialog.Builder builder = 
						new AlertDialog.Builder(CustomToyActivity.this);

				// set dialog title & message, and provide Button to dismiss
				builder.setTitle(R.string.errorTitle); 
				builder.setMessage(R.string.errorMessage);
				builder.setPositiveButton(R.string.okButton, null); 
				builder.show(); 
			}
		}
	};

	private void saveToy() {
		// get DBConnector to interact with the SQLite database
		DBConnector dbConnector = new DBConnector(this);

		if (getIntent().getExtras() == null) {
			Log.d(TAG, "fileuri: " + fileUri.getPath());
			dbConnector.insert(
					name.getText().toString(),
					descriptionSet.getText().toString(),
					year.getText().toString(), 
					fileUri.getPath(),
					String.valueOf(collectionOwn.isChecked()), 
					String.valueOf(collectionWant.isChecked()),
					String.valueOf(collectionTrade.isChecked()),
					//TODO: fix the value of custom. Just checking on the toy lists.
					1); // 1 indicates that this is a custom toy. 0 for debuggin only
		} 
		Log.d(TAG, "insert");
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.custom_toy, menu);
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

}
