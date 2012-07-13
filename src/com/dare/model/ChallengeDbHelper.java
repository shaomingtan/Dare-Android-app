package com.dare.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ChallengeDbHelper extends SQLiteOpenHelper {
	public static final String 	TABLE_NAME = "challenges";
	public static final String 	COLUMN_ID = "_id";
	public static final String 	COLUMN_BRAND_NAME = "brand_name";
	public static final String 	COLUMN_TITLE = "title";
	public static final String 	COLUMN_DESCRIPTION = "description";
	public static final String 	COLUMN_CREATED_AT = "created_at";
	public static final String 	COLUMN_UPDATED_AT = "updated_at";

	private static final String DATABASE_NAME = "challenges.db";
	private static final int 	DATABASE_VERSION = 2;
	
	public SimpleDateFormat dateFormatter;
	
	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_NAME 
			+ "(" + COLUMN_ID
			+ " integer primary key, " 
			+ COLUMN_BRAND_NAME + " text, "
			+ COLUMN_TITLE + " text, "
			+ COLUMN_DESCRIPTION + " text, "
			+ COLUMN_CREATED_AT + " text, "
			+ COLUMN_UPDATED_AT + " text"
			+ ");";


	public ChallengeDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(ChallengeDbHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
			
	public Date challengeExists(long challengeID)
	{
		Date lastUpdated = null;
		SQLiteDatabase db = getReadableDatabase();
		
		if (db != null)
		{
			String selection = (COLUMN_ID + " = " + challengeID);
			String[] columns = {COLUMN_UPDATED_AT};
			Cursor cursor = db.query(TABLE_NAME, columns, selection, null, null, null, null, null);
			
			if (cursor != null && cursor.moveToFirst())
			{
				try {					
					String dateStr = cursor.getString(0);
					lastUpdated = dateFormatter.parse(dateStr);
				} catch (ParseException e) {
					Log.e(ChallengeDbHelper.class.toString(), "DateParseIssue: " + e.toString());
				}
			}									
		}
		
		return lastUpdated;
	}
	
	
}
