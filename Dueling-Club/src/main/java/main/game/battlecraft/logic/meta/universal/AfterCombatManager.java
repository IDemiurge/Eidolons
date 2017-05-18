package main.game.battlecraft.logic.meta.universal;

/**
 * Created by JustMe on 5/8/2017.
 */
public abstract class AfterCombatManager<E extends MetaGame> extends MetaGameHandler<E>{
    public AfterCombatManager(MetaGameMaster master) {
        super(master);
    }
}
