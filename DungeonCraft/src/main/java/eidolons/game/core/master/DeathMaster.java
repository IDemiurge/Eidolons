package eidolons.game.core.master;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.handlers.bf.unit.UnitChecker;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.universal.PartyHelper;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.battlecraft.rules.combat.damage.DamageCalculator;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.state.DC_GameState;
import eidolons.game.module.dungeoncrawl.explore.DungeonCrawler;
import eidolons.game.module.herocreator.logic.HeroLevelManager;
import eidolons.game.netherflame.main.soul.SoulforceMaster;
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
        String message;
        if (_killed == _killer) {
            message = _killed + " annihilates";// + _killed.getInfoString();
        } else
            message = _killed + " is annihilated by " + _killer;
        SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.MAIN, message);
        getGame().getLogManager().log(message);
        getGame().getGraveyardManager().removeCorpse(_killed);
        _killed.setAnnihilated(true);

        getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_ANNIHILATED,
                new Context(_killer, _killed)));

        getGameMaster().remove(_killed, true);

    }

    public void unitDies(DC_ActiveObj activeObj, Obj _killed, Obj _killer, boolean leaveCorpse, boolean quietly) {
        unitDies(activeObj, _killed, _killer, leaveCorpse, quietly, false);
    }

    public void unitDies(DC_ActiveObj activeObj, Obj _killed, Obj _killer, boolean leaveCorpse, boolean quietly, boolean annihilate) {
        if (_killed.isDead())
            return;
        if (!quietly)
            if (_killed.getChecker() instanceof UnitChecker) {
                if (((UnitChecker) _killed.getChecker()).isImmortalityOn()) {
                    ((Unit) _killed).preventDeath();
                    return;
                }
            }
        BattleFieldObject killed = (BattleFieldObject) _killed;
        BattleFieldObject killer = (BattleFieldObject) _killer;
        Ref ref = Ref.getCopy(killed.getRef());
        ref.setSource(killer.getId());
        ref.setTarget(killed.getId());

        if (!quietly)
            if (!new Event(STANDARD_EVENT_TYPE.UNIT_IS_BEING_KILLED, ref).fire()) {
                return;
            }

        String message;
        if (_killed == _killer) {
            message = _killed + " dies ";// + _killed.getInfoString();
        } else {
            message = _killed + " killed by " + _killer + " with " + activeObj;


        }
        SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.MAIN, message);
        _killed.setDead(true);


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
            if (!getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED, REF))) {
                return;
            }
            if (activeObj != null)
                REF.setObj(KEYS.ACTIVE, activeObj);
            if (killed instanceof Unit) {
                getGame().getRules().getMoraleKillingRule().unitDied((Unit) killed,
                        killer.getRef().getAnimationActive());
            }
            //        TODO too bad now really
            //         DC_SoundMaster.playEffectSound(SOUNDS.DEATH, killed);

            game.getLogManager().logDeath(killed, killer);
            game.getLogManager().doneLogEntryNode();
        } else {
            GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL, killed);
        }
        if (killed.isSummoned()) {
            leaveCorpse = false;
        }
        if (!leaveCorpse) {
            // leave a *ghost*?
            // destroy items?
            if (annihilate) {
                unitAnnihilated(killed, killer);
            }
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

        if (killed instanceof Structure) {
            game.getObjMaster().clearCache(killed.getCoordinates());
        } else if (!killed.isMine())
            if (killer.isPlayerCharacter()) {
                if (killed instanceof Unit) {
                    SoulforceMaster.slain((Unit) killed);
                }
            }

        DC_GameState.gridChanged=true;
        getGame().getVisionMaster().getIllumination().setResetRequired(true);
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
