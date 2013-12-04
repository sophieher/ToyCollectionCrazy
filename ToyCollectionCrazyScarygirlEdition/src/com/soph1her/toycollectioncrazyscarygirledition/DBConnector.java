package com.soph1her.toycollectioncrazyscarygirledition;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author sophia hernandez
 *
 */
public class DBConnector {

	private String DB_NAME = "AllToys";
	private SQLiteDatabase database;
	private DatabaseOpenHelper dbOpenHelper;
	
    private static final String DB_PATH = "/data/data/com.soph1her.toycollectioncrazyscarygirledition/databases/";
	private static final String TAG = "DBConnector >>>>>";

	
	public DBConnector(Context context) {
		dbOpenHelper = new DatabaseOpenHelper(context, DB_NAME, null, 1);
		
	}
	
	public void open() throws SQLException {
		// check to see if the database has been loaded
		// Adapted from Reign Design 
	 
	    	SQLiteDatabase checkDB = null;
	 
	    	try{
	    		String myPath = DB_PATH + DB_NAME;
	    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	    	}catch(SQLiteException e){
	    		// there is not a DB yet, so copy it
	    	}
	    	
	    	if(checkDB != null){
	    		checkDB.close();
	    		database = dbOpenHelper.getWritableDatabase();
	    	}
	    	else {
	    		database = dbOpenHelper.getWritableDatabase();
	    		try {
					dbOpenHelper.copyDataBase();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	}
	}
	
	public void close() {
		if(database != null)
			database.close();
	}
	
	
	
	/**
	 *  Insert inserts the values of a toy into the database
	 * @param name
	 * @param descriptionSet
	 * @param year
	 * @param imgURL
	 * @param collectionOwn
	 * @param collectionWant
	 * @param collectionTrade
	 * @param tag
	 */
    public void insert(String name, String descriptionSet, 
            String year, String imgURL, String collectionOwn, String collectionWant, String collectionTrade, int tag) {

        ContentValues newToy = new ContentValues();
        newToy.put("name", name);
        newToy.put("descriptionSet", descriptionSet);
        newToy.put("year", year);
        newToy.put("imgURL", imgURL);
        newToy.put("collectionOwn", collectionOwn);
        newToy.put("collectionWant", collectionWant);
        newToy.put("collectionTrade", collectionTrade);
        newToy.put("custom", tag);

        open();
        database.insert("toys", null, newToy);
        close();
    }
    
    /**
     * Updates the database with new values
     * @param id
     * @param name
     * @param descriptionSet
     * @param year
     * @param imgURL
     * @param collectionOwn
     * @param collectionWant
     * @param collectionTrade
     */
    public void update(long id, String collectionOwn, String collectionWant, String collectionTrade) {

        ContentValues editToy = new ContentValues();

        editToy.put("collectionOwn", collectionOwn);
        editToy.put("collectionWant", collectionWant);
        editToy.put("collectionTrade", collectionTrade);
        Log.d("DB UPDATE" , " " +collectionOwn+ collectionWant+ collectionTrade);

        open();
        database.update("toys", editToy, "_id=" + id, null);
        close();
    }
    
    // get the Cursor list of all the toys
    public Cursor getAllToys() {
        return database.query("toys", new String[] {"_id", "name", "descriptionSet"}, 
                null, null, null, null, "name");
        // query(String table, 
        // String[] columns, String selection, String[] selectionArgs, 
        // String groupBy, String having, String orderBy)
    }
    
    // get the Cursor list of all my toys in collection
    public Cursor getMyToys() {
    	String Where = "collectionOwn='true'";
        return database.query("toys", new String[] {"_id", "name", "descriptionSet"}, 
                Where, null, null, null, "name");
        // query(String table, 
        // String[] columns, String selection, String[] selectionArgs, 
        // String groupBy, String having, String orderBy)
    }
    
 // get a Cursor containing all information about the toy specified
    // by the given id
    public Cursor getOneToy(long id) {
        return database.query(
                "toys", null, "_id=" + id, null, null, null, null);

        // public Cursor query (String table, String[] columns, 
        // String selection, String[] selectionArgs, String groupBy, 
        // String having, String orderBy, String limit)
    }
    
 // delete the toy specified by the given id
    public void deleteToy(long id) {
        open(); 
//        if(database.query("toys", null, "_id="+ id, null, null, null, null))
        database.delete("toys", "_id=" + id, null);
        close();
    } 
	
	private class DatabaseOpenHelper extends SQLiteOpenHelper {
		Context context;
 	 	 	    
        public DatabaseOpenHelper(Context context, String name,
                CursorFactory factory, int version) {
            super(context, name, factory, version);
            this.context = context;
        }

        /**
         * create a SQLite DB named toys with all of the columns shown
         * Called when the database is created for the first time.
         */
		@Override
		public void onCreate(SQLiteDatabase db) {
            String createQuery = "CREATE TABLE toys" +
                    "(_id INTEGER PRIMARY KEY autoincrement, " +
                    "name TEXT, " +
                    "descriptionSet TEXT, " +
                    "year TEXT, " +
                    "imgURL TEXT, " +
                    "collectionOwn TEXT, " +
                    "collectionWant TEXT, " +
                    "collectionTrade TEXT, " +
                    "custom INTEGER);";

            db.execSQL(createQuery);
            
		}

		
		/**
		 * FROM REIGN DESIGN
	     * Copies your database from your local assets folder to the just created empty database in the
	     * system folder, from where it can be accessed and handled.
	     * This is done by transferring byte stream.
	     * */
	    private void copyDataBase() throws IOException{
	    	Log.d(TAG, "copying the database");
	    	
	    	//Open your local db as the input stream
	    	InputStream myInput = context.getAssets().open(DB_NAME);
	 
	    	// Path to the just created empty db
	    	String outFileName = DB_PATH + DB_NAME;
	 
	    	//Open the empty db as the output stream
	    	OutputStream myOutput = new FileOutputStream(outFileName);
	 
	    	//transfer bytes from the input file to the output file
	    	byte[] buffer = new byte[1024];
	    	int length;
	    	while ((length = myInput.read(buffer))>0){
	    		myOutput.write(buffer, 0, length);
	    	}
	    	//Close the streams
	    	myOutput.flush();
	    	myOutput.close();
	    	myInput.close();
	 
	    }

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			/* "nothing to be done." */
		} 

	}
}
