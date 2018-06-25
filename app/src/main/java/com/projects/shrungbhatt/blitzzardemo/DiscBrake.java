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

public class DiscBrake extends Renderable {

    private Context mContext;

    private final FloatBuffer mCubeTextureCoordinates;
    public FloatBuffer mVertexBuffer;
    public ShortBuffer drawListBuffer;

    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mTextureHandle;
    private int mMVPMatrixHandle;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;
    private final int mTextureCoordinateDataSize = 2;
    private int mProjectionUniform = -1;
    private int mModelViewUniform = -1;
    private int mScaleMatrixUniform = -1;
    private int mTranslateMatrixUniform = -1;


    private final String vertexShaderCode =
            "attribute vec2 a_TexCoordinate;" +
                    "varying vec2 v_TexCoordinate;" +
                    "attribute vec4 vPosition;" +
                    "uniform mat4 u_projection;" +
                    "uniform mat4 u_modelView;" +
                    "uniform mat4 u_scale;" +
                    "uniform mat4 u_translation;" +
                    "void main() {" +
                    "  gl_Position = u_projection * u_modelView * u_translation * u_scale * vPosition;" +
                    "v_TexCoordinate = a_TexCoordinate;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "uniform sampler2D u_Texture;" +
                    "varying vec2 v_TexCoordinate;" +
                    "void main() {" +
                    "gl_FragColor = (vColor * texture2D(u_Texture, v_TexCoordinate));" +
                    "}";

    static final int COORDS_PER_VERTEX = 2;

    private float mXScale = 1.8f;
    private float mYScale = 1.8f;
    private float mZScale = 1.8f;

    private float mXTranslate = 0.0f;
    private float mYTranslate = 0.0f;
    private float mZTranslate = 0.0f;

    static float mSpriteVertices[] = {
            -0.2f, 0.2f,   // top left
            -0.2f, -0.2f,   // bottom left
            0.2f, -0.2f,   // bottom right
            0.2f, 0.2f

    };

    final float[] cubeTextureCoordinateData = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f
    };


    private short drawOrder[] = {0, 1, 2, 0, 2, 3}; //Order to draw vertices
    private final int vertexStride = COORDS_PER_VERTEX * 4; //Bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = {1.0f, 1.0f, 1.0f, 1.0f};

    public DiscBrake(Context context) {

        mContext = context;

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);

        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgram, vertexShader);

        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glBindAttribLocation(mProgram, 0, "a_TexCoordinate");

        GLES20.glLinkProgram(mProgram);

        mTextureHandle = loadTexture(mContext, R.drawable.disc_brake);

        mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4).
                order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                mSpriteVertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(mSpriteVertices);
        mVertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
    }

    public void Draw() {
        //Add program to OpenGL ES Environment
        GLES20.glUseProgram(mProgram);

        //Get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mModelViewUniform = GLES20.glGetUniformLocation(mProgram, "u_modelView");
        mProjectionUniform = GLES20.glGetUniformLocation(mProgram, "u_projection");
        mScaleMatrixUniform = GLES20.glGetUniformLocation(mProgram, "u_scale");
        mTranslateMatrixUniform = GLES20.glGetUniformLocation(mProgram, "u_translation");


        //Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);


        //Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, vertexStride, mVertexBuffer);

        //Get Handle to Fragment Shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        //Set the Color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);


        //Set Texture Handles and bind Texture
        mTextureUniformHandle = GLES20.glGetAttribLocation(mProgram, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");

        //Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        //Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);

        //Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        //Pass in the texture coordinate information
        mCubeTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT,
                false, 8, mCubeTextureCoordinates);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);


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

        //Get Handle to Shape's Transformation Matrix
//        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        //Apply the projection and view transformation
//        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

//        GLES20.glEnable(GLES20.GL_BLEND);
//        GLES20.glBlendFunc(GLES20.GL_SRC_COLOR,GLES20.GL_DST_ALPHA);
        //Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        //Disable Vertex Array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public static int loadTexture(final Context context, final int resourceId) {
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


    public int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
    @Override
    public void onSurfaceCreated() {

    }

    @Override
    public void onDrawFrame() {

        if (this.projectionMatrix == null || this.viewMatrix == null) {
            return;
        }

        Draw();
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

}
