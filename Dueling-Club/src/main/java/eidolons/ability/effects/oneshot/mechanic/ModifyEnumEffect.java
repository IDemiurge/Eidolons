package eidolons.ability.effects.oneshot.mechanic;

import eidolons.ability.effects.DC_Effect;
import main.ability.effects.OneshotEffect;
import main.content.ContentValsManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

public class ModifyEnumEffect extends DC_Effect implements OneshotEffect {

    boolean set;
    private final String name;
    private final String prop;
    private final int mod;

    public ModifyEnumEffect(String name, int mod) {
        this.name = name;
        this.mod = mod;
        prop = ContentValsManager.getPROP(name).getName();
    }

    @Override
    public boolean applyThis() {
        Class<?> clazz = EnumMaster.getEnumClass(name);
        String prevValue = getTarget().getProperty(prop);
        int index = EnumMaster.getEnumConstIndex(clazz, prevValue);
        int newIndex = index + mod;
        String value = StringMaster.format(clazz.getEnumConstants()[newIndex]
         .toString());
        getTarget().setProperty(prop, value);

        return true;
    }

}
