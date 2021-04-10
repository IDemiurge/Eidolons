package eidolons.puzzle.gridobj;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.content.consts.Sprites;
import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import main.content.enums.GenericEnums;
import main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class Veil extends GridObject {

    protected final boolean black;
    protected final boolean enter;

    //link to puzzle
    public Veil(  Coordinates c, boolean black, boolean enter) {
        this(black ? CUSTOM_OBJECT.GATE.spritePath : Sprites.LIGHT_VEIL , c,
                black, enter);
    }

    public Veil(String path,   Coordinates c, boolean black, boolean enter) {
        super(c, path);
        this.black = black;
        this.enter = enter;
    }

    @Override
    public void init() {
        super.init();
        if (black)
            sprite.setBlending(GenericEnums.BLENDING.INVERT_SCREEN);
        else
            sprite.setBlending(GenericEnums.BLENDING.SCREEN);

    }

    @Override
    protected int getFps() {
        return black ? 17 : 20;
    }

    @Override
    public boolean checkVisible() {
        //TODO gdx events - just send a msg to close...
        // if (puzzle.isActive()) {
        //     return !enter;
        // }
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
        if (black)
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
