package eidolons.game.battlecraft.logic.battlefield.vision.mapper;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;

/**
 * Created by JustMe on 3/30/2018.
 */
public class ObjVisionMapper<T> extends GenericMapper<BattleFieldObject, T> {

    public T get(DC_Obj obj) {
       return get(obj.getGame().getVisionMaster().getSeeingUnit(), obj);
    }
    public void set(DC_Obj obj, T t) {
        set(obj.getGame().getVisionMaster().getSeeingUnit(), obj, t);
    }
    public T getForMe(DC_Obj obj) {
        return get(obj.getGame().getManager().getMainHero(), obj);
    }
    public void setForMe(DC_Obj obj, T t) {
        set(obj.getGame().getManager().getMainHero(), obj, t);
    }
}
