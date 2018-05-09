package tests;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.attach.DC_BuffObj;
import eidolons.entity.obj.unit.Unit;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.logic.action.context.Context;

/**
 * Created by JustMe on 4/6/2018.
 */
public interface JUnitHelper {

       Unit unit(String name, int x, int y, boolean mine);
       Unit hero(String name, int x, int y, boolean mine);
       Structure object(String name, int x, int y);
       DC_ActiveObj doAction(Unit source, String name, Context context, boolean waitForFinish);
       DC_BuffObj buff(BattleFieldObject basis, String name, float defaultDuration);

       void kill(Entity killer, boolean leaveCorpse, Boolean quiet, Obj... objects);
       void move(Obj source, int x, int y);
       void turn(Unit source, FACING_DIRECTION newDirection);
       void turn(Unit source, boolean clockwise, boolean asAction);

       void resetAll();
       void newRound();
//       void event();
//       void check();
}
