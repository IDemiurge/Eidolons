package main.game.battlecraft.logic.battle.universal;

/**
 * Created by JustMe on 5/7/2017.
 */
public class BattleConstructor<E extends Battle> extends BattleHandler<E>{
    //based on difficulty, adjusts unit data

    public BattleConstructor(BattleMaster<E> master) {
        super(master);
    }

    public void init() {

    }

}
