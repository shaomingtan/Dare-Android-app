package com.dare.view;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements Callback {

	public static final int PIC_WIDTH = 600;
	public static final int PIC_HEIGHT = 600;
	
	private SurfaceHolder 	_holder;
    private Camera 			_camera;
	
    @SuppressWarnings("deprecation")
	public CameraPreview(Context context, Camera camera){
    	super(context);
        _camera = camera;
        
        _holder = getHolder();
        _holder.addCallback(this);
        
        // deprecated setting, but required on Android versions prior to 3.0
        _holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);        
    }	

	public void surfaceCreated(SurfaceHolder holder) {
		 // The Surface has been created, now tell the camera where to draw the preview.
        try {
            _camera.setPreviewDisplay(holder);
            _camera.startPreview();
        } catch (IOException e) {
            Log.d(CameraPreview.class.toString(), "Error setting camera preview: " + e.getMessage());
        }

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
		// android docs suggest to leave this blank
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (_holder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            _camera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        Camera.Parameters params = _camera.getParameters();
        
        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
          params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
                
        Camera.Size size = getAppropriatePictureSize(params.getSupportedPictureSizes());
        params.setPictureSize(size.width, size.height);
        
        _camera.setParameters(params);
        
        // start preview with new settings
        try {
            _camera.setPreviewDisplay(_holder);
            _camera.startPreview();

        } catch (Exception e){
            Log.d(CameraPreview.class.toString(), "Error starting camera preview: " + e.getMessage());
        }
	}
	
	
	public Camera.Size getAppropriatePictureSize(List<Camera.Size> sizes){		
		Camera.Size tempSize = null;
		
		int length = sizes.size();
		for (int i = (length-1); i >= 0; i-- ){
			tempSize = sizes.get(i);
			if (tempSize.width > PIC_WIDTH && tempSize.height > PIC_HEIGHT)
			{
				return tempSize;
			}
		}
		
		return sizes.get(0);
	}

}
