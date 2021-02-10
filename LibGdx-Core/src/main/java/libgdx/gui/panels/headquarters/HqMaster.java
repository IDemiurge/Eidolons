package libgdx.gui.panels.headquarters;

import eidolons.entity.SimCache;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import main.content.enums.system.MetaEnums;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.Flags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by JustMe on 4/16/2018.
 */
public class HqMaster {

    public static final float TAB_WIDTH = 440;
    public static final float TAB_HEIGHT = 732;
    private static final MetaEnums.WORKSPACE_GROUP FILTER_GROUP = MetaEnums.WORKSPACE_GROUP.COMPLETE;
    private static final MetaEnums.WORKSPACE_GROUP FILTER_GROUP_DEV = MetaEnums.WORKSPACE_GROUP.IGG_TODO;
    private static final MetaEnums.WORKSPACE_GROUP TEST_GROUP = MetaEnums.WORKSPACE_GROUP.IGG_TESTING;
    private static Unit activeHero;

    public static void closeHqPanel() {
        GuiEventManager.trigger(GuiEventType.SHOW_HQ_SCREEN, null);
        GuiEventManager.trigger(GuiEventType.GAME_RESUMED, null);
    }

    public static void tab(String spells) {
        try {
            HqPanel.getActiveInstance().hqTabs.tabSelected(spells);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
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
        if (list.isEmpty()) {
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
        return SimCache.getInstance();
    }

    public static void setActiveHero(Unit activeHero) {
        HqMaster.activeHero = activeHero;
    }

    public static Unit getActiveHero() {
        return activeHero;
    }


    public static boolean isDirty() {
        if (HqDataMaster.isSimulationOff())
            return false;
        for (HqHeroDataSource sub : HqPanel.getActiveInstance().getHeroes()) {
            if (!sub.getEntity().getModificationList().isEmpty())
                return true;
        }
        return false;
    }

    public static void filterContent(Collection<ObjType> list) {
        if (Flags.isIDE())
            list.removeIf(t -> t.getWorkspaceGroup() != FILTER_GROUP
                    && t.getWorkspaceGroup() != FILTER_GROUP_DEV);
        else
            list.removeIf(t -> t.getWorkspaceGroup() != FILTER_GROUP);
    }

    public static void filterTestContent(List<ObjType> list) {
        list.removeIf(t -> t.getWorkspaceGroup() != TEST_GROUP);
    }

    public static boolean isContentDisplayable(Entity entity) {

        if (entity.getWorkspaceGroup() == FILTER_GROUP) {
            return true;
        }
        if (Flags.isIDE())
            return entity.getWorkspaceGroup() == FILTER_GROUP_DEV;

        return false;
    }

    public static boolean isDisabled(Entity type) {
        if (type.getWorkspaceGroup() == MetaEnums.WORKSPACE_GROUP.COMPLETE) {
            return true;
        }
        return type.getWorkspaceGroup() == MetaEnums.WORKSPACE_GROUP.IGG_TODO;
    }

}
