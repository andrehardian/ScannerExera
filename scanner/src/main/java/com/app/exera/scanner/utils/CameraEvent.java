package com.app.exera.scanner.utils;

import android.graphics.Rect;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class CameraEvent implements Camera.AutoFocusCallback {
    private CameraEventListener cameraEventListener;
    private static final int FOCUS_AREA_SIZE = 300;

    public void focusOnTouch(MotionEvent event, Camera camera, FrameLayout cameraView) {
        if (camera != null) {

            Camera.Parameters parameters = camera.getParameters();
            if (parameters.getMaxNumMeteringAreas() > 0) {
                Rect rect = calculateFocusArea(event.getX(), event.getY(), cameraView);
                if (parameters.getSupportedFocusModes() != null && parameters.getSupportedFocusModes().
                        contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(rect, 800));
                parameters.setFocusAreas(meteringAreas);

                try {
                    camera.setParameters(parameters);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                camera.autoFocus(this);
            } else {
                camera.autoFocus(this);
            }
        }
    }

    private Rect calculateFocusArea(float x, float y, FrameLayout cameraView) {
        int left = clamp(Float.valueOf((x / cameraView.getWidth()) * 2000 - 1000)
                .intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / cameraView.getHeight())
                * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
            if (touchCoordinateInCameraReper > 0) {
                result = 1000 - focusAreaSize / 2;
            } else {
                result = -1000 + focusAreaSize / 2;
            }
        } else {
            result = touchCoordinateInCameraReper - focusAreaSize / 2;
        }
        return result;
    }

    @Override
    public void onAutoFocus(boolean b, Camera camera) {
        if (cameraEventListener != null) {
            cameraEventListener.onAutoFocus();
        }
    }

    public void setCameraEventListener(CameraEventListener cameraEventListener) {
        this.cameraEventListener = cameraEventListener;
    }
}
