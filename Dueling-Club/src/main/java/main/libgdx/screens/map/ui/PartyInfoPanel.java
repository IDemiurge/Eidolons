package main.libgdx.screens.map.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.entity.obj.unit.Unit;
import main.game.module.adventure.entity.MacroParty;
import main.libgdx.GdxColorMaster;
import main.libgdx.GdxMaster;
import main.libgdx.StyleHolder;
import main.libgdx.anims.ActorMaster;
import main.libgdx.bf.generic.ImageContainer;
import main.libgdx.bf.generic.SuperContainer;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager.STD_IMAGES;


/**
 * Created by JustMe on 2/10/2018.
 * vertical
 * click to show UnitInfo
 * move on click arrow
 * SelectionPanel based?
 */
public class PartyInfoPanel extends TablePanel {
    MacroParty party;
    private ImageContainer arrow;

    public PartyInfoPanel() {

    }

    public void init(MacroParty party) {
//        debug();
        clearChildren();
        clearListeners();
        this.party = party;
        TablePanel main = new TablePanel();
        addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                updateRequired = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });
//        setX(-getMainWidth());
        this.columnDefaults(0).width(getMainWidth());
        this.columnDefaults(0).minWidth(getMainWidth());
        for (Unit sub : party.getMembers()) {
            PartyMemberComponent component = new PartyMemberComponent(sub);
            main.add(component).maxWidth(getMainWidth());
            main.row();
        }
//        getColumnPrefWidth(1)
        setSize((getMainWidth()) + STD_IMAGES.DIRECTION_POINTER.getWidth(), 128 * party.getMembers().size());
        add(main);//;
        arrow = new ImageContainer(STD_IMAGES.DIRECTION_POINTER.getPath());
        arrow.setRotation(270);
        arrow.setOrigin(arrow.getWidth() / 2, arrow.getHeight() / 2);
        arrow.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (getActions().size > 0)
                    return true;
                toggle();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        add(arrow).left(); //rotate on click
        setX(0);
    }

    private void open() {
        //wait for non-moving
        toggle(true);
    }

    private void toggle() {
        toggle(!isOpen());
    }

    private void toggle(boolean open) {
        int toX = open ? 0 : -getMainWidth();

        ActorMaster.addMoveToAction(
         this, toX, getY(), getDuration());

        ActorMaster.addRotateByAction(
         arrow.getContent(), 180);
    }

    private boolean isOpen() {
        return getX() >= 0;
    }

    private float getDuration() {
        return 0.5f;
    }

    private int getMainWidth() {
        return (int) GdxMaster.adjustSize(256);
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
    }

    public class PartyMemberComponent extends TablePanel {
        SuperContainer portrait;
        Unit hero;

        public PartyMemberComponent(Unit hero) {
//            debug();
            this.hero = hero;
            portrait = new ImageContainer(hero);
            portrait.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    GuiEventManager.trigger(GuiEventType.SHOW_UNIT_INFO_PANEL, hero);
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
            defaults().width(128);

            addNormalSize(portrait);
            LabelStyle style = StyleHolder.getSizedColoredLabelStyle
             (FONT.MAIN, 18, GdxColorMaster.getDefaultTextColor());
            ValueContainer name = new ValueContainer(new Label(hero.getName(), style));
            style = StyleHolder.getSizedColoredLabelStyle
             (FONT.MAIN, 16, GdxColorMaster.getDefaultTextColor());
            //class and level?
            String text = "Level " + hero.getLevel();
            ValueContainer subname = new ValueContainer(new Label(text, style));
            text = party.getMemberRank(hero);
            ValueContainer rank = new ValueContainer(new Label(text, style));
            text = party.getStatus(hero).toString();
            ValueContainer status = new ValueContainer(new Label(text, style));
            TablePanel tablePanel = new TablePanel().initDefaultBackground();
            tablePanel.add(name);//.maxWidth(getMainWidth()-128);
            tablePanel.row();
            tablePanel.add(subname);//.maxWidth(getMainWidth()-128);

            TablePanel tablePanel2 = new TablePanel().initDefaultBackground();
            tablePanel2.add(rank);//.maxWidth(getMainWidth()-128);
            tablePanel2.row();
            tablePanel2.add(status);//.maxWidth(getMainWidth()-128);


            add(tablePanel).left().maxWidth(getMainWidth() - 128);
            row();
            add(tablePanel2).left().maxWidth(getMainWidth() - 128);

        }
    }

}
