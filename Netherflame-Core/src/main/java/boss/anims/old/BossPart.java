package boss.anims.old;

import boss.BOSS_PART;
import com.badlogic.gdx.graphics.g2d.Batch;
import boss.anims.BossAnimHandler;
import boss.anims.BossAnims;
import libgdx.gui.generic.GroupX;

import java.util.Queue;

public class BossPart extends GroupX {
    private final BOSS_PART type;
    private final BossAnimHandler animator;
    /*
    animated via queue - won't interrupt anim until finished (normally)
    via GuiEvents?
     */

    Queue<PartAnim> queue;
    PartAnim current;
    PartAnim idleAnim;
    boolean idle;
    private boolean waitForIdle;

    public BossPart(BOSS_PART part, BossAnimHandler animator) {
        this.type = part;
        this.animator = animator;
    }

    public void init() {
        idleAnim = animator.createAnim(BossAnims.BOSS_ANIM_COMMON.idle, type);
    }

    public void addAnim(PartAnim anim) {
        waitForIdle=true;
        queue.add(anim);
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (waitForIdle){
            if (!idleAnim.draw()){
                current = nextAnim();
                waitForIdle = false;
            }
        }
        if (current != null) {
            if (!current.draw())
                current = nextAnim();
        } else {
            idle = true;
            idleAnim.draw();
        }
        super.draw(batch, parentAlpha);
    }

    private PartAnim nextAnim() {
        if (queue.isEmpty()) {
            return null;
        }
        return queue.poll();
    }

    public BOSS_PART getType() {
        return type;
    }

    public boolean isIdle() {
        return idle;
    }
}
