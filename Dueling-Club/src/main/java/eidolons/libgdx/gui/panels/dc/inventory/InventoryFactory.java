package eidolons.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroSlotItem;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.gui.UiMaster;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.libgdx.texture.TextureCache;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;
import main.system.text.TextParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by JustMe on 3/31/2017.
 */
public class InventoryFactory {
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
        String baseType = entity.getName();
        if (entity instanceof DC_HeroSlotItem) {
            DC_HeroSlotItem item = ((DC_HeroSlotItem) entity);
            baseType = item.getBaseTypeName();
        }
        String basePath="";
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
        String path = StrPathBuilder.build(basePath,
         baseType + ".png");
        if (!ImageManager.isImage(path))
            path = entity.getImagePath();
        return path;
    }
    public static String getWeaponIconPath(Entity entity) {
        return getItemIconPath(entity);
    }

    public InventoryValueContainer get(Entity entity, CELL_TYPE cellType) {
        int size = UiMaster.getIconSize();
        String path = getWeaponIconPath(entity);
        if (entity != null) {
            if (!C_OBJ_TYPE.ITEMS.equals(entity.getOBJ_TYPE_ENUM())) {
                size = 128;
            }
        }
        InventoryValueContainer container = new InventoryValueContainer(

         entity == null ? getEmptyImageForCell(cellType) :
          TextureCache.getOrCreateSizedRegion(size, path)
         , entity == null ? "Empty" : entity.getName()
        );
        if (entity == null) {
            container.addListener(new ValueTooltip(StringMaster.getWellFormattedString(cellType.toString()) +
             " slot").getController());
        } else {
            String vals = getTooltipsVals(entity);
            container.addListener(new ValueTooltip(entity.getName() + "\n" +
             vals).getController());
        }
        container.setEntity(entity);
        container.setCellType(cellType);
        container.setHandler(handler);
        return container;
    }

    public static  String getTooltipsVals(Entity entity) {
        String text = "";
        if (entity != null) {
            Ref ref = Eidolons.getMainHero().getRef().getCopy();
            ref.setID(KEYS.SKILL, entity.getId());

            if (entity.getOBJ_TYPE_ENUM() instanceof DC_TYPE) {
                switch (((DC_TYPE) entity.getOBJ_TYPE_ENUM())) {
                    case WEAPONS:
                        DC_WeaponObj weapon = null ;
                        if (entity instanceof DC_QuickItemObj)
                        {
                            weapon= ((DC_QuickItemObj) entity).getWrappedWeapon();
                        }
                            else weapon=
                        (DC_WeaponObj) entity;
                        int min = weapon.calculateDamageMin(ref);
                        int max = min + weapon.calculateDiceMax();
                        text += "\n" + PARAMS.DAMAGE.getName() + ": " + min
                         + "-" + max;

                        text += "\n" + PARAMS.ATTACK_MOD.getName() + ": " + entity.getIntParam(PARAMS.ATTACK_MOD);

                        text += "\n" + PARAMS.DURABILITY.getName() + ": " +
                         StringMaster.getCurrentOutOfBaseVal(entity, PARAMS.DURABILITY);
                        break;
                    case ARMOR:
                        text += "\n" + PARAMS.ARMOR.getName() + ": " +
                         entity.getIntParam(PARAMS.ARMOR);
                        text += "\n" + PARAMS.COVER_PERCENTAGE.getName() + ": " +
                         entity.getIntParam(PARAMS.COVER_PERCENTAGE)+"%";

                        text += "\n" + PARAMS.DURABILITY.getName() + ": " +
                         StringMaster.getCurrentOutOfBaseVal(entity, PARAMS.DURABILITY);
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

    private TextureRegion getEmptyImageForCell(CELL_TYPE cellType) {

        return TextureCache.getOrCreateR(cellType.getSlotImagePath());
    }

    public List<InventoryValueContainer> getList(Collection<? extends Entity> items,
                                                 CELL_TYPE type) {
        List<InventoryValueContainer> list = new ArrayList<>();
        items.forEach(item -> list.add(
         get(item, type)));
        return list;
    }

}
