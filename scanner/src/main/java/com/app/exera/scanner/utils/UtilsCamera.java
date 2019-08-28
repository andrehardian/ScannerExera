package com.app.exera.scanner.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;

import static com.app.exera.scanner.utils.ScannerConstant.SCAN_MODES;


/**
 * Created by AndreHF on 9/27/2016.
 */
public class UtilsCamera {

    private Camera camera = null;

    public Camera getCameraInstance(int cameraOn) {
        try {
            releaseCameraAndPreview();
            camera = Camera.open(cameraOn);
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
