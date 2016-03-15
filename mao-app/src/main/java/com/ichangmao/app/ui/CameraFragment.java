package com.ichangmao.app.ui;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.ichangmao.app.R;
import com.ichangmao.app.ui.widget.CameraPreview;
import com.ichangmao.commons.IOUtils;
import com.ichangmao.commons.MaoLog;
import com.ichangmao.commons.Utils;

public class CameraFragment extends Fragment {

    MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());
    private int cameraId;
    private Camera mCamera;
    private CameraPreview mPreview;
    Button btn_capture;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        // Create an instance of Camera
        cameraId = getCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
        mCamera = getCameraInstance(cameraId);
        setCameraDisplayOrientation(getActivity(), cameraId, mCamera);
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(getContext(), mCamera);
        FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.flyt_camera_preview);
        preview.addView(mPreview);
        btn_capture = (Button) rootView.findViewById(R.id.btn_capture);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPictureCallback);
            }
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            c = Camera.open(cameraId); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;   // compensate the mirror
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private int getCameraId(int facing) {
        int mNumberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < mNumberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facing) {
                return i;
            }
        }
        return -1;
    }

    private PictureCallback mPictureCallback = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            IOUtils.save(Utils.getSdcardPath() + "/vanq/" + System.currentTimeMillis() + ".jpg", data);
            mCamera.startPreview();
        }
    };
}
