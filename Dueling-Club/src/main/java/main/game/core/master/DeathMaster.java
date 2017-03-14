package main.game.core.master;

import main.ability.AbilityObj;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.entity.Ref;
import main.entity.obj.Attachment;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.logic.combat.damage.DamageDealer;
import main.game.core.game.DC_Game;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.generic.PartyManager;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.datatypes.DequeImpl;
import main.system.graphics.ANIM;
import main.system.graphics.AnimPhase;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;
import main.system.text.LogEntryNode;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 2/16/2017.
 */
public class DeathMaster extends Master {

    public DeathMaster(DC_Game game) {
        super(game);
    }


    public void unitAnnihilated(Obj _killed, Obj _killer) {
        getGame().getGraveyardManager().removeCorpse(_killed);
//	TODO 	getGame().getDroppedItemManager().remove((DC_HeroObj) _killed, item);

    }

    public void unitDies(Obj _killed, Obj _killer, boolean leaveCorpse, boolean quietly) {

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
                    e.printStackTrace();
                }
            }
            try {
                getGame().getGraveyardManager().unitDies(killed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        getGame().getMaster().remove(killed); TODO why was it ever here?! killed doesn't mean removed...
        // getGame().getBattleField().remove(killed); // TODO GRAVEYARD
        if (!quietly) {
            Ref REF = Ref.getCopy(killer.getRef());
            REF.setTarget(killed.getId());
            REF.setSource(killer.getId());
            if (killed instanceof Unit) {
                getGame().getBattleManager().unitDies((Unit) killed);
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

            SoundMaster.playEffectSound(SOUNDS.DEATH, killed);

            game.getLogManager().logDeath(killed, killer);
            getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED, REF));
            game.getLogManager().doneLogEntryNode();
        } else {
            GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL,
                    new EventCallbackParam<>(killed));
        }
        // refreshAll();
    }


    public void checkForDeaths() {
        for (DC_TYPE TYPE : C_OBJ_TYPE.BF_OBJ.getTypes()) {
            for (Obj unit : getState().getObjMaps().get(TYPE).values()) {
                if (!unit.isDead()) {
                    if (DamageDealer.checkDead((BattleFieldObject) unit)) {
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
        List<BattleFieldObject> toRemove = new LinkedList<>();
        list.addAll(getGame().getUnits());
        if (removeBfObjects) {
            list.addAll(getGame().getStructures());
        }
        for (BattleFieldObject unit : list) {

            if (retainPlayerParty) {
                if (PartyManager.getParty() != null) {
                    if (PartyManager.getParty().getMembers().contains(unit)) {
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
