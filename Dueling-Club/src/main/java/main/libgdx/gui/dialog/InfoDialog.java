package main.libgdx.gui.dialog;

import main.entity.obj.DC_HeroObj;
import main.libgdx.gui.layout.LayoutParser;
import main.libgdx.gui.panels.sub.Comp;
import main.libgdx.gui.panels.sub.Container;

/**
 * Created by JustMe on 1/5/2017.
 */
public class InfoDialog extends Dialog {

    private   LayoutParser parser;
    Container top;
    Comp portrait;
    Comp portraitBg;
    Container armor;
    Container armorTraits;
    Container mainWeapon;
    Container offWeapon;
    Container attributes;
    Container dynamicParams;
    Container mainParams;
    Container params;
    Container resistances;

    Container description;
    Container lore;
    String mainLayout = "attributes: 0 0; ";
    public final static String bgPath = "";



    public InfoDialog(DC_HeroObj unit) {
        super(bgPath);
//        armor = new Container( ->{
//           add(new ObjComp(unit.getArmor()), x, y)
//        });

        parser=new LayoutParser(this);
        parser.parse(this, mainLayout, attributes  );
    }













}
