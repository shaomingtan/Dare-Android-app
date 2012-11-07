package com.dare.db;

import com.dare.DareApplication;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DareDbHelper extends SQLiteOpenHelper{
	private static final String DATABASE_NAME = "dare.db";
	private static final int 	DATABASE_VERSION = 5;
	
	private static DareDbHelper _db;
	
	// NOTE: This should only be initialized once by the main application
	public DareDbHelper(DareApplication application) {
		super(application, DATABASE_NAME, null, DATABASE_VERSION);
		_db = this;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ChallengeTable.onCreate(db);	
		
		// NOTE: submission table needs to be created after the challenge table
		SubmissionTable.onCreate(db); 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ChallengeTable.onUpgrade(db, oldVersion, newVersion);
		// NOTE: submission table needs to be updated after the challenge table
		SubmissionTable.onUpgrade(db, oldVersion, newVersion);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
	    super.onOpen(db);
	    if (!db.isReadOnly()) {
	        // Enable foreign key constraints
	        db.execSQL("PRAGMA foreign_keys=ON;");
	    }
	}
	
	public static SQLiteDatabase getDb(){
		return _db.getWritableDatabase();
	}
	
}
