package com.dare.model;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import com.dare.Constants;
import com.dare.db.DareDbHelper;
import com.dare.db.SubmissionTable;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class SubmissionProvider extends ContentProvider {

	private static final String BASE_PATH = "submissions";
	public static final Uri CONTENT_URI = Uri.parse("content://" + Constants.SUBMISSION_PROVIDER_AUTHORITY + "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/submissions";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/submission";

	// UriMatcher setup
	private static final int SUBMISSIONS = 10;
	private static final int SUBMISSION_ID = 20;
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(Constants.SUBMISSION_PROVIDER_AUTHORITY, BASE_PATH, SUBMISSIONS);
		sURIMatcher.addURI(Constants.SUBMISSION_PROVIDER_AUTHORITY, BASE_PATH + "/#", SUBMISSION_ID);
	}		
	
	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (values == null)
		{
			throw new IllegalArgumentException("ContentValues must not be null");
		}
		
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase db = DareDbHelper.getDb();
		long id = -1;
		
		switch (uriType) {
			case SUBMISSIONS:
				id = db.insert(SubmissionTable.TABLE_NAME, null,values);		
				break;
			default:				
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		if (id >= 0)
		{
			getContext().getContentResolver().notifyChange(uri, null);		
			return Uri.parse(CONTENT_URI + "/" + id);
		}
		
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase db = DareDbHelper.getDb();
		int rowsUpdated = 0;
		
		switch (uriType) {
			case SUBMISSIONS:
				rowsUpdated = db.update(SubmissionTable.TABLE_NAME, 
						values, 
						selection,
						selectionArgs);
				break;
			case SUBMISSION_ID:
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = db.update(SubmissionTable.TABLE_NAME, 
							values,
							SubmissionTable.COLUMN_ID + "=" + id, 
							null);
				} else {
					rowsUpdated = db.update(SubmissionTable.TABLE_NAME, 
							values,
							SubmissionTable.COLUMN_ID + "=" + id 
							+ " and " 
							+ selection,
							selectionArgs);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		queryBuilder.setTables(SubmissionTable.TABLE_NAME);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
			case SUBMISSIONS:
				break;
			case SUBMISSION_ID:
				// Adding the ID to the original query
				queryBuilder.appendWhere(SubmissionTable.COLUMN_ID + "="
						+ uri.getLastPathSegment());
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = DareDbHelper.getDb();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}	
	
	@Override
	public String getType(Uri uri) {
		//not worrying about MIME type right now
		return null;
	}
	
	private void checkColumns(String[] projection) {
		String[] available = { SubmissionTable.COLUMN_ID, SubmissionTable.COLUMN_CHALLENGE_ID, SubmissionTable.COLUMN_DESCRIPTION };
		
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
	
	public static ContentValues submissionToContentValues(Submission submission){
		ContentValues values = new ContentValues();
		values.put(SubmissionTable.COLUMN_ID, submission.getId());
		values.put(SubmissionTable.COLUMN_CHALLENGE_ID, submission.getChallengeId());
		values.put(SubmissionTable.COLUMN_DESCRIPTION, submission.getDescription());
		values.put(SubmissionTable.COLUMN_CREATED_AT, submission.getCreatedAt());
		values.put(SubmissionTable.COLUMN_UPDATED_AT, submission.getUpdatedAt());
		return values;
	}
	
	public static Date submissionExists(long submissionID)
	{
		Date lastUpdated = null;
		SQLiteDatabase db = DareDbHelper.getDb();
		
		if (db != null)
		{
			String selection = (SubmissionTable.COLUMN_ID + " = " + submissionID);
			String[] columns = {SubmissionTable.COLUMN_UPDATED_AT};
			Cursor cursor = db.query(SubmissionTable.TABLE_NAME, columns, selection, null, null, null, null, null);
			
			if (cursor != null && cursor.moveToFirst())
			{
				try {					
					String dateStr = cursor.getString(0);
					lastUpdated = Constants.DARE_DATE_FORMATTER.parse(dateStr);
				} catch (ParseException e) {
					Log.e(SubmissionTable.class.toString(), "DateParseIssue: " + e.toString());
				}
			}									
		}
		
		return lastUpdated;
	}

}
