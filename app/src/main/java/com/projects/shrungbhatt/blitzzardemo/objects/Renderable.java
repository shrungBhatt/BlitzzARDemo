package com.projects.shrungbhatt.blitzzardemo.objects;

import com.projects.shrungbhatt.blitzzardemo.utils.Interface_ResetAngle;

public abstract class Renderable {
    public float[] projectionMatrix = null;
    public float[] viewMatrix = null;

    public abstract void onSurfaceCreated();
    public abstract void onDrawFrame(float deltaX,float deltaY, Interface_ResetAngle interfaceResetAngle);
}
