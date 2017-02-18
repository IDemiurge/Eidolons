package main.entity.item;

import main.content.OBJ_TYPE;
import main.content.DC_TYPE;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.MicroGame;
import main.game.logic.battle.player.Player;

public class ItemFactory {

    public static DC_HeroItemObj createItemObj(ObjType type, Player originalOwner, MicroGame game,
                                               Ref ref, boolean quick) {
        if (type == null) {
            return null;
        }
        OBJ_TYPE TYPE = type.getOBJ_TYPE_ENUM();
        switch ((DC_TYPE) TYPE) {
            case TRAP:
                return new DC_QuickItemObj(type, originalOwner, game, ref, true);
            case ARMOR:
                return new DC_ArmorObj(type, originalOwner, game, ref);
            case ITEMS:
                DC_QuickItemObj dc_QuickItemObj = new DC_QuickItemObj(type, originalOwner, game,
                        ref);
                return dc_QuickItemObj;
            case WEAPONS:
                if (!quick) {
                    return new DC_WeaponObj(type, originalOwner, game, ref);
                }

                return new DC_QuickItemObj(type, originalOwner, game, ref, true);
            case JEWELRY:
                return new DC_JewelryObj(type, originalOwner, game, ref);

            default:
                break;
        }
        return null;
    }

}
