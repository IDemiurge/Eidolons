package tests;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DefaultActionHandler;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.attach.DC_BuffObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.core.ActionInput;
import eidolons.game.core.atb.AtbMaster;
import eidolons.game.core.atb.AtbTurnManager;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.UnitDataModelSnapshot;
import main.content.DC_TYPE;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.logic.action.context.Context;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import tests.utils.JUnitUtils;

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
    public DC_ActiveObj doAction(Unit source, String name, Context context, boolean waitForCompletion) {
        DC_ActiveObj action = source.getAction(name);
        assertTrue(action != null);
       return  doAction(action, context, waitForCompletion);
    }

    public DC_ActiveObj doAction(DC_ActiveObj action,
                                  Context context, boolean waitForCompletion) {

        float readiness = 0;
        if (isCheckAtbReadiness())

            readiness = game.getAtbController().getAtbUnit(
             (Unit) context.getSourceObj()).getAtbReadiness();
        game.getLoop().actionInput(new ActionInput(action, context));
        if (waitForCompletion) {
            Object result = WaitMaster.waitForInput(
             WAIT_OPERATIONS.ACTION_COMPLETE);
            if (isCheckAtbReadiness()) {
                float newReadiness = game.getAtbController().getAtbUnit((Unit) context.getSourceObj())
                 .getAtbReadiness();
                float cost = AtbMaster.getReadinessCost(action);
                assertTrue(readiness - newReadiness == cost);
            }
            assertTrue((boolean) result);
        }
        return action;
    }

    private boolean isCheckAtbReadiness() {
//        DC_Engine.isAtbMode())
//        if (!ExplorationMaster.isExplorationOn())
        return false;
    }

    @Override
    public DC_BuffObj buff(BattleFieldObject basis, String name,
                           float duration) {
        DC_BuffObj buff= new DC_BuffObj(name, basis, duration);
        game.getManager().getBuffMaster().buffCreated(buff, basis);
        return buff;
    }

    @Override
    public void kill(Entity killer, boolean corpse, Boolean quiet, Obj... objects) {
        for (Obj sub : objects) {
            sub.kill(killer, corpse, quiet);
            if (!corpse)
                assertTrue(sub.isAnnihilated());
            else
                assertTrue(sub.isDead());
        }
    }

    public void kill(Entity killer, Obj... objects) {
        kill(killer, true, false, objects);
    }

    public void kill(Obj... objects) {
        kill(null, objects);

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
        Coordinates c = Coordinates.get(x, y);
        game.getCombatMaster().getMovementManager().move(source,
         c);
        assertTrue(source.getCoordinates().equals(c));

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

    public void endCombat() {

    }

    public void aggroClosest() {

    }

    public UnitDataModelSnapshot makeSnapshot(Unit unit) {
        return new UnitDataModelSnapshot(unit);
    }

    public void passTime(Float time) {
        JUnitUtils.log_("Time = " + game.getDungeonMaster().getExplorationMaster().getTimeMaster().getTime());
        JUnitUtils.log_("Passing " + time + " seconds");
        if (ExplorationMaster.isExplorationOn()) {
            if (game.getTurnManager() instanceof AtbTurnManager) {
                game.getDungeonMaster().getExplorationMaster().getTimeMaster().act(time);
// TODO ???                game.getDungeonMaster().getExplorationMaster().getTimeMaster().checkTimedEvents();
            }
        } else {
            ((AtbTurnManager) game.getTurnManager()).getAtbController().passTime(time);
        }

        JUnitUtils.log_("Time = " + game.getDungeonMaster().getExplorationMaster().getTimeMaster().getTime());
    }

    public void addCounters(Unit unit, COUNTER counter, int i) {
        unit.addCounter(counter.getName(), i + "");
    }


    public DC_ActiveObj defaultAttack(Unit unit, BattleFieldObject enemy) {
        DC_ActiveObj action = DefaultActionHandler.getPreferredAttackAction(unit, enemy);
        doAction(action, new Context(unit, enemy), true);
        return action;


    }

    @Override
    public void resetAll() {
        game.getManager().reset();

    }

    @Override
    public void newRound() {
        game.getManager().getStateManager().newRound();
    }

    public void resetUnit(Unit unit) {
        unit.fullReset(game);

    }

    public void refreshVisibility() {
        game.getVisionMaster().refresh();
    }

    public DC_WeaponObj equipWeapon(String itemName) {
       return equipWeapon(itemName, ITEM_SLOT.MAIN_HAND);
    }
        public DC_WeaponObj equipWeapon(String itemName, ITEM_SLOT slot) {
        ObjType sub = DataManager.getType(itemName, DC_TYPE.WEAPONS);
        if (sub == null) {
            main.system.auxiliary.log.LogMaster.log(1, "No weapon " + itemName);
            return null ;
        }

        DC_WeaponObj weapon = new DC_WeaponObj(sub, getHero());
        getHero().equip(weapon, slot);

        return weapon;
    }

    public Unit getHero() {
        return game.getManager().getMainHero();
    }
}
