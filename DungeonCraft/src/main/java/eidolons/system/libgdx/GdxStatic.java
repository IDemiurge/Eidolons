package eidolons.system.libgdx;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import eidolons.content.consts.GraphicData;
import eidolons.content.consts.Sprites;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.ColorMapDataSource;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import main.data.filesys.PathFinder;
import main.entity.obj.Obj;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.FileManager;

import java.util.Set;

public class GdxStatic {
    public static boolean isLwjglThread() {
        return Thread.currentThread().getName().equalsIgnoreCase("LWJGL Application");
    }

    public static int getWidth() {
        return GdxAdapter.getInstance().getCurrentScreen().getWidth();
    }

    public static void switchScreen(ScreenData screenData) {
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, screenData);
    }

    public static void switchBackScreen() {
        GdxAdapter.getInstance().getManager().switchBackScreen();

    }

    public static void illuminationUpdated(Set<ColorMapDataSource.LightDS> set) {
          GdxAdapter.getInstance().getDungeonScreen().illuminationUpdated(set);
    }

    public static void preloadAssets(Unit leader) {
        // Eidolons.onGdxThread(() -> Assets.preloadHero(party.getLeader()));
    }

    public static void waitForAnimations(Object o) {
    }

    public static boolean isImage(String s) {
        return  GdxAdapter.getInstance().getManager().isImage(s);
    }

    public static String getSpritePath(String s) {
        return  GdxAdapter.getInstance().getManager().getSpritePath(s);
    }

    public static String getVfxImgPath(String vfxPath) {
        return  GdxAdapter.getInstance().getManager().getVfxImgPath(vfxPath);
    }

    public static void setDefaultCursor() {
        GdxAdapter.getInstance().getGdxApp().setDefaultCursor();
    }

    public static void setTargetingCursor() {
        GdxAdapter.getInstance().getGdxApp().setTargetingCursor();
    }
    public static void checkHpBarReset(Obj sourceObj) {
GdxAdapter.getInstance().getManager().checkHpBarReset(sourceObj);
    }

    public static boolean isEventDisplayable(Event event) {
        return GdxAdapter.getInstance().getManager().isEventDisplayable(event);
    }

    public static boolean isEventAnimated(Event event) {
        return GdxAdapter.getInstance().getManager().isEventAnimated(event);
    }


    /*
    GdxImageMaster.cropImagePath

    GridMaster


     */
}
