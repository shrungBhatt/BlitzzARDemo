package com.projects.shrungbhatt.blitzzardemo;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.wikitude.NativeStartupConfiguration;
import com.wikitude.WikitudeSDK;
import com.wikitude.common.camera.CameraSettings;
import com.wikitude.common.rendering.RenderExtension;
import com.wikitude.rendering.ExternalRendering;
import com.wikitude.tracker.ObjectTarget;
import com.wikitude.tracker.ObjectTracker;
import com.wikitude.tracker.ObjectTrackerListener;
import com.wikitude.tracker.TargetCollectionResource;
import com.wikitude.tracker.TargetCollectionResourceLoadingCallback;


public class MainActivity extends AppCompatActivity implements ObjectTrackerListener,
        ExternalRendering, AdapterView.OnItemSelectedListener {

    public static final String TAG = "MainActivity";

    private static final int CHILD_GLSURFACEVIEW = 100;
    private static final int CHILD_FILTER_SPINNER = 101;

    private WikitudeSDK mWikitudeSDK;
    private CustomSurfaceView mView;
    private Driver mDriver;
    private GLRenderer mGLRenderer;

    private TargetCollectionResource mTargetCollectionResource;
    private DropDownAlert mDropDownAlert;

    private int mCount = 0;


    private ConstraintLayout mParentConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mParentConstraintLayout = findViewById(R.id.parent_constraint_layout);

        mWikitudeSDK = new WikitudeSDK(this);
        NativeStartupConfiguration startupConfiguration = new NativeStartupConfiguration();
        startupConfiguration.setLicenseKey(WikitudeSDKConstants.WIKITUDE_SDK_KEY);
        startupConfiguration.setCameraPosition(CameraSettings.CameraPosition.BACK);
        startupConfiguration.setCameraResolution(CameraSettings.CameraResolution.AUTO);

        mWikitudeSDK.onCreate(getApplicationContext(), this, startupConfiguration);

        /*mTargetCollectionResource = mWikitudeSDK.getTrackerManager().
                createTargetCollectionResource("file:///android_asset/jeep_engine.wto",
                        new TargetCollectionResourceLoadingCallback() {
                            @Override
                            public void onError(int errorCode, String errorMessage) {
                                Log.v(TAG, "Failed to load target collection resource. Reason: " + errorMessage);
                            }

                            @Override
                            public void onFinish() {
                                mWikitudeSDK.getTrackerManager().createObjectTracker(mTargetCollectionResource,
                                        MainActivity.this, null);
                            }
                        });

        mDropDownAlert = new DropDownAlert(this);
        mDropDownAlert.setText("Loading Target:");
        mDropDownAlert.setTextWeight(1);
        mDropDownAlert.show();*/

//        createTargetResource(Const.DETECT_ENGINE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mWikitudeSDK.onResume();
        mView.onResume();
        mDriver.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWikitudeSDK.onPause();
        mView.onPause();
        mDriver.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWikitudeSDK.clearCache();
        mWikitudeSDK.onDestroy();
    }

    @Override
    public void onRenderExtensionCreated(final RenderExtension renderExtension) {
        mGLRenderer = new GLRenderer(renderExtension);
        createView(getApplicationContext(), mGLRenderer);
//        mView = new CustomSurfaceView(getApplicationContext(), mGLRenderer);
//        mDriver = new Driver(mView, 30);
//        setContentView(mView);
    }

    @Override
    public void onTargetsLoaded(ObjectTracker tracker) {
        Log.v(TAG, "Object tracker loaded");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDropDownAlert.setText("Scan Target:");
                try {
                    mDropDownAlert.addImages("firetruck_image.png");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mDropDownAlert.setTextWeight(0.5f);
            }
        });
    }

    @Override
    public void onErrorLoadingTargets(ObjectTracker tracker, int errorCode, final String errorMessage) {
        Log.v(TAG, "Unable to load image tracker. Reason: " + errorMessage);
    }

    @Override
    public void onObjectRecognized(ObjectTracker tracker, final ObjectTarget target) {
        Log.v(TAG, "Recognized target " + target.getName());
        mDropDownAlert.dismiss();

        StrokedCube strokedCube = new StrokedCube();
        OccluderCube occluderCube = new OccluderCube();
        Engine engine = new Engine(this);
        Sprite sprite = new Sprite(this);

        mGLRenderer.setRenderablesForKey(target.getName(), strokedCube, occluderCube, engine, sprite);
    }

    @Override
    public void onObjectTracked(ObjectTracker tracker, final ObjectTarget target) {
        /*StrokedCube strokedCube = (StrokedCube)mGLRenderer.getRenderableForKey(target.getName());
        if (strokedCube != null) {
            strokedCube.projectionMatrix = target.getProjectionMatrix();
            strokedCube.viewMatrix = target.getViewMatrix();

            strokedCube.setYTranslate(0.5f);

            strokedCube.setXScale(target.getTargetScale().x);
            strokedCube.setYScale(target.getTargetScale().y);
            strokedCube.setZScale(target.getTargetScale().z);
        }

        OccluderCube occluderCube = (OccluderCube)mGLRenderer.getOccluderForKey(target.getName());
        if (occluderCube != null) {
            occluderCube.projectionMatrix = target.getProjectionMatrix();
            occluderCube.viewMatrix = target.getViewMatrix();

            occluderCube.setYTranslate(0.5f);

            occluderCube.setXScale(target.getTargetScale().x);
            occluderCube.setYScale(target.getTargetScale().y);
            occluderCube.setZScale(target.getTargetScale().z);
        }
*/

//        Engine engine = (Engine)mGLRenderer.getEnginForKey(target.getName());
//
//        if(engine != null){
//            engine.projectionMatrix = target.getProjectionMatrix();
//            engine.viewMatrix = target.getViewMatrix();
//
//
//            engine.setYTranslate(0.5f);
//
//
//            engine.setXScale(target.getTargetScale().x);
//            engine.setYScale(target.getTargetScale().y);
//            engine.setZScale(target.getTargetScale().z);
//        }


        Sprite sprite = (Sprite) mGLRenderer.getSpriteForKey(target.getName());

        if (sprite != null) {
            sprite.projectionMatrix = target.getProjectionMatrix();
            sprite.viewMatrix = target.getViewMatrix();


            sprite.setYTranslate(0.5f);

            sprite.setXTranslate(-0.3f);

            sprite.setXScale(target.getTargetScale().x);
            sprite.setYScale(target.getTargetScale().y);
            sprite.setZScale(target.getTargetScale().z);

        }

        if (mCount == 0) {
            AudioUtils.getInstance(this, R.raw.splash_sound).playSound();
//            mVibrator.vibrate(700);
            mCount--;
        }


    }

    @Override
    public void onObjectLost(ObjectTracker tracker, final ObjectTarget target) {
        Log.v(TAG, "Lost target " + target.getName());
        mGLRenderer.removeRenderablesForKey(target.getName());
        mCount = 0;
    }

    /**
     * This method is used for adding the GLSurfaceView in the activity_main.xml dynamically.
     *
     * @param context
     * @param glRenderer
     */
    private void createView(Context context, GLRenderer glRenderer) {

        mView = new CustomSurfaceView(context, glRenderer);

        mView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mView.setId(CHILD_GLSURFACEVIEW);

        mDriver = new Driver(mView, 30);

        mParentConstraintLayout.addView(mView, 0);

        generateFilterSpinner();

    }

    private void generateFilterSpinner() {

        Spinner spinner = new Spinner(this);

        spinner.setId(CHILD_FILTER_SPINNER);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                dpToPx(48), dpToPx(48));


        spinner.setLayoutParams(layoutParams);

        spinner.setBackground(getResources().getDrawable(R.drawable.filter_icon));


        mParentConstraintLayout.addView(spinner, 1);

        setAdapter(R.array.filter_type, spinner);

        ConstraintSet constraintSet = new ConstraintSet();

        constraintSet.clone(mParentConstraintLayout);
        constraintSet.connect(CHILD_FILTER_SPINNER, ConstraintSet.BOTTOM, mParentConstraintLayout.getId(),
                ConstraintSet.BOTTOM, 20);
        constraintSet.connect(CHILD_FILTER_SPINNER, ConstraintSet.RIGHT, mParentConstraintLayout.getId(),
                ConstraintSet.RIGHT, 15);

        mParentConstraintLayout.setConstraintSet(constraintSet);
    }

    private void setAdapter(int id, Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                id, R.layout.simple_spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int selectedPosition, long l) {

        ((TextView) view).setText(null);

        switch (selectedPosition){
            case Const.FILTER_OF_ENGINE:
                createTargetResource(Const.DETECT_ENGINE);
                break;
            case Const.FILTER_OF_BRAKES:
                createTargetResource(Const.DETECT_BRAKES);
                break;
            case Const.FILTER_OF_DASHBOARD:
                createTargetResource(Const.DETECT_DASHBOARD);
                break;
            case Const.FILTER_OF_ALL:
                createTargetResource(Const.DETECT_ALL);
                break;


        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private int dpToPx(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    private void createTargetResource(String targetConstant) {

        switch (targetConstant) {

            case Const.DETECT_ALL:
                getTargetResource(Const.TARGET_ALL);
                break;
            case Const.DETECT_ENGINE:
                getTargetResource(Const.TARGET_ENGINE);
                break;
            case Const.DETECT_BRAKES:
                getTargetResource(Const.TARGET_BRAKES);
                break;
            case Const.DETECT_DASHBOARD:
                getTargetResource(Const.TARGET_DASHBOARD);
                break;

        }


    }


    private void getTargetResource(final String targetFileName) {

        mTargetCollectionResource = mWikitudeSDK.getTrackerManager().
                createTargetCollectionResource("file:///android_asset/" + targetFileName,
                        new TargetCollectionResourceLoadingCallback() {
                            @Override
                            public void onError(int errorCode, String errorMessage) {
                                Log.v(TAG, "Failed to load target collection resource. Reason: "
                                        + errorMessage);
                            }

                            @Override
                            public void onFinish() {
                                mWikitudeSDK.getTrackerManager().createObjectTracker(mTargetCollectionResource,
                                        MainActivity.this, null);
                            }
                        });

        mDropDownAlert = new DropDownAlert(this);
        mDropDownAlert.setText("Loading Target:");
        mDropDownAlert.setTextWeight(1);
        mDropDownAlert.show();


    }
}
