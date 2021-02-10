package libgdx.bf.grid.cell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import libgdx.bf.overlays.bar.HpBar;

/**
 * Created by JustMe on 11/17/2018.
 */
public interface HpBarView {

      void resetHpBar( );
      HpBar getHpBar();
      void setHpBar(HpBar hpBar);

    default Actor getActor() {
        return (Actor) this;
    }

    default void animateHpBarChange() {
        if (!getHpBar().isVisible())
            return;

        getHpBar().animateChange();
    }
}
