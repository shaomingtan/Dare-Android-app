package com.dare.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.dare.Constants;

public class SubmissionController {

	private Context _context;

	public SubmissionController(Context context) {
		_context = context;
	}

	public void fetchSubmissionsForChallengeId(long challengeId) {
		try {
			String urlString = (Constants.DARE_SERVICE_URL + "/challenges/"
					+ challengeId + "/submissions.json");
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(25000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);

			// Starts the query
			conn.connect();
			InputStream stream = conn.getInputStream();

			String response = new java.util.Scanner(stream).useDelimiter("\\A").next();
			updateDbWithRefreshResponse(new JSONArray(response));
		} catch (IOException ioEx) {
			Log.e(SubmissionController.class.toString(),
					("IOException Fetching Submissions: " + ioEx.toString()));
		} catch (JSONException jsonEx) {
			Log.e(SubmissionController.class.toString(),
					("JSONException Fetching Submissions: " + jsonEx.toString()));
		} catch (Exception ex) {
			Log.e(SubmissionController.class.toString(),
					("Exception Fetching Submissions: " + ex.toString()));
		}
	}

	private void updateDbWithRefreshResponse(JSONArray response) {
		if (response != null && response.length() > 0) {
			for (int i = 0; i < response.length(); i++) {
				try {
					Submission submission = new Submission(response.getJSONObject(i));					
					long submissionId = submission.getId();					
										
					Date lastUpdate = SubmissionProvider.submissionExists(submissionId);
					if (lastUpdate == null) {						
						downloadContent(submission);
						ContentValues submissionValues = SubmissionProvider.submissionToContentValues(submission);
						_context.getContentResolver().insert(SubmissionProvider.CONTENT_URI, submissionValues);
					} else {
						Date currentUpdate = Constants.DARE_DATE_FORMATTER.parse(submission.getUpdatedAt());
						if (currentUpdate.after(lastUpdate)) {
							Uri updateUri = Uri.withAppendedPath(SubmissionProvider.CONTENT_URI,String.valueOf(submissionId));
							ContentValues submissionValues = SubmissionProvider.submissionToContentValues(submission);
							_context.getContentResolver().update(updateUri, submissionValues, null, null);
						}
					}
				} 
				catch (JSONException jsonEx) {} 
				catch (ParseException parseEx) {}
			}
		}
	}

	private void downloadContent(Submission submission){
		try{
			URL url = new URL(submission.getContentUrl());	
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    		conn.setReadTimeout(10000 /* milliseconds */);
    		conn.setConnectTimeout(25000 /* milliseconds */);
    		conn.setRequestMethod("GET");
    		conn.setDoInput(true);

    		conn.connect();
    		InputStream inStream = conn.getInputStream();
            BufferedInputStream bufferedStream = new BufferedInputStream(inStream);
            
            //Read bytes to the Buffer until there is nothing more to read(-1).
            ByteArrayBuffer baf = new ByteArrayBuffer(5000);
            int current = 0;
            while ((current = bufferedStream.read()) != -1) {
               baf.append((byte) current);               
            }
                        
            // Load the app's semi-private storage directory. Create if it does not exist
            File storageDir = new File(_context.getExternalFilesDir(null),"submission_imgs");
            if (! storageDir.exists()){
            	if (! storageDir.mkdirs()){
            		Log.e(SubmissionController.class.toString(), "downloadContent failed to create directory");
            		return;
            	}
            }                        
            
            /* Output the the bytes read to a file. */
            File contentFile = new File(storageDir.getPath() + File.separator + submission.getContentFileName());
            FileOutputStream fos = new FileOutputStream(contentFile);
            fos.write(baf.toByteArray());
            fos.flush();
            fos.close();
            
            submission.setLocalPath(contentFile.getAbsolutePath());
		}
		catch (MalformedURLException malEx){
			Log.e(SubmissionController.class.toString(), ("MalformedURLException downloading submission content: " + malEx.toString()));
		}
		catch (IOException ioEx) {
    		Log.e(SubmissionController.class.toString(), ("IOException downloading submission content: " + ioEx.toString()));
    	}		
	}
}
