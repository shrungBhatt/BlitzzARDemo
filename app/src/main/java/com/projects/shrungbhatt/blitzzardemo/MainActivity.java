package com.projects.shrungbhatt.blitzzardemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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


import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Light;


public class MainActivity extends AppCompatActivity implements ObjectTrackerListener, ExternalRendering {

    public static final String TAG ="MainActivity";

    private WikitudeSDK mWikitudeSDK;
    private CustomSurfaceView mView;
    private Driver mDriver;
    private GLRenderer mGLRenderer;

    private TargetCollectionResource mTargetCollectionResource;
    private DropDownAlert mDropDownAlert;
    private Object3dContainer faceObject3D;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mWikitudeSDK = new WikitudeSDK(this);
        NativeStartupConfiguration startupConfiguration = new NativeStartupConfiguration();
        startupConfiguration.setLicenseKey(WikitudeSDKConstants.WIKITUDE_SDK_KEY);
        startupConfiguration.setCameraPosition(CameraSettings.CameraPosition.BACK);
        startupConfiguration.setCameraResolution(CameraSettings.CameraResolution.AUTO);

        mWikitudeSDK.onCreate(getApplicationContext(), this, startupConfiguration);

        mTargetCollectionResource = mWikitudeSDK.getTrackerManager().createTargetCollectionResource("file:///android_asset/jeep_target.wto", new TargetCollectionResourceLoadingCallback() {
            @Override
            public void onError(int errorCode, String errorMessage) {
                Log.v(TAG, "Failed to load target collection resource. Reason: " + errorMessage);
            }

            @Override
            public void onFinish() {
                mWikitudeSDK.getTrackerManager().createObjectTracker(mTargetCollectionResource, MainActivity.this, null);
            }
        });

        mDropDownAlert = new DropDownAlert(this);
        mDropDownAlert.setText("Loading Target:");
        mDropDownAlert.setTextWeight(1);
        mDropDownAlert.show();
    }

   /* @Override
    public void initScene() {
        super.initScene();

       *//* scene.lights().add(new Light());
        scene.lights().add(new Light());
        Light myLight = new Light();
        myLight.position.setZ(150);
        scene.lights().add(myLight);
        IParser myParser = Parser.createParser(Parser.Type.OBJ, getResources(), "com.projects.shrungbhatt.blitzzardemo:raw/camaro_obj",true);
        myParser.parse();
        faceObject3D = myParser.getParsedObject();
        faceObject3D.position().x = faceObject3D.position().y = faceObject3D.position().z = 0;
        scene.addChild(faceObject3D);*//*

    }

    @Override
    public void onUpdateScene() {
        super.onUpdateScene();

//        faceObject3D.rotation().y++;
    }*/



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
        mView = new CustomSurfaceView(getApplicationContext(), mGLRenderer);
        mDriver = new Driver(mView, 30);
        setContentView(mView);
    }

    @Override
    public void onTargetsLoaded(ObjectTracker tracker) {
        Log.v(TAG, "Object tracker loaded");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDropDownAlert.setText("Scan Target:");
                mDropDownAlert.addImages("firetruck_image.png");
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

        mGLRenderer.setRenderablesForKey(target.getName(), strokedCube, occluderCube,engine);
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

        Engine engine = (Engine)mGLRenderer.getEnginForKey(target.getName());

        if(engine != null){
            engine.projectionMatrix = target.getProjectionMatrix();
            engine.viewMatrix = target.getViewMatrix();


            engine.setYTranslate(0.5f);


            engine.setXScale(target.getTargetScale().x);
            engine.setYScale(target.getTargetScale().y);
            engine.setZScale(target.getTargetScale().z);
        }
    }

    @Override
    public void onObjectLost(ObjectTracker tracker, final ObjectTarget target) {
        Log.v(TAG, "Lost target " + target.getName());
        mGLRenderer.removeRenderablesForKey(target.getName());
    }
}
