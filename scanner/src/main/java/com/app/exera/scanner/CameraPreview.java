package com.app.exera.scanner;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by AndreHF on 9/27/2016.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private List<Camera.Size> cameraSize;
    private Camera.Size previewSize;
    private final Camera.AutoFocusCallback autoFocus;
    private final Camera.PreviewCallback previewCallback;
    private final boolean istab;


    public CameraPreview(Context context, Camera camera, Camera.AutoFocusCallback autoFocus,
                                Camera.PreviewCallback previewCallback, boolean istab) {
        super(context);
        this.camera = camera;
        this.autoFocus = autoFocus;
        this.previewCallback = previewCallback;
        this.istab = istab;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraSize = camera.getParameters().getSupportedPreviewSizes();
        setFocusable(true);
        setFocusableInTouchMode(true);
    }


    public CameraPreview(Context context, AttributeSet attrs, Camera.AutoFocusCallback autoFocus,
                                Camera.PreviewCallback previewCallback, boolean istab) {
        super(context, attrs);
        this.autoFocus = autoFocus;
        this.previewCallback = previewCallback;
        this.istab = istab;
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr,
                                Camera.AutoFocusCallback autoFocus, Camera.PreviewCallback previewCallback, boolean istab) {
        super(context, attrs, defStyleAttr);
        this.autoFocus = autoFocus;
        this.previewCallback = previewCallback;
        this.istab = istab;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr,
                                int defStyleRes, Camera.AutoFocusCallback autoFocus,
                                Camera.PreviewCallback previewCallback, boolean istab) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.autoFocus = autoFocus;
        this.previewCallback = previewCallback;
        this.istab = istab;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Camera.Parameters parameters = camera.getParameters();
            try {
                parameters.set("jpeg-quality", 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            parameters.setPictureFormat(PixelFormat.JPEG);

            try {
                if (istab) {
                    parameters.setPreviewSize(previewSize.height, previewSize.width);
                } else {
                    parameters.setPreviewSize(previewSize.width, previewSize.height);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (istab) {
                    parameters.setPictureSize(previewSize.height, previewSize.width);
                } else {
                    parameters.setPictureSize(previewSize.width, previewSize.height);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                camera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }

            camera.setPreviewCallback(previewCallback);
            scheduleAutoFocus();
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void scheduleAutoFocus() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    setAutoFocus();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
    }

    private void setAutoFocus() {
        List<String> supportedFocusModes = camera.getParameters().getSupportedFocusModes();
        boolean hasAutoFocus = supportedFocusModes != null &&
                supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO);
        if (hasAutoFocus)
            try {
                camera.autoFocus(autoFocus);
            } catch (RuntimeException e) {
                scheduleAutoFocus();
            }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (camera != null) {
            previewSize = getOptimalPreviewSize(cameraSize, width, height);
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }



}
