package com.dare.model;

import java.util.Arrays;
import java.util.HashSet;

import com.dare.Constants;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class ChallengeProvider extends ContentProvider {
	
	// database
	private ChallengeDbHelper _dbHelper;

	// Used for the UriMacher
	private static final int CHALLENGES = 10;
	private static final int CHALLENGE_ID = 20;	

	private static final String BASE_PATH = "challenges";
	public static final Uri CONTENT_URI = Uri.parse("content://" 
			+ Constants.CONTENT_PROVIDER_AUTHORITY
			+ "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/challenges";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/challenge";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(Constants.CONTENT_PROVIDER_AUTHORITY, BASE_PATH, CHALLENGES);
		sURIMatcher.addURI(Constants.CONTENT_PROVIDER_AUTHORITY, BASE_PATH + "/#", CHALLENGE_ID);
	}
	
	@Override
	public boolean onCreate() {
		_dbHelper = new ChallengeDbHelper(getContext());
		return true;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (values == null)
		{
			throw new IllegalArgumentException("ContentValues must not be null");
		}
		
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase db = _dbHelper.getWritableDatabase();
		long id = -1;
		
		switch (uriType) {
			case CHALLENGES:
				id = db.insert(ChallengeDbHelper.TABLE_NAME, null,values);		
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
		SQLiteDatabase db = _dbHelper.getWritableDatabase();
		int rowsUpdated = 0;
		
		switch (uriType) {
			case CHALLENGES:
				rowsUpdated = db.update(ChallengeDbHelper.TABLE_NAME, 
						values, 
						selection,
						selectionArgs);
				break;
			case CHALLENGE_ID:
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = db.update(ChallengeDbHelper.TABLE_NAME, 
							values,
							ChallengeDbHelper.COLUMN_ID + "=" + id, 
							null);
				} else {
					rowsUpdated = db.update(ChallengeDbHelper.TABLE_NAME, 
							values,
							ChallengeDbHelper.COLUMN_ID + "=" + id 
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
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		queryBuilder.setTables(ChallengeDbHelper.TABLE_NAME);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case CHALLENGES:
			break;
		case CHALLENGE_ID:
			// Adding the ID to the original query
			queryBuilder.appendWhere(ChallengeDbHelper.COLUMN_ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = _dbHelper.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}
	
	@Override
	public String getType(Uri uri) {
		//not worrying about MIME type right now
		return null;
	}
	
	private void checkColumns(String[] projection) {
		String[] available = { ChallengeDbHelper.COLUMN_ID, ChallengeDbHelper.COLUMN_BRAND_NAME, ChallengeDbHelper.COLUMN_TITLE, ChallengeDbHelper.COLUMN_DESCRIPTION };
		
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
	
	public static ContentValues challengeToContentValues(Challenge challenge){
		ContentValues values = new ContentValues();
		values.put(ChallengeDbHelper.COLUMN_ID, challenge.getId());
		values.put(ChallengeDbHelper.COLUMN_BRAND_NAME, challenge.getBrand());
		values.put(ChallengeDbHelper.COLUMN_TITLE, challenge.getTitle());
		values.put(ChallengeDbHelper.COLUMN_DESCRIPTION, challenge.getDescription());
		values.put(ChallengeDbHelper.COLUMN_CREATED_AT, challenge.getCreatedAt());
		values.put(ChallengeDbHelper.COLUMN_UPDATED_AT, challenge.getUpdatedAt());
		return values;
	}
	
	public static Challenge cursorToChallenge(Cursor cursor){
		Challenge challenge = null; 
		
		if (cursor != null && !(cursor.isAfterLast() || cursor.isBeforeFirst())){
			challenge = new Challenge();
			int index;
			
			if ((index = cursor.getColumnIndex(ChallengeDbHelper.COLUMN_ID)) >= 0){
				challenge.setId(cursor.getLong(index));
			}
			if ((index = cursor.getColumnIndex(ChallengeDbHelper.COLUMN_BRAND_NAME)) >= 0){
				challenge.setBrand(cursor.getString(index));
			}
			if ((index = cursor.getColumnIndex(ChallengeDbHelper.COLUMN_TITLE)) >= 0){
				challenge.setTitle(cursor.getString(index));
			}
			if ((index = cursor.getColumnIndex(ChallengeDbHelper.COLUMN_DESCRIPTION)) >= 0){
				challenge.setDescription(cursor.getString(index));
			}
			if ((index = cursor.getColumnIndex(ChallengeDbHelper.COLUMN_CREATED_AT)) >= 0){
				challenge.setCreatedAt(cursor.getString(index));
			}
			if ((index = cursor.getColumnIndex(ChallengeDbHelper.COLUMN_UPDATED_AT)) >= 0){
				challenge.setUpdatedAt(cursor.getString(index));
			}			
		}
		
		return challenge;
	}

}
