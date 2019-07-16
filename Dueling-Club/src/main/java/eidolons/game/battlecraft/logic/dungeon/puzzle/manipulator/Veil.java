package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.texture.Sprites;
import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT;
import main.game.bf.Coordinates;

public class Veil extends GridObject {

    private final boolean pale;
    private final boolean enter;
//link to puzzle
    public Veil(Coordinates c, boolean pale, boolean enter) {
        super(c,  pale ? Sprites.VEIL : CUSTOM_OBJECT.LIGHT.spritePath);
        this.pale = pale;
        this.enter = pale;
    }

    @Override
    protected void init() {
        super.init();
        sprite.setBlending(SuperActor.BLENDING.SCREEN);

    }
    @Override
    protected int getFps() {
        return pale? 14: 20;
    }
    @Override
    protected boolean isClearshotRequired() {
        return false;
    }

    @Override
    protected double getDefaultVisionRange() {
        return 3;
    }

    @Override
    protected void createEmittersUnder() {
        createEmitter("unit/black soul bleed 3", 0, 64);
    }

    @Override
    protected void createEmittersOver() {
        //TODO soul direction
//        createEmitter("spell/shape/soul dissipation", -32, 32);
//        createEmitter("spell/shape/soul dissipation pale", 32, 32);
//        createEmitter("unit/black soul bleed 3", 0, 64);
//        createEmitter("unit/chaotic dark", 0, 32);

//        createEmitter("unit/black soul bleed 3", 64, 64);
//        createEmitter("unit/chaotic dark", 32, 32);
//        createEmitter("unit/black soul bleed 3", -64, 64);
//        createEmitter("unit/chaotic dark", -32, 32);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
