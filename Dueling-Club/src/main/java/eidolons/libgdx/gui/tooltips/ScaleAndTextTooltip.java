package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.anims.actions.ActionMaster;

import java.util.function.Supplier;

/**
 * Created by JustMe on 3/29/2018.
 */
public class ScaleAndTextTooltip extends DynamicTooltip {
    private float scale=1.2f;
    private float duration=0.65f;
    private boolean centered;

    public ScaleAndTextTooltip(Actor actor, Supplier<String> text) {
        super(text);
        setActor(actor);
    }

    @Override
    protected void exited() {
        super.exited();
        ActionMaster.addScaleAction(actor,
         1,
         1,
         getDuration(), isCentered());
    }

    @Override
    protected void entered() {
        super.entered();
        ActionMaster.addScaleAction(actor,
         getScale(),
         getScale(),
         getDuration(), isCentered());
    }

    public float getScale() {
        return scale;
    }

    @Override
    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public boolean isCentered() {
        return centered;
    }

    public void setCentered(boolean centered) {
        this.centered = centered;
    }
}
