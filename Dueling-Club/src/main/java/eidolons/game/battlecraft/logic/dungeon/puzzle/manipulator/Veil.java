package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.libgdx.texture.Sprites;
import main.content.enums.GenericEnums;
import main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class Veil extends GridObject {

    protected final boolean pale;
    protected final boolean enter;
    protected Puzzle puzzle;

    //link to puzzle
    public Veil(Puzzle puzzle, Coordinates c, boolean pale, boolean enter) {
        this(pale ? CUSTOM_OBJECT.LIGHT.spritePath : Sprites.LIGHT_VEIL, puzzle, c,
                pale, enter);
    }

    public Veil(String path, Puzzle puzzle, Coordinates c, boolean pale, boolean enter) {
        super(c, path);
        this.pale = pale;
        this.enter = enter;
        this.puzzle = puzzle;
        GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ, this);
    }

    @Override
    public void init() {
        super.init();
        sprite.setBlending(GenericEnums.BLENDING.SCREEN);

    }

    @Override
    protected int getFps() {
        return pale ? 14 : 20;
    }

    @Override
    public boolean checkVisible() {
        if (puzzle.isActive()) {
            return !enter;
        }
        if (!enter) {
            return false;
        }
        return super.checkVisible();
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
    public void act(float delta) {
        super.act(delta);
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
