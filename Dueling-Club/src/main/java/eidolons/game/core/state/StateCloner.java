package eidolons.game.core.state;

import eidolons.content.DC_ContentValsManager;
import eidolons.entity.active.DC_QuickItemAction;
import eidolons.entity.active.Spell;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.item.DC_ArmorObj;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.attach.DC_BuffObj;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.herocreator.logic.party.Party;
import main.ability.Ability;
import main.ability.ActiveAbility;
import main.ability.PassiveAbility;
import main.ability.PassiveAbilityObj;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.continuous.ContinuousEffect;
import main.content.OBJ_TYPE;
import main.content.enums.system.MetaEnums.DC_OBJ_CLASSES;
import main.content.values.parameters.PARAMETER;
import main.data.ability.construct.AbilityConstructor;
import main.elements.triggers.Trigger;
import main.entity.obj.Attachment;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.Chronos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 3/15/2017.
 */
public class StateCloner {
    DC_Game game;

    public StateCloner(DC_Game game) {
        this.game = game;
    }

    public static void test() {
        Chronos.setOn(true);
        StateCloner cloner = new StateCloner(Eidolons.game);
        DC_GameState clone = cloner.clone(cloner.game.getState());
        main.system.auxiliary.log.LogMaster.log(1, Eidolons.game.getState() + "\n VS \n" + clone);
        Eidolons.game.setState(clone);
    }

    public DC_GameState clone(DC_GameState state) {
//game's fields - units, structures - ???
        game.setCloningMode(true);
        DC_GameState clone = null;
        try {
            Chronos.mark("clone");
            clone = new DC_GameState(game);
            clone.setManager(new DC_StateManager(clone, true));
//        FlattenedState
            cloneMaps(state, clone);
            constructObjMap(clone); //TODO why need TYPE-split maps?
            cloneEffects(state, clone);
            cloneTriggers(state, clone);
            copyTypeMap(state, clone);
            cloneAttachments(state, clone);
            clone.setCloned(true);
//  triggerRules ??
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return state;
        } finally {
            game.setCloningMode(false);
            Chronos.logTimeElapsedForMark("clone");
        }
        //TODO things that are kept in MASTERS - dungeon, graveyard, battle-stats,
        return clone;
    }

    private void cloneMaps(DC_GameState state, DC_GameState clone) {

//        Map<Integer, Obj> cloneMap = clone.getObjMap();
//        Map<Integer, Obj> map = state.getObjMap() ;
//        for (Obj e : map.values()) {
//            Obj cloneObj = cloneObj(e);
//            cloneMap.put(cloneObj.getId(), cloneObj);
//        }

        for (OBJ_TYPE TYPE : state.getObjMaps().keySet()) {
            Map<Integer, Obj> cloneMap = getObjMap();
            Map<Integer, Obj> map = state.getObjMaps().get(TYPE);
            if (map == null) {
                continue;
            }
            for (Obj e : map.values()) {
                Obj cloneObj = cloneObj(e);
                cloneMap.put(cloneObj.getId(), cloneObj);
            }
            clone.getObjMaps().put(TYPE, cloneMap);
        }
    }

    private Map<Integer, Obj> getObjMap() {
        return new HashMap<>();
    }

    private void constructObjMap(DC_GameState state) {
        for (OBJ_TYPE TYPE : state.getObjMaps().keySet()) {
            for (Obj obj : state.getObjMaps().get(TYPE).values()) {
                state.getObjMap().put(obj.getId(), obj);
            }
        }
    }

    private void cloneAttachments(DC_GameState state, DC_GameState clone) {
        state.getAttachments().forEach(attachment -> {
            Attachment a = cloneAttachment(attachment);
            clone.getAttachments().add(a);
            Obj basis = a.getBasis();
            if (basis != null) {
                MapMaster.addToListMap(clone.getAttachmentsMap(), basis, a);
            }
        });
    }

    private Attachment cloneAttachment(Attachment attachment) {
        if (attachment instanceof PassiveAbilityObj) {
//            ((AbilityType) ((PassiveAbilityObj)attachment).getType()).getDoc()
            return (PassiveAbilityObj) AbilityConstructor.newAbility(((PassiveAbilityObj)
             attachment).getName(), attachment.getBasis(), true);

        } else if (attachment instanceof DC_BuffObj) {
            DC_BuffObj obj = (DC_BuffObj) getInstance((DC_BuffObj) attachment);
            obj.setDuration(attachment.getDuration());
            //retain condition?
            return obj;
        }
        return attachment; //TODO
    }

