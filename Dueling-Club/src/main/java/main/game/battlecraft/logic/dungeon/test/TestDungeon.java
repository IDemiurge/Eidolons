package main.game.battlecraft.logic.dungeon.test;

import main.content.PROPS;
import main.game.battlecraft.logic.dungeon.Dungeon;
import main.game.battlecraft.logic.dungeon.DungeonMaster;
import main.game.battlecraft.logic.dungeon.DungeonWrapper;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.graphics.GuiManager;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 5/8/2017.
 */
public class TestDungeon extends DungeonWrapper {
    public TestDungeon(Dungeon dungeon,  DungeonMaster master) {
        super(dungeon, master);
    }

    public Coordinates getPlayerSpawnCoordinates() {
        String prop = getProperty(PROPS.PARTY_SPAWN_COORDINATES);
        if (prop.isEmpty()) {
            return Coordinates.getMiddleCoordinate(FACING_DIRECTION.NONE);
        }
        return new Coordinates(prop);
    }

    public Coordinates getEnemySpawningCoordinates() {
        String prop = getProperty(PROPS.ENEMY_SPAWN_COORDINATES);
        if (prop.isEmpty()) {
            return getDefaultEnemyCoordinates();
        }
        return new Coordinates(prop);
    }

    public Coordinates getDefaultEnemyCoordinates() {
        // TODO encounter?
        // default - getOrCreate a random point in some range from player start
        Coordinates playerC = getPlayerSpawnCoordinates();
        Loop.startLoop(100);
        int n = getDefaultDistanceToEnemy();
        while (Loop.loopContinues()) {
            int x = playerC.x;
            int y = playerC.y + MathMaster.getPlusMinusRandom(getOffsetEnemyMode(), n);

            if (y > GuiManager.getBattleFieldHeight() - 1 || y < 0) {
                // TODO adjust offset if static!
                continue;
            }
            if (isOffsetEnemyByX()) {
                x = x + RandomWizard.getRandomIntBetween(-n, n);
                if (x > GuiManager.getBattleFieldWidth() - 1) {
                    continue;
                }
                if (x < 0) {
                    continue;
                }
            }
            return new Coordinates(x, y);
        }
        return null;
    }

    private Boolean getOffsetEnemyMode() {
        return false;
    }

    private boolean isOffsetEnemyByX() {
        return false;
    }

    private int getDefaultDistanceToEnemy() {
        return 4;
    }
}
