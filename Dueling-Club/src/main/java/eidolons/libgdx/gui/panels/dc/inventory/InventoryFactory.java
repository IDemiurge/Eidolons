package eidolons.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.content.PARAMS;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.gui.UiMaster;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.libgdx.texture.TextureCache;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
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

    public InventoryValueContainer get(Entity entity, CELL_TYPE cellType) {
        int size = UiMaster.getIconSize();
        String path = getWeaponIconPath(entity) ;
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
            container.addListener( new ValueTooltip(entity.getName()+"\n"+
             vals).getController());
        }
        container.setEntity(entity);
        container.setCellType(cellType);
        container.setHandler(handler);
        return container;
    }

    private String getTooltipsVals(Entity entity) {
        String text="";
        if (entity!=null ){
            Ref ref = Eidolons.getMainHero().getRef().getCopy();
            ref.setID(KEYS.SKILL,  entity.getId());

            if (entity.getOBJ_TYPE_ENUM() instanceof DC_TYPE) {
                switch (((DC_TYPE) entity.getOBJ_TYPE_ENUM())) {
                    case WEAPONS:
                    case ARMOR:
                        text += "\n" +PARAMS.DURABILITY.getName()+ ": "+
                         StringMaster.getCurrentOutOfBaseVal(entity, PARAMS.DURABILITY);
                }

            }

            text +="\n"+ entity.getName();
            text +="\n"+ entity.getProperty(G_PROPS.TOOLTIP);
            text +="\n"+ TextParser.parse( entity.getDescription(),
             ref, TextParser.TOOLTIP_PARSING_CODE, TextParser.INFO_PARSING_CODE);
            text +="\n"+PARAMS.WEIGHT.getName()+ ": "+ entity.getParam(PARAMS.WEIGHT);
        }
        return text;
    }

    public static String getWeaponIconPath(Entity entity) {
        if (entity == null) {
            return "";
        }
        String baseType=entity.getName();
        if (entity instanceof DC_WeaponObj) {
            baseType = ((DC_WeaponObj) entity).getBaseTypeName();
        }
        String  path = StrPathBuilder.build(PathFinder.getItemIconPath(),
         baseType + ".png");
        if (!ImageManager.isImage(path))
            path = entity.getImagePath();
        return path;
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
