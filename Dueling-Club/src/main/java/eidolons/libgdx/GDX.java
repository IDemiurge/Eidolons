package eidolons.libgdx;

import eidolons.system.graphics.RESOLUTION;
import main.system.auxiliary.EnumMaster;

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
        String string = ((int) dim.getWidth()) + "_" + ((int) dim.getHeight());
        RESOLUTION res = new EnumMaster<RESOLUTION>().retrieveEnumConst(RESOLUTION.class, string);
        if (res==null ){
            return "blast";
        }
        return res.name();
    }
}
