package eidolons.system.utils;

import eidolons.game.battlecraft.DC_Engine;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryFactory;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.xml.XML_Writer;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 5/21/2018.
 */
public class IconFinder {


    public static void main(String[] args) {
        DC_Engine.mainMenuInit();
        for (ObjType sub : DataManager.getTypes(DC_TYPE.WEAPONS)) {
            String icon= InventoryFactory.getWeaponIconPath(sub);
            sub.setImage(icon);
        }
        for (ObjType sub : DataManager.getTypes(DC_TYPE.ACTIONS)) {

            String icon=sub.getImagePath();
            //default weapon?!
//            GdxImageMaster.getAttackActionPath()

            sub.setImage(icon);
        }

        XML_Writer.saveAll();
    }
}
