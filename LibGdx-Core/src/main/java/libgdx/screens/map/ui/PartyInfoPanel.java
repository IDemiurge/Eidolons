package libgdx.screens.map.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.entity.obj.unit.Unit;
import libgdx.GdxColorMaster;
import libgdx.StyleHolder;
import libgdx.bf.generic.ImageContainer;
import libgdx.bf.generic.SuperContainer;
import libgdx.gui.LabelX;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.panels.TablePanel;
import libgdx.screens.map.MapScreen;
import eidolons.macro.entity.party.MacroParty;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.MapEvent;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;


/**
 * Created by JustMe on 2/10/2018.
 * vertical
 * click to show UnitInfo
 * move on click arrow
 * SelectionPanel based?
 */
public class PartyInfoPanel extends TablePanel {
    MacroParty party;

    public PartyInfoPanel() {

    }

    public void init(MacroParty party) {

        clearChildren();
        clearListeners();
        this.party = party;
        TablePanel main = new TablePanel();
        this.columnDefaults(0).width(getMainWidth());
        this.columnDefaults(0).minWidth(getMainWidth());
        for (Unit sub : party.getMembers()) {
            PartyMemberComponent component = new PartyMemberComponent(sub);
            main.add(component).maxWidth(getMainWidth());
            main.row();
        }
        setSize((getMainWidth())
         , 128 * party.getMembers().size());
        add(main);//;

        setX(0);
    }

    private int getMainWidth() {
        return //(int) GdxMaster.adjustSize
         (256);
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        init((MacroParty) getUserObject());
    }
/*
header for the party?
 */

    @Override
    public void act(float delta) {
        super.act(delta);
        GuiEventManager.bind(MapEvent.MAP_GUI_UPDATE, p -> {
            setUpdateRequired(true);
        });
    }

    public class PartyMemberComponent extends TablePanel {
        SuperContainer portrait;
        Unit hero;

        public PartyMemberComponent(Unit hero) {
            this.hero = hero;
            portrait = new ImageContainer(hero);
            portrait.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == 1) {
                        GuiEventManager.trigger(GuiEventType.SHOW_UNIT_INFO_PANEL,
                          (hero));
                    } else {
                        MapScreen.getInstance().centerCamera();
                    }
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
            defaults().width(128);

            addNormalSize(portrait);
            LabelStyle style = StyleHolder.getSizedColoredLabelStyle
             (FONT.MAIN, 18, GdxColorMaster.getDefaultTextColor());
            ValueContainer name = new ValueContainer(new LabelX(hero.getName(), style));
            style = StyleHolder.getSizedColoredLabelStyle
             (FONT.MAIN, 16, GdxColorMaster.getDefaultTextColor());
            //class and level?
            String text = "Level " + hero.getLevel();
            ValueContainer subname = new ValueContainer(new LabelX(text, style));
            text = party.getMemberRank(hero);
            ValueContainer rank = new ValueContainer(new LabelX(text, style));
            text = StringMaster.format(party.getStatus(hero).toString());
            ValueContainer status = new ValueContainer(new LabelX(text, style));
            TablePanel tablePanel = new TablePanel().initDefaultBackground();
            tablePanel.add(name);//.maxWidth(getMainWidth()-128);
            tablePanel.row();
            tablePanel.add(subname);//.maxWidth(getMainWidth()-128);

            TablePanel tablePanel2 = new TablePanel().initDefaultBackground();
            tablePanel2.add(rank);//.maxWidth(getMainWidth()-128);
            tablePanel2.row();
            tablePanel2.add(status);//.maxWidth(getMainWidth()-128);


            TablePanel container = new TablePanel();
            container.add(tablePanel).left().maxWidth(getMainWidth() - 128);
            container.row();
            container.add(tablePanel2).left().maxWidth(getMainWidth() - 128);

            add(container).left().maxWidth(getMainWidth() - 128);

            initDefaultBackground();
        }
    }

}
