package main.ability.effects;

import main.ability.Interruptable;
import main.data.ability.construct.Reconstructable;
import main.elements.triggers.Trigger;
import main.entity.Ref;
import main.entity.Referred;
import main.entity.group.GroupImpl;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.system.graphics.ANIM;
import main.system.math.Formula;

public interface Effect extends Interruptable, Referred,
  Reconstructable<Effect> {
    int BASE_LAYER = 0;
    int SECOND_LAYER = 1;
    int ZERO_LAYER = -1;
    Integer BUFF_RULE = 2; // effects from dynamic

    boolean apply(Ref ref);

    boolean apply();

    boolean applyThis();

    void setRef(Ref ref);

    Formula getFormula();

    void setFormula(Formula newFormula); // altering effects on the fly

    boolean isReconstruct();

    void setReconstruct(boolean reconstruct);

    boolean isAltering();

    void setAltering(boolean altering);

    boolean isAltered();

    void setAltered(boolean altered);

    boolean isQuietMode();

    void setQuietMode(boolean b);

    Obj getSpell();

    void setTargetGroup(GroupImpl group);

    boolean isIrresistible();

    void setIrresistible(boolean b);

    int getLayer();

    void setForcedLayer(Integer layer);

    void initLayer();

    void setConcurrent(boolean b);

    boolean isCopied();

    void setCopied(boolean copied);

    boolean isIgnoreGroupTargeting();

    void setForceStaticParse(Boolean forceStaticParse);

    Boolean isForceStaticParse();

    void resetOriginalFormula();

    void appendFormulaByMod(Object mod);

    boolean isContinuousWrapped();

    void setContinuousWrapped(boolean isContinuousWrapped);

    Trigger getTrigger();

    void setTrigger(Trigger trigger);

    ActiveObj getAnimationActive();

    void setAnimationActive(ActiveObj animationActive);

    ANIM getAnimation();

    void setAnimation(ANIM animation);

    ActiveObj getActiveObj();

    String getTooltip();

    void multiplyFormula(Object mod);

    void addToFormula(Object mod);

    void remove();

    enum UPKEEP_FAIL_ACTION {
        TREASON(" turns on its summoner as he is no longer able to pay the due!"),
        DEATH(" is destroyed as its summoner is no longer able to pay the upkeep!"),
        STASIS(" stands still as its summoner is no longer able to pay the upkeep!"),
        CONFUSION(" is rendered mindless as its summoner is no longer able to pay the upkeep!"),
        BERSERK(" goes berserk as its summoner is no longer able to pay the upkeep!"),;

        private String logString;

        UPKEEP_FAIL_ACTION(String l) {
            this.logString = l;
        }

        public String getLogString() {
            return logString;
        }
    }

    enum MOD_PROP_TYPE {
        ADD(1), REMOVE(2), SET(3);

        MOD_PROP_TYPE(int i) {
        }
    }

    enum MOD {
        MODIFY_BY_PERCENT(1), MODIFY_BY_CONST(2), SET(3);

        MOD(int i) {
        }
    }

    enum RAISE_MODIFIER {
        PLAGUE, GHASTLY, BLOODCRAZED, FROZEN,
    }

    enum RAISE_TYPE {
        SKELETON, ZOMBIE, GHOUL, GHOST, VAMPIRE, LICH, WRAITH_LORD
    }

    enum SPECIAL_EFFECTS_CASE {
        BEFORE_ATTACK,
        BEFORE_HIT,
        ON_ATTACK,
        ON_HIT, //melee only
        ON_KILL,
        ON_DEATH,
        ON_CRIT,
        ON_DODGE,
        ON_SHIELD_BLOCK,
        ON_CRIT_SELF,
        ON_DODGE_SELF,
        ON_SHIELD_BLOCK_SELF,
        ON_SNEAK_ATTACK,
        ON_SNEAK_ATTACK_SELF,
        ON_SNEAK_HIT,
        ON_PARRY_SELF,
        ON_PARRY,
        SPELL_IMPACT,
        SPELL_HIT,
        SPELL_RESISTED,
        SPELL_RESIST,
        MOVE,
        NEW_TURN,
        END_TURN,
    }

    enum ABILITY_MANIPULATION {
        ADD, REMOVE, REMOVE_ALL, MODIFY_FORMULA, STEAL
    }

    enum SPELL_MANIPULATION {
        PREPARE, OBLIVIATE, BLOCK, UNBLOCK, STEAL, ADD,

    }

    enum BIND_TYPE {
        SHARE,

        REDIRECT,
        // MULTI_SHARE,
    }

    enum BIND_FILTER {
        DAMAGE, SPELL_DAMAGE, ALL_DAMAGE, ALL_SPELLS, CUSTOM, // counters
        // ALL_BUFFS?
    }

    enum BLOCK_TYPES {
        HOSTILE_SPELLS,
        DAMAGE,
        ATTACK,
        HOSTILE_ACTION,
        DAMAGE_TYPE,
        SPELLS_FROM_SOURCE,
        DAMAGE_FROM_SOURCE,

    }

    enum ABSORB_TYPES {
        HOSTILE_SPELLS, DAMAGE, DAMAGE_TYPE, SPELLS_FROM_SOURCE, DAMAGE_FROM_SOURCE,

    }

}
