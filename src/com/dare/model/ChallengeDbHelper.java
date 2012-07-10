package com.dare.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
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
	
	private SQLiteDatabase _db;
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
	
	public void open() throws SQLException {
		_db = getWritableDatabase();	
	}

	@Override
	public void close() {
		_db = null;
		super.close();
	}
	
	public Date challengeExists(long challengeID)
	{
		Date lastUpdated = null;
		
		if (_db != null)
		{
			String selection = (COLUMN_ID + " = " + challengeID);
			String[] columns = {COLUMN_UPDATED_AT};
			Cursor cursor = _db.query(TABLE_NAME, columns, selection, null, null, null, null, null);
			
			if (cursor != null)
			{
				try {
					cursor.moveToFirst();
					String dateStr = cursor.getString(0);
					lastUpdated = dateFormatter.parse(dateStr);
				} catch (ParseException e) {
					Log.e(ChallengeDbHelper.class.toString(), "DateParseIssue: " + e.toString());
				}
			}									
		}
		
		return lastUpdated;
	}
	
	public boolean addChallenge(Challenge challenge)
	{
		if (challenge != null && challenge.isValid())
		{
			ContentValues values = challengeToContentValues(challenge);
			
			if (_db != null)
			{
				long insertId = _db.insert(TABLE_NAME, null,values);
				if (insertId != -1)
				{
					return true;
				}
			}
		}
		else{
			Log.e(ChallengeDbHelper.class.toString(), "Tried to store an invalid challenge");
		}
		return false;
	}

	public boolean updateChallenge(Challenge challenge) {
		if (challenge != null && challenge.isValid())
		{
			ContentValues values = challengeToContentValues(challenge);
			
			if (_db != null)
			{
				String selection = (COLUMN_ID + " = " + challenge.getId());
				int updatedRows = _db.update(TABLE_NAME, values, selection,null);
				
				//TODO some serious wtf here. I had (rows == 1) but the compiler was optimizing the block out for some crazy reason
				if (updatedRows > 0) 
				{
					return true;
				}
			}
		}
		else{
			Log.e(ChallengeDbHelper.class.toString(), "Tried to store an invalid challenge");
		}
		return false;
	}
	
	private ContentValues challengeToContentValues(Challenge challenge){
		ContentValues values = new ContentValues();
		values.put(COLUMN_ID, challenge.getId());
		values.put(COLUMN_BRAND_NAME, challenge.getBrand());
		values.put(COLUMN_TITLE, challenge.getTitle());
		values.put(COLUMN_DESCRIPTION, challenge.getDescription());
		values.put(COLUMN_CREATED_AT, challenge.getCreatedAt());
		values.put(COLUMN_UPDATED_AT, challenge.getUpdatedAt());
		return values;
	}
	
}
