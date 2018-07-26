package com.projects.shrungbhatt.blitzzardemo.utils;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.nio.ByteBuffer;

public final class FrameInputPluginModule implements CameraCallback {

    private static final int FRAME_WIDTH = 640;
    private static final int FRAME_HEIGHT = 480;

    private final long mNativeHandle;
    private final Camera mCamera;
    private final Display mDisplay;

    @Nullable
    private HandlerThread backgroundThread;
    @Nullable
    private Handler backgroundHandler;


    public FrameInputPluginModule(Context context, long nativeHandle) {
        this.mNativeHandle = nativeHandle;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            mCamera = new BlitzzCamera2(context, this, FRAME_WIDTH, FRAME_HEIGHT);
        } else {
            mCamera = new BlitzzCamera(this, FRAME_WIDTH, FRAME_HEIGHT);
        }

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            throw new IllegalStateException("Unable to get the WindowManager from the given context.");
        }

        mDisplay = windowManager.getDefaultDisplay();
    }

    public void start() {
        mCamera.start();
        if (backgroundThread == null) {
            backgroundThread = new HandlerThread("FrameInputPluginModule");
            backgroundThread.start();
            backgroundHandler =  new Handler(backgroundThread.getLooper());
        }

        backgroundHandler.post(new Runnable() {
            private int lastOrientation = -1;
            private int cameraOrientation = mCamera.getCameraOrientation();

            @Override
            public void run() {
                int rotation = mDisplay.getRotation();
                float orientation = 0;
                if (rotation != lastOrientation) {
                    switch (rotation) {
                        case Surface.ROTATION_0:
                            orientation = 0f;
                            break;
                        case Surface.ROTATION_90:
                            orientation = 90f;
                            break;
                        case Surface.ROTATION_180:
                            orientation = 180f;
                            break;
                        case Surface.ROTATION_270:
                            orientation = 270f;
                            break;
                    }
                    lastOrientation = rotation;

                    float camToSurfaceAngle = cameraOrientation - orientation;
                    if (camToSurfaceAngle < 0) {
                        camToSurfaceAngle += 360;
                    }
                    nativeCameraToSurfaceAngleChanged(mNativeHandle, camToSurfaceAngle);
                }
                backgroundHandler.postDelayed(this, 50);
            }
        });
    }

    public void stop() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                backgroundThread = null;
                backgroundHandler = null;
            }
        }
        mCamera.stop();
    }


    @Override
    public void notifyNewCameraFrameYUV420888(final ByteBuffer luminanceData, final ByteBuffer chromaBlueData, final ByteBuffer chromaRedData, final int rowStrideLuminance, final int
            pixelStrideChroma, final int rowStrideChroma) {
        nativeNotifyNewCameraFrameYUV420888(
                mNativeHandle,
                luminanceData,
                chromaBlueData,
                chromaRedData,
                rowStrideLuminance,
                pixelStrideChroma,
                rowStrideChroma
        );
    }

    @Override
    public void notifyNewCameraFrameNV21(final byte[] data) {
        nativeNotifyNewCameraFrameNV21(mNativeHandle, data);
    }

    @Override
    public void fieldOfViewChanged(final float fov) {
        nativeFieldOfViewChanged(mNativeHandle, fov);
    }

    @Override
    public void cameraReleased() {
        nativeCameraReleased(mNativeHandle);
    }

    //These are native method's, they are supposed to look red, don't delete them! They will get angry.
    private native void nativeNotifyNewCameraFrameYUV420888(long nativeHandle, ByteBuffer luminanceData, ByteBuffer chromaBlueData, ByteBuffer chromaRedData,int rowStrideLuminance, int pixelStrideChroma,
                                                       int rowStrideChroma);
    private native void nativeNotifyNewCameraFrameNV21(long nativeHandle, byte[] data);
    private native void nativeFieldOfViewChanged(long nativeHandle, float fov);
    private native void nativeCameraToSurfaceAngleChanged(long nativeHandle, float angle);
    private native void nativeCameraReleased(long nativeHandle);

}
