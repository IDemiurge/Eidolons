package main.libgdx.gui.dialog;

import main.content.DC_ContentManager;
import main.entity.obj.DC_HeroObj;
import main.libgdx.StyleHolder;
import main.libgdx.gui.layout.LayoutParser.LAYOUT;
import main.libgdx.gui.panels.generic.*;
import main.libgdx.gui.panels.info.WeaponPanel;
import main.system.images.ImageManager.ALIGNMENT;

import java.util.Arrays;

/**
 * Created by JustMe on 1/5/2017.
 */
public class InfoDialog extends Dialog {
    Container top;
    Container fxAndAbils;
    Container armor;
    EntityContainer armorTraits;
    WeaponPanel mainWeapon;
    WeaponPanel offWeapon;
    ValueContainer attributes;
    ValueContainer dynamicParams;
    Container points;
    ValueContainer mainParams;
    TabbedPanel tabs;

    Container description;
    Container lore;
    public final static String bgPath =
     "UI\\components\\2017\\dialog\\info\\info panel big.png";


    public InfoDialog(DC_HeroObj unit) {
        super(bgPath);
//        VISUALS.DOUBLE_CONTAINER,
        fxAndAbils = new Container("fxAndAbils") {
            @Override
            public void initComps() {

                PagedContainer effects = new PagedContainer("Active Effects", true, 32, 2, 2,
                 unit, () -> unit.getBuffs()
                );
                PagedContainer abilities = new PagedContainer("Special Abilities", true, 32, 2, 2,
                 unit, () -> unit.getPassives());
                setComps(abilities, effects);
            }
        };


        armor = new Container("", LAYOUT.HORIZONTAL) {
            @Override
            public void initComps() {

                PagedContainer buffs = new PagedContainer("Buffs", true, 32, 2, 2,
                 unit, () -> unit.getArmor().getBuffs());
                PagedContainer traits = new PagedContainer("Traits", true, 32, 2, 2,
                 unit, () ->   unit.getArmor().getPassives());
//EntityComp armor = new EntityComp()
                setComps(buffs, traits);
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


        dynamicParams = new ValueContainer(unit, 2, 3, true, false, ALIGNMENT.SOUTH,
         StyleHolder.getAVQLabelStyle(),
         () -> {
             return Arrays.asList(DC_ContentManager.DYNAMIC_PARAMETERS);
         });

        mainWeapon = new WeaponPanel();
        description = new Container("", LAYOUT.HORIZONTAL);
        tabs = new TabbedPanel("");

        mainParams = new ValueContainer(unit, 1,
         DC_ContentManager.MAIN_PARAMETERS.length,
         true, false,
         ALIGNMENT.SOUTH, StyleHolder.getAVQLabelStyle(),
         () -> {
             return Arrays.asList(DC_ContentManager.MAIN_PARAMETERS);
         });


        points = new Container("", LAYOUT.HORIZONTAL);
        top = new Container("", LAYOUT.HORIZONTAL);
        {

            Comp portrait;
            Comp portraitBg;
        }
        offWeapon = new WeaponPanel();
        lore = new Container("", LAYOUT.HORIZONTAL);

        setComps(
         //from bottom left
         fxAndAbils, attributes, dynamicParams, mainWeapon, description,
         new Wrap(false), //next column
         armor, mainParams, points, top,
         new Wrap(false),//next column
         new Space(false, 0.2f), //leave 20% space
         tabs, offWeapon, lore
        );
    }
}














