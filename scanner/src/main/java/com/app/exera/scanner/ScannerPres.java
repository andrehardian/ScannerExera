package com.app.exera.scanner;

import android.app.Activity;
import android.view.MotionEvent;

public interface ScannerPres {
    void init(Activity activity);
    boolean cameraPreview(MotionEvent event);
    void close();
}
