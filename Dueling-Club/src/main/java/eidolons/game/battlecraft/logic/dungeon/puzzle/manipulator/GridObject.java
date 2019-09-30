package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.game.battlecraft.logic.meta.igg.pale.PaleAspect;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.gui.generic.GroupWithEmitters;
import eidolons.libgdx.particles.DummyEmitterActor;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.particles.EmitterPools;
import main.data.XLinkedMap;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.io.File;
import java.util.*;

public abstract class GridObject extends GroupWithEmitters<EmitterActor> {
    protected SpriteX sprite;
    protected Map<EmitterActor, Vector2> emitters = new XLinkedMap<>();
    protected Coordinates c;
    protected double visionRange;
    private String spritePath;
    protected boolean initialized;
    private boolean flipX;
    String key;
    private boolean hidden;
    private Boolean under;

    public GridObject(Coordinates c, String spritePath) {
        this.c = c;
        this.spritePath = spritePath;
        //black underlay?
        visionRange = getDefaultVisionRange();
        if (!StringMaster.isEmpty(spritePath)) {
            setKey(StringMaster.cropFormat(PathUtils.getLastPathSegment(spritePath)));
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
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

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
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
        try {
            sprite = new SpriteX(spritePath);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        if (sprite != null) {
            if (sprite.getSprite() == null) {
                sprite = null; //TODO igg demo fix
            } else {
                sprite.setFlipX(flipX);
                sprite.setFps(getFps());
                addActor(sprite);
            }
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
        for (String path : ContainerUtils.openContainer(paths)) {
            List<File> files = FileManager.getFilesFromDirectory(PathFinder.getVfxAtlasPath() + path, false, false);
            Collections.shuffle(files);
            for (File file : files) {
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
        if (getEmitters().iterator().hasNext())
            if (!(getEmitters().iterator().next() instanceof DummyEmitterActor)) {
                super.draw(batch, parentAlpha);
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
        if (((hidden || !checkVisible()))) {
            fadeOut();
        } else {
            {
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
        if (getColor().a == 1) {
            super.fadeOut();
        }
        for (EmitterActor emitterActor : emitters.keySet()) {
            emitterActor.getEffect().getEmitters().forEach(e -> e.allowCompletion());
        }
    }

    @Override
    public void fadeIn() {

        if (getColor().a == 0)
            super.fadeIn();
        for (EmitterActor emitterActor : emitters.keySet()) {
            emitterActor.getEffect().getEmitters().forEach(e -> {
                if (e.isComplete()) {
                    e.reset();
                    e.start();
                }
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
    public Collection<EmitterActor> getEmitters() {
        return emitters.keySet();
    }

    public void fadeOut(boolean manual) {
        fadeOut();
        hidden = true;
    }

    public void setUnder(Boolean under) {
        this.under = under;
    }

    public Boolean getUnder() {
        return under;
    }
}
