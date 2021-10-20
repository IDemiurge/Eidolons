package libgdx.bf.decor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.core.Core;
import libgdx.bf.mouse.InputController;
import libgdx.gui.generic.ContainerGroup;
import libgdx.particles.EmitterActor;
import libgdx.screens.CustomSpriteBatch;
import libgdx.screens.ScreenMaster;
import libgdx.shaders.DarkShader;
import libgdx.shaders.ShaderDrawer;
import main.system.launch.CoreEngine;

/*
visibility from cell - ideally separate for vfx/textures
 */
public class CellDecor extends ContainerGroup {
    Actor actor;
    private final int sightRange; //TODO we actually need to DARKEN it if...
    private final DC_Cell cell; //can we place it on a platform?..
    private final boolean hideOrDarken;
    float x1, x2, y1, y2;
    private boolean withinCamera;
    private boolean hidden;
    private ShaderProgram shader;
    private EmitterActor emitterActor;
    private Color baseColor;

    public CellDecor(Actor actor, int sightRange, DC_Cell cell, boolean hideOrDarken) {
        addActor(this.actor = actor);
        this.sightRange = sightRange;
        this.cell = cell;
        this.hideOrDarken = hideOrDarken;
        if (hideOrDarken){

        }
        if (x2 < actor.getX() + actor.getWidth()) x2 = actor.getX() + actor.getWidth();
        if (y2 < actor.getY() + actor.getHeight()) y2 = actor.getY() + actor.getHeight();
        if (x1 > actor.getX()) x1 = actor.getX();
        if (y1 > actor.getY()) y1 = actor.getY();

    }

    public CellDecor(EmitterActor emitterActor, Actor actor, int defaultSightRange,
                     DC_Cell cell, boolean sprite) {
        this(actor, defaultSightRange, cell, sprite);
        addActor(this.emitterActor = emitterActor);
        emitterActor.start();
    }

    @Override
    public void act(float delta) {
        if (((hidden || !checkVisible()))) {
            if (hideOrDarken) fadeOut();
            else shader = DarkShader.getDarkShader();
        } else {
            if (checkVisible()) {
                if (hideOrDarken) fadeIn();
                else shader = null;
            }
        }
        super.act(delta);
    }

    @Override
    public void fadeOut() {
        super.fadeOut();
        if (emitterActor != null) {
            emitterActor.allowFinish();
        }
    }

    public void setBaseColor(Color baseColor) {
        this.baseColor = baseColor;
        actor.setColor(new Color(baseColor));
    }

    public Color getBaseColor() {
        return baseColor;
    }

    @Override
    public void fadeIn() {
        if (!CoreEngine.isLevelEditor())
            super.fadeIn();
        if (emitterActor != null) {
            emitterActor.reset();
            emitterActor.start();
        }
    }

    private boolean checkVisible() {
        if (CoreEngine.isLevelEditor())
            return true;
        return !(Core.getGame().getManager().
                getMainHeroCoordinates().dst_(cell.getCoordinates()) > sightRange);
    }

    public InputController getController() {
        return ScreenMaster.getScreen().controller;
    }

    public boolean isWithinCameraCheck() {
        return getController().isWithinCamera(
                x1+getX(), y1+getY(), x2 - x1+getX(), y2 - y1+getY(), true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (shader != null)
        if (parentAlpha != ShaderDrawer.SUPER_DRAW) {
            ShaderDrawer.drawWithCustomShader(this, batch, shader);
            return;
        }
        if (!CoreEngine.isLevelEditor()) {
            if (InputController.cameraMoved)
                withinCamera = isWithinCameraCheck();
            if (!withinCamera) {
                return;
            }
        }
        super.draw(batch, parentAlpha);
        ((CustomSpriteBatch) batch).resetBlendingLite();
    }

}
