package boss.demo;

import boss.logic.BossCycle;
import eidolons.game.exploration.dungeon.generator.model.AbstractCoordinates;
import boss.BossModel;
import main.game.bf.Coordinates;

public class DemoBoss extends BossModel {


    @Override
    public String getName() {
        return  "Lord Arius";
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public String getName(BossCycle.BOSS_TYPE type) {
        switch (type) {
            case caster:
                return "Arius";
                // return "Charred Soul-Vessel";
            case melee:
                return "Crimson Knight";
        }
        return null;
    }

    @Override
    public Coordinates getOffset(BossCycle.BOSS_TYPE type) {
        switch (type) {
            case caster:
                return AbstractCoordinates.get(true, 0, -3);
        }
        return Coordinates.get(0, 0);
    }

    @Override
    public BossCycle.BOSS_TYPE[] getCycle() {
        return new BossCycle.BOSS_TYPE[ ]{
          BossCycle.BOSS_TYPE.caster,
          BossCycle.BOSS_TYPE.melee
        };
    }
}
