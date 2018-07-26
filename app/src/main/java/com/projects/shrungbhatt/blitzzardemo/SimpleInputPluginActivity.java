package com.projects.shrungbhatt.blitzzardemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.projects.shrungbhatt.blitzzardemo.utils.Driver;
import com.projects.shrungbhatt.blitzzardemo.utils.DropDownAlert;
import com.projects.shrungbhatt.blitzzardemo.utils.FrameInputPluginModule;
import com.projects.shrungbhatt.blitzzardemo.utils.WikitudeSDKConstants;
import com.wikitude.NativeStartupConfiguration;
import com.wikitude.WikitudeSDK;
import com.wikitude.common.ErrorCallback;
import com.wikitude.common.WikitudeError;
import com.wikitude.common.rendering.RenderExtension;
import com.wikitude.rendering.ExternalRendering;
import com.wikitude.tracker.ObjectTarget;
import com.wikitude.tracker.ObjectTracker;
import com.wikitude.tracker.ObjectTrackerListener;
import com.wikitude.tracker.TargetCollectionResource;
import com.wikitude.tracker.TargetCollectionResourceLoadingCallback;

public class SimpleInputPluginActivity extends Activity implements ObjectTrackerListener,ExternalRendering {

    static {
        System.loadLibrary("wikitudePlugins");
    }

    private static final String TAG = "SimpleInputPlugin";

    private WikitudeSDK mWikitudeSDK;
    private FrameInputPluginModule mInputPluginModule;

    private CustomSurfaceView mCustomSurfaceView;
    private Driver mDriver;
    private GLRenderer mGLRenderer;

    private DropDownAlert mDropDownAlert;
    private TargetCollectionResource mTargetCollectionResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // new instance of the WikitudeSDK with ExternalRendering
        mWikitudeSDK = new WikitudeSDK(this);

        // creating configuration for the SDK
        NativeStartupConfiguration startupConfiguration = new NativeStartupConfiguration();
        startupConfiguration.setLicenseKey(WikitudeSDKConstants.WIKITUDE_SDK_KEY);

        // wikitude SDK will be created with the given configuration
        mWikitudeSDK.onCreate(getApplicationContext(), this, startupConfiguration);

        // creating a new TargetCollectionResource from a .wtc file containing information about the image to track
        mTargetCollectionResource = mWikitudeSDK.getTrackerManager().
                createTargetCollectionResource("file:///android_asset/" + "jeep.wto",
                        new TargetCollectionResourceLoadingCallback() {
                            @Override
                            public void onError(WikitudeError wikitudeError) {
                                Log.v(TAG, "Failed to load target collection resource. Reason: "
                                        + wikitudeError.getMessage());
                            }

                            @Override
                            public void onFinish() {
                                mWikitudeSDK.getTrackerManager().createObjectTracker(mTargetCollectionResource,
                                        SimpleInputPluginActivity.this,
                                        null);
                            }
                        });
//        ClassScope.getLoadedLibraries(ClassLoader.getSystemClassLoader());


        // sets this activity in the plugin
        initNative();

        // register Plugin in the wikitude SDK and in the jniRegistration.cpp
        mWikitudeSDK.getPluginManager().registerNativePlugins("wikitudePlugins", "simple_input_plugin", new ErrorCallback() {
            @Override
            public void onError(@NonNull WikitudeError error) {
                Log.v(TAG, "Plugin failed to load. Reason: " + error.getMessage());
            }
        });
/*
        // alert showing which target image to scan
        mDropDownAlert = new DropDownAlert(this);
        mDropDownAlert.setText("Scan Target #1 (surfer):");
        mDropDownAlert.addImages("surfer.png");
        mDropDownAlert.setTextWeight(0.5f);
        mDropDownAlert.show();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWikitudeSDK.onResume();
        mCustomSurfaceView.onResume();
        mDriver.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCustomSurfaceView.onPause();
        mDriver.stop();
        mWikitudeSDK.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWikitudeSDK.onDestroy();
    }

    @Override
    public void onRenderExtensionCreated(final RenderExtension renderExtension) {
        mGLRenderer = new GLRenderer(renderExtension);
        mWikitudeSDK.getCameraManager().setRenderingCorrectedFovChangedListener(mGLRenderer);
        mCustomSurfaceView = new CustomSurfaceView(getApplicationContext(), mGLRenderer);
        mDriver = new Driver(mCustomSurfaceView, 30);

        setContentView(mCustomSurfaceView);
    }




    /**
     * Called from c++ on initialization of the Plugin.
     */
    public void onInputPluginInitialized() {
        mInputPluginModule = new FrameInputPluginModule(this, getInputModuleHandle());
    }

    /**
     * Called from c++ onCameraReleased of the CameraFrameInputPluginModule.
     */
    public void onSDKCameraReleased() {
        mInputPluginModule.start();
    }

    /**
     * Called from c++ on pause of the Plugin.
     */
    public void onInputPluginPaused() {
        mInputPluginModule.stop();
    }

    /**
     * Called from c++ on resume of the Plugin.
     */
    public void onInputPluginResumed() {
       mInputPluginModule.start();
    }

    /**
     * Called from c++ on destroy of the Plugin.
     */
    public void onInputPluginDestroyed() {

    }



    @Override
    public void onTargetsLoaded(ObjectTracker objectTracker) {

    }

    @Override
    public void onErrorLoadingTargets(ObjectTracker objectTracker, WikitudeError wikitudeError) {

    }

    @Override
    public void onObjectRecognized(ObjectTracker objectTracker, ObjectTarget objectTarget) {

    }

    @Override
    public void onObjectTracked(ObjectTracker objectTracker, ObjectTarget objectTarget) {

    }

    @Override
    public void onObjectLost(ObjectTracker objectTracker, ObjectTarget objectTarget) {

    }

    @Override
    public void onExtendedTrackingQualityChanged(ObjectTracker objectTracker, ObjectTarget objectTarget, int i, int i1) {

    }

    //These are native method's, they are supposed to look red, don't delete them! They will get angry.
    private native void initNative();
    private native long getInputModuleHandle();
}
