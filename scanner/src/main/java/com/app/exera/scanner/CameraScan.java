package com.app.exera.scanner;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.app.exera.scanner.utils.PermissionMarshmellow;
import com.app.exera.scanner.utils.UtilsCamera;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import static com.app.exera.scanner.utils.ScannerConstant.SCAN_MODES;
import static com.app.exera.scanner.utils.ScannerConstant.SCAN_RESULT;
import static com.app.exera.scanner.utils.ScannerConstant.SCAN_RESULT_TYPE;

public class CameraScan extends AppCompatActivity implements Camera.AutoFocusCallback {
    private FrameLayout cameraPreview;
    private PermissionMarshmellow permissionMarshmellow;

    private Camera camera;
    private CameraPreview cameraPreviewer;
    private UtilsCamera utilsCamera;
    private ImageScanner mScanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeContent();
        permissionCamera();
    }

    private void initializeContent() {
        setContentView(R.layout.activity_camera_scan);
        cameraPreview = findView(R.id.camera_preview);
        utilsCamera = new UtilsCamera(this);
        permissionMarshmellow = new PermissionMarshmellow(this);
        setupScanner();
    }

    protected <K extends View> K findView(int id) {
        return (K) findViewById(id);
    }

    private void permissionCamera() {
        if (!permissionMarshmellow.checkPermissionForCamera()) {
            permissionMarshmellow.requestPermissionForCamera();
        } else {
            openCamera();
        }

    }

    private void openCamera() {
        camera = utilsCamera.getCameraInstance();
        if (camera != null) {
            camera.setDisplayOrientation(90);
            cameraPreviewer = new CameraPreview(this, camera, this, (byte[] bytes, Camera camera) ->
                    callBackPreview(bytes, camera));
            cameraPreview.addView(cameraPreviewer);
        }

    }



    @Override
    public void onAutoFocus(boolean b, Camera camera) {
        new Handler().postDelayed(() -> setFocusCamera(), 1000);
    }

    private void setFocusCamera() {
        if (camera != null) {
            camera.autoFocus(CameraScan.this);
        }
    }

    private void setupScanner() {
        mScanner = new ImageScanner();
        mScanner.setConfig(0, Config.X_DENSITY, 3);
        mScanner.setConfig(0, Config.Y_DENSITY, 3);

        int[] symbols = getIntent().getIntArrayExtra(SCAN_MODES);
        if (symbols != null) {
            mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
            for (int symbol : symbols) {
                mScanner.setConfig(symbol, Config.ENABLE, 1);
            }
        }
    }


    private void callBackPreview(byte[] bytes, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();

        Image barcode = new Image(size.width, size.height, "Y800");
        barcode.setData(bytes);

        int result = mScanner.scanImage(barcode);

        if (result != 0) {
            camera.cancelAutoFocus();
            camera.setPreviewCallback(null);
            camera.release();
            SymbolSet syms = mScanner.getResults();
            for (Symbol sym : syms) {
                String symData = sym.getData();
                if (!TextUtils.isEmpty(symData)) {
                    Intent dataIntent = new Intent();
                    dataIntent.putExtra(SCAN_RESULT, symData);
                    dataIntent.putExtra(SCAN_RESULT_TYPE, sym.getType());
                    setResult(Activity.RESULT_OK, dataIntent);
                    finish();
                    break;
                }
            }
        }

    }
}
