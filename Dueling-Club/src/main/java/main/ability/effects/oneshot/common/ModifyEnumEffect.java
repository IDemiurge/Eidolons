package main.ability.effects.oneshot.common;

import main.ability.effects.DC_Effect;
import main.content.ContentManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

public class ModifyEnumEffect extends DC_Effect {

    boolean set;
    private String name;
    private String prop;
    private int mod;

    public ModifyEnumEffect(String name, int mod) {
        this.name = name;
        this.mod = mod;
        prop = ContentManager.getPROP(name, false).getName();
    }

    @Override
    public boolean applyThis() {
        Class<?> clazz = EnumMaster.getEnumClass(name);
        String prevValue = getTarget().getProperty(prop);
        int index = EnumMaster.getEnumConstIndex(clazz, prevValue);
        int newIndex = index + mod;
        String value = StringMaster.getWellFormattedString(clazz.getEnumConstants()[newIndex]
                .toString());
        getTarget().setProperty(prop, value);

        return true;
    }

}
