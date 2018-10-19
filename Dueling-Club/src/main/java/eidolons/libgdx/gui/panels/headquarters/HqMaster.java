package eidolons.libgdx.gui.panels.headquarters;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 4/16/2018.
 */
public class HqMaster {

    public static final float TAB_WIDTH = 440;
    public static final float TAB_HEIGHT = 732;
    private static SimCache simCache = new SimCache();
    private static Unit activeHero;

    public static void closeHqPanel() {
        GuiEventManager.trigger(GuiEventType.SHOW_HQ_SCREEN, null);
        GuiEventManager.trigger(GuiEventType.GAME_RESUMED, null);
    }

    public static void openHqPanel() {

        List<Unit> members = new ArrayList<>(DC_Game.game.getMetaMaster().getPartyManager().getParty().getMembers());
//        members.add(DC_Game.game.getManager().getMainHero());
        List<HqHeroDataSource> list = new ArrayList<>();

        for (Unit sub : members) {
            list.add(new HqHeroDataSource(
             HqDataMaster.getOrCreateInstance(
             sub).getHeroModel()));

        }
        if (list.isEmpty()){
            list.add(new HqHeroDataSource(
             HqDataMaster.getOrCreateInstance(
              Eidolons.getMainHero()).getHeroModel()));
        }
        GuiEventManager.trigger(GuiEventType.SHOW_HQ_SCREEN, list);
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, null);


    }

    public static void toggleHqPanel() {
        if (HqPanel.getActiveInstance() != null) {
            closeHqPanel();
        } else {
            openHqPanel();
        }

    }

    public static SimCache getSimCache() {
        return simCache;
    }

    public static void setSimCache(SimCache simCache) {
        HqMaster.simCache = simCache;
    }

    public static void setActiveHero(Unit activeHero) {
        HqMaster.activeHero = activeHero;
    }

    public static Unit getActiveHero() {
        return activeHero;
    }


    public static boolean isDirty() {
        for (HqHeroDataSource sub: HqPanel.getActiveInstance().getHeroes()){
            if (!sub.getEntity().getModificationList().isEmpty())
                return true;
        }
        return false;
    }
}
