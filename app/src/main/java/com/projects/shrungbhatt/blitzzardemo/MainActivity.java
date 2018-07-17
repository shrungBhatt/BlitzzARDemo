package com.projects.shrungbhatt.blitzzardemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
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

    private String mSelectedAugmentation;

    private ObjectTracker mObjectTracker = null;
    private ObjectTarget mObjectTarget = null;


    private ConstraintLayout mParentConstraintLayout;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private Cube mCube;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mParentConstraintLayout = findViewById(R.id.parent_constraint_layout);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

        } else {

            initializeWikitudeSdk();

        }

    }

    private void initializeWikitudeSdk() {

        mWikitudeSDK = new WikitudeSDK(this);
        NativeStartupConfiguration startupConfiguration = new NativeStartupConfiguration();
        startupConfiguration.setLicenseKey(WikitudeSDKConstants.WIKITUDE_SDK_KEY);
        startupConfiguration.setCameraPosition(CameraSettings.CameraPosition.BACK);
        startupConfiguration.setCameraResolution(CameraSettings.CameraResolution.AUTO);

        mWikitudeSDK.onCreate(getApplicationContext(), this, startupConfiguration);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mWikitudeSDK != null) {
            mWikitudeSDK.onResume();
            mView.onResume();
            mDriver.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mWikitudeSDK != null) {
            mWikitudeSDK.onPause();
            mView.onPause();
            mDriver.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWikitudeSDK != null) {
            mWikitudeSDK.clearCache();
            mWikitudeSDK.onDestroy();
        }
    }

    @Override
    public void onRenderExtensionCreated(final RenderExtension renderExtension) {
        mGLRenderer = new GLRenderer(renderExtension);
        createGlSurfaceView(getApplicationContext(), mGLRenderer);
    }

    @Override
    public void onTargetsLoaded(ObjectTracker tracker) {
        Log.v(TAG, "Object tracker loaded");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDropDownAlert.setText("Scan Target:");
                /*try {
                    mDropDownAlert.addImages("firetruck_image.png");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mDropDownAlert.setTextWeight(0.5f);*/
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

        mObjectTarget = target;
        mObjectTracker = tracker;


        mCube.loadTexture();

        mGLRenderer.setRenderablesForKey(target.getName(),mCube);
    }

    @Override
    public void onObjectTracked(ObjectTracker tracker, final ObjectTarget target) {

        Cube cube = (Cube)mGLRenderer.getCubes(target.getName());

        if(cube != null){
            cube.projectionMatrix = target.getProjectionMatrix();
            cube.viewMatrix = target.getViewMatrix();


            cube.setXScale(0.2f);
            cube.setYScale(0.2f);
            cube.setZScale(0.2f);

            cube.setXTranslate(0.22f);
            cube.setYTranslate(0.1f);
            cube.setZTranslate(-0.1f);
        }


       /* switch (mSelectedAugmentation) {

            case Const.DETECT_ENGINE:
                Sprite engine = (Sprite) mGLRenderer.getSpriteForKey(target.getName());

                if (engine != null) {
                    engine.projectionMatrix = target.getProjectionMatrix();
                    engine.viewMatrix = target.getViewMatrix();


                    engine.setYTranslate(0.4f);

                    engine.setXTranslate(0.1f);

                    engine.setXScale(target.getTargetScale().x);
                    engine.setYScale(target.getTargetScale().y);
                    engine.setZScale(target.getTargetScale().z);

                }
                if (mCount == 0) {
                    AudioUtils.getInstance(this, R.raw.splash_sound).playSound();
                    mCount--;
                }
                break;
            case Const.DETECT_BRAKES:
                DiscBrake discBrake = (DiscBrake) mGLRenderer.getDiscBrakes(target.getName());

                if (discBrake != null) {
                    discBrake.projectionMatrix = target.getProjectionMatrix();
                    discBrake.viewMatrix = target.getViewMatrix();

                    discBrake.setYTranslate(-0.4f);

                    discBrake.setXTranslate(-0.1f);

                    discBrake.setXScale(1.5f);
                    discBrake.setYScale(1.5f);
                    discBrake.setZScale(1.5f);

                }
                break;

            case Const.DETECT_ALL:
                Sprite engineForAllView = (Sprite) mGLRenderer.getSpriteForKey(target.getName());

                if (engineForAllView != null) {
                    engineForAllView.projectionMatrix = target.getProjectionMatrix();
                    engineForAllView.viewMatrix = target.getViewMatrix();


                    engineForAllView.setYTranslate(0.45f);

                    engineForAllView.setXTranslate(-0.32f);

                    engineForAllView.setXScale(target.getTargetScale().x);
                    engineForAllView.setYScale(target.getTargetScale().y);
                    engineForAllView.setZScale(target.getTargetScale().z);

                }

                DiscBrake discBrakeForAllView = (DiscBrake) mGLRenderer.getDiscBrakes(target.getName());

                if (discBrakeForAllView != null) {
                    discBrakeForAllView.projectionMatrix = target.getProjectionMatrix();
                    discBrakeForAllView.viewMatrix = target.getViewMatrix();

                    discBrakeForAllView.setYTranslate(0.1f);

                    discBrakeForAllView.setXTranslate(0.28f);

                    discBrakeForAllView.setXScale(0.7f);
                    discBrakeForAllView.setYScale(0.7f);
                    discBrakeForAllView.setZScale(0.7f);
                }
                break;

            case Const.DETECT_DASHBOARD:
                Sprite dashboard = (Sprite) mGLRenderer.getSpriteForKey(target.getName());

                if (dashboard != null) {
                    dashboard.projectionMatrix = target.getProjectionMatrix();
                    dashboard.viewMatrix = target.getViewMatrix();


                    dashboard.setYTranslate(0.4f);

                    dashboard.setXTranslate(0.1f);

                    dashboard.setXScale(target.getTargetScale().x);
                    dashboard.setYScale(target.getTargetScale().y);
                    dashboard.setZScale(target.getTargetScale().z);

                }
                break;
        }*/


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
    private void createGlSurfaceView(Context context, GLRenderer glRenderer) {


        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        mView = new CustomSurfaceView(context, glRenderer, displayMetrics.density);

        mView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mView.setId(CHILD_GLSURFACEVIEW);

        mDriver = new Driver(mView, 30);

        mParentConstraintLayout.addView(mView, 0);

        generateFilterSpinner();

        mCube = new Cube(this);


    }

    private void generateFilterSpinner() {

        Spinner spinner = new Spinner(this);

        spinner.setId(CHILD_FILTER_SPINNER);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                dpToPx(40), dpToPx(40));


        spinner.setLayoutParams(layoutParams);

        spinner.setBackground(getResources().getDrawable(R.drawable.filter));


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


        if (mObjectTracker != null && mObjectTarget != null) {
            onObjectLost(mObjectTracker, mObjectTarget);
        }

        ((TextView) view).setText(null);

        switch (selectedPosition) {
            case Const.FILTER_OF_ENGINE:
                createTargetResource(Const.DETECT_ENGINE);
                mSelectedAugmentation = Const.DETECT_ENGINE;
                break;
            case Const.FILTER_OF_BRAKES:
                createTargetResource(Const.DETECT_BRAKES);
                mSelectedAugmentation = Const.DETECT_BRAKES;
                break;
            case Const.FILTER_OF_DASHBOARD:
                createTargetResource(Const.DETECT_DASHBOARD);
                mSelectedAugmentation = Const.DETECT_DASHBOARD;
                break;
            case Const.FILTER_OF_ALL:
                createTargetResource(Const.DETECT_ALL);
                mSelectedAugmentation = Const.DETECT_ALL;
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
                getTargetResourceFromAssets(Const.TARGET_ALL);
                break;
            case Const.DETECT_ENGINE:
                getTargetResourceFromAssets(Const.TARGET_ENGINE);
                break;
            case Const.DETECT_BRAKES:
                getTargetResourceFromAssets(Const.TARGET_BRAKES);
                break;
            case Const.DETECT_DASHBOARD:
                getTargetResourceFromAssets(Const.TARGET_DASHBOARD);
                break;

        }


    }


    private void getTargetResourceFromAssets(final String targetFileName) {

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


        showDropDownAlert();


    }

    private void showDropDownAlert() {

        mDropDownAlert = new DropDownAlert(this);
        mDropDownAlert.setText("Loading Target:");
        mDropDownAlert.setTextWeight(1);
        mDropDownAlert.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeWikitudeSdk();
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage("This App won't work without camera permission. Closing BlitzzArDemo..")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, which) -> finish())
                            .create().show();


                }

            }
        }
    }
}
