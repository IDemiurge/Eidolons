package main.libgdx.gui.panels.info;

import main.entity.obj.unit.Unit;
import main.libgdx.gui.layout.LayoutParser.LAYOUT;
import main.libgdx.gui.panels.generic.Container;
import main.libgdx.gui.panels.generic.EntityComp;
import main.libgdx.gui.panels.generic.EntityContainer;
import main.libgdx.gui.panels.generic.TextComp;
import main.libgdx.old.framework.InfoDialog;

/**
 * Created by JustMe on 1/8/2017.
 */
public class WeaponPanel extends Container {
    static String imagePath = InfoDialog.path + "weapon bg.png";

    public WeaponPanel(Unit unit, boolean offhand) {
        super(imagePath, LAYOUT.VERTICAL);
//        if (offhand) flip();

        EntityContainer attacks = new EntityContainer(null, 64, 5, 1,
                () -> unit.getAttacks(offhand), unit
        );
        EntityComp weapon = new EntityComp(
                unit.getWeapon(offhand));
//        weapon2 = new EntityComp(
//         unit.getNaturalWeapon(offhand));
//        traits = new EntityContainer(2, 2, ()-> unit.getWeapon(offhand).getPassives());
//         tabs = new Tabs(natural, weapon);

        TextComp label = new TextComp(unit.getWeapon(offhand).getName());
        setComps(attacks, weapon, label);
    }
}
