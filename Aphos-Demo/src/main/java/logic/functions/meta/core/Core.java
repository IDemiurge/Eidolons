package logic.functions.meta.core;

import content.AphosEvent;
import content.LOG;
import eidolons.system.libgdx.wrapper.Color;
import main.system.GuiEventManager;

public class Core {
    /*
    probably shouldn't be like a normal entity
     */
    public static final int DEFAULT_HP = 500;
    public static final int DEFAULT_ARMOR = 5;
    public static final int DEFAULT_REGEN = 10;
    private int hp, armor, regen;
    Color color;

    public Core() {
        hp = DEFAULT_HP;
        armor = DEFAULT_ARMOR;
        regen = DEFAULT_REGEN;
    }

    public void newRound(int round) {
//        armor = randomizeArmor();
        int prev = hp;
        hp = Math.min(DEFAULT_HP, hp + regen);
        if (hp-prev > 0 )
        LOG.log("Core hp regen: ", hp-prev);
    }

    public boolean dealDamage(int damage) {
        damage -= armor;
        if (damage <= 0) {
            LOG.log("Core resisted damage ", damage);
            return true;
        }
        LOG.log("Core damaged by ", damage);
        hp -= damage;
        if (hp <= 0)
        {
            hp = 0;
            LOG.log("Core destroyed!");
            return false;
        }
        LOG.log("Core hp left: ", hp);
        //callback to update a label?
        GuiEventManager.trigger(AphosEvent.CORE_HP, hp);
        return true;
    }

    public int getHp() {
        return hp;
    }

    public int getArmor() {
        return armor;
    }

    public int getRegen() {
        return regen;
    }

    public Color getColor() {
        return color;
    }
}
