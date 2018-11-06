package eidolons.libgdx;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.system.graphics.RESOLUTION;
import main.game.bf.directions.DIRECTION;
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
    public static RESOLUTION getCurrentResolution() {
        return new EnumMaster<RESOLUTION>().retrieveEnumConst(RESOLUTION.class, getCurrentResolutionString());
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

    public static float width(float y) {
        return GdxMaster.adjustWidth(y);
    }
    public static float height(float y) {
        return GdxMaster.adjustHeight(y);
    }

    public static void position(Actor actor, DIRECTION at) {
        actor.setPosition(getPosX(actor, at), getPosY(actor, at));
    }

    private static float getPosX(Actor actor, DIRECTION at) {
        switch (at) {

            case UP_RIGHT:
            case DOWN_RIGHT:
            case RIGHT:
                return right(actor);
            case DOWN:
            case UP:
                return centerWidth(actor);
        }
        return 0;
    }
    private static float getPosY(Actor actor, DIRECTION at) {
        switch (at) {

            case UP:
            case UP_LEFT:
            case UP_RIGHT:
                return top(actor);
            case LEFT:
            case RIGHT:
                return centerHeight(actor);
        }
        return 0;
    }

    public static float top(Actor actor) {
        return GdxMaster.top(actor);
    }

    public static float right(Actor actor) {
        return GdxMaster.right(actor);
    }

    public static float centerWidth(Actor actor) {
        return GdxMaster.centerWidth(actor);
    }

    public static float centerHeight(Actor actor) {
        return GdxMaster.centerHeight(actor);
    }
}
