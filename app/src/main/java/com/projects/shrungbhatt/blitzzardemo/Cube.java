package com.projects.shrungbhatt.blitzzardemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import min3d.Shared;
import min3d.core.FacesBufferedList;
import min3d.core.Object3d;

import static android.opengl.GLES20.*;

public class Cube extends Renderable {

    private static final String TAG = "Engine";
    private Context mContext;
    int pos, len;


    String mFragmentShaderCode = "precision mediump float;" +
            "uniform vec4 v_color;" +
            "uniform sampler2D u_texture;" +
            "varying vec2 v_texCoordinate;" +
            "void main() {" +
            "gl_FragColor = (v_color * texture2D(u_texture, v_texCoordinate));" +
            "}";

    String mVertexShaderCode = "attribute vec2 a_texCoordinate;" +
            "varying vec2 v_texCoordinate;" +
            "attribute vec4 v_position;" +
            "uniform mat4 u_projection;" +
            "uniform mat4 u_modelView;" +
            "uniform mat4 u_scale;" +
            "uniform mat4 u_translation;" +
            "void main()" +
            "{" +
            "  gl_Position = u_projection * u_modelView * u_translation * u_scale * v_position;" +
            "v_texCoordinate = a_texCoordinate;" +
            "}";


    private int mAugmentationProgram = -1;
    private int mPositionSlot = -1;
    private int mProjectionUniform = -1;
    private int mModelViewUniform = -1;
    private int mScaleMatrixUniform = -1;
    private int mTranslateMatrixUniform = -1;
    private int mTextureHandle = -1;
    private int mTextureCoordinate = -1;
    private int mTextureUniformHandle = -1;
    private int mColorHandle = -1;
    private int mTextureCoordinateDataSize = 2;


    private FloatBuffer mEngineTextureCoordinates;
    private FloatBuffer mEngineVertexBuffer;
    private ShortBuffer mEngineIndiceBuffer;

    private float mXScale = 1.0f;
    private float mYScale = 1.0f;
    private float mZScale = 1.0f;

    private float mXTranslate = 0.0f;
    private float mYTranslate = 0.0f;
    private float mZTranslate = 0.0f;


    static final int COORDS_PER_VERTEX = 3;

    float mColor[] = {1.0f, 1.0f, 1.0f, 1.0f};




    public Cube(Context context) {

        mContext = context;

        Shared.context(mContext);


        Object3d object3d = Util.getObject3d(context,"brakes_obj");

        mEngineTextureCoordinates = object3d.vertices().uvs().buffer();
        mEngineTextureCoordinates.position(0);


        mEngineVertexBuffer = object3d.vertices().points().buffer();
        mEngineVertexBuffer.position(0);

        // initialize byte buffer for the draw list

        if (!object3d.faces().renderSubsetEnabled()) {
            pos = 0;
            len = object3d.faces().size();
        } else {
            pos = object3d.faces().renderSubsetStartIndex() * FacesBufferedList.PROPERTIES_PER_ELEMENT;
            len = object3d.faces().renderSubsetLength();
        }

        mEngineIndiceBuffer = object3d.faces().buffer();
        mEngineIndiceBuffer.position(pos);
    }


    public void loadTexture(){
        mTextureHandle = loadTexture(mContext, R.drawable.merge_from_ofoct);
    }


    @Override
    public void onSurfaceCreated() {
        compileShaders();
    }

    @Override
    public void onDrawFrame() {

        if (mAugmentationProgram == -1) {
            compileShaders();
        }


        if (this.projectionMatrix == null || this.viewMatrix == null) {
            return;
        }



        glUseProgram(mAugmentationProgram);
//
//        glBindBuffer(GL_ARRAY_BUFFER, 0);
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);


        glVertexAttribPointer(mPositionSlot, COORDS_PER_VERTEX, GL_FLOAT,
                false, 0, mEngineVertexBuffer);
        glEnableVertexAttribArray(mPositionSlot);

        //Set the color handle
        GLES20.glUniform4fv(mColorHandle, 1, mColor, 0);

