package eidolons.libgdx;

import eidolons.system.graphics.RESOLUTION;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.LOG_CHANNEL;

import java.awt.*;

/**
 * Created by JustMe on 4/16/2018.
 */
public class GDX {
    public static float size(float s){
        return GdxMaster.adjustSize(s);
    }
    public static float size(float s,float coef){
        return GdxMaster.adjustSize(s, coef);
    }

    public static String getDisplayResolutionString() {
        Toolkit toolkit = Toolkit.getDefaultToolkit ();
        Dimension dim = toolkit.getScreenSize();
        String string = "_" +((int) dim.getWidth()) + "x" + ((int) dim.getHeight());
        RESOLUTION res = new EnumMaster<RESOLUTION>().retrieveEnumConst(RESOLUTION.class, string);
        if (res==null ){
            main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.ERROR_CRITICAL,"FAILED TO FIND RESOLUTION: "+string );
            return "blast";
        }
        return res.name();
    }
    public static RESOLUTION getDisplayResolution() {
        return new EnumMaster<RESOLUTION>().retrieveEnumConst(RESOLUTION.class, getDisplayResolutionString());
    }

    public static String getCurrentResolutionString() {

        String string = "_" +((int) GdxMaster.getWidth()) + "x" + ((int) GdxMaster.getHeight());
        RESOLUTION res = new EnumMaster<RESOLUTION>().retrieveEnumConst(RESOLUTION.class, string);
        if (res==null ){
            main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.ERROR_CRITICAL,"FAILED TO FIND RESOLUTION: "+string );
            return "blast";
        }
        return res.name();
    }
}
