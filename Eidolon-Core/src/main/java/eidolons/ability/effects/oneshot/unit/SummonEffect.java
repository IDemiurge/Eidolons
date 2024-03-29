package eidolons.ability.effects.oneshot.unit;

import eidolons.entity.unit.trainers.UnitTrainingMaster;
import eidolons.ability.effects.DC_Effect;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.system.audio.DC_SoundMaster;
import main.ability.effects.Effect;
import main.ability.effects.OneshotEffect;
import main.content.C_OBJ_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.SpellEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.OmittedConstructor;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.IActiveObj;
import main.entity.obj.BuffObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.entity.type.impl.BuffType;
import main.game.bf.Coordinates;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.math.Formula;
import main.system.sound.AudioEnums;

public class SummonEffect extends DC_Effect implements OneshotEffect {

    protected String typeName;
    protected Effect effects;
    protected BattleFieldObject unit;
    boolean summoningSickness;
    private Formula summonedUnitXp;
    private Player owner;
    Formula durationFormula;

    /*
     * 1) weight string 2) @ -> extends/shrink
     */
    @OmittedConstructor
    public SummonEffect(String typeName, Formula xp) {
        this.typeName = typeName;
        this.summonedUnitXp = xp;
    }

    public SummonEffect(String typeName, Effect effects) {
        this.effects = effects;
        this.typeName = typeName;
    }

    public SummonEffect(String typeName, String durFormula) {
        this.typeName = typeName;
        this.durationFormula = new Formula(durFormula);
    }


    public SummonEffect(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public void setRef(Ref REF) {
        super.setRef(REF);
        this.ref = REF;
    }


    protected void applyLifetimeBuff() {
        if (durationFormula == null) {
            return;
        }
        double dur = durationFormula.getDouble(ref);
        BuffType type = new BuffType();
        type.setName("Stupido");
        Ref REF = Ref.getCopy(ref);
        REF.setBasis(unit.getId());

        BuffObj buff = getGame().getManager().getBuffMaster().createBuff(type, REF, dur);
        buff.setOnDispel(() -> {
            unit.kill();
        });
    }

    @Override
    public boolean applyThis() {
        // TODO XP -> LEVEL!
        Ref REF = Ref.getCopy(ref);
        REF.setValue(KEYS.STRING, typeName);
        ObjType type = DataManager.getType(typeName, getTYPE());
        game.fireEvent(new Event(getEventType(), REF));
        Coordinates c = ref.getTargetObj().getCoordinates();

        if (type == null) {
            String str = typeName.equalsIgnoreCase("[3]") ? "Ruined Column" : "Fallen Column";
            //            String str = new Property(typeName, true).getStr(ref);
            type = DataManager.getType(str);
        }
        //TODO PARAMS.SUMMONED_POWER_MOD)
        if (owner == null) {
            if (ref.getObj(KEYS.THIS) != null) {
                owner = ref.getObj(KEYS.THIS).getOwner();
            } else
                owner = ref.getSourceObj().getOwner();
        }
        Ref REF2 = ref.getCopy();
        REF2.removeValue(KEYS.TARGET);
        setUnit((BattleFieldObject) game.createObject(type, c.x, c.y, owner, REF2));
        unit.setSummoned(true);
        if (unit instanceof Unit) {
            if (!((Unit) unit).isHero()) {
                UnitTrainingMaster.train((Unit) unit);
            }
        }

        getSourceUnitOrNull().getRef().setID(KEYS.SUMMONER, ref.getSource());

        IActiveObj active = ref.getActive();
        if (active == null) {
            active = unit.getGame().getLoop().getLastActionInput().getAction();
        }
        if (active.checkProperty(G_PROPS.SPELL_TAGS, SpellEnums.SPELL_TAGS.EXCLUSIVE_SUMMON.toString())) {
            Obj prev = ref.getSourceObj().getRef().getObj(KEYS.SUMMONED);
            if (prev != null) {
                unit.getGame().getLogManager().log(ref.getSourceObj().getNameIfKnown() + " unsummons " + prev.getNameIfKnown());
                prev.kill();
            }
        }

        REF.setID(KEYS.SUMMONED, getSourceUnitOrNull().getId());
        ref.setID(KEYS.SUMMONED, getSourceUnitOrNull().getId());
        game.fireEvent(new Event(getEventTypeDone(), REF));

        unit.getRef().setID(KEYS.ACTIVE, ref.getId(KEYS.ACTIVE));
        unit.getRef().setID(KEYS.SPELL, ref.getId(KEYS.SPELL));

        applyLifetimeBuff();

        // UpkeepRule.addUpkeep(unit);
        if (effects != null) {
            REF.setTarget(getSourceUnitOrNull().getId());
            return effects.apply(REF);
        }


        DC_SoundMaster.playEffectSound(AudioEnums.SOUNDS.READY, unit);
        EUtils.showInfoText(true, unit.getName() + " is summoned");
        return true;
    }

    public STANDARD_EVENT_TYPE getEventTypeDone() {
        return STANDARD_EVENT_TYPE.UNIT_SUMMONED;
    }



    protected STANDARD_EVENT_TYPE getEventType() {
        return STANDARD_EVENT_TYPE.UNIT_BEING_SUMMONED;
    }

    protected OBJ_TYPE getTYPE() {
        return C_OBJ_TYPE.UNITS_CHARS;
    }


    public void setUnit(BattleFieldObject unit) {
        this.unit = unit;
    }

    public boolean isSummoningSickness() {
        return summoningSickness;
    }

    public void setSummoningSickness(boolean summoningSickness) {
        this.summoningSickness = summoningSickness;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Effect getEffects() {
        return effects;
    }

    public void setEffects(Effect effects) {
        this.effects = effects;
    }


    public void setSummonedUnitXp(Formula summonedUnitXp) {
        this.summonedUnitXp = summonedUnitXp;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
