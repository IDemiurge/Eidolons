package eidolons.game.netherflame.main.misc;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.advanced.engagement.EngageEvent;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.module.cinematic.CinematicLib;
import eidolons.game.module.dungeoncrawl.quest.DungeonQuest;
import eidolons.game.netherflame.main.NF_PartyManager;
import eidolons.system.text.tips.StdTips;
import eidolons.system.text.tips.TipMessageMaster;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

public class SoloPartyManager extends NF_PartyManager {
    public static final boolean TEST_MODE = true;

    public SoloPartyManager(MetaGameMaster master) {
        super(master);
    }

    @Override
    public boolean deathEndsGame() {
        return true;
    }

    @Override
    protected ObjType getPartyType() {
        return DataManager.getType("Anphis", DC_TYPE.PARTY);
    }

    @Override
    protected Coordinates getRespawnCoordinates(ObjType type) {
        return super.getRespawnCoordinates(type);
    }

    @Override
    public void gameStarted() {
        super.gameStarted();
        ObjType type = DataManager.getType("Third Passage", DC_TYPE.QUEST);
        if (master.isCustomQuestsEnabled()) {
            DungeonQuest quest = new DungeonQuest(type);
            getMaster().getQuestMaster().questTaken(quest, type);
            quest.update();
        }
    }

    @Override
    public boolean heroUnconscious(Unit hero) {
        //instead of all unconscious mechanics
        if (hero != Eidolons.getMainHero()) {
            return false;
        }

        CinematicLib.run(CinematicLib.StdCinematic.UNCONSCIOUS_BEFORE);
        getGame().getLogManager().log(hero.getName() + " falls...");

        TipMessageMaster.tip(StdTips.fallen, () -> {
            Coordinates respawnCoordinates = getRespawnCoordinates(null);
            hero.setCoordinates(respawnCoordinates);
            hero.cleanReset();
            getGame().engageEvent(EngageEvent.ENGAGE_EVENT.combat_end);
            //TODO big text?
            getGame().getMovementManager().moved(hero, true);

            getGame().getLogManager().log("... and rises again.");
            getGame().getLogManager().log("Endurance remaining: " + findMainHero().getIntParam(PARAMS.C_ENDURANCE));
            CinematicLib.run(CinematicLib.StdCinematic.UNCONSCIOUS_AFTER, hero);
        });


        return true;
    }
}
