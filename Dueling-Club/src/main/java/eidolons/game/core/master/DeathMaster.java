package eidolons.game.core.master;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.meta.universal.PartyHelper;
import eidolons.game.battlecraft.rules.combat.damage.DamageCalculator;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.DungeonCrawler;
import eidolons.game.module.herocreator.logic.HeroLevelManager;
import eidolons.libgdx.anims.std.HitAnim;
import eidolons.system.audio.DC_SoundMaster;
import main.ability.AbilityObj;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums.STD_BOOLS;
import main.entity.EntityCheckMaster;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Attachment;
import main.entity.obj.Obj;
import main.game.logic.action.context.Context;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.SpecialLogger;
import main.system.datatypes.DequeImpl;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 2/16/2017.
 */
public class DeathMaster extends Master {

    public DeathMaster(DC_Game game) {
        super(game);
    }


    public void unitAnnihilated(Obj _killed, Obj _killer) {

        String message = null;
        if (_killed == _killer) {
            message = _killed + " annihilates";// + _killed.getInfoString();
        } else
            message = _killed + " is annihilated by " + _killer;
        SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.MAIN, message);
        getGame().getLogManager().log(message);
        getGame().getGraveyardManager().removeCorpse(_killed);
        _killed.setAnnihilated(true);

        getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_ANNIHILATED,
         new Context(_killer, _killed)));

        GuiEventManager.trigger(GuiEventType.SHOW_SPRITE,
                _killed.getCoordinates(),
                HitAnim.HIT.BONE_CRACK.spritePath);

        getGameMaster().remove(_killed, true);
//	TODO 	getGame().getDroppedItemManager().remove((DC_HeroObj) _killed, item);

    }

    private void checkXpGranted(Obj killed, Obj killer) {
        if ( killed ==  killer)
            return;
        if (killed instanceof Unit) {
            if (!((Unit) killed).isHostileTo((DC_Player) killer.getOwner())) {
                return;
            }
        } else
        {
            if (killed.checkBool(STD_BOOLS.FAUX)){
                if (EntityCheckMaster.isWall(killed)) {
                    DungeonCrawler.secretFound(killed, (Unit) killer);
                }
            }
            return;
        }
            Unit unit = (Unit) killed;

            HeroLevelManager.addXpForKill(unit,(Unit) killer );

    }

    public void unitDies(DC_ActiveObj activeObj, Obj _killed, Obj _killer, boolean leaveCorpse, boolean quietly) {
        if (_killed.isDead())
            return;
        String message = null;
        if (_killed == _killer) {
            message = _killed + " dies ";// + _killed.getInfoString();
        } else
        {
            message = _killed + " killed by " + _killer + " with " + activeObj;


        }
        if (!quietly)
            checkXpGranted(_killed, _killer);
        SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.MAIN, message);
        _killed.setDead(true);
        BattleFieldObject killed = (BattleFieldObject) _killed;
        BattleFieldObject killer = (BattleFieldObject) _killer;
        Ref ref = Ref.getCopy(killed.getRef());
        ref.setSource(killer.getId());
        ref.setTarget(killed.getId());


        for (AbilityObj abil : killed.getPassives()) {
            abil.kill();
        }
        if (killed.getBuffs() != null) {
            for (Attachment attach : killed.getBuffs()) {
                if (!attach.isRetainAfterDeath()) {
                    getState().getAttachmentsMap().get(killed).remove(attach);
                    attach.remove();

                }
            }
        }

        if (!quietly) {
            Ref REF = Ref.getCopy(killer.getRef());
            REF.setTarget(killed.getId());
            REF.setSource(killer.getId());
            if (!getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED, REF))){
                return;
            }
            if (activeObj != null)
                REF.setObj(KEYS.ACTIVE, activeObj);
            if (killed instanceof Unit) {
                getGame().getRules().getMoraleKillingRule().unitDied((Unit) killed,
                 killer.getRef().getAnimationActive());
            }

            DC_SoundMaster.playEffectSound(SOUNDS.DEATH, killed);

            game.getLogManager().logDeath(killed, killer);
            getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED, REF));
            game.getLogManager().doneLogEntryNode();
        } else {
            GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL, killed);
        }
        if (!leaveCorpse) {
            // leave a *ghost*?
            // destroy items?
        } else {

            if (killed instanceof Unit) {
                try {
                    getGame().getDroppedItemManager().dropDead((Unit) killed);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            try {
                getGame().getGraveyardManager().unitDies(killed);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }


    public void checkForDeaths() {
        for (DC_TYPE TYPE : C_OBJ_TYPE.BF_OBJ.getTypes()) {
            for (Obj unit : getState().getObjMaps().get(TYPE).values()) {
                if (!unit.isDead()) {
                    if (DamageCalculator.isDead((BattleFieldObject) unit)) {
                        unit.kill(unit, true, false);
                    }
                }
            }
        }
    }


    public void killAllUnits(boolean retainPlayerParty) {
        killAllUnits(false, retainPlayerParty,
         true);
    }

    public void killAllUnits(boolean removeBfObjects, boolean retainPlayerParty, boolean quiet) {
        DequeImpl<BattleFieldObject> list = new DequeImpl();
        List<BattleFieldObject> toRemove = new ArrayList<>();
        list.addAll(getGame().getUnits());
        if (removeBfObjects) {
            list.addAll(getGame().getStructures());
        }
        for (BattleFieldObject unit : list) {

            if (retainPlayerParty) {
                if (PartyHelper.getParty() != null) {
                    if (PartyHelper.getParty().getMembers().contains(unit)) {
                        continue;
                    }
                }
            }
            unit.kill(unit, false, quiet);
            toRemove.add(unit);

        }
        for (BattleFieldObject unit : toRemove) {
            getGame().remove(unit);
        }
    }

    public void killAll(boolean retainSelected) {
        for (Unit unit : getGame().getUnits()) {
            if (retainSelected) {
                if (unit.equals(getGame().getManager().getActiveObj())) {
                    continue;
                }
            }
            killUnitQuietly(unit);
        }
        getGameManager().reset();
        getGameManager().refreshAll();
        WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_COMPLETE, true);
    }

    public void killUnitQuietly(Unit unit) {
        unit.kill(unit, false, true);

    }
}
