package com.soph1her.toycollectioncrazyscarygirledition;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

import com.soph1her.toycollectioncrazyscarygirledition.toyendpoint.Toyendpoint;
import com.soph1her.toycollectioncrazyscarygirledition.toyendpoint.model.Toy;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ToyListActivity extends FragmentActivity implements ActionBar.TabListener {

	public static int TAB = 0;
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	// ListView and adapter
	public static final String ROW_ID = "row_id"; // Intent extra key
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toy_list);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		} 
	}


	@Override

	protected void onResume() {
		super.onResume(); 

		//		new GetToysTask().execute((Object[]) null);
	} 


	@SuppressWarnings("deprecation")
	@Override
	protected void onStop() {
		//		Cursor cursor = mcollectionAdapter.getCursor(); 
		//
		//		if (cursor != null) 
		//			cursor.deactivate(); // deactivate it
		//
		//		mcollectionAdapter.changeCursor(null); //make cursor null
		super.onStop();
	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.toy_list, menu);
		return true;
	}


	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) { 
		case R.id.action_add_to_collection: 
			startActivity(new Intent(getBaseContext(), CustomToyActivity.class)); 
			return true; 
		}
		return false;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			TAB = position;
			Log.d("TOYLISTACTIVITY", " " + TAB);
			// getItem is called to instantiate the fragment for the given page.
			if(position == 0){

				Fragment fragment = new AllToysFragment();
				return fragment;
			}
			else if( position == 1){

				Fragment fragment = new MyToysFragment();
				return fragment;
			}
			else{
				Fragment fragment = new DummySectionFragment();
				//				Bundle args = new Bundle();
				//				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
				//				fragment.setArguments(args);
				return fragment;
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text. Place holder for when I get the 
	 * google authentication working again.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_toy_list_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText("Feature Coming Soon...");
			return rootView;
		}
	}
	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text. Place holder for when I get the 
	 * google authentication working again.
	 */
	public static class MyToysFragment extends ListFragment {
		private ListView mToyList;
		private static CursorAdapter mcollectionAdapter; 
		private BitmapCache mBitmapCache;


		public MyToysFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_toy_list_my,
					container, false);
			return rootView;
		}

		// code idea from http://www.mysamplecode.com/2012/08/android-fragment-example.html
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			//Generate list View from Cursor
			//mBitmapCache = new BitmapCache(getActivity());
			displayListView();
		}

		@Override
		public void onResume() {
			super.onResume();
			new GetMyToysTask().execute((Object[]) null);
		}

		// Nested AsyncTask performs database query outside GUI thread
		private class GetMyToysTask extends AsyncTask<Object, Object, Cursor> {
			DBConnector databaseConnector = new DBConnector(getActivity());

			// perform the database access
			@Override
			protected Cursor doInBackground(Object... params) {
				databaseConnector.open();
				return databaseConnector.getMyToys(); 

			} 

			// use the Cursor returned from the doInBackground method
			@Override
			protected void onPostExecute(Cursor result) {
				mcollectionAdapter.changeCursor(result); 
				databaseConnector.close();
			} 
		} 

		private void displayListView() {
			mToyList = (ListView) getView().findViewById(android.R.id.list);
			mToyList.setOnItemClickListener(viewToyListener);  
			TextView imgPath = (TextView) getView().findViewById(R.id.imgTextView);
			ImageView img = (ImageView) getView().findViewById(R.id.imageView1);
			// map each toy's name to a TextView in the ListView layout
			String[] from = new String[] { "name", "descriptionSet"};
			int[] to = new int[] { R.id.nameTextView, R.id.setTextView};
			
			// class used to override setViewImage to use AsyncTask to load images
			mcollectionAdapter = new SimpleCursorAdapter(getActivity(), 
					R.layout.toy_list_item, null, 
					from, to){
				
				@Override 
				public void setViewImage(ImageView v, String val){
					final String url = val;
					Log.d("MY TOYS FRAGMENT", "Cursor val: " +val);
	                v.setImageBitmap(null);
	                //new LoadImageAsyncTask(mBitmapCache, v, url).execute();
				}
			};
			setListAdapter(mcollectionAdapter); 
		}


		@Override
		public void onPause() {
			super.onPause();
		}

		@Override
		public void onStop() {
			super.onStop();
			Cursor cursor = mcollectionAdapter.getCursor(); 

			if (cursor != null) 
				cursor.deactivate(); // deactivate it

			mcollectionAdapter.changeCursor(null); //make cursor null
		}

		// ListView listener for specific toy
		OnItemClickListener viewToyListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				Log.d("ToyList", "position: " + position + ", id: " + id);
				// create an Intent to launch ToyView
				Intent viewToy = 
						new Intent(getActivity(), ToyView.class);

				// pass the selected contact's row ID as an extra with the Intent
				viewToy.putExtra(ROW_ID, id);
				startActivity(viewToy);
			} 
		};
	}

	/**
	 * Fragment for All of the toys within main activity
	 * May be used for both ListView toy fragments
	 * All Toys & My Toys
	 *
	 */
	public static class AllToysFragment extends Fragment {
		private static final String TAG = "TOY LIST ACTIVITY";
		private ListView mToyList; 
		private static CursorAdapter mcollectionAdapter; 


		/* default */
		public AllToysFragment(){
		}


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, 
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_toy_list_all, container, false);
			return rootView;
		}

		// code idea from http://www.mysamplecode.com/2012/08/android-fragment-example.html
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			//Generate list View from Cursor
			displayListView();

		}

		@Override
		public void onStart() {
			super.onStart();
			Log.i(TAG, "onStart");
		}

		@Override
		public void onResume() {
			super.onResume();
			Log.i(TAG, "onResume");
			new GetToysTask().execute((Object[]) null);
		}

		// Nested AsyncTask performs database query outside GUI thread
		private class GetToysTask extends AsyncTask<Object, Object, Cursor> {
			DBConnector databaseConnector = new DBConnector(getActivity());

			// perform the database access
			@Override
			protected Cursor doInBackground(Object... params) {
				databaseConnector.open();
				return databaseConnector.getAllToys(); 

			} 

			// use the Cursor returned from the doInBackground method
			@Override
			protected void onPostExecute(Cursor result) {
				mcollectionAdapter.changeCursor(result); 
				int customIndex = result.getColumnIndex("custom");
				int imgIndex = result.getColumnIndex("imgURL");
				if(imgIndex != -1){
				final ImageView img = (ImageView) getActivity().findViewById(R.id.imageView1);
				if(result.getInt(customIndex) == 1){
					img.setImageBitmap(BitmapFactory.decodeFile(result.getString(imgIndex)) );
				}
				else{
					Log.d(TAG, "IMG URL" +  result.getString(imgIndex));
					final String imurl = result.getString(imgIndex);
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
				}}
				databaseConnector.close();
			} 
		} 

		private void displayListView() {
			mToyList = (ListView) getView().findViewById(android.R.id.list);
			mToyList.setOnItemClickListener(viewToyListener);  
			TextView imgPath = (TextView) getView().findViewById(R.id.imgTextView);
			ImageView img = (ImageView) getView().findViewById(R.id.imageView1);
			
			// map each toy's name to a TextView in the ListView layout
			String[] from = new String[] { "name", "descriptionSet" };
			int[] to = new int[] { R.id.nameTextView, R.id.setTextView };
			mcollectionAdapter = new SimpleCursorAdapter(getActivity(), 
					R.layout.toy_list_item, null, 
					from, to);
			// public SimpleCursorAdapter (Context context, 
			// int layout, Cursor c, 
			// String[] from, int[] to)

			mToyList.setAdapter(mcollectionAdapter); 
		}


		@Override
		public void onPause() {
			super.onPause();
			Log.i(TAG, "onPause");
		}

		@Override
		public void onStop() {
			super.onStop();
			Log.i(TAG, "onStop");
			Cursor cursor = mcollectionAdapter.getCursor(); 

			if (cursor != null) 
				cursor.deactivate(); // deactivate it

			mcollectionAdapter.changeCursor(null); //make cursor null
		}

		// ListView listener for specific toy
		OnItemClickListener viewToyListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				Log.d("ToyList", "position: " + position + ", id: " + id);
				// create an Intent to launch ToyView
				Intent viewToy = 
						new Intent(getActivity(), ToyView.class);

				// pass the selected contact's row ID as an extra with the Intent
				viewToy.putExtra(ROW_ID, id);
				startActivity(viewToy);
			} 
		};
	}
}
