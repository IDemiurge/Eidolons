package eidolons.system.libgdx;

import eidolons.content.consts.VisualEnums;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.ColorMapDataSource;
import eidolons.system.libgdx.datasource.ScreenData;
import eidolons.system.libgdx.wrapper.VectorGdx;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;

import java.util.Set;

import static main.system.GuiEventType.CREATE_FLOATING_TEXT;

public class GdxStatic {
    public static boolean isLwjglThread() {
        return Thread.currentThread().getName().equalsIgnoreCase("LWJGL Application");
    }

    public static int getWidth() {
        return GdxAdapter.getInstance().getCurrentScreen().getWidth();
    }

    public static int getHeight() {
        return GdxAdapter.getInstance().getCurrentScreen().getHeight();
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

    public static void setDefaultCursor() {
        GdxAdapter.getInstance().getGdxApp().setDefaultCursor();
    }

    public static void setTargetingCursor() {
        GdxAdapter.getInstance().getGdxApp().setTargetingCursor();
    }
    public static void checkHpBarReset(Obj sourceObj) {
        if (!CoreEngine.isGraphicsOff())
            GdxAdapter.getInstance().getManager().checkHpBarReset(sourceObj);
    }

    public static boolean isEventDisplayable(Event event) {
        return GdxAdapter.getInstance().getManager().isEventDisplayable(event);
    }

    public static boolean isEventAnimated(Event event) {
        return GdxAdapter.getInstance().getManager().isEventAnimated(event);
    }

    public static Coordinates getCameraCenter() {
        return GdxAdapter.getInstance().getGridManager().getCameraCenter();
    }

    public static VectorGdx getCenteredPos(Coordinates coordinate) {
        return GdxAdapter.getInstance().getGridManager(). getCenteredPos(coordinate);
    }

    public static void floatingText(VisualEnums.TEXT_CASES cases, String s, Entity unit) {
        GuiEventManager.triggerWithParams(CREATE_FLOATING_TEXT, cases, s, unit);
    }


    /*
    GdxImageMaster.cropImagePath

    GridMaster


     */
}
