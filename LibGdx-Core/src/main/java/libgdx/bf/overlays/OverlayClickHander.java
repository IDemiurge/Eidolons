package libgdx.bf.overlays;

import main.entity.Entity;

/**
 * Created by JustMe on 5/12/2018.
 */
public class OverlayClickHander {
    public static void handle(Entity entity, GridOverlaysManager.OVERLAY overlay) {
        switch (overlay) {
            case BAG:
                // if (ExplorationMaster.isExplorationOn()) {
                //     if (entity == Core.getMainHero()
                //      || PositionMaster.getDistance(Core.getMainHero(), (Obj) entity) == 0
                //      ) {
                //         DC_UnitAction action = Core.getMainHero().getAction(ActionEnums.PICK_UP);
                //         if (action == null) {
                //             Core.getGame().getDroppedItemManager().reset(Core.getMainHero().getX(),
                //              Core.getMainHero().getY());
                //             Core.getGame().getActionManager().resetActions(Core.getMainHero());
                //         }
                //         Core.activateMainHeroAction(ActionEnums.PICK_UP);
                //     } else {
                //         FloatingTextMaster.getInstance().createFloatingText(VisualEnums.TEXT_CASES.REQUIREMENT,
                //          "Move onto this cell to pick up items", Core.getMainHero());
                //     }
                // }
        }
    }
}
