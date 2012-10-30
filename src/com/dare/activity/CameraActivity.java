package com.dare.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dare.Constants;
import com.dare.R;
import com.dare.model.ChallengeProvider;
import com.dare.model.Submission;
import com.dare.view.CameraPreview;

public class CameraActivity extends Activity implements PictureCallback {		
	
	private Uri _fileUri;
	private long _challenge_id;
	
	private Camera _camera;
	private CameraPreview _preview;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        
        Bundle extras = getIntent().getExtras();
		if (extras != null) {			
			_challenge_id =  extras.getLong(ChallengeProvider.CONTENT_ID_KEY);
		}
        
		_camera = getCameraInstance();	
		_camera.setDisplayOrientation(90);
        _fileUri = getOutputMediaFileUri(); // create a file to save the image
                
        _preview = new CameraPreview(this, _camera);
        FrameLayout previewFrame = (FrameLayout) findViewById(R.id.camera_preview);
        previewFrame.addView(_preview);        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_camera, menu);
        return true;
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }
    
    public void takePicture(View v){
    	_camera.takePicture(null, null, this);
    }
    
    public void onPictureTaken(byte[] data, Camera camera) {		
        try {
        	Bitmap bitmapOriginal = BitmapFactory.decodeByteArray(data , 0, data .length);
        	Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOriginal, 0, 0, CameraPreview.PIC_WIDTH, CameraPreview.PIC_HEIGHT, matrix, true);            
        	
        	File pictureFile = new File(new URI(_fileUri.toString()));
        	FileOutputStream fos = new FileOutputStream(pictureFile);            
        	resizedBitmap.compress(CompressFormat.JPEG, 90, fos);        	
            fos.flush();
            fos.close();
        } catch (URISyntaxException e) {
            Log.d(CameraActivity.class.toString(), "Syntax Exception: " + e.getMessage());
        } catch (FileNotFoundException e) {
            Log.d(CameraActivity.class.toString(), "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(CameraActivity.class.toString(), "Error accessing file: " + e.getMessage());
        }
        
        releaseCamera();
        
        //TODO take them to the confirmation screen, but for now we'll just start uploading        
        //uploadImage(_fileUri);        
        new UploadOnNewThread ().execute(_fileUri);
	}

    private class UploadOnNewThread extends AsyncTask<Uri, Void, String> {
    	@Override
        protected String doInBackground(Uri... fileUri) {
    		String response = "";
    		try {
    			String imgPath = fileUri[0].getPath();
        		String imgName = fileUri[0].getLastPathSegment();
        		File imgFile = new File(imgPath);

        		
        		AmazonS3Client s3Client = new AmazonS3Client( new BasicAWSCredentials("AKIAJVSVARKT3MJSUHOA", "JblOe9ab7ZDa98Ei0PA2tT/Z3u5RpSoWopXyg4ss"));
        		PutObjectRequest request = new PutObjectRequest(Constants.S3_IMG_BUCKET, imgName, imgFile);  
        		request.setCannedAcl(CannedAccessControlList.PublicRead);
        		s3Client.putObject(request);
        		
        		String url = Constants.getS3Url(imgName);

        		Submission submission = new Submission();
        		submission.setContentUrl(url);
        		submission.setDescription("hardcoded description");
        		submission.setLocalPath(imgPath);
        		submission.setChallengeId(_challenge_id);
        		
        		SubmissionController subController = new SubmissionController(this);
        		subController.uploadSubmission(submission);
        		
    		} catch (AmazonClientException awsClientEx){
        		Log.e(CameraActivity.class.toString(), awsClientEx.getLocalizedMessage());
    		}
    	return response;
    	}
    	
    	@Override
        protected void onPostExecute(String response) {
        }
    }
 
    
    
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    
    private void releaseCamera(){
        if (_camera != null){
        	_camera.release();        // release the camera for other applications
        	_camera = null;
        }
    }
    
    
    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(){
          return Uri.fromFile(getOutputMediaFile());
    }

    /** Create a File for saving an image */
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File submissionDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Dare");

        // Create the storage directory if it does not exist
        if (! submissionDir.exists()){
            if (! submissionDir.mkdirs()){
                Log.d(CameraActivity.class.toString(), "failed to public submission directory");
                return null;
            }
        }

        // Create a media file name
        String uniqueFilename = UUID.randomUUID().toString();
        return new File(submissionDir.getPath() + File.separator + uniqueFilename + ".jpg");
    }
	
}
