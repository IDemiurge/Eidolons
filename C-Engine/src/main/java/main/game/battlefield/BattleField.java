package main.game.battlefield;

import main.entity.obj.Obj;

/**
 * Created by JustMe on 11/22/2016.
 */
public interface BattleField {

    void selectInfoObj(Obj obj, boolean b);

    void deselectInfoObj(Obj selectedObj, boolean b);

    void selectActiveObj(Obj obj, boolean b);

    void deselectActiveObj(Obj selectedObj, boolean b);


}
