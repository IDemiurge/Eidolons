package main.libgdx.gui.dialog;

import main.content.DC_ContentManager;
import main.entity.obj.DC_HeroObj;
import main.libgdx.gui.layout.LayoutParser.LAYOUT;
import main.libgdx.gui.panels.sub.Comp;
import main.libgdx.gui.panels.sub.Container;
import main.libgdx.gui.panels.sub.EntityContainer;
import main.libgdx.gui.panels.sub.ValueContainer;

/**
 * Created by JustMe on 1/5/2017.
 */
public class InfoDialog extends Dialog {
    Container top;
    Container fxAndAbils;
    Container armor;
    Container armorTraits;
    Container mainWeapon;
    Container offWeapon;
    ValueContainer attributes;
    Container dynamicParams;
    Container points;
    Container mainParams;
    Container params;
    Container resistances;

    Container description;
    Container lore;
    String mainLayout = "attributes: 0 0; ";
    public final static String bgPath =
     "UI\\components\\2017\\dialog\\info\\info panel big.jpg";


    public InfoDialog(DC_HeroObj unit) {
        super(bgPath);
//        VISUALS.DOUBLE_CONTAINER,
        fxAndAbils = new Container("fxAndAbils") {
            @Override
            public void layout() {

                EntityContainer effects = new EntityContainer("Active Effects", 32, 6, 4,
                        () -> {
                            return unit.getBuffs();
                        });
                EntityContainer abilities = new EntityContainer("Special Abilities", 32, 6, 4,
                        () -> {
                            return unit.getPassives();
                        });
            }
        };


        armor = new Container("", LAYOUT.HORIZONTAL) {
            @Override
            public void layout() {

                EntityContainer buffs = new EntityContainer("Buffs", 32, 2, 2,
                        () -> {
                            return unit.getArmor().getBuffs();
                        });

                EntityContainer traits = new EntityContainer("Traits", 32, 2, 2,
                        () -> {
                            return unit.getArmor().getPassives();
                        });

            }
        };

        attributes = new ValueContainer(unit, 5, 2, () -> {
            return DC_ContentManager.getFinalAttributes();
        }) {
            @Override
            protected boolean isNameDisplayed() {
                return super.isNameDisplayed();
            }
        };


        dynamicParams = new Container("", LAYOUT.HORIZONTAL);
        mainWeapon = new Container("", LAYOUT.HORIZONTAL);
        description = new Container("", LAYOUT.HORIZONTAL);
        resistances = new Container("", LAYOUT.HORIZONTAL);
        mainParams = new Container("", LAYOUT.HORIZONTAL);
        points = new Container("", LAYOUT.HORIZONTAL);
        top = new Container("", LAYOUT.HORIZONTAL);
        {
            Comp portrait;
            Comp portraitBg;
        }
        params = new Container("", LAYOUT.HORIZONTAL);
        offWeapon = new Container("", LAYOUT.HORIZONTAL);
        lore = new Container("", LAYOUT.HORIZONTAL);

        setComps(
         //from bottom left
                fxAndAbils, attributes, dynamicParams, mainWeapon, description,
                new Wrap(false), //next column
                resistances, armor, mainParams, points, top,
                new Wrap(false),//next column
                new Space(false, 0.2f), //leave 20% space
                params, offWeapon, lore
        );
    }
}














