package main.game.battlecraft.logic.battle;

/**
 * Created by JustMe on 5/7/2017.
 */
public class BattleOptionManager<E extends Battle> extends  BattleHandler<E>  {

    BattleOptions options;


    public BattleOptionManager(BattleMaster<E> master) {
        super(master);
    }

//    public int getBattleLevel() {
//        battleLevel = 0;
//
//        List<? extends Obj> units = new LinkedList<>(game.getPlayer(true).getControlledUnits());
//        if (units.isEmpty() && game.getParty() != null) {
//            units = new LinkedList<>(game.getParty().getMembers());
//        }
//        for (Obj unit : units) {
//            battleLevel += unit.getIntParam(PARAMS.POWER);
//        }
//
//        return battleLevel;
//    }

    public BattleOptions getOptions() {
        return options;
    }
}
