package eidolons.entity.item;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.objects.vendor.GoldMaster;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.NumberUtils;

public class ItemFactory {

    public static DC_HeroItemObj createItemObj(ObjType type, Player originalOwner, GenericGame game,
                                               Ref ref, boolean quick) {
        return createItemObj(type.getName(), type.getOBJ_TYPE_ENUM(), originalOwner, game, ref, quick);
    }

    public static DC_HeroItemObj createItemObj(String subString, OBJ_TYPE TYPE, Player originalOwner, GenericGame game,
                                               Ref ref, boolean quick) {
        String var = VariableManager.getVar(subString);
        String typeName = VariableManager.removeVarPart(subString);
        ObjType type = DataManager.getType(typeName, TYPE);
        if (type == null) {
            type = DataManager.getType(subString, TYPE);
        }
        if (type == null) {
            return null;
        }
        TYPE = type.getOBJ_TYPE_ENUM();
        DC_HeroItemObj item = createItem(type, TYPE, originalOwner, ref, game, quick);
        if (GoldMaster.isGoldPack(type)
         && NumberUtils.isInteger(var)
         ) {
            item.setParameter(GoldMaster.GOLD_VALUE, Integer.parseInt(var));
            item.getType(). setParameter(GoldMaster.GOLD_VALUE, Integer.parseInt(var));
        }
        return item;
    }

    private static DC_HeroItemObj createItem(ObjType type, OBJ_TYPE TYPE, Player originalOwner, Ref ref, GenericGame game, boolean quick) {

        switch ((DC_TYPE) TYPE) {
            //            case TRAP:
            //                return new DC_QuickItemObj(type, originalOwner, game, ref, true);
            case ARMOR:
                return new DC_ArmorObj(type, originalOwner, game, ref);
            case ITEMS:
                return new DC_QuickItemObj(type, originalOwner, game,
                 ref);
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

    public static DC_HeroItemObj createItemObj(ObjType type, Unit hero, boolean quick) {
        return createItemObj(type, hero.getOwner(), hero.getGame(), hero.getRef(), quick);
    }

    public static DC_HeroItemObj createItemObj(String name, DC_TYPE type, boolean quick) {
        return createItemObj(name, type, DC_Player.NEUTRAL, Eidolons.getGame(), new Ref(), quick);
    }
}
