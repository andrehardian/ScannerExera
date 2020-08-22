package com.app.exera.scanner;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.app.exera.scanner.utils.CameraEvent;
import com.app.exera.scanner.utils.CameraEventListener;
import com.app.exera.scanner.utils.PermissionMarshmellow;
import com.app.exera.scanner.utils.UtilsCamera;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import static com.app.exera.scanner.utils.ScannerConstant.SCAN_RESULT;
import static com.app.exera.scanner.utils.ScannerConstant.SCAN_RESULT_TYPE;


public class ScannerImpl implements ScannerPres, CameraEventListener {
    protected UtilsCamera utilsCamera;
    protected CameraEvent cameraEvent;
    private ImageScanner imageScanner;
    private Camera camera;
    private ScannerView viewAct;
    private CameraPreview cameraPreviewer;
    private Activity activity;
    private PermissionMarshmellow permissionMarshmellow;


    public void setupScanner() {

        imageScanner = new ImageScanner();
        imageScanner.setConfig(0, Config.X_DENSITY, 3);
        imageScanner.setConfig(0, Config.Y_DENSITY, 3);

        int[] symbols = new int[]{Symbol.QRCODE};
        if (symbols != null) {
            imageScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
            for (int symbol : symbols) {
                imageScanner.setConfig(symbol, Config.ENABLE, 1);
            }
        }

    }

    @Override
    public void init(Activity activity) {
        this.activity = activity;
        permissionMarshmellow = new PermissionMarshmellow(activity);
        cameraEvent = new CameraEvent();
        cameraEvent.setCameraEventListener(this);
        utilsCamera = new UtilsCamera();
        setupScanner();
        permissionCamera();
    }

    private void permissionCamera() {
        if (!permissionMarshmellow.checkPermissionForCamera()) {
            permissionMarshmellow.requestPermissionForCamera();
        } else {
            openCamera();
        }
    }


    private void openCamera() {
        camera = utilsCamera.getCameraInstance(Camera.CameraInfo.CAMERA_FACING_BACK);
        if (camera != null) {
            camera.setDisplayOrientation(90);
            cameraPreviewer = new CameraPreview(activity, camera, cameraEvent,
                    (byte[] bytes, Camera camera) ->
                            callBackPreview(bytes, camera), false);
            viewAct.getCameraView().addView(cameraPreviewer);
        }
    }

    private void callBackPreview(byte[] bytes, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();

        Image barcode = new Image(size.width, size.height, "Y800");
        barcode.setData(bytes);

        int result = imageScanner.scanImage(barcode);

        if (result != 0) {
            SymbolSet syms = imageScanner.getResults();
            for (Symbol sym : syms) {
                String symData = sym.getData();
                if (!TextUtils.isEmpty(symData)) {
                    Intent dataIntent = new Intent();
                    dataIntent.putExtra(SCAN_RESULT, symData);
                    dataIntent.putExtra(SCAN_RESULT_TYPE, sym.getType());
                    activity.setResult(Activity.RESULT_OK, dataIntent);
                    activity.finish();
                }
            }
        }
    }


    @Override
    public boolean cameraPreview(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            cameraEvent.focusOnTouch(event, camera, viewAct.getCameraView());
        return true;
    }

    @Override
    public void close() {
        try {
            camera.cancelAutoFocus();
            camera.stopPreview();
            cameraPreviewer.getHolder().removeCallback(cameraPreviewer);
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAutoFocus() {
        new Handler().postDelayed(() -> setFocusCamera(), 1000);
    }

    private void setFocusCamera() {
        if (camera != null) {
            try {
                camera.autoFocus(cameraEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == permissionMarshmellow.CAMERA_PERMISSION_REQUEST_CODE)
                openCamera();
        }

    }


    public void setViewAct(ScannerView viewAct) {
        this.viewAct = viewAct;
    }
}
