package boss.demo.anims;

import boss.anims.BossAnim3dHandler;
import boss.anims.BossAnims;
import boss.anims.generic.BossSwitchVisuals;
import boss.anims.generic.BossVisual;
import boss.logic.BossCycle;
import com.badlogic.gdx.Input;
import boss.BossManager;
import eidolons.libgdx.texture.Sprites;

public class DemoAnimHandler3d extends BossAnim3dHandler {
    public DemoAnimHandler3d(BossManager manager) {
        super(manager);
    }

    @Override
    public String getSpritePath() {
        return Sprites.BOSS_HARVESTER;
    }

    @Override
    public void toggleActive(BossCycle.BOSS_TYPE type, boolean active) {
        // cinematics(type, active);
        BossVisual visual= getVisual(type);
        visual.setActive(active);

    }

    @Override
    protected void keyTyped(int i) {
        super.keyTyped(i);
        switch (i) {
            case Input.Keys.TAB:
                ((BossSwitchVisuals) getVisual(BossCycle.BOSS_TYPE.melee)).toggle();
                break;
            case  Input.Keys.F: {
                ((BossSwitchVisuals) getVisual(BossCycle.BOSS_TYPE.melee)).
                        getCurrent().animate(BossAnims.BOSS_ANIM_COMMON.attack);
                break;
            }
            case Input.Keys.B:
                ((BossSwitchVisuals) getVisual(BossCycle.BOSS_TYPE.melee)).
                        getCurrent().animate(BossAnims.BOSS_ANIM_COMMON.hit);
                break;
            }
    }
}
