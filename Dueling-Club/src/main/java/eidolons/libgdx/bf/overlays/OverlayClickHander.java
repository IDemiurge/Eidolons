package eidolons.libgdx.bf.overlays;

import eidolons.entity.active.DC_ActionManager;
import eidolons.entity.active.DC_UnitAction;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import eidolons.libgdx.bf.overlays.OverlaysManager.OVERLAY;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.system.math.PositionMaster;

/**
 * Created by JustMe on 5/12/2018.
 */
public class OverlayClickHander {
    public static void handle(Entity entity, OVERLAY overlay) {
        switch (overlay) {
            case BAG:
                if (ExplorationMaster.isExplorationOn()) {
                    if (entity == Eidolons.getMainHero()
                     || PositionMaster.getDistance(Eidolons.getMainHero(), (Obj) entity) == 0
                     ) {
                        DC_UnitAction action = Eidolons.getMainHero().getAction(DC_ActionManager.PICK_UP);
                        if (action == null) {
                            Eidolons.getGame().getDroppedItemManager().reset(Eidolons.getMainHero().getX(),
                             Eidolons.getMainHero().getY());
                            Eidolons.getGame().getActionManager().resetActions(Eidolons.getMainHero());
                        }
                        Eidolons.activateMainHeroAction(DC_ActionManager.PICK_UP);
                    } else {
                        FloatingTextMaster.getInstance().createFloatingText(TEXT_CASES.REQUIREMENT,
                         "Move onto this cell to pick up items", Eidolons.getMainHero());
                    }
                }
        }
    }
}
