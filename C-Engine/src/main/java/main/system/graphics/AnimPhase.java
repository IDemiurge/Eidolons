package main.system.graphics;

import main.entity.Entity;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AnimPhase {
    static {
        PHASE_TYPE.ROLL.setTimeModifier(125);
        PHASE_TYPE.BUFF.setTimeModifier(125);
        PHASE_TYPE.PARAM_MODS.setTimeModifier(125);
    }

    private PHASE_TYPE type;
    private Object[] args;

    public AnimPhase(PHASE_TYPE t, Object... args) {
        type = t;
        this.args = args;
    }

    @Override
    public String toString() {
        return type.toString() + " with args: " + ListMaster.toStringList(args);
    }

    public void addArgs(Object... args) {
        for (Object o : args) {
            addArg(o);
        }
    }

    public void addArg(Object o) {
        List<Object> list = new LinkedList<>(Arrays.asList(args));
        list.add(o);
        args = list.toArray();
    }

    public boolean isDrawOnSource() {

        switch (type) {

        }
        return false;
    }

    public PHASE_TYPE getType() {
        return type;
    }

    public Object[] getArgs() {
        return args;

    }

    public enum PHASE_TYPE {
        PRE_CAST, CHANNELING, ACTION, PRE_MOVE,
        // PREVIEW RESULTS OF AN ACTION VIA ANIMS?
        PRE_ATTACK,
        REDUCTION_ARMOR,
        REDUCTION_SHIELD, // DAMAGE_FINAL
        DAMAGE_FORMULA,
        DAMAGE_FINAL,
        DAMAGE_DEALT, // TODO UNCONSCIOUS, DEATH, AFTER-EFFECTS
        MISSED,
        ATTACK_DODGED,

        // ABSORB
        FORCE_KNOCKDOWN,
        DISPLACEMENT,
        PARAM_MODS,
        PROP_MODS,
        BUFF,
        INTERRUPTED,
        ATTACK_DEFENSE("Attack vs Defense", DAMAGE_FORMULA),
        ATTACK_CRITICAL("Critical Attack", DAMAGE_FORMULA),
        ATTACK_EXTRA_MODS("Extra Mods", DAMAGE_FORMULA),
        ATTACK_ACTION_MODS("Action Mods", DAMAGE_FORMULA),
        ATTACK_WEAPON_MODS("Weapon Mods", DAMAGE_FORMULA),
        ATTACK_POSITION_MODS("Position Mods", DAMAGE_FORMULA),
        DAMAGE_FORMULA_MODS,
        DICE_ROLL("Dice Roll", DAMAGE_FORMULA),
        ACTION_RESOLVES {
            public String getLabelName(Object arg) {
                return ((Entity) arg).getName();
            }
        },
        COSTS_PAID,
        REDUCTION_NATURAL,
        PARRY,
        DODGE_FORMULA,
        ROLL,
        DEATH,
        COUNTER,;
        int timeModifier;
        private String labelName;
        private PHASE_TYPE enclosingPhase;

        PHASE_TYPE(String labelName, PHASE_TYPE enclosingPhase) {
            this.labelName = labelName;
            this.enclosingPhase = enclosingPhase;
        }

        PHASE_TYPE() {

        }

        public int getTimeModifier() {
            return timeModifier;
        }

        public void setTimeModifier(int timeModifier) {
            this.timeModifier = timeModifier;
        }

        public PHASE_TYPE getEnclosingPhase() {
            return enclosingPhase;
        }

        public boolean isSubPhase() {
            return getEnclosingPhase() != null;
        }

        public String getLabelName(Object arg) {
            if (labelName == null) {
                labelName = StringMaster.getWellFormattedString(name());
            }
            return labelName;
        }

    }

}
