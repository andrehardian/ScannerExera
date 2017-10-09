package com.app.exera.scanner.utils;

import android.app.Activity;
import android.hardware.Camera;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;

import static com.app.exera.scanner.utils.ScannerConstant.SCAN_MODES;


/**
 * Created by AndreHF on 9/27/2016.
 */
public class UtilsCamera {

    private final Activity activity;

    public static String resultTagCamera = "camera";
    private Camera camera = null;

    public UtilsCamera(Activity activity) {
        this.activity = activity;
    }

    public Camera getCameraInstance() {
        try {
            releaseCameraAndPreview();
            camera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }

    private void releaseCameraAndPreview() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }



}
