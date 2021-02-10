package eidolons.libgdx.particles;

import eidolons.libgdx.particles.spell.SpellVfx;
import main.game.bf.Coordinates;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by JustMe on 12/1/2018.
 */
public class VfxContainer<T extends EmitterActor> extends SpellVfx {

    private final Set<T> nested = new LinkedHashSet<>();

    public VfxContainer(String path) {
        super(path);
    }

    public Set<T> getNested() {
        return nested;
    }

    public void add(T actor) {
        addActor(actor);
        getNested().add(actor);
    }
    @Override
    public void start() {
        super.start();
        nested.forEach(EmitterActor::start);
    }

    @Override
    public void setFlipX(boolean flipX) {
        super.setFlipX(flipX);
    }

    @Override
    public void setFlipY(boolean flipY) {
        super.setFlipY(flipY);
    }

    @Override
    public void offsetAlpha(float alpha) {
        super.offsetAlpha(alpha);
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void setAttached(boolean attached) {
        super.setAttached(attached);
    }

    @Override
    public void setTarget(Coordinates target) {
        super.setTarget(target);
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    public void setSpeed(float speed) {
        super.setSpeed(speed);
    }
}
