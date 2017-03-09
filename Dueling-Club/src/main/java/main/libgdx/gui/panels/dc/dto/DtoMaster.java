package main.libgdx.gui.panels.dc.dto;

import com.badlogic.gdx.graphics.Texture;
import main.content.VALUE;
import main.entity.Entity;
import main.libgdx.gui.panels.dc.unitinfo.dto.IconGridDTO;
import main.libgdx.gui.panels.dc.unitinfo.dto.TabsDTO;
import main.libgdx.gui.panels.dc.unitinfo.dto.ValueDTO;
import main.libgdx.texture.TextureCache;
import main.system.images.ImageManager;

import java.util.Collection;

/**
 * Created by JustMe on 3/1/2017.
 */
public class DtoMaster {

    public static IconGridDTO getIconGrid(Collection<? extends Entity> items) {
        IconGridDTO grid = new IconGridDTO();
        items.forEach(e -> {
            grid.add(e.getName(), TextureCache.getOrCreate(e.getImagePath()));
        });
        return grid;
    }


    public static TabsDTO getTabs(Entity unit,
                                  Collection<Collection<VALUE>> parameters) {
        TabsDTO tabs = new TabsDTO();
        parameters.forEach(params -> {
            tabs.newTab();
            params.forEach(param -> {
                tabs.addValue(getValue(param, unit));
            });
        });

        return tabs;
    }

    public static ValueDTO getValue(VALUE param, Entity unit) {
        return getValue(param, unit, null);
    }

    public static ValueDTO getValue(VALUE param, Entity unit, Boolean showName) {
        if (showName == null)
            showName = isShowName(param);
        String name = null;
        if (showName)
            name = param.getShortName();
        Texture icon = TextureCache.getOrCreate(ImageManager.getValueIconPath(param));
        String text = unit.getValue(param);


        return new ValueDTO(icon,
                name,
                text);

    }

    private static Boolean isShowName(VALUE param) {
        //not for Main, Resources, ... ?

        return true;
    }
}
