package eidolons.game.battlecraft.logic.meta.universal;

import com.badlogic.gdx.Gdx;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.quest.DungeonQuest;
import eidolons.game.module.dungeoncrawl.quest.QuestMaster;
import eidolons.game.module.dungeoncrawl.quest.advanced.Quest;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import eidolons.macro.FauxMacroGame;
import eidolons.macro.entity.MacroRef;
import eidolons.macro.entity.town.Town;
import eidolons.system.audio.MusicMaster;
import eidolons.system.audio.MusicMaster.MUSIC_SCOPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.Waiter;

import java.util.ArrayList;

/**
 * Created by JustMe on 10/13/2018.
 */
public class TownMaster extends MetaGameHandler {

    protected static final java.lang.String DEFAULT_TOWN = "Strangeville"; //"Headquarters"
    ShopManager shopManager;
    QuestMaster questMaster;
    protected boolean inTown;
    protected Town town;

    public TownMaster(MetaGameMaster master) {
        super(master);
        shopManager = createShopManager();
        questMaster = createQuestMaster();
        shopManager.init();
    }

    protected QuestMaster createQuestMaster() {
        return new QuestMaster(master);
    }

    protected ShopManager createShopManager() {
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
        if (town == null) {
            town = getOrCreateTown();
        }
        getMaster().getGame().getLoop().setPaused(true);
        boolean result = enterTown(town, true);
        if (!CoreEngine.isIggDemoRunning()) // igg demo hack ?
            getMaster().getGame().getDungeonMaster().getExplorationMaster().getTimeMaster().playerWaits();
        try {
            getMaster().getGame().getManager().reset();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (!result) {
            EUtils.info("Something went wrong in this town...");
            GuiEventManager.trigger(GuiEventType.SHOW_TOWN_PANEL, null);
//            Eidolons.exitToMenu();
            return;
        }
        getMaster().getGame().getLoop().setPaused(false);
    }

    protected void updateQuests(Town town) {
        Unit hero = Eidolons.getMainHero();
        for ( Quest quest : new ArrayList<>(questMaster.getRunningQuests())) {
            if (quest.isComplete()) {
                EUtils.onConfirm(true, "You have succeeded in our quest " +
                                quest.getTitle() +
                                "? Strange, well, here is your reward of " +
                                quest.getReward().getGoldFormula() +
                                " gold pieces...",
                        false, () -> {
                            hero.getGame().getMetaMaster().getQuestMaster().questComplete(quest);
                        }, true);

                town.reputationImpact(quest.getReward().getReputationImpactComplete());
            }
        }
        town.setQuests(questMaster.getQuestsPool());

    }

    public boolean enterTown(Town town, boolean reenter) {
        if (this.town == null) { // wtf TODO
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
        inTown = true;
        GuiEventManager.trigger(GuiEventType.SHOW_TOWN_PANEL, town);
        try {
            updateQuests(town);
            town.enter(reenter);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (CoreEngine.isFullFastMode()) {
            new Thread(() -> {
                GuiEventManager.trigger(GuiEventType.QUEST_TAKEN, "To the Rescue");
                WaitMaster.WAIT(900);
                TownPanel.getActiveInstance().done();
            }, " thread").start();
        } else {
            MusicMaster.getInstance().scopeChanged(MUSIC_SCOPE.MAP);
            MusicMaster.getInstance().stopAmbience();
        }
        if (!GdxMaster.hasController(Gdx.input.getInputProcessor(), TownPanel.getActiveInstance().getStage())) {
            GuiEventManager.trigger(GuiEventType.SHOW_TOWN_PANEL, town);
            main.system.auxiliary.log.LogMaster.log(1, "Town is SCREWED, trying it again");
        }

        getGame().fireEvent(new Event(Event.STANDARD_EVENT_TYPE.TOWN_ENTERED, town.getRef()));

        boolean result =
                (boolean) WaitMaster.waitForInput(TownPanel.DONE_OPERATION);
        Waiter t = WaitMaster.getWaiters().remove(TownPanel.DONE_OPERATION);
        if (t != null) {
            main.system.auxiliary.log.LogMaster.log(1, "WAITERS ARE SCREWED ");
        }
        master.getQuestMaster().startQuests();


        MusicMaster.getInstance().scopeChanged(MUSIC_SCOPE.ATMO);
        inTown = false;
        town.exited();
        return result;
    }

    public Town getTown() {
        return town;
    }

    protected Town getOrCreateTown() {
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
        if (isFoodRequired())
            if (!Eidolons.getMainHero().hasItems("Food", n)) {
                EUtils.info("You need at least " +
                        n + " Food to travel back to " +
                        town.getName() +
                        " (Get closer to where you entered here to reduce the cost)");
                return;
            }
        int finalN = n;
        if (isFoodRequired())
            EUtils.onConfirm("Traveling back to " +
                    town.getName() +
                    " will require " + finalN +
                    "Food. Shall we get underway?", true, () -> {
                reenter(finalN, entrance);
            }, true);
        else reenter(finalN, entrance);
    }

    private void reenter(int finalN, Entrance entrance) {
        if (isFoodRequired()) {
            Eidolons.getMainHero().removeItemsFromAnywhere("Food", finalN);
            Eidolons.getMainHero().setCoordinates(entrance.getCoordinates());
            GuiEventManager.trigger(GuiEventType.UNIT_MOVED, Eidolons.getMainHero());
        }
        reenterTown();
    }

    private boolean isFoodRequired() {
        return !CoreEngine.isFastMode() && !CoreEngine.isIggDemoRunning();
    }
}
