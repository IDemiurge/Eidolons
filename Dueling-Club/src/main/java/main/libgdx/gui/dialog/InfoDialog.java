package main.libgdx.gui.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.google.gwt.rpc.server.WebModeClientOracle.Triple;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.content.ValuePages;
import main.content.properties.G_PROPS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.libgdx.StyleHolder;
import main.libgdx.gui.layout.LayoutParser.LAYOUT;
import main.libgdx.gui.panels.generic.*;
import main.libgdx.gui.panels.info.WeaponPanel;
import main.system.images.ImageManager.ALIGNMENT;

import java.util.*;

/**
 * Created by JustMe on 1/5/2017.
 */
public class InfoDialog extends Dialog {
    Container top;
    Container fxAndAbils;
    Container armor;
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
     "UI\\components\\2017\\dialog\\info\\background.png";


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
                 unit, () -> unit.getArmor().getPassives());
                EntityComp armor = new EntityComp(() -> unit.getArmor());
                setComps(buffs, armor, traits);
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
         () -> Arrays.asList(DC_ContentManager.DYNAMIC_PARAMETERS), null);

        mainWeapon = new WeaponPanel();
        description = new Container("", LAYOUT.HORIZONTAL);

        tabs = new TabbedPanel("", ()-> getTabs(unit));

        mainParams = new ValueContainer(unit, 1,
         DC_ContentManager.MAIN_PARAMETERS.length,
         true, false,
         ALIGNMENT.SOUTH, StyleHolder.getAVQLabelStyle(),
         () -> Arrays.asList(DC_ContentManager.MAIN_PARAMETERS), null);


        points = new Container("", LAYOUT.HORIZONTAL);
        top = new Container("", LAYOUT.HORIZONTAL) {
            @Override
            public void initComps() {
                EntityComp portrait = new EntityComp(unit);
                Comp portraitBg = new Comp("");
                new Container(LAYOUT.VERTICAL, "", new ValueComp(unit, G_PROPS.NAME)
                 , new ValueComp(unit, PARAMS.LEVEL)
                 , new ValueComp(unit, G_PROPS.ASPECT));
            }

            ;
        };
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

    private Collection<Triple<String,String,Actor>> getTabs(DC_Obj unit) {
         List<Triple<String,String,Actor>> list = new LinkedList<>();
        Iterator<String> iterator = Arrays.stream(ValuePages.INFO_TABLE_NAMES).iterator();
        Arrays.stream(ValuePages.UNIT_INFO_PARAMS).forEach(arrays->{
            List<ValueContainer> comps=    new LinkedList<>() ;
            Arrays.stream(arrays).forEach(s-> {
                 comps.add( new ValueContainer(unit, 4, 4, () -> Arrays.asList(s)));
            });

             Container tables = new Container
              (LAYOUT.VERTICAL, "", comps.toArray(new Actor[comps.size()]));

            String text =iterator.next();
            Triple<String, String, Actor> t = new Triple<>(text, null, tables);
            list.add(t);
        });

        return list;
    }
}














