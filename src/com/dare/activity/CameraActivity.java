package com.dare.activity;

import java.io.File;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.dare.Constants;
import com.dare.R;
import com.dare.model.Submission;
import com.dare.model.SubmissionController;

public class CameraActivity extends Activity {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	
	private static final String FORM_CDATA_SEPERATOR = "--";
	private static final String FORM_BOUNDARY =  "*****AaB03x";
	private static final String FORM_LINE_END = "\n";
	private static final String FORM_FIELD_SEPERATOR = FORM_CDATA_SEPERATOR + FORM_BOUNDARY + FORM_LINE_END;	
	
	private Uri _fileUri;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        
        _fileUri = getOutputMediaFileUri(); // create a file to save the image
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        
        //uploadImage(Uri.parse("/mnt/sdcard/Pictures/Dare/6236a79a-fe80-4757-b627-c49ccb8b4a1d.jpg"));
        //uploadImage(Uri.parse("/mnt/sdcard/Android/data/com.dare/files/submission_imgs/cvs-receipt.jpg"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_camera, menu);
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
            	uploadImage(_fileUri);
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    private void uploadImage(Uri fileUri){    	
    	try{
    		
    		String imgPath = fileUri.getPath();
    		String imgName = fileUri.getLastPathSegment();
    		File imgFile = new File(imgPath);

    		AmazonS3Client s3Client = new AmazonS3Client( new BasicAWSCredentials("AKIAJVSVARKT3MJSUHOA", "JblOe9ab7ZDa98Ei0PA2tT/Z3u5RpSoWopXyg4ss"));
    		PutObjectRequest request = new PutObjectRequest(Constants.S3_IMG_BUCKET, imgName, imgFile);  
    		request.setCannedAcl(CannedAccessControlList.PublicRead);
    		PutObjectResult result = s3Client.putObject(request);  		
    		
    		String url = Constants.getS3Url(imgName);
    		
    		Submission submission = new Submission();
    		submission.setContentUrl(url);
    		submission.setDescription("hardcoded description");
    		submission.setLocalPath(imgPath);
    		
    		SubmissionController subController = new SubmissionController(this);
    		subController.uploadSubmission(submission)
    	}
    	catch (AmazonClientException awsClientEx){
    		Log.e(CameraActivity.class.toString(), awsClientEx.getLocalizedMessage());
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
