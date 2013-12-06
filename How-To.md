ToyCollectionCrazy
==================

Sophia Hernandez
smh3665

###Setting up a SQLite Database to Send with an App
I wanted to have a list of toys already populated in the app so that the users could immediately begin adding to their own collections. 
So, I looked up ways to create a database, and then add it to the app project to pull from. 
<a href="http://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/">This tutorial</a>, found here: http://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/,  from the reigndesign blog uses the application SQLite Database Browser to create the database. On the first run of the app, the table is copied into the database of the device for the application to access.

###Loading an Image from a URL
In the ToyView, the activity that loads needs to grab an image, either from the MediaStore if it is a user-added toy, or from a URL that is located in the SQLite Database of toys. There are a lot of resources for this available online, and I used a mash-up of a number of them to fit my own needs. One solution that I found particularly useful was on StackOverflow: 
<a href="http://stackoverflow.com/questions/3090650/android-loading-an-image-from-the-web-with-asynctask">Android : Loading an image from the Web with Asynctask</a>, found here: http://stackoverflow.com/questions/3090650/android-loading-an-image-from-the-web-with-asynctask. <br>My solution uses BitmapFactory.decodeStream() with an input stream returned from url.openConnection() in order to retrieve the image from the URL. It then assigns the bitmap to the ImageView in the layout of this activity.

###Adding Two Fragments with Lists in the Same Activity
ToyListActivity is the main activity for this app. This activity has a ViewPagerAdaptor for using tab navigation and has Fragments for each of the tabs. I use two of these Fragment classes to hold lists, and kept running into the problem of having only one of the lists populate. On my first iteration, I had both of the Fragments as ListFragments, which the Android Developer reference page says, "hosts a ListView object that can be bound to different data sources."
For my app, I used a Cursor to hold results from querying the SQLite Database. <br>When I had two ListFragments, I could only get one of the lists, the second one namely, to populate in the app. I decided that maybe I could just use one ListFragment, and have the ViewPager call it for each tab with a filtered result, one for All Toys and one for My Toys. This created an interesting result. Upon launching the app the All Toys list would populate, then after switching through tabs, the All Toys list would be gone from sight, and the My Toys tab would populate, only showing those toys that were marked as such. <br>I searched all over the web, read the documentation online and read countless questions about similar issues that other people were having on the web regarding ListFragments within a single activity class, and while I found many solutions, none of them seemed to work.<br>In the end, I decided to use two separate classes, one that extends ListFragment one Fragment that holds a ListView.
<br>
As you can see below, each class now has its own ListView and CursorAdaptor to retrieve the database information.

````
public static class AllToysFragment extends Fragment {
		private ListView mToyList; 
		private static CursorAdapter mcollectionAdapter; 
		...
````

````
public static class MyToysFragment extends ListFragment {
		private ListView mToyList;
		private static CursorAdapter mcollectionAdapter;
		...
````

In fact, both of the Fragment classes have the same layout inflation and list population techniques, which unfortunately is redundant. I changed from the single ListFragment with this:

````
@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, 
				Bundle savedInstanceState) {
			if(allToys)
			  View rootView = inflater.inflate(R.layout.fragment_toy_list_all, container, false);
			else
			  View rootView = inflater.inflate(R.layout.fragment_toy_list_my, container, false);
			return rootView;
		}
		...
		// Nested AsyncTask performs database query outside GUI thread
		private class GetToysTask extends AsyncTask<Object, Object, Cursor> {
			DBConnector databaseConnector = new DBConnector(getActivity());

			// perform the database access
			@Override
			protected Cursor doInBackground(Object... params) {
				databaseConnector.open();
				if(allToys)
				  return databaseConnector.getAllToys(); 
				else
				  return databaseConnector.getMyToys();
			} 
			...
````
Where the databaseConnector is of the type DBConnector class and uses different queries to retrieve the specified data from the database.<br>
This code was then repeated like this in each class:

````
@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, 
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_toy_list_all, container, false);
			return rootView;
		}
````
Again, this redundancy is unsettling, but this allows for both lists to be shown in the app, without any loss of information, which is what is needed for the design.
