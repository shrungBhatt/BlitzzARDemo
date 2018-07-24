package com.projects.shrungbhatt.blitzzardemo.utils;

import android.content.Context;

import min3d.core.Object3d;
import min3d.core.Object3dContainer;
import min3d.parser.IParser;
import min3d.parser.Parser;

public class Util {

    public static Object3d getObject3d(Context context, String resourceName){
        IParser myParser = Parser.createParser(Parser.Type.OBJ, context.getResources(),
                "com.projects.shrungbhatt.blitzzardemo:raw/" + resourceName, false);
        myParser.parse();
        Object3dContainer faceObject3D = myParser.getParsedObject();

        return faceObject3D.getChildAt(0);
    }

}
