package eidolons.game.battlecraft.logic.battlefield.vision.mapper;

import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;

/**
 * Created by JustMe on 4/1/2018.
 */
public class PlayerMapper<T> extends  GenericMapper<DC_Player, T> {

    public T get(DC_Obj obj) {
        return get(obj.getGame().getVisionMaster().getSeeingUnit().getOwner(), obj);
    }
    public void set(DC_Obj obj, T t) {
        set(obj.getGame().getVisionMaster().getSeeingUnit().getOwner(), obj, t);
    }
    public T getForMe(DC_Obj obj) {
        return get(obj.getGame().getPlayer(true), obj);
    }
    public void setForMe(DC_Obj obj, T t) {
        set(obj.getGame().getPlayer(true), obj, t);
    }
//    public void log(O unit) {
//        log(unit, map.getVar(unit).keySet().toArray(new DC_Obj[map.getVar(unit).size()]));
//    }
}

