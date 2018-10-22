package eidolons.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_HeroSlotItem;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.libgdx.texture.TextureCache;
import main.content.DC_TYPE;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;
import main.system.text.TextParser;

import java.util.*;

/**
 * Created by JustMe on 3/31/2017.
 */
public class InventoryFactory {
    private static Map<Entity, InvItemActor> cache = new HashMap<>();
    private InventoryClickHandler handler;

    public InventoryFactory(InventoryClickHandler inventoryClickHandler) {
        this.handler = inventoryClickHandler;
    }

    public static String getArmorIconPath(Entity entity) {
        return getItemIconPath(entity);
    }

    public static String getItemIconPath(Entity entity) {
        if (entity == null) {
            return "";
        }
        DC_TYPE TYPE = (DC_TYPE) entity.getOBJ_TYPE_ENUM();
        String basePath = "";
        switch (TYPE) {
            case ARMOR:
                basePath = PathFinder.getArmorIconPath();
                break;
            case WEAPONS:
                basePath = PathFinder.getWeaponIconPath();
                break;
            case JEWELRY:
                basePath = PathFinder.getJewelryIconPath();
                break;
        }
        String typeName = entity.getName();


        if (entity instanceof DC_HeroSlotItem) {
            DC_HeroSlotItem item = ((DC_HeroSlotItem) entity);
            typeName = item.getBaseTypeName();
//            int durability = DataManager.getType(item.getBaseTypeName(),
//             item.getOBJ_TYPE_ENUM()).getIntParam(PARAMS.DURABILITY);
//TODO
//            float perc = new Float(durability) / item.getIntParam(PARAMS.C_DURABILITY);
//
//            if (perc<0.75f){
//                typeName += " 2";
//            }
//            if (perc<1.25f) {
//                typeName += " 1";
//            }
        }

        String path = StrPathBuilder.build(basePath,
         typeName + ".png");
        if (!ImageManager.isImage(path))
            path = entity.getImagePath();
        return path;
    }

    public static String getWeaponIconPath(Entity entity) {
        return getItemIconPath(entity);
    }

    public static String getTooltipsVals(Entity entity) {
        String text = "";
        if (entity != null) {
            Ref ref = Eidolons.getMainHero().getRef().getCopy();
            ref.setID(KEYS.SKILL, entity.getId());

            if (entity.getOBJ_TYPE_ENUM() instanceof DC_TYPE) {
                switch (((DC_TYPE) entity.getOBJ_TYPE_ENUM())) {
                    case WEAPONS:
                        DC_WeaponObj weapon = null;
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
            //            text +="\n"+ entity.getProperty(G_PROPS.TOOLTIP);
            text += "\n" + TextParser.parse(entity.getDescription(),
             ref, TextParser.TOOLTIP_PARSING_CODE, TextParser.INFO_PARSING_CODE);
            text += "\n" + PARAMS.WEIGHT.getName() + ": " + entity.getParam(PARAMS.WEIGHT);
        }
        return text;
    }

    public InvItemActor get(Entity entity, CELL_TYPE cellType) {
        return get(entity, cellType, true);
    }

    public InvItemActor get(Entity entity, CELL_TYPE cellType, boolean cached) {
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
                container = new InvItemActor(entity.getImagePath());
            }
        }
        if (container == null)
            container = new InvItemActor(
             item, cellType, handler);
        if (entity == null) {
            container.addListener(new ValueTooltip(StringMaster.getWellFormattedString(cellType.toString()) +
             " slot").getController());
        } else {
            String vals = getTooltipsVals(entity);
            container.addListener(new ValueTooltip(entity.getName() + "\n" +
             vals).getController());
        }
        if (entity != null)
            if (cached) {
                cache.put(entity, container);
            }
        return container;
    }

    private TextureRegion getEmptyImageForCell(CELL_TYPE cellType) {

        return TextureCache.getOrCreateR(cellType.getSlotImagePath());
    }

    public List<InvItemActor> getList(Collection<? extends Entity> items,
                                      CELL_TYPE type) {
        List<InvItemActor> list = new ArrayList<>();
        items.forEach(item -> list.add(
         get(item, type)));
        return list;
    }

}
