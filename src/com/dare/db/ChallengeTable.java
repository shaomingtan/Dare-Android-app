package com.dare.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ChallengeTable  {
	public static final String 	TABLE_NAME = "challenges";
	public static final String 	COLUMN_ID = "_id";
	public static final String 	COLUMN_BRAND_NAME = "brand_name";
	public static final String 	COLUMN_BRAND_DESC = "brand_description";
//	public static final String 	COLUMN_BRAND_LOGO = "brand_logo_url";
//	public static final String 	COLUMN_CHALLENGE_PHOTO = "challenge_cover_photo";
	public static final String 	COLUMN_CHALLENGE_REWARD = "challenge_reward";
	public static final String 	COLUMN_TITLE = "title";
	public static final String 	COLUMN_DESCRIPTION = "description";
	public static final String 	COLUMN_CREATED_AT = "created_at";
	public static final String 	COLUMN_UPDATED_AT = "updated_at";	
	
	// Database creation sql statement
	private static final String TABLE_CREATE = "create table "
			+ TABLE_NAME 
			+ "(" + COLUMN_ID + " integer primary key, " 
			+ COLUMN_BRAND_NAME + " text, "
			+ COLUMN_BRAND_DESC + " text, "
//			+ COLUMN_BRAND_LOGO + " text, "
//			+ COLUMN_CHALLENGE_PHOTO + " text, "
			+ COLUMN_CHALLENGE_REWARD + " text, "
			+ COLUMN_TITLE + " text, "
			+ COLUMN_DESCRIPTION + " text, "
			+ COLUMN_CREATED_AT + " text, "
			+ COLUMN_UPDATED_AT + " text"
			+ ");";	

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(ChallengeTable.class.getName(),
				"Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		ChallengeTable.onCreate(db);
	}					
	
}