    private void copyTypeMap(DC_GameState state, DC_GameState clone) {
        for (ObjType type : state.getTypeMap().values()) {
            state.getTypeMap().put(type.getId(), type);
        }
    }


    private Effect cloneEffect(Effect e) {
        Effect cloneEffect = null;
        if (e.getConstruct() != null) {
            cloneEffect = (Effect) e.getConstruct().construct();
        } else {
            //TODO
        }
        if (e instanceof ContinuousEffect) {
            e = ((ContinuousEffect) e).getEffect();
            if (e.getConstruct() == null) {
                return ContinuousEffect.transformEffectToContinuous(e);
            }
            cloneEffect = (Effect) e.getConstruct().construct();
            cloneEffect = ContinuousEffect.transformEffectToContinuous(cloneEffect);
        }
        if (cloneEffect == null) {
            return e;
        }
        return cloneEffect;
    }

    private void cloneEffects(DC_GameState state, DC_GameState clone) {
        for (Effect e : state.getEffects()) {
            clone.addEffect(cloneEffect(e));
        }
    }

    private Trigger cloneTrigger(Trigger e) {
        return new Trigger(e.getEventType(), e.getConditions(), cloneAbility(e.getAbilities()));
    }

    private Ability cloneAbility(Ability abilities) {
        Effects effects = new Effects();
        for (Effect e : abilities.getEffects()) {
            effects.add(cloneEffect(e));
        }
        Ability ability = (abilities instanceof ActiveAbility) ?
         new ActiveAbility(abilities.getTargeting(), effects) : new PassiveAbility(
         abilities.getTargeting(), effects);
        return ability;
    }

    private void cloneTriggers(DC_GameState state, DC_GameState clone) {
        for (Trigger e : state.getTriggers()) {
            clone.addTrigger(cloneTrigger(e));
        }
    }

    private Obj cloneObj(Obj e) {
        //TODO only modified values? compare with type? ONLY DYNAMIC!
        Obj clone = getInstance(e);
        clone.setId(e.getId());
        clone.reset();
        copyDynamicValues(e, clone);
        return clone;
    }

    private void copyDynamicValues(Obj e, Obj clone) {
//        DC_ContentManager.getBackgroundDynamicParams()
        List<PARAMETER> dynamicParams = DC_ContentValsManager.getDynamicParams();
        for (PARAMETER p : dynamicParams) {
            clone.setParam(p, e.getParam(p));
        }
        clone.resetPercentages();
    }

    private Obj getInstance(Obj e) {
        DC_OBJ_CLASSES c = new EnumMaster<DC_OBJ_CLASSES>()
         .retrieveEnumConst(DC_OBJ_CLASSES.class, e.getClass().getSimpleName());
        if (c != null) {
            switch (c) {
                case Unit:
                    return new Unit((Unit) e);
                case Structure:
                    return new Structure(e.getType(), e.getOwner(), game, e.getRef().getCopy());
                case DC_WeaponObj:
                    return new DC_WeaponObj(
                     e.getType(), e.getOwner(), game, e.getRef().getCopy());
                case DC_ArmorObj:
                    return new DC_ArmorObj(e.getType(), e.getOwner(), game, e.getRef().getCopy());
                case DC_QuickItemObj:
                    return new DC_QuickItemObj(e.getType(), e.getOwner(), game, e.getRef().getCopy());
                case DC_ItemActiveObj:
                    return new DC_QuickItemAction(e.getType(), e.getOwner(), game, e.getRef().getCopy());
                case DC_UnitAction:
                    return new DC_UnitAction(e.getType(), e.getOwner(), game, e.getRef().getCopy());
                case DC_SpellObj:
                    return new Spell(e.getType(), e.getOwner(), game, e.getRef().getCopy());
                case DC_FeatObj:
                    return new DC_FeatObj(e.getType(), e.getOwner(), game, e.getRef().getCopy());
                case DC_BuffObj:
                    return new DC_BuffObj(
                     (DC_BuffObj) e);
                case DC_Cell:
                    return new DC_Cell(
                     e.getType(), e.getX(), e.getY(), game, e.getRef().getCopy()
                     ,
                     game.getDungeon());

            }
        }
        switch (e.getOBJ_TYPE_ENUM()) {
            case ABILS:
                return (Obj) AbilityConstructor.newAbility(e.getName(), e.getRef().getSourceObj(), e instanceof PassiveAbilityObj);
            case PARTY:
                return new Party(e.getType(), ((Party) e).getLeader());

            case DEITIES:
            case ARCADES:
            case DUNGEONS:
            case ENCOUNTERS:
                break;
        }
        return null;
    }


}
