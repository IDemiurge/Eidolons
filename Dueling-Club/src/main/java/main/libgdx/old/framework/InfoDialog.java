package main.libgdx.old.framework;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.content.VALUE;
import main.content.ValuePages;
import main.content.values.properties.G_PROPS;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.libgdx.StyleHolder;
import main.libgdx.gui.dialog.Dialog;
import main.libgdx.gui.layout.LayoutParser.LAYOUT;
import main.libgdx.gui.panels.generic.*;
import main.libgdx.gui.panels.info.WeaponPanel;
import main.system.images.ImageManager.ALIGNMENT;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;

/**
 * Created by JustMe on 1/5/2017.
 */
public class InfoDialog extends Dialog {
    public final static String path = "UI\\components\\2017\\dialog\\info\\";
    public final static String portraitBg =
            path +
                    "portrait bg.png";
    public final static String bgPath = path +
            "background.png";
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


    public InfoDialog(Unit unit) {
        super(bgPath);
//        VISUALS.DOUBLE_CONTAINER,
        fxAndAbils = new Container(path + "abils and fxs bg.png") {
            @Override
            public void initComps() {

                EntityContainer effects = new EntityContainer("Active Effects", 32, 2, 2
                        , () -> unit.getBuffs(),
                        unit, n -> ((Obj) n.get()).invokeClicked()
                );
                EntityContainer abilities = new EntityContainer("Special Abilities", 32, 2, 2
                        , () -> unit.getPassives(),
                        unit, n -> ((Obj) n.get()).invokeClicked()
                );
                setComps(effects, abilities);
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
        attributes.setHorizontal(false);
        attributes.setImagePath(path + "params frame.png");

        dynamicParams = new ValueContainer(unit, 2, 3, true, false, ALIGNMENT.SOUTH,
                StyleHolder.getAVQLabelStyle(),
                () -> Arrays.asList(DC_ContentManager.DYNAMIC_PARAMETERS), null);

        mainWeapon = new WeaponPanel(unit, false);
        offWeapon = new WeaponPanel(unit, true);
        description = new Container("", LAYOUT.HORIZONTAL);

//        tabs = new TabbedPanel("", () -> getTabs(unit));

        mainParams = new ValueContainer(unit, 1,
                DC_ContentManager.MAIN_PARAMETERS.length,
                true, false,
                ALIGNMENT.SOUTH, StyleHolder.getAVQLabelStyle(),
                () -> Arrays.asList(DC_ContentManager.MAIN_PARAMETERS), null);


        points = new Container("", LAYOUT.HORIZONTAL);


        top = new Container(portraitBg, LAYOUT.HORIZONTAL) {
            @Override
            public void initComps() {
                EntityComp portrait = new EntityComp(unit);
                ValueContainer values = new ValueContainer(unit, 3, 1,
                        () -> new LinkedList<>(Arrays.asList(new VALUE[]{
                                G_PROPS.NAME, PARAMS.LEVEL, G_PROPS.ASPECT
                        })));
                setComps(values, portrait);
            }

            ;
        };
//        lore = new TextContainer("", LAYOUT.HORIZONTAL);

        setComps(
                //from bottom left
//         fxAndAbils,
                attributes

                , dynamicParams, mainWeapon, description,
                new Wrap(false), //next column
                armor, mainParams, points, top,
                new Wrap(false),//next column
                new Space(false, 0.2f), //leave 20% space
                tabs, offWeapon, lore
        );
    }


    private Collection<Triple<String, String, Actor>> getAdditionalParamTabs(DC_Obj unit) {
        List<Triple<String, String, Actor>> list = new LinkedList<>();
//        add ( new Triple<>(text, null,
//         new Container(LAYOUT.VERTICAL, null , fxAndAbils, res)));
        Iterator<String> iterator = Arrays.stream(ValuePages.INFO_TABLE_NAMES).iterator();
        Arrays.stream(ValuePages.UNIT_INFO_PARAMS).forEach(arrays -> {
            List<ValueContainer> comps = new LinkedList<>();
            Arrays.stream(arrays).forEach(s -> {
                comps.add(new ValueContainer(unit, 4, 4, () -> Arrays.asList(s)));
            });

            Container tables = new Container
                    (LAYOUT.VERTICAL, "", comps.toArray(new Actor[comps.size()]));

            String text = iterator.next();
            Triple<String, String, Actor> t = new ImmutableTriple<>(text, null, tables);
            list.add(t);
        });

        return list;
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}














