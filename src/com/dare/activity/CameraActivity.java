package com.dare.activity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import android.app.Activity;
import android.content.Intent;
import android.net.SSLCertificateSocketFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;

import com.dare.R;

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
        
//        _fileUri = getOutputMediaFileUri(); // create a file to save the image
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, _fileUri); // set the image file name
//
//        // start the image capture Intent
//        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        
        //uploadImage(Uri.parse("/mnt/sdcard/Pictures/Dare/6236a79a-fe80-4757-b627-c49ccb8b4a1d.jpg"));
        uploadImage(Uri.parse("/mnt/sdcard/Android/data/com.dare/files/submission_imgs/cvs-receipt.jpg"));
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
    		int responseCode = -1;
    		
    		String imgPath = fileUri.getPath();
    		String imgName = fileUri.getLastPathSegment();
    		File imgFile = new File(imgPath);
    		FileInputStream fileInputStream = new FileInputStream(imgFile);
    		    		
    		String urlString = ("https://dare-submissions-img.s3.amazonaws.com/");
			URL url = new URL(urlString);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();			
			conn.setRequestMethod("POST");			
			conn.setDoInput(true);
			conn.setDoOutput(true);
			Log.d(CameraActivity.class.toString(), String.valueOf(conn.getDoOutput()));
			conn.setUseCaches(false);			
			conn.setRequestProperty("Connection","Keep-Alive"); 
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+FORM_BOUNDARY);									
			conn.setReadTimeout(60000);			
			conn.setSSLSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
			conn.setHostnameVerifier(new AllowAllHostnameVerifier() );			
			conn.setChunkedStreamingMode(0);												
			
			DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());						
			
			outputStream.writeBytes(encodeFormData("key", "${filename}"));
			outputStream.writeBytes(encodeFormData("AWSAccessKeyId", "AKIAJVSVARKT3MJSUHOA"));
			outputStream.writeBytes(encodeFormData("acl", "public-read"));
			outputStream.writeBytes(encodeFormData("policy", "eyJleHBpcmF0aW9uIjoiMjAxNS0wMS0wMVQwMDowMDowMFoiLCJjb25kaXRpb25zIjpbeyJidWNrZXQiOiJkYXJlLXN1Ym1pc3Npb25zLWltZyJ9LFsic3RhcnRzLXdpdGgiLCIka2V5IiwiIl0seyJhY2wiOiJwdWJsaWMtcmVhZCJ9LFsic3RhcnRzLXdpdGgiLCIkQ29udGVudC1UeXBlIiwiIl0sWyJjb250ZW50LWxlbmd0aC1yYW5nZSIsMCwyMDk3MTUyXV19"));
			outputStream.writeBytes(encodeFormData("signature", "FUVg7VW+STtE+F8Eew5dhgAr6oU="));
			outputStream.writeBytes(encodeFormData("Content-Type", "image/jpeg"));						
			
			outputStream.writeBytes(FORM_FIELD_SEPERATOR);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + imgName +"\"" + FORM_LINE_END );
			outputStream.writeBytes("Content-Type: image/jpeg" + FORM_LINE_END + FORM_LINE_END );			

			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 1*1024*1024;
			
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0)
			{
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
			
			outputStream.writeBytes(FORM_LINE_END + FORM_CDATA_SEPERATOR + FORM_BOUNDARY + FORM_CDATA_SEPERATOR + FORM_LINE_END);
			fileInputStream.close();
			outputStream.flush();
			
//			conn.connect();
						
			responseCode = conn.getResponseCode();			
			Log.d(CameraActivity.class.toString(), "code: " + responseCode);
			
			String response = conn.getResponseMessage();
			InputStream stream = conn.getInputStream();
			
			
			BufferedReader bufIn = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String sResponse;
            StringBuilder s = new StringBuilder();

            //bufIn is null as error as closed urlcConnection
            while ((sResponse = bufIn.readLine()) != null) {
                s = s.append(sResponse);
            }
            
            Log.d("foo",s.toString());
            
            outputStream.close();
    	}    	
    	catch(SSLException sslEx){
    		Log.e(CameraActivity.class.toString(), sslEx.getLocalizedMessage());
    	}
//    	catch(IOException ioEx){
//    		Log.e(CameraActivity.class.toString(), ioEx.toString());
//    	}
    	catch(Exception ex){
    		//Log.e(CameraActivity.class.toString(), ex.toString());
    	}
    }
    
    private static String encodeFormData(String fieldName, String fieldValue){
    	if (fieldName == null){
    		return null;
    	}
    	
    	String response = FORM_FIELD_SEPERATOR;
    	response += "Content-Disposition: form-data; name=\"" + fieldName + "\"" + FORM_LINE_END + FORM_LINE_END; 
		response += fieldValue + FORM_LINE_END;
		
		return response;
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
