package eidolons.game.netherflame.boss;

import eidolons.game.netherflame.boss.logic.BossCycle;
import eidolons.game.netherflame.boss.logic.entity.BossUnit;
import main.game.bf.Coordinates;

import java.util.Map;
import java.util.Set;

public abstract class BossModel {

    public abstract String getName();

    public abstract int getWidth();
    public abstract int getHeight();
    Map<BossCycle.BOSS_TYPE, BossUnit> entities ;

    public void init( Map<BossCycle.BOSS_TYPE, BossUnit> entities){
        this.entities = entities;
    }

    public Map<BossCycle.BOSS_TYPE, BossUnit> getEntities() {
        return entities;
    }

    public BossUnit getEntity(BossCycle.BOSS_TYPE type) {
        return entities.get(type);
    }

    public Set<BossCycle.BOSS_TYPE> getEntitiesSet() {
        return entities.keySet();
    }

    public abstract BossCycle.BOSS_TYPE[] getCycle();

    public abstract String getName(BossCycle.BOSS_TYPE type);

    public abstract Coordinates getOffset(BossCycle.BOSS_TYPE boss_type);
/*
                            Requirements:
Gameplay:
Targeting rules AND visuals
- Highlighting the boss - ? Ideally we'd do this via SHADER
And if that works, we can choose which part to highlight when!

Entity splitting / syncing => I'd offer to have MAIN entity and Additional entities - with custom rules
E.g. Searing Pearl
- Alternative way of defeating the guy?
- Has its own AI, set of abilities, entire entity
- Shares some basics with MAIN entity - vision, checks,
- in 2d, ...

Animations
1) SYNC
2) SPLIT
3) Layers
4) Performance

2d Variant
- Intercepts all animation calls and replaces them with simplified stuff
- Implements same interface as


 */

}
