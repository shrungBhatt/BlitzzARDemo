package com.projects.shrungbhatt.blitzzardemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Engine extends Renderable {

    private static final String TAG = "Engine";
    private Context mContext;

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


    static final int COORDS_PER_VERTEX = 2;

    static float mEngineVertices[] = {
            -0.5f, 0.5f,   // top left
            -0.5f, -0.5f,   // bottom left
            0.5f, -0.5f,   // bottom right
            0.5f, 0.5f

    };

    final float mEngineTextureVertices[] = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f
    };


    private short mEngineIndiceDrawOrder[] = {0, 1, 2, 0, 2, 3}; //Order to draw vertices
    private final int vertexStride = COORDS_PER_VERTEX * 4; //Bytes per vertex

    float mColor[] = {1.0f, 1.0f, 1.0f, 1.0f};


    public Engine(Context context) {

        mContext = context;


        mTextureHandle = loadTexture(mContext, R.drawable.generator);



        mEngineTextureCoordinates = ByteBuffer.allocateDirect(mEngineTextureVertices.length * 4).
                order(ByteOrder.nativeOrder()).asFloatBuffer();
        mEngineTextureCoordinates.put(mEngineTextureVertices).position(0);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                mEngineVertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mEngineVertexBuffer = bb.asFloatBuffer();
        mEngineVertexBuffer.put(mEngineVertices);
        mEngineVertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                mEngineIndiceDrawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        mEngineIndiceBuffer = dlb.asShortBuffer();
        mEngineIndiceBuffer.put(mEngineIndiceDrawOrder);
        mEngineIndiceBuffer.position(0);
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



        GLES20.glUseProgram(mAugmentationProgram);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);


        GLES20.glVertexAttribPointer(mPositionSlot, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, vertexStride, mEngineVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionSlot);

        //Set the color handle
        GLES20.glUniform4fv(mColorHandle, 1, mColor, 0);

        mTextureCoordinate = GLES20.glGetUniformLocation(mAugmentationProgram,"a_texCoordinate");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mAugmentationProgram,"u_texture");
        //Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        //Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);

        //Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        //Pass in the texture coordinate information
        mEngineTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinate, mTextureCoordinateDataSize, GLES20.GL_FLOAT,
                false, 8, mEngineTextureCoordinates);
        GLES20.glEnableVertexAttribArray(mTextureCoordinate);

        GLES20.glUniformMatrix4fv(mProjectionUniform, 1, false, this.projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(mModelViewUniform, 1, false, this.viewMatrix, 0);

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

        GLES20.glUniformMatrix4fv(mScaleMatrixUniform, 1, false, scaleMatrix, 0);
        GLES20.glUniformMatrix4fv(mTranslateMatrixUniform, 1, false, translateMatrix, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_COLOR,GLES20.GL_DST_ALPHA);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mEngineIndiceDrawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, mEngineIndiceBuffer);

        GLES20.glDisableVertexAttribArray(mPositionSlot);

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
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShaderCode);
        mAugmentationProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mAugmentationProgram, vertexShader);
        GLES20.glAttachShader(mAugmentationProgram, fragmentShader);
        GLES20.glBindAttribLocation(mAugmentationProgram, 0, "a_texCoordinate");
        GLES20.glLinkProgram(mAugmentationProgram);

        mPositionSlot = GLES20.glGetAttribLocation(mAugmentationProgram, "v_position");
        mModelViewUniform = GLES20.glGetUniformLocation(mAugmentationProgram, "u_modelView");
        mProjectionUniform = GLES20.glGetUniformLocation(mAugmentationProgram, "u_projection");
        mScaleMatrixUniform = GLES20.glGetUniformLocation(mAugmentationProgram, "u_scale");
        mTranslateMatrixUniform = GLES20.glGetUniformLocation(mAugmentationProgram, "u_translation");

        mColorHandle = GLES20.glGetUniformLocation(mAugmentationProgram,"v_color");

        mTextureHandle = loadTexture(mContext, R.drawable.generator);


    }

    private static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    private int loadTexture(final Context context, final int resourceId) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

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
