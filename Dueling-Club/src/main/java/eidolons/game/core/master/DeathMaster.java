package eidolons.game.core.master;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.universal.PartyHelper;
import eidolons.game.battlecraft.rules.combat.damage.DamageCalculator;
import eidolons.game.core.game.DC_Game;
import eidolons.system.audio.DC_SoundMaster;
import main.ability.AbilityObj;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
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
import main.system.graphics.ANIM;
import main.system.graphics.AnimPhase;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;
import main.system.text.LogEntryNode;
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
            message = _killed + " annihilates ";// + _killed.getInfoString();
        } else
            message = _killed + " annihilated by " + _killer;
        SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.MAIN, message);
        getGame().getGraveyardManager().removeCorpse(_killed);
        _killed.setAnnihilated(true);
        getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_ANNIHILATED,
         new Context(_killer, _killed)));
//	TODO 	getGame().getDroppedItemManager().remove((DC_HeroObj) _killed, item);

    }

    public void unitDies(DC_ActiveObj activeObj, Obj _killed, Obj _killer, boolean leaveCorpse, boolean quietly) {
        if (_killed.isDead())
            return;
        String message = null;
        if (_killed == _killer) {
            message = _killed + " dies ";// + _killed.getInfoString();
        } else
            message = _killed + " killed by " + _killer + " with " + activeObj;

        SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.MAIN, message);
        _killed.setDead(true);
        BattleFieldObject killed = (BattleFieldObject) _killed;
        BattleFieldObject killer = (BattleFieldObject) _killer;
        Ref ref = Ref.getCopy(killed.getRef());
        ref.setSource(killer.getId());
        ref.setTarget(killed.getId());

        // List<Attachment> attachments = getState().getAttachmentsMap()
        // .getOrCreate(killed);

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

//        getGame().getMaster().remove(killed); TODO why was it ever here?! killed doesn't mean removed...
        // getGame().getBattleField().remove(killed); // TODO GRAVEYARD
        if (!quietly) {
            Ref REF = Ref.getCopy(killer.getRef());
            REF.setTarget(killed.getId());
            REF.setSource(killer.getId());
            if (activeObj != null)
                REF.setObj(KEYS.ACTIVE, activeObj);
            if (killed instanceof Unit) {
                getGame().getRules().getMoraleKillingRule().unitDied((Unit) killed,
                 killer.getRef().getAnimationActive());
            }

            LogEntryNode node = game.getLogManager().newLogEntryNode(ENTRY_TYPE.DEATH, killed);

            if (killer.getRef().getAnimationActive() != null) {
                ANIM animation = killer.getRef().getAnimationActive().getAnimation();
                if (animation != null) {
                    animation.addPhase(new AnimPhase(PHASE_TYPE.DEATH, killer, killed));
                    node.setLinkedAnimation(animation);
                }
            }

            DC_SoundMaster.playEffectSound(SOUNDS.DEATH, killed);

            game.getLogManager().logDeath(killed, killer);
            getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED, REF));
            game.getLogManager().doneLogEntryNode();
        } else {
            GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL, killed);
        }
        // refreshAll();
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
//                if (unit.isMine())
//                    continue;
            }
            unit.kill(unit, false, quiet);
            toRemove.add(unit);

        }
        //if (remove)
        for (BattleFieldObject unit : toRemove) {
            getGame().remove(unit);
        }
        // reset();
        // refreshAll();
        // WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_COMPLETE, true);
    }

    public void killAll(boolean retainSelected) {
        for (Unit unit : getGame().getUnits()) {
            if (retainSelected) {
                if (unit.isActiveSelected()) {
                    continue;
                }
                if (unit.getOwner().isMe()) {
                    if (getGameManager().getInfoObj().getOwner().isMe()) {
                        continue;
                    }
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
