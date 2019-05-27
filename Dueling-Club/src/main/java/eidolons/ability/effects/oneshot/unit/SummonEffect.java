package eidolons.ability.effects.oneshot.unit;

import eidolons.ability.UnitTrainingMaster;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.magic.SummoningSicknessRule;
import eidolons.game.battlecraft.rules.round.UpkeepRule;
import eidolons.game.core.EUtils;
import eidolons.game.module.herocreator.logic.UnitLevelManager;
import eidolons.system.DC_Formulas;
import eidolons.system.audio.DC_SoundMaster;
import main.ability.effects.Effect;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.data.DataManager;
import main.data.ability.OmittedConstructor;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.math.Formula;
import main.system.math.MathMaster;
import main.system.math.Property;
import main.system.sound.SoundMaster.SOUNDS;

public class SummonEffect extends MicroEffect implements OneshotEffect {

    protected String typeName;
    protected Effect effects;
    protected BattleFieldObject unit;
    protected String automataApFormula;
    boolean summoningSickness;
    private Formula summonedUnitXp;
    private Player owner;

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

    public SummonEffect(String typeName, String automataApFormula) {
        this.typeName = typeName;
        this.automataApFormula = automataApFormula;
    }

    public SummonEffect(String typeName, UPKEEP_FAIL_ACTION ufa, Integer ess_upkeep,
                        Integer foc_upkeep, Integer sta_upkeep, Integer end_upkeep
                        // perhaps it is better off as PARAMETERs because those can be easier to
                        // manipulate!
    ) {

    }

    public SummonEffect(String typeName, UPKEEP_FAIL_ACTION ufa, SOUNDS unitSound) {

    }

    public SummonEffect(String typeName, UPKEEP_FAIL_ACTION ufa) {

    }

    public SummonEffect(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public void setRef(Ref REF) {
        super.setRef(REF);
        this.ref = REF;
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
            String str = new Property(typeName, true).getStr(ref);
            type = DataManager.getType(str);

        }
        if (type.getOBJ_TYPE_ENUM() != DC_TYPE.CHARS) {
            type = addXp(type);
        }
        if (owner == null) {
            owner = ref.getSourceObj().getOwner();
        }
        Ref REF45 = ref.getCopy();
        REF45.removeValue(KEYS.TARGET);
        setUnit((BattleFieldObject) game.createUnit(type, c.x, c.y, owner, REF45));
        unit.setSummoned(true);
        if (unit instanceof Unit) {
            if (!((Unit) unit).isHero()) {
                UnitTrainingMaster.train((Unit) unit);
            }
        }

        getUnit().getRef().setID(KEYS.SUMMONER, ref.getSource());

        REF.setID(KEYS.SUMMONED, getUnit().getId());
        ref.setID(KEYS.SUMMONED, getUnit().getId());
        game.fireEvent(new Event(getEventTypeDone(), REF));

        unit.getRef().setID(KEYS.ACTIVE, ref.getId(KEYS.ACTIVE));
        unit.getRef().setID(KEYS.SPELL, ref.getId(KEYS.SPELL));

        UpkeepRule.addUpkeep(unit);
        if (unit instanceof Unit) {
            SummoningSicknessRule.apply((Unit) unit);
        }

        if (effects != null) {
            REF.setTarget(getUnit().getId());
            return effects.apply(REF);
        }
        DC_SoundMaster.playEffectSound(SOUNDS.READY, unit);
        EUtils.showInfoText(true, unit.getName() + " is summoned");
        return true;
    }

    public STANDARD_EVENT_TYPE getEventTypeDone() {
        return STANDARD_EVENT_TYPE.UNIT_SUMMONED;
    }

    protected ObjType addXp(ObjType type) {

        try {
            Integer xp = getSummonedUnitXp().getInt(ref);
            try {
                Integer mod = ref.getObj(KEYS.ACTIVE).getIntParam(PARAMS.SUMMONED_XP_MOD);
                if (mod != 0) {
                    xp = MathMaster.applyMod(xp, mod);
                }
                if (ref.getObj(KEYS.SOURCE) != null) {
                    mod = ref.getObj(KEYS.SOURCE).getIntParam(PARAMS.SUMMONED_XP_MOD);
                    if (mod != 0) {
                        xp = MathMaster.applyMod(xp, mod);
                    }
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

            LogMaster.log(1, "Awarding xp to " + type.getName() + ": " + xp);
            type = new UnitLevelManager().awardXP(type, xp, false);
            LogMaster.log(1, "Unit level: "
             + type.getParam(PARAMS.UNIT_LEVEL));
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        return type;
    }

    protected Formula getXpFormula(Obj spell) {
        Formula summonedUnitXp = DC_Formulas.SUMMONED_UNIT_XP;
        if (!StringMaster.isEmpty(spell.getParam(PARAMS.FORMULA))) {
            if (spell.getIntParam(PARAMS.FORMULA) != 0) {
                summonedUnitXp = new Formula(spell.getParam(PARAMS.FORMULA));
            }
        }
        return summonedUnitXp;
    }

    protected STANDARD_EVENT_TYPE getEventType() {
        return STANDARD_EVENT_TYPE.UNIT_BEING_SUMMONED;
    }

    protected OBJ_TYPE getTYPE() {
        return C_OBJ_TYPE.UNITS_CHARS;
    }

    public MicroObj getUnit() {
        return unit;
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

    public Formula getSummonedUnitXp() {
        if (summonedUnitXp == null) {
            if (ref.getObj(KEYS.ACTIVE) != null) {
                summonedUnitXp = getXpFormula(ref.getObj(KEYS.ACTIVE));
            }
        }
        return summonedUnitXp;
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
