package main.game.battlecraft.logic.dungeon.arena;

import main.game.battlecraft.logic.dungeon.universal.*;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/8/2017.
 */
public class ArenaDungeonMaster extends DungeonMaster<ArenaDungeon> {
    public ArenaDungeonMaster(DC_Game game) {
        super(game);
    }

    @Override
    protected FacingAdjuster<ArenaDungeon> createFacingAdjuster() {
        return new FacingAdjuster<>(this);
    }

    @Override
    protected Positioner<ArenaDungeon> createPositioner() {
        return new ArenaPositioner(this);
    }

    @Override
    protected Spawner<ArenaDungeon> createSpawner() {
        return new ArenaSpawner(this);
    }

    @Override
    protected DungeonInitializer<ArenaDungeon> createInitializer() {
        return new ArenaInitializer(this);
    }
}
