package libgdx.gui.dungeon.panels.dc.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.content.PARAMS;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.unit.Unit;
import eidolons.game.core.Core;
import libgdx.assets.texture.TextureCache;
import main.content.DC_TYPE;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.NumberUtils;
import main.system.text.TextParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by JustMe on 3/31/2017.
 */
public class InventoryFactory {
    private static final ObjectMap<Entity, InvItemActor> cache = new ObjectMap<>();
    private final InventoryClickHandler handler;

    public InventoryFactory(InventoryClickHandler inventoryClickHandler) {
        this.handler = inventoryClickHandler;
    }


    public static String getTooltipsVals(Entity entity) {
        String text = "";
        if (entity != null) {
            Ref ref = Core.getMainHero().getRef().getCopy();
            ref.setID(KEYS.SKILL, entity.getId());
            DC_TYPE t = (DC_TYPE) entity.getOBJ_TYPE_ENUM();
            if (entity.getOBJ_TYPE_ENUM() instanceof DC_TYPE) {
                switch (t) {
                    case WEAPONS:
                        DC_WeaponObj weapon;
                        if (entity instanceof DC_QuickItemObj) {
                            weapon = ((DC_QuickItemObj) entity).getWrappedWeapon();
                        } else weapon =
                         (DC_WeaponObj) entity;
                        int min = weapon.calculateDamageMin(ref);
                        int max = min + weapon.calculateDiceMax();
                        text += "\n" + PARAMS.DAMAGE.getName() + ": " + min
                         + "-" + max;

                        text += "\n" + PARAMS.ATTACK_MOD.getName() + ": " + entity.getIntParam(PARAMS.ATTACK_MOD);

                        text += "\n" + PARAMS.DURABILITY.getName() + ": " +
                         NumberUtils.getCurrentOutOfBaseVal(entity, PARAMS.DURABILITY);
                        break;
                    case ARMOR:
                        text += "\n" + PARAMS.ARMOR.getName() + ": " +
                         entity.getIntParam(PARAMS.ARMOR);
                        text += "\n" + PARAMS.COVER_PERCENTAGE.getName() + ": " +
                         entity.getIntParam(PARAMS.COVER_PERCENTAGE) + "%";

                        text += "\n" + PARAMS.DURABILITY.getName() + ": " +
                         NumberUtils.getCurrentOutOfBaseVal(entity, PARAMS.DURABILITY);
                        break;
                }

            }
            text += "\n" + entity.getName();
            switch (t) {
                case JEWELRY:
                case ITEMS:
                    break;

                    default:
                        text += "\n" + TextParser.parse(entity.getDescription(),
                         ref, TextParser.TOOLTIP_PARSING_CODE, TextParser.INFO_PARSING_CODE);
                        text += "\n" + PARAMS.WEIGHT.getName() + ": " + entity.getParam(PARAMS.WEIGHT);
            }
            //            text +="\n"+ entity.getProperty(G_PROPS.TOOLTIP);
        }
        return text;
    }

    public InvItemActor get(Entity entity, VisualEnums.CELL_TYPE cellType) {
        return get(entity, cellType, true);
    }

    public InvItemActor get(Entity entity, VisualEnums.CELL_TYPE cellType, boolean cached) {
        InvItemActor container = null;
        if (entity != null)
            if (cached) {
                container = cache.get(entity);
                if (container != null) {
                    container.setCellType(cellType);
                    return container;
                }
            }
        DC_HeroItemObj item = null;
        if (entity instanceof DC_HeroItemObj) {
            item = (DC_HeroItemObj) entity;
        } else {
            if (entity instanceof Unit) {
                container = new InvItemActor(entity.getImagePath(), 128);
            }
        }
        if (container == null)
            container = new InvItemActor(
             item, cellType, handler);

        if (entity != null)
            if (cached) {
                cache.put(entity, container);
            }
        return container;
    }

    private TextureRegion getEmptyImageForCell(VisualEnums.CELL_TYPE cellType) {

        return TextureCache.getOrCreateR(cellType.getSlotImagePath());
    }

    public List<InvItemActor> getList(Collection<? extends Entity> items,
                                      VisualEnums.CELL_TYPE type) {
        List<InvItemActor> list = new ArrayList<>();
        items.forEach(item -> list.add(
         get(item, type)));
        return list;
    }

}
