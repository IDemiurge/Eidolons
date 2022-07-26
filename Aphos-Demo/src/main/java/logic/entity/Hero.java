package logic.entity;

import logic.core.Aphos;
import logic.lane.HeroPos;

import java.util.Map;

public class Hero extends Entity {

    private HeroPos pos;

    public Hero(HeroPos pos, Map<String, Object> valueMap) {
        super(valueMap);
        this.pos = pos;
    }

    public HeroPos getPos() {
        return pos;
    }

    public void setPos(HeroPos pos) {
        this.pos = pos;
    }

    public boolean isLeftSide() {
        return pos.isLeftSide();
    }

    @Override
    public String toString() {
        return "Hero - " + name;
    }

    public boolean isPlayerControlled() {
        return true;
    }
    public boolean isOnAtb() {
        return true;
    }
    public int getLane() {
        return pos.getLane();
    }

    public boolean isInFrontLine() {
        return pos.getCell() % 2 == 1;
    }
    @Override
    public void killed(Entity source) {
        super.killed(source);
        if (Aphos.hero==this)
            Aphos.game.getRoundHandler().setGameOver(true);
    }
}
