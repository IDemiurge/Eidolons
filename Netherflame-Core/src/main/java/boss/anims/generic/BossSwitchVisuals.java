package boss.anims.generic;

import boss.logic.entity.BossUnit;
import boss.anims.BossAnims;

public abstract class BossSwitchVisuals extends BossVisual {
    protected BossVisual active;
    protected BossVisual passive;
    protected BossVisual current;
    private boolean activeOn;

    public BossSwitchVisuals(BossUnit unit) {
        super(unit);
        addActor(active = createActive(unit));
        addActor(passive = createPassive(unit));
        active.setVisible(false);
        // Vector2 v = getOffsetActive();
        // active.setPosition(v.x, v.y);
        //what about mouse events? all that stuff? Just delegate to the ..?
    }

    public void toggle() {
        toggle(!activeOn);
    }

    public void toggle(boolean activeOn) {
        this.activeOn = activeOn;
        (!activeOn ? active : passive).fadeOut();
        current = activeOn ? active : passive;
        current.setVisible(true);
        current.fadeIn();
    }

    @Override
    public void animate(BossAnims.BOSS_ANIM_COMMON anim) {
        current.animate(anim);
    }

    protected abstract BossVisual createPassive(BossUnit unit);

    protected abstract BossVisual createActive(BossUnit unit);

    public BossVisual getCurrent() {
        return current;
    }

    public BossVisual getActive() {
        return active;
    }

    public BossVisual getPassive() {
        return passive;
    }

    @Override
    public void targetHighlight(boolean on) {
        current.targetHighlight(on);
    }

    @Override
    public void setAiMoving(boolean on) {
        current.setAiMoving(on);
    }

    @Override
    public void setActive(boolean on) {
        current.setActive(on);
    }

    @Override
    public void hovered(boolean on) {
        current.hovered(on);
    }
}
