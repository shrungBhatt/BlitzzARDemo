package com.projects.shrungbhatt.blitzzardemo;


import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;


import com.projects.shrungbhatt.blitzzardemo.objects.DiscBrake;
import com.projects.shrungbhatt.blitzzardemo.objects.Engine;
import com.projects.shrungbhatt.blitzzardemo.objects.Renderable;
import com.projects.shrungbhatt.blitzzardemo.utils.Interface_ResetAngle;
import com.wikitude.camera.CameraManager;
import com.wikitude.common.rendering.RenderExtension;


import java.util.TreeMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glClearDepthf;
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDepthMask;
import static android.opengl.GLES20.glDepthRangef;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glFrontFace;


public class GLRenderer implements GLSurfaceView.Renderer,Interface_ResetAngle,
        CameraManager.FovChangedListener{

    private RenderExtension mWikitudeRenderExtension = null;
    private TreeMap<String, Renderable> mCubes = new TreeMap<>();
    private TreeMap<String, Renderable> mEngines = new TreeMap<>();

    private final float[] mProjectionMatrix = new float[16];



    public volatile float mDeltaX;
    public volatile float mDeltaY;

    public int mWidth;
    public int mHeight;
    /**
     * This are the params for the displaying the object
     */



    public GLRenderer(RenderExtension wikitudeRenderExtension) {
        mWikitudeRenderExtension = wikitudeRenderExtension;
        /*
         * Until Wikitude SDK version 2.1 onDrawFrame triggered also a logic update inside the SDK core.
         * This behaviour is deprecated and onUpdate should be used from now on to update logic inside the SDK core. <br>
         *
         * The default behaviour is that onDrawFrame also updates logic. <br>
         *
         * To use the new separated drawing and logic update methods, RenderExtension.useSeparatedRenderAndLogicUpdates should be called.
         * Otherwise the logic will still be updated in onDrawFrame.
         */
        mWikitudeRenderExtension.useSeparatedRenderAndLogicUpdates();

    }

    @Override
    public synchronized void onDrawFrame(final GL10 unused) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClearDepthf(1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (mWikitudeRenderExtension != null) {
            // Will trigger a logic update in the SDK
            mWikitudeRenderExtension.onUpdate();
            // will trigger drawing of the camera frame
            mWikitudeRenderExtension.onDrawFrame(unused);
        }


        for(TreeMap.Entry<String,Renderable> cubes : mCubes.entrySet()){
            Renderable renderable = cubes.getValue();
            if(renderable != null) {
                renderable.onDrawFrame(mDeltaX,mDeltaY,this);
            }
        }

        for(TreeMap.Entry<String,Renderable> engines : mEngines.entrySet()){
            Renderable renderable = engines.getValue();
            if(renderable != null) {
                renderable.onDrawFrame(mDeltaX,mDeltaY,this);
            }
        }

    }

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {

        reset();

        if (mWikitudeRenderExtension != null) {
            mWikitudeRenderExtension.onSurfaceCreated(unused, config);
        }


        for(TreeMap.Entry<String,Renderable> cubes : mCubes.entrySet()){
            Renderable renderable = cubes.getValue();
            if(renderable != null) {
                renderable.onSurfaceCreated();
            }
        }

        for(TreeMap.Entry<String,Renderable> engines : mEngines.entrySet()){
            Renderable renderable = engines.getValue();
            if(renderable != null) {
                renderable.onSurfaceCreated();
            }
        }




    }

    @Override
    public void onSurfaceChanged(final GL10 unused, final int width, final int height) {

        mWidth = width;
        mHeight = height;

        if (mWikitudeRenderExtension != null) {
            mWikitudeRenderExtension.onSurfaceChanged(unused, width, height);
        }
    }

    public void onResume() {
        if (mWikitudeRenderExtension != null) {
            mWikitudeRenderExtension.onResume();
        }
    }

    public void onPause() {
        if (mWikitudeRenderExtension != null) {
            mWikitudeRenderExtension.onPause();
        }
    }

    public synchronized void setRenderablesForKey(final String key, final DiscBrake discBrake,
                                                  final Engine engine) {

        if(discBrake != null){
            discBrake.projectionMatrix = mProjectionMatrix;
            mCubes.put(key, discBrake);
        }

        if(engine != null){
            engine.projectionMatrix = mProjectionMatrix;
            mEngines.put(key,engine);
        }



    }

    public synchronized void removeRenderablesForKey(final String key) {
        mCubes.remove(key);
        mEngines.remove(key);
    }

    public synchronized void removeAllRenderables() {
        mEngines.clear();
        mCubes.clear();
    }


    public synchronized Renderable getCubes(final String key){
        return mCubes.get(key);
    }

    public synchronized Renderable getEngines(final String key){
        return mEngines.get(key);
    }

    //These is to give initial settings to opengl.
    private void reset(){

        glEnable(GL10.GL_DEPTH_TEST);
        glClearDepthf(1.0f);
        glDepthFunc(GL10.GL_LESS);
        glDepthRangef(0, 1f);
        glDepthMask(true);

//        glEnable(GLES20.GL_BLEND);
//        glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        glFrontFace(GLES20.GL_CCW);
        glCullFace(GLES20.GL_BACK);
        glEnable(GLES20.GL_CULL_FACE);

    }

    @Override
    public void resetAngle() {
        mDeltaX = 0.0f;
        mDeltaY = 0.0f;
    }

    @Override
    public void onFovChanged(float fieldOfView) {
        if (mWidth != 0 && mHeight != 0) {
            Matrix.perspectiveM(mProjectionMatrix, 0, fieldOfView,
                    (float)mWidth/(float)mHeight,
                    0.05f, 5000f);
        }
    }
}