        mTextureCoordinate = glGetAttribLocation(mAugmentationProgram,"a_texCoordinate");
        mTextureUniformHandle = glGetUniformLocation(mAugmentationProgram,"u_texture");

        //Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        //Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, mTextureHandle);

        //Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(mTextureUniformHandle, 0);

        //Pass in the texture coordinate information
        glVertexAttribPointer(mTextureCoordinate, mTextureCoordinateDataSize, GL_FLOAT,
                false, 0, mEngineTextureCoordinates);
        glEnableVertexAttribArray(mTextureCoordinate);

        glUniformMatrix4fv(mProjectionUniform, 1, false, this.projectionMatrix, 0);
        glUniformMatrix4fv(mModelViewUniform, 1, false, this.viewMatrix, 0);

        float[] scaleMatrix = {
                mXScale,    0.0f,       0.0f,       0.0f,
                0.0f,       mYScale,    0.0f,       0.0f,
                0.0f,       0.0f,       mZScale,    0.0f,
                0.0f,       0.0f,       0.0f,       1.0f
        };

        float[] translateMatrix = {
                1.0f,               0.0f,               0.0f,               0.0f,
                0.0f,               1.0f,               0.0f,               0.0f,
                0.0f,               0.0f,               1.0f,               0.0f,
                mXTranslate,        mYTranslate,        mZTranslate,        1.0f
        };

        glUniformMatrix4fv(mScaleMatrixUniform, 1, false, scaleMatrix, 0);
        glUniformMatrix4fv(mTranslateMatrixUniform, 1, false, translateMatrix, 0);


        glDrawElements(GL_TRIANGLES, len * FacesBufferedList.PROPERTIES_PER_ELEMENT,
                GL_UNSIGNED_SHORT, mEngineIndiceBuffer);

        glDisableVertexAttribArray(mPositionSlot);

    }

    public float getXScale() {
        return mXScale;
    }

    public void setXScale(float xScale) {
        this.mXScale = xScale;
    }

    public float getYScale() {
        return mYScale;
    }

    public void setYScale(float yScale) {
        this.mYScale = yScale;
    }

    public float getZScale() {
        return mZScale;
    }

    public void setZScale(float zScale) {
        this.mZScale = zScale;
    }

    public float getXTranslate() {
        return mXTranslate;
    }

    public void setXTranslate(float xTranslate) { this.mXTranslate = xTranslate; }

    public float getYTranslate() {
        return mYTranslate;
    }

    public void setYTranslate(float yTranslate) { this.mYTranslate = yTranslate; }

    public float getZTranslate() {
        return mZTranslate;
    }

    public void setZTranslate(float zTranslate) { this.mZTranslate = zTranslate; }

    private void compileShaders() {
        int vertexShader = loadShader(GL_VERTEX_SHADER, mVertexShaderCode);
        int fragmentShader = loadShader(GL_FRAGMENT_SHADER, mFragmentShaderCode);
        mAugmentationProgram = glCreateProgram();
        glAttachShader(mAugmentationProgram, vertexShader);
        glAttachShader(mAugmentationProgram, fragmentShader);
        glBindAttribLocation(mAugmentationProgram, 0, "a_texCoordinate");
        glLinkProgram(mAugmentationProgram);

        mPositionSlot = glGetAttribLocation(mAugmentationProgram, "v_position");
        mModelViewUniform = glGetUniformLocation(mAugmentationProgram, "u_modelView");
        mProjectionUniform = glGetUniformLocation(mAugmentationProgram, "u_projection");
        mScaleMatrixUniform = glGetUniformLocation(mAugmentationProgram, "u_scale");
        mTranslateMatrixUniform = glGetUniformLocation(mAugmentationProgram, "u_translation");

        mColorHandle = glGetUniformLocation(mAugmentationProgram,"v_color");


    }

    private static int loadShader(int type, String shaderCode) {
        int shader = glCreateShader(type);

        glShaderSource(shader, shaderCode);
        glCompileShader(shader);

        return shader;
    }

    public static int loadTexture(final Context context, final int resourceId) {
        final int[] textureHandle = new int[1];

        glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }
}
