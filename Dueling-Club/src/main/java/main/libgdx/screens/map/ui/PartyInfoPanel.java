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
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager.STD_IMAGES;


/**
 * Created by JustMe on 2/10/2018.
 vertical
 click to show UnitInfo
 move on click arrow
 SelectionPanel based?
 */
public class PartyInfoPanel extends TablePanel{
    MacroParty party;
    private ImageContainer arrow;

    public PartyInfoPanel( ) {

    }
    public void init(MacroParty party) {
        debug();
        clearChildren();
        this.party = party;
        TablePanel main= new TablePanel();
        setX(-getMainWidth());
        this.columnDefaults(0).width(getMainWidth());
        this.columnDefaults(0).minWidth(getMainWidth());
    for (Unit sub:     party.getMembers()){
        PartyMemberComponent component = new PartyMemberComponent(sub);
        main. add(component).maxWidth(getMainWidth());
        main. row();
        }
//        getColumnPrefWidth(1)

        add(main);
         arrow = new ImageContainer(STD_IMAGES.DIRECTION_POINTER.getPath());
       arrow. setRotation(90);
        arrow.setOrigin(arrow.getWidth()/2,arrow.getHeight()/2);
        arrow.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               if (getActions().size>0)
                   return true;
               toggle();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        add(arrow).left(); //rotate on click
    }

    private void open() {
        //wait for non-moving
        toggle(true);
    }
    private void toggle() {
        toggle(!isOpen());
    }
        private void toggle(boolean open) {
        int toX =!open?-getMainWidth() :  0;

        ActorMaster.addMoveToAction(
          this, toX, getY(), getSpeed());

        ActorMaster.addRotateByAction(
         arrow.getContent(),180);
    }

    private boolean isOpen() {
        return getX()>=0;
    }

    private float getSpeed() {
        return 2000;
    }

    private int getMainWidth() {
   return (int) GdxMaster.adjustSize(500);
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        init((MacroParty) getUserObject());
    }
/*
header for the party?
 */
    public class PartyMemberComponent extends TablePanel {
        SuperContainer portrait;

        Label name;
        Label subname;
        Label rank;
        Label status;
        //subname - level, classes, ...
        //rank - mercenary, companion, sojourn
        //status
        //function icons - dismiss, talk, inv, challenge
        Unit hero;

        public PartyMemberComponent(Unit hero) {
            debug();
            this.hero = hero;
            portrait = new ImageContainer(hero);
            portrait.addListener(new ClickListener(){
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
            name = new Label(hero.getName(), style);
            style = StyleHolder.getSizedColoredLabelStyle
             (FONT.MAIN, 16, GdxColorMaster.getDefaultTextColor());
            //class and level?
            String text = "Level "+hero.getLevel();
            subname = new Label(text, style);
            text=party.getMemberRank(hero);
            rank = new Label(text, style);
            text=party.getStatus(hero).toString();
             status=new Label(text, style);

            TablePanel tablePanel = new TablePanel();   tablePanel.debug();
            tablePanel.add(name);//.maxWidth(getMainWidth()-128);
            tablePanel.row();
            tablePanel.add(subname);//.maxWidth(getMainWidth()-128);
            tablePanel.row();
            tablePanel.add(rank);//.maxWidth(getMainWidth()-128);
            tablePanel.row();
            tablePanel.add(status);//.maxWidth(getMainWidth()-128);
            addNormalSize(tablePanel).left().padLeft(10). maxWidth(getMainWidth()-128);

        }
    }

}
