package com.dare.fragment;

import com.dare.Constants;
import com.dare.R;
import com.dare.db.SubmissionTable;
import com.dare.model.SubmissionController;
import com.dare.model.SubmissionProvider;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

public class SubmissionsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
	
	private SimpleCursorAdapter _cursorAdapter;
	private SubmissionController _submissionController;
	private long _challengeId;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
                
        _submissionController = new SubmissionController(getActivity());
        
        _cursorAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.list_item_submission, null,
                new String[] {SubmissionTable.COLUMN_DESCRIPTION},
                new int[] { R.id.submission_description}, 0);
        setListAdapter(_cursorAdapter);
        
        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(Constants.LOADER_SUBMISSIONS, null, this);
        
        refresh();
    }
	
	// Uses AsyncTask subclass to download the challenge feed from the server
    public void refresh() {    	
    	new RefreshTask().execute();                    
    }
    
    // AsyncTask implementation to make sure we get data in the background
    private class RefreshTask extends AsyncTask<Void, Void, Void> {

    	@Override
    	protected Void doInBackground(Void...voids) {    		
    		_submissionController.fetchSubmissionsForChallengeId(_challengeId);
    		return null;    		    		
    	}

    	@Override
    	protected void onPostExecute(Void param) {    		
    		
    	}
    }
	
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] columnProjection = { SubmissionTable.COLUMN_ID, SubmissionTable.COLUMN_DESCRIPTION, SubmissionTable.COLUMN_CHALLENGE_ID };
		String selection = (SubmissionTable.COLUMN_CHALLENGE_ID + "=" + _challengeId);
		CursorLoader cursorLoader = new CursorLoader(getActivity(), SubmissionProvider.CONTENT_URI, columnProjection, selection, null, null);
		
		return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		_cursorAdapter.swapCursor(cursor);		
	}

	public void onLoaderReset(Loader<Cursor> arg0) {
		_cursorAdapter.swapCursor(null);		
	}
	
	public void setChallengeId(long challengeId){
		_challengeId = challengeId;
	}

}
