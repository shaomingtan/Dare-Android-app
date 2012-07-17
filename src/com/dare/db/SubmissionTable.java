package com.dare.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SubmissionTable {
	public static final String 	TABLE_NAME = "submissions";
	public static final String 	COLUMN_ID = "_id";
	public static final String 	COLUMN_CHALLENGE_ID = "challenge_id";
	public static final String 	COLUMN_CONTENT_URL = "content_url";
	public static final String 	COLUMN_DESCRIPTION = "description";
	public static final String 	COLUMN_LOCAL_PATH = "local_path";
	public static final String 	COLUMN_CREATED_AT = "created_at";
	public static final String 	COLUMN_UPDATED_AT = "updated_at";	
	public static final String 	INDEX_CHALLENGE = "challengeIndex";
	
	private static final String TABLE_CREATE = "create table "
			+ TABLE_NAME 
			+ "(" + COLUMN_ID + " integer primary key, " 
			+ COLUMN_CHALLENGE_ID + " INTEGER REFERENCES " + ChallengeTable.TABLE_NAME + " ON DELETE CASCADE, "
			+ COLUMN_CONTENT_URL + " text, "
			+ COLUMN_DESCRIPTION + " text, "
			+ COLUMN_LOCAL_PATH + " text, "
			+ COLUMN_CREATED_AT + " text, "
			+ COLUMN_UPDATED_AT + " text"
			+ ");";	
	
	private static final String TABLE_CHALLENGE_INDEX = "CREATE INDEX " + INDEX_CHALLENGE + " ON " + TABLE_NAME + "(" + COLUMN_CHALLENGE_ID + ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE);
		database.execSQL(TABLE_CHALLENGE_INDEX);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SubmissionTable.class.getName(),
				"Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		SubmissionTable.onCreate(db);
	}					
}
