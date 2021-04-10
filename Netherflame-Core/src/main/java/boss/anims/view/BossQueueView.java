package boss.anims.view;

import boss.anims.generic.BossVisual;
import boss.logic.entity.BossUnit;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.content.consts.libgdx.GdxColorMaster;

public class BossQueueView extends QueueView {
    private final BossVisual parent;

    //intent icon?
    /*
    GuiEventManager.trigger(TARGET_SELECTION, BaseView.this);
     */

    public BossQueueView(BossUnit unit, String path, BossVisual parent) {
        super(createOptions(unit, path), 0);
        // portrait= new FadeImageContainer(path);
        this.parent = parent;
        hpBar = new HpBar(unit);
    }

    private static UnitViewOptions createOptions(BossUnit unit, String path) {
        UnitViewOptions unitViewOptions = new UnitViewOptions(unit);
        unitViewOptions.setPortraitPath(path);
        unitViewOptions.setTeamColor(GdxColorMaster.RED);
        // unitViewOptions.setEmblem();
        return unitViewOptions;
    }

    @Override
    public Actor getParentView() {
        return parent;
    }

}

