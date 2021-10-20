package eidolons.game.battlecraft.logic.meta.universal;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Core;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

import java.util.List;

public abstract class SpawnManager extends MetaGameHandler  {
    public static final WaitMaster.WAIT_OPERATIONS WAIT_OPERATION =
            WaitMaster.WAIT_OPERATIONS.HERO_SELECTION;
    public static final String NEW_HERO_PARTY = "Your Party";
    protected static String selectedHero;

    public SpawnManager(MetaGameMaster master) {
        super(master);
    }

    public static void setSelectedHero(String selectedHero) {
        SpawnManager.selectedHero = selectedHero;
    }
    // @Override
    // protected String chooseHero(List<String> members) {
    //     if (isWaitForGdx())
    //         WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED);
    //     return super.chooseHero(members);
    // }
    protected String chooseHero(List<String> members) {
        GuiEventManager.trigger(
         GuiEventType.SHOW_SELECTION_PANEL, DataManager.toTypeList(members, DC_TYPE.CHARS));

        selectedHero = (String) WaitMaster.
         waitForInput( WAIT_OPERATION);
        main.system.auxiliary.log.LogMaster.log(1, "+++++++++selectedHero = " + selectedHero);
        return selectedHero;
    }

    public void gameStarted() {
        Unit hero = findMainHero();
        hero.getOwner().setHeroObj(hero);
        hero.setMainHero(true);
        Core.setMainHero(hero);
        getMetaGame().setRestarted(false);
    }

    protected abstract Unit findMainHero();

    public void preStart() {

    }

    public void heroSelected(Unit newHero) {
        /**
         * set as main
         * spawn
         * update chain
         *
         */
    }


    public String checkLeveledHeroVersionNeeded(String heroName) {
        return heroName;
    }

    public boolean deathEndsGame() {
        return true;
    }

    public boolean heroUnconscious(Unit unit) {
        return false;
    }
}
