package com.projects.shrungbhatt.blitzzardemo;


import android.opengl.GLES20;
import android.opengl.GLSurfaceView;


import com.wikitude.common.rendering.RenderExtension;


import java.util.TreeMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;




public class GLRenderer implements GLSurfaceView.Renderer {

    private RenderExtension mWikitudeRenderExtension = null;
    private TreeMap<String, Renderable> mOccluders = new TreeMap<>();
    private TreeMap<String, Renderable> mRenderables = new TreeMap<>();
    private TreeMap<String, Renderable> mEngines = new TreeMap<>();
    private TreeMap<String, Renderable> mSprites = new TreeMap<>();
    private TreeMap<String, Renderable> mDiscBrake = new TreeMap<>();

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

        for (TreeMap.Entry<String, Renderable> pairOccluder : mOccluders.entrySet()) {
            Renderable renderable = pairOccluder.getValue();
            renderable.onDrawFrame();
        }

        for (TreeMap.Entry<String, Renderable> pairRenderables : mRenderables.entrySet()) {
            Renderable renderable = pairRenderables.getValue();
            renderable.onDrawFrame();
        }

        for(TreeMap.Entry<String,Renderable> engineRenderable : mEngines.entrySet()){
            Renderable renderable = engineRenderable.getValue();
            renderable.onDrawFrame();
        }

        for(TreeMap.Entry<String,Renderable> spriteRenderable : mSprites.entrySet()){
            Renderable renderable = spriteRenderable.getValue();
            if(renderable != null) {
                renderable.onDrawFrame();
            }
        }

        for(TreeMap.Entry<String,Renderable> discBrake : mDiscBrake.entrySet()){
            Renderable renderable = discBrake.getValue();
            if(renderable != null) {
                renderable.onDrawFrame();
            }
        }

    }

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
        if (mWikitudeRenderExtension != null) {
            mWikitudeRenderExtension.onSurfaceCreated(unused, config);
        }

        for (TreeMap.Entry<String, Renderable> pairOccluder : mOccluders.entrySet()) {
            Renderable renderable = pairOccluder.getValue();
            renderable.onSurfaceCreated();
        }

        for (TreeMap.Entry<String, Renderable> pairRenderables : mRenderables.entrySet()) {
            Renderable renderable = pairRenderables.getValue();
            renderable.onSurfaceCreated();
        }

        for(TreeMap.Entry<String,Renderable> engineRenderable : mEngines.entrySet()){
            Renderable renderable = engineRenderable.getValue();
            renderable.onSurfaceCreated();
        }

        for(TreeMap.Entry<String,Renderable> spriteRenderable : mSprites.entrySet()){
            Renderable renderable = spriteRenderable.getValue();
            renderable.onSurfaceCreated();
        }

        for(TreeMap.Entry<String,Renderable> discBrake : mDiscBrake.entrySet()){
            Renderable renderable = discBrake.getValue();
            renderable.onSurfaceCreated();
        }




    }

    @Override
    public void onSurfaceChanged(final GL10 unused, final int width, final int height) {
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

    public synchronized void setRenderablesForKey(final String key, final Renderable renderbale,
                                                  final Renderable occluder, final Engine engine,
                                                  final Sprite sprite,final DiscBrake discBrake) {
        if (occluder != null) {
            mOccluders.put(key, occluder);
        }

        if(engine != null){
            mEngines.put(key,engine);
        }

        if(sprite != null){
            mSprites.put(key,sprite);
        }

        if(discBrake != null){
            mDiscBrake.put(key,discBrake);
        }

        mRenderables.put(key, renderbale);
    }

    public synchronized void removeRenderablesForKey(final String key) {
        mRenderables.remove(key);
        mOccluders.remove(key);
        mEngines.remove(key);
        mSprites.remove(key);
        mDiscBrake.remove(key);
    }

    public synchronized void removeAllRenderables() {
        mRenderables.clear();
        mOccluders.clear();
        mEngines.clear();
        mSprites.clear();
        mDiscBrake.clear();
    }

    public synchronized Renderable getRenderableForKey(final String key) {
        return mRenderables.get(key);
    }

    public synchronized Renderable getOccluderForKey(final String key) {
        return mOccluders.get(key);
    }

    public synchronized Renderable getEngineForKey(final String key){
        return mEngines.get(key);
    }

    public synchronized Renderable getSpriteForKey(final String key){
        return mSprites.get(key);
    }

    public synchronized Renderable getDiscBrakes(final String key){
        return mDiscBrake.get(key);
    }

}
