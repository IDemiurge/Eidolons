package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.game.battlecraft.logic.meta.igg.pale.PaleAspect;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.particles.EmitterPools;
import eidolons.libgdx.texture.Sprites;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.system.launch.CoreEngine;
import net.miginfocom.layout.Grid;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class GridObject extends GroupX {
    protected SpriteX sprite;
    protected Map<EmitterActor, Vector2> emitters = new XLinkedMap<>();
    protected Coordinates c;
    protected double visionRange;
    private String spritePath;
    protected boolean initialized;
    private boolean flipX;

    public GridObject(Coordinates c, String spritePath) {
        this.c = c;
        this.spritePath = spritePath;
        //black underlay?
        visionRange = getDefaultVisionRange();

    }

    protected abstract boolean isClearshotRequired();

    protected abstract double getDefaultVisionRange();

    public boolean checkVisible() {
        if (CoreEngine.isIDE()) {
            if (CoreEngine.isLiteLaunch())
                return true;
        }
        if (isClearshotRequired()) {

        }
        if (PaleAspect.ON) {
            return true;
        }
        if (Eidolons.getMainHero().getCoordinates().dst_(c) > visionRange) {
            return false;
        }
        return true;
    }

    protected EmitterActor createEmitter(String path, int offsetX, int offsetY) {
        path = PathFinder.getVfxAtlasPath() + path;
        EmitterActor emitter = EmitterPools.getEmitterActor(path);
        initEmitter(emitter, offsetX, offsetY);
        return emitter;
    }

    protected void initEmitter(EmitterActor emitter, int offsetX, int offsetY) {
        if (emitters == null) {
            emitters = new LinkedHashMap<>();
        }
        emitters.put(emitter, new Vector2(offsetX, offsetY));
        addActor(emitter);
//        emitter.start();
//        emitter.setZIndex(1);
        emitter.setPosition(getWidth() / 2 + offsetX, getHeight() / 2 + offsetY);

    }

    protected void init() {
        getColor().a = 0;
        createEmittersUnder();
        sprite = new SpriteX(spritePath);
        sprite.setFlipX(flipX);
        addActor(sprite);
        createEmittersOver();

//        sprite.setBlending(SuperActor.BLENDING.SCREEN);
        sprite.setFps(getFps());
        if (!(getParent() instanceof GridObject)) {
            Vector2 pos = GridMaster.getCenteredPos(
                    (c));
            setPosition(pos.x, pos.y);
        }
        initialized = true;
    }

    protected void createEmittersUnder() {
    }

    protected void createEmittersOver() {
    }

    protected abstract int getFps();

    @Override
    public void act(float delta) {
        if (!initialized) {
            init();
        }
        if (getColor().a == 1) {
            if (!checkVisible()) {
                fadeOut();
            }
        } else {
            if (getColor().a == 0) {
                if (checkVisible()) {
                    fadeIn();
                }
            }
        }
        sprite.setColor(getColor());
        super.act(delta);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void fadeOut() {
        super.fadeOut();
        for (EmitterActor emitterActor : emitters.keySet()) {
            emitterActor.getEffect().getEmitters().forEach(e -> e.allowCompletion());
        }
    }

    @Override
    public void fadeIn() {
        super.fadeIn();
        for (EmitterActor emitterActor : emitters.keySet()) {
            emitterActor.getEffect().getEmitters().forEach(e -> {
                e.reset();
                e.start();
            });
        }
    }

    protected boolean isHideWhenFade() {
        return false;
    }

    public Coordinates getCoordinates() {
        return c;
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public boolean getFlipX() {
        return flipX;
    }
}
