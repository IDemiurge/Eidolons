package tests;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.attach.DC_BuffObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.core.ActionInput;
import eidolons.game.core.game.DC_Game;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.logic.action.context.Context;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 4/6/2018.
 */
public class DcHelper implements JUnitHelper {
    DC_Game game;

    public DcHelper(DC_Game game) {
        this.game = game;
    }

    @Override
    public Unit unit(String name, int x, int y, boolean mine) {
        return (Unit) object(name, x, y, game.getPlayer(mine), DC_TYPE.UNITS);
    }

    @Override
    public Unit hero(String name, int x, int y, boolean mine) {
        return (Unit) object(name, x, y, game.getPlayer(mine), DC_TYPE.CHARS);
    }

    @Override
    public Structure object(String name, int x, int y) {
        return (Structure) object(name, x, y, DC_Player.NEUTRAL, DC_TYPE.BF_OBJ);
    }

    @Override
    public void doAction(Unit source, String name, Context context, boolean waitForCompletion) {
        DC_ActiveObj action = source.getAction(name);
        assertTrue(action != null);
        doAction(action, context, waitForCompletion);
    }

    private void doAction(DC_ActiveObj action,
                          Context context, boolean waitForCompletion) {
        WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_INPUT,
         new ActionInput(action, context));
        if (waitForCompletion) {
            Object result = WaitMaster.waitForInput(
             WAIT_OPERATIONS.ACTION_COMPLETE);
            assertTrue(result == new Boolean(true));
        }
    }

    @Override
    public DC_BuffObj buff(BattleFieldObject basis, String name) {
        return null;
    }

    @Override
    public void kill(Entity killer, boolean corpse, Boolean quiet, Obj... objects) {
        for (Obj sub : objects) {
            sub.kill(killer, corpse, quiet);
        }
    }

    public void kill(Entity killer, Obj... objects) {
        for (Obj sub : objects) {
            sub.kill(killer, true, false);
        }
    }

    public void kill(Obj... objects) {
        for (Obj sub : objects) {
            sub.kill(sub, true, false);
        }
    }

    public BattleFieldObject object(String name, int x, int y, DC_Player owner,
                                    DC_TYPE TYPE) {
        ObjType type = DataManager.getType(name, TYPE);
        assertTrue(type != null);
        return (BattleFieldObject) game.getManager().getObjCreator().createUnit(type, x, y,
         owner, new Ref(game));
    }

    @Override
    public void move(Obj source, int x, int y) {
        game.getCombatMaster().getMovementManager().move(source,
         new Coordinates(x, y));


    }

    @Override
    public void turn(Unit source, FACING_DIRECTION newDirection) {
        source.setFacing(newDirection);

    }

    @Override
    public void turn(Unit source, boolean clockwise, boolean asAction) {
        if (asAction) {
            doAction(source.getTurnAction(clockwise),
             new Context(source.getRef()), true);
        } else {
            source.setFacing(FacingMaster.rotate(source.getFacing(), clockwise));
        }
    }


    @Override
    public void reset() {
        game.getManager().reset();

    }

    @Override
    public void newRound() {
        game.getManager().getStateManager().newRound();
    }
}
