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
    Container fxAndAbils;
    Container armor;
    Container armorTraits;
    Container mainWeapon;
    Container offWeapon;
    Container attributes;
    Container dynamicParams;
    Container points;
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

        armor = new Container(""){
            @Override
            public void layout() {
//                public class EntityContainer extends PagedPanel {
//
//                    public EntityContainer(String name, int itemSize, int columns, int rows
//                     ,   Supplier<Collection> supplier
//                    ) {
//                        super(columns, rows);
////        this.name=name;
////        this.itemSize=itemSize;
////        this.columns=columns;
////        this.rows=rows;
////        this.supplier=supplier;
//                    }
//                }
//                EntityContainer buffs = new EntityContainer("Buffs", 32, 2, 2,
//                 ()->{
//                     return unit.getArmor().getBuffs();
//                 });
//
//                EntityContainer traits = new EntityContainer("Traits", 32, 2, 2,
//                 ()->{
//                     return unit.getArmor().getPassives();
//                 });
//
//
//                super.layout();
            }
        };
                parser = new LayoutParser(this);
                parser.parse(this,
                 mainLayout, fxAndAbils, attributes, dynamicParams, mainWeapon, description,

                 resistances, armor, mainParams, points, portrait,

                 params, offWeapon, lore


                );
            }
        }














