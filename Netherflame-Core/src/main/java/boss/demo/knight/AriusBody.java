package boss.demo.knight;

import boss.anims.generic.BossSwitchVisuals;
import boss.anims.generic.BossVisual;
import boss.logic.entity.BossUnit;
import com.badlogic.gdx.graphics.g2d.Batch;

public class AriusBody extends BossSwitchVisuals {
    public AriusBody(BossUnit unit) {
        super(unit);
    }

    @Override
    protected BossVisual createPassive(BossUnit unit) {
        return new AriusBodyPassive(unit);
    }

    @Override
    protected BossVisual createActive(BossUnit unit) {
        return new AriusBodyActive(unit);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        active.setX(250);
        active.setY(150);
        passive.setX(210);
        passive.setY(210);
    }
    /*

     */

}
