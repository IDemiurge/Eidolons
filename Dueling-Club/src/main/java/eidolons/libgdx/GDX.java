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

    public static String getDisplayResolution() {
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
}
