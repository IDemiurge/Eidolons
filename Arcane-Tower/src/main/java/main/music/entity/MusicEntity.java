package main.music.entity;

import main.ArcaneTower;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;

public abstract class MusicEntity extends Entity {
    public MusicEntity(ObjType type) {
        super(type, Player.NEUTRAL, ArcaneTower.getSimulation(), ArcaneTower.getRef());
    }

    @Override
    public void init() {
        toBase();
    }

    @Override
    public boolean isTypeLinked() {
        return true;
    }
}
