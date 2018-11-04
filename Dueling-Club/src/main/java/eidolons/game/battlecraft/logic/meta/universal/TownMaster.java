package eidolons.game.battlecraft.logic.meta.universal;

import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.quest.QuestMaster;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import eidolons.macro.FauxMacroGame;
import eidolons.macro.entity.MacroRef;
import eidolons.macro.entity.town.Town;
import eidolons.system.audio.MusicMaster;
import eidolons.system.audio.MusicMaster.MUSIC_SCOPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.Waiter;

/**
 * Created by JustMe on 10/13/2018.
 */
public class TownMaster extends MetaGameHandler {

    private static final java.lang.String DEFAULT_TOWN = "Strangeville"; //"Headquarters"
    ShopManager shopManager;
    QuestMaster questMaster;
    private boolean inTown;
    private Town town;

    public TownMaster(MetaGameMaster master) {
        super(master);
        shopManager = createShopManager();
        questMaster = new QuestMaster(master);
        shopManager.init();
    }

    private ShopManager createShopManager() {
        return new ShopManager(master);
    }
    //    LibraryManager

    public boolean initTownPhase() {
        try {
            Town town = getOrCreateTown();
            town.setQuests(questMaster.getQuestsPool());
            return enterTown(town, false);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return false;
    }

    public void reenterTown() {
        getMaster().getGame().getLoop().setPaused(true);
        boolean result = enterTown(town, true);
        if (!result) {
            EUtils.info("Something went wrong in this town...");
            Eidolons.exitToMenu();
            return;
        }
        getMaster().getGame().getLoop().setPaused(true);
    }

    public boolean enterTown(Town town, boolean reenter) {
        if (this.town == null) {
            try {
                SkillMaster.initMasteryRanks(
                 Eidolons.getMainHero());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            try {
                Eidolons.getMainHero().reset();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        this.town = town;
        town.enter(reenter);
        GuiEventManager.trigger(GuiEventType.SHOW_TOWN_PANEL, town);
        inTown = true;
        if (CoreEngine.isFullFastMode()) {
            new Thread(() -> {
                GuiEventManager.trigger(GuiEventType.QUEST_TAKEN, "To the Rescue");
                WaitMaster.WAIT(900);
                TownPanel.getActiveInstance().done();
            }, " thread").start();
        } else {
            MusicMaster.getInstance().scopeChanged(MUSIC_SCOPE.MAP);
        }
        boolean result =
         (boolean) WaitMaster.waitForInput(TownPanel.DONE_OPERATION);
        Waiter t = WaitMaster.getWaiters().remove(TownPanel.DONE_OPERATION);
        if (t != null) {
            main.system.auxiliary.log.LogMaster.log(1, "WAITERS ARE SCREWED ");
        }
//        if (reenter)
            MusicMaster.getInstance().scopeChanged(MUSIC_SCOPE.ATMO);
        inTown = false;
        town.exited();
        return result;
    }

    public Town getTown() {
        return town;
    }

    private Town getOrCreateTown() {
        Town town = getGame().town; //TODO [refactor] !!!
        if (town != null) {
            return town;
        }
        ObjType type = DataManager.getType(DEFAULT_TOWN, MACRO_OBJ_TYPES.TOWN);
        town = new Town(new FauxMacroGame()
         //         FauxMacroGame.getInstance() not safe?
         , type, MacroRef.getMainRef());
        getGame().town = town;
        return town;
    }


    public boolean isInTown() {
        return inTown;
    }

    public QuestMaster getQuestMaster() {
        return questMaster;
    }

    public void tryReenterTown() {
        if (!ExplorationMaster.isExplorationOn()) {
            EUtils.info("You cannot travel back while in battle!");
            return;
        }
        Entrance entrance = ((Location) master.getGame().getDungeonMaster().
         getDungeonWrapper()).getMainEntrance();
        int dst = Eidolons.getMainHero().getCoordinates().dst(
         entrance.getOriginalCoordinates());
        int n = 2 + dst / 8;
        if (master.getMetaDataManager().getMetaGame() instanceof ScenarioMeta) {
            n += 2 * ((ScenarioMeta) master.getMetaDataManager().getMetaGame()).getMissionIndex();
        }
        if (!Eidolons.getMainHero().hasItems("Food", n)) {
            EUtils.info("You need at least " +
             n + " Food to travel back to " +
             town.getName() +
             " (Get closer to where you entered here to reduce the cost)");
            return;
        }
        int finalN = n;
        EUtils.onConfirm("Traveling back to " +
         town.getName() +
         " will require " + finalN +
         "Food. Shall we get underway?", true, () -> {
            Eidolons.getMainHero().removeItemsFromAnywhere("Food", finalN);
            reenterTown();
        }, true);
    }
}
