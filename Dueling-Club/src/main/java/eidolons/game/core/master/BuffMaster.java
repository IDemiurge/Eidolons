package eidolons.game.core.master;

import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.attach.DC_BuffObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.target.EffectFinder;
import eidolons.game.battlecraft.rules.round.UpkeepRule;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Attachment;
import main.entity.obj.BuffObj;
import main.entity.obj.Obj;
import main.entity.type.BuffType;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;
import main.system.GuiEventManager;
import main.system.auxiliary.log.LogMaster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static main.entity.obj.BuffObj.DUMMY_BUFF_TYPE;
import static main.system.GuiEventType.UPDATE_BUFFS;

public class BuffMaster extends Master {
    public BuffMaster(DC_Game game) {
        super(game);
    }

    public static void applyBuff(String name, Effect effect, Obj target) {
        applyBuff(name, effect, target, null);
    }

    public static void applyBuff(Effect effect, Obj target, Integer duration) {
        applyBuff(null, effect, target, duration);

    }

    public static void applyBuff(Effect effect, Obj target) {
        applyBuff(null, effect, target, null);
    }

    public static void applyBuff(String buffName, Effect effect, Obj target,
                                 Integer duration) {
        AddBuffEffect addBuffEffect = new AddBuffEffect(buffName, effect);
        if (duration != null) {
            addBuffEffect.setDuration(duration);
        }
        addBuffEffect.apply(Ref.getSelfTargetingRefCopy(target));

    }

    public static boolean checkBuffDispelable(BuffObj buff) {
        if (buff.getBuffType() == GenericEnums.BUFF_TYPE.SPELL) {
            if (!buff.isPermanent()) {
                if (!buff.checkBool(GenericEnums.STD_BOOLS.NON_DISPELABLE)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static List<ObjType> getBuffsFromSpell(DC_ActiveObj spell) {
        List<ObjType> buffTypes = new ArrayList<>();
        for (Effect e : EffectFinder.getEffectsOfClass(spell.getAbilities(),
         AddBuffEffect.class)) {
            ObjType buffType = ((AddBuffEffect) e).getBuffTypeLazily();

            if (buffType != null) {
                buffTypes.add(buffType);
            }
        }

        return buffTypes;
    }

    public static ObjType getBuffType(String name) {
        ObjType type = DataManager.getType(name, DC_TYPE.BUFFS);
        if (type == null) {
            type = new BuffType(DataManager.getType(DUMMY_BUFF_TYPE, DC_TYPE.BUFFS));
            type.setName(name);
        }
        return type;
    }

    public void atbTimeElapsed(Float time) {
        for (Obj sub : getBuffs()) {
            BuffObj buff = ((BuffObj) sub);
            if (buff.isDead())
                continue;
            buff.timeElapsed(time);

        }

    }

    private Collection<Obj> getBuffs() {
        return state.getObjMaps().get(DC_TYPE.BUFFS).values();
    }

    public void checkForDispels() {
        for (Attachment attachment : game.getState().getAttachments()) {
            attachment.checkRetainCondition();
        }

    }

    public void addAttachment(Attachment attachment, Obj basis) {
        List<Attachment> list = getState().getAttachmentsMap().get(basis);
        if (list == null) {
            list = new ArrayList<>();
            getState().getAttachmentsMap().put(basis, list);
        }
        if (attachment instanceof BuffObj) {
            basis.addBuff((BuffObj) attachment);
        }
        getState().addAttachment(attachment);
        list.add(attachment);
        if (attachment.isTransient()) // e.g. auras
        {
            return;
        }
        for (Effect e : attachment.getEffects()) {
            // e.apply(basis.getRef()); // how to add retain conditions?
            // else
            // if (!(e instanceof AttachmentEffect))
            getState().addEffect(e);
        }
    }

    public BuffObj createBuff(BuffType type, Obj active, Player player, Ref ref, Effect effect,
                              double duration, Condition retainCondition) {
        ref = Ref.getCopy(ref);
        if (type.getName().equals(DUMMY_BUFF_TYPE)) {
            try {
                String name = ref.getObj(KEYS.ACTIVE.name()).getName() + "'s buff";
                String img = ref.getObj(KEYS.ACTIVE.name()).getProperty(G_PROPS.IMAGE);
                type = new BuffType(type);
                type.setProperty(G_PROPS.NAME, name);
                type.setProperty(G_PROPS.IMAGE, img);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        Obj basis = game.getObjectById(ref.getBasis());
        if (basis == null) {
            return null;
        }
        DC_BuffObj buff = (DC_BuffObj) basis.getBuff(type.getName());
        if (buff != null) {
            if (!type.checkBool(GenericEnums.STD_BOOLS.STACKING) && !active.checkBool(GenericEnums.STD_BOOLS.STACKING)) {
                basis.removeBuff(type.getName());
                // TODO duration or do nothing
            } else {
                if (buff.isMaxStacks()) {
                    return buff;
                }
                buff.modifyParameter(PARAMS.BUFF_STACKS, 1);
            }
        } else {
            // preCheck cache
        }

        buff = new DC_BuffObj(type, player, getGame(), ref, effect, duration, retainCondition);
        buff.setActive(active);

        buff.applyEffect(); // be careful!

        buffCreated(buff, basis);
        if (type.checkBool(GenericEnums.STD_BOOLS.APPLY_THRU) || active.checkBool(GenericEnums.STD_BOOLS.APPLY_THRU)) {
            buff.setAppliedThrough(true);
            if (basis instanceof Unit) {
                Ref REF = ref.getCopy();
                Obj cell = game.getCellByCoordinate(basis.getCoordinates());
                if (!cell.hasBuff(buff.getName())) {
                    REF.setBasis(cell.getId());
                    REF.setTarget(cell.getId());
                    // copy buff
                    Effect copy = effect.getCopy();
                    if (copy == null) {
                        LogMaster.error("APPLY THRU ERROR: " + effect + " HAS NO CONSTRUCT");
                    } else {
                        createBuff(type, active, player, REF, copy, duration, retainCondition)
                         .setAppliedThrough(true);
                    }
                }
            }
        }
        return buff;
    }

    public void buffCreated(BuffObj buff, Obj basis) {
        game.getState().addObject(buff);
        addAttachment(buff, basis);
        UpkeepRule.addUpkeep(buff);
        GuiEventManager.trigger(UPDATE_BUFFS, buff);
    }

    public void copyBuff(BuffObj buff, Obj obj, Condition retainCondition) {
        Ref REF = buff.getRef().getCopy();
        REF.setBasis(obj.getId());
        REF.setTarget(obj.getId());
        createBuff(buff.getType(), buff.getActive(), buff.getOwner(), REF, buff.getEffect(), buff
         .getDuration(), Conditions.join(buff.getRetainConditions(), retainCondition));

    }

}
