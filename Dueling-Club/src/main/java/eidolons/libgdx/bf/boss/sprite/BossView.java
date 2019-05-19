package eidolons.libgdx.bf.boss.sprite;

import eidolons.libgdx.bf.grid.UnitViewOptions;
import eidolons.libgdx.bf.grid.UnitViewSprite;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.List;

/**
 *
 * BossView contains all sprite-parts, controls their animation, as well as
 *  * visibility
 *  * highlight
 *  * zoom
 *  * targeting
 *
 * It should have the same interface as normal unit views
 * But it will do things differently
 */
public class BossView extends UnitViewSprite {
    List<BossPart> parts;

    public BossView(UnitViewOptions o) {
        super(o);
        GuiEventManager.trigger(GuiEventType.BOSS_VIEW_CREATED, this);
    }

}
