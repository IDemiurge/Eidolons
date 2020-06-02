package eidolons.game.netherflame.boss;

public abstract class BossModel {

    public abstract String getName();

    public abstract int getWidth();
    public abstract int getHeight();


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
