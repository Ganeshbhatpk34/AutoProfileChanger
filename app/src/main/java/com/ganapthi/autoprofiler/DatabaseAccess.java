
package com.ganapthi.autoprofiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author harshmer
 *
 */
public class DatabaseAccess implements Serializable{
	
	private static final String DB_NAME = "profiler_db.sqlite";

    private Context context;
    
    public DatabaseAccess(Context c) {
        this.context = c;
    }
    
    /*
     * copy database to phone memory if doesn't exist on memory
     */
    private void copyDatabase(File dbFile) throws IOException {
        InputStream is = context.getAssets().open("profiler_db.sqlite");
        OutputStream os = new FileOutputStream(dbFile.getAbsoluteFile(), false);

        byte[] buffer = new byte[1024];
        while (is.read(buffer) > 0) {
            os.write(buffer);
        }

        os.flush();
        os.close();
        is.close();
    }
    
    /*
     * checks if the database is in the phone memory and calls the copy database() accordingly
     */
    public SQLiteDatabase openDatabase() {
        File dbFile = context.getDatabasePath(DB_NAME);
        File databaseFile = new File("/data/data/com.nightowl.profiler/databases");

        if (!dbFile.exists()) {
            try {
            	databaseFile.mkdir();
                copyDatabase(dbFile);
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }

        return SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
    }
    
    /*
     * Add values to the database
     */
    public int store_location(SQLiteDatabase db, Location loc){
    	
    	//loc = new Location();
    	String profile_name, profile;
    	Double longitude, latitude;
    	
    	//get values
    	profile_name = loc.getName();
    	profile = loc.getProfile_name();
    	longitude = loc.getLongitude();
    	latitude = loc.getLatitude();
    	
    	/*
    	 * need to apply a check for duplicate values here in this section
    	 */
    	
    	ContentValues values = new ContentValues();
    	
    	//store values
    	values.put("name", profile_name);
    	values.put("profile", profile);
    	values.put("longitude", longitude);
    	values.put("latitude", latitude);
    	
    	//insert
    	int rowCheck = (int) db.insert("location", null, values);
    	
    	//return row check for success or failure
		return rowCheck;
    	
    }
    
    /*
     * to delete a record from the database
     */
    public int delete_profile(SQLiteDatabase db){
		return 0;
    }
    
    /*
     * read the record from database
     */
    public Cursor retrieveLocation(SQLiteDatabase db){
    	Cursor c = db.rawQuery("SELECT * FROM location", null);
    	return c;   	
    	
    }
    
}
