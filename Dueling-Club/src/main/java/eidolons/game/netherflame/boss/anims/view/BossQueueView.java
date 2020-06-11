package eidolons.game.netherflame.boss.anims.view;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.game.netherflame.boss.anims.generic.BossVisual;
import eidolons.game.netherflame.boss.logic.entity.BossUnit;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.bf.grid.cell.QueueView;
import eidolons.libgdx.bf.grid.cell.UnitViewOptions;

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

