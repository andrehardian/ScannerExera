package com.app.exera.scanner;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.app.exera.scanner.utils.ScannerListener;

public class CameraScan extends AppCompatActivity implements ScannerView{
    private FrameLayout cameraPreview;
    private Button close;
    private ScannerImpl impl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeContent();
    }

    private void initializeContent() {
        setContentView(R.layout.activity_camera_scan);
        impl = new ScannerImpl();
        impl.setViewAct(this);
        cameraPreview = findView(R.id.camera_preview);
        close = findView(R.id.close);
        close.setOnClickListener((view -> close()));
        cameraPreview.setOnTouchListener((View view, MotionEvent motionEvent) ->
                cameraPreview(motionEvent));
        impl.init(this);

    }

    protected boolean cameraPreview(MotionEvent event) {
        impl.cameraPreview(event);
        return true;
    }


    private void close() {
        impl.close();
    }

    protected <K extends View> K findView(int id) {
        return (K) findViewById(id);
    }


    @Override
    public FrameLayout getCameraView() {
        return cameraPreview;
    }


}
