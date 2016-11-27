package main.game.battlefield;

import main.entity.obj.Obj;

import java.util.Set;

/**
 * Created by JustMe on 11/22/2016.
 */
public interface BattleField {
    void createObj(Obj obj);

    void selectInfoObj(Obj obj, boolean b);

    void deselectInfoObj(Obj selectedObj, boolean b);

    void selectActiveObj(Obj obj, boolean b);

    void deselectActiveObj(Obj selectedObj, boolean b);

    void moveBattleFieldObj(Obj obj, int x, int y);

    void remove(Obj unit);

    void highlight(Set<Obj> set);

    void highlightsOff();
}
