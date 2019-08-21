package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.game.battlecraft.logic.meta.igg.pale.PaleAspect;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.gui.generic.GroupWithEmitters;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.particles.EmitterPools;
import main.data.XLinkedMap;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.FileManager;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class GridObject extends GroupWithEmitters {
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
//        if (CoreEngine.isIDE()) {
//            if (CoreEngine.isLiteLaunch())
//                return true;
//        }
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
        if (emitter == null) {
            return;
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
        if (sprite.getSprite() == null) {
            sprite = null; //TODO igg demo fix
        }
        if (sprite != null) {
            sprite.setFlipX(flipX);
//        sprite.setBlending(SuperActor.BLENDING.SCREEN);
            sprite.setFps(getFps());
            addActor(sprite);
        }
        createEmittersOver();

        if (!(getParent() instanceof GridObject)) {
            Vector2 pos = GridMaster.getCenteredPos(
                    (c));
            setPosition(pos.x, pos.y);
        }
        initialized = true;

        if (sprite != null)
            sprite.act(RandomWizard.getRandomFloatBetween(0, 4));
    }

    protected void createEmittersUnder() {
    }

    protected void createEmittersOver() {
    }

    protected void createEmittersFromFolder(String paths, float vfxChance, int max) {
        int i = 0;
        for (String path : ContainerUtils.openContainer(paths))
            for (File file : FileManager.getFilesFromDirectory(PathFinder.getVfxAtlasPath() + path, false, false)) {
                if (!RandomWizard.chance((int) (100 * vfxChance))) {
                    continue;
                }
                createEmitter(PathUtils.removePreviousPathSegments(
                        FileManager.formatPath(file.getPath(), true)
                        , FileManager.formatPath(PathFinder.getVfxAtlasPath(), true
                        )), c.x, c.y);
                i++;
                if (i >= max) {
                    return;
                }
            }
    }

    protected void createEmittersFromString(String data, boolean mirrorX, boolean mirrorY, float vfxChance) {
        for (String substring : ContainerUtils.openContainer(data)) {
            if (vfxChance != 0)
                if (!RandomWizard.chance((int) (100 * vfxChance))) {
                    continue;
                }
            Coordinates c = AbstractCoordinates.createFromVars(substring);
            createEmitter(VariableManager.removeVarPart(substring), c.x, c.y);
            if (mirrorX) {
                createEmitter(VariableManager.removeVarPart(substring), c.x, c.y).setFlipX(true);
            }
            if (mirrorY) {
                createEmitter(VariableManager.removeVarPart(substring), c.x, c.y).setFlipY(true);
            }
        }
    }

    protected abstract int getFps();

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (isIgnored()) {
            return;
        }
        if (!initialized) {
            init();
            act(RandomWizard.getRandomFloat());
        }
        super.draw(batch, parentAlpha);
    }

    protected boolean isIgnored() {
        return false;
    }

    @Override
    public void act(float delta) {
        if (isIgnored()) {
            return;
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
        if (sprite != null)
            sprite.setColor(getColor());
        super.act(delta);
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

    @Override
    public Collection getEmitters() {
        return emitters.keySet();
    }
}
