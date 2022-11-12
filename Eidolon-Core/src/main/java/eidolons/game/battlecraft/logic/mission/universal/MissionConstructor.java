package eidolons.game.battlecraft.logic.mission.universal;

/**
 * Created by JustMe on 5/7/2017.
 */
public class MissionConstructor<E extends DungeonSequence> extends MissionHandler<E> {
    //based on difficulty, adjusts unit data

    public MissionConstructor(MissionMaster<E> master) {
        super(master);
    }

    public void init() {

    }

}
