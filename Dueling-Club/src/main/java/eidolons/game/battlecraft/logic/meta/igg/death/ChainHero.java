package eidolons.game.battlecraft.logic.meta.igg.death;

import eidolons.entity.obj.unit.Unit;
import main.entity.type.ObjType;

public class ChainHero {
    int lives;
    Unit unit;
    ObjType type;

    public ChainHero(int lives, ObjType type) {
        this.lives = lives;
        this.type = type;
    }

    public ChainHero(Unit member) {
        this(1, member.getType());
        this.unit = member;
    }

    public int getLives() {
        return lives;
    }

    public Unit getUnit() {
        return unit;
    }

    public ObjType getType() {
        return type;
    }

    public boolean hasLives() {
        return getLives()>0;
    }

    public void death() {
        lives--;
    }
}
