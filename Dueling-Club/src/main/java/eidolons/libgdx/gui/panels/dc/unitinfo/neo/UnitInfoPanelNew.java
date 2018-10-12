package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.AdjustingVerticalGroup;
import eidolons.libgdx.gui.panels.dc.actionpanel.BuffPanelSimple;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.hero.HqParamPanel;
import eidolons.libgdx.gui.panels.headquarters.hero.HqScrolledValuePanel;
import eidolons.libgdx.gui.panels.headquarters.hero.HqTraitsPanel;
import eidolons.libgdx.gui.panels.headquarters.hero.HqVerticalValueTable;
import eidolons.libgdx.gui.panels.headquarters.tabs.stats.HqAttributeTable;
import eidolons.libgdx.stage.Blocking;
import eidolons.libgdx.stage.StageWithClosable;

import static eidolons.libgdx.stage.BattleGuiStage.isNewUnitInfoPanelWIP;

/**
 * Created by JustMe on 5/14/2018.
 * <p>
 * requirements
 * <p>
 * > easy to show in Menu
 * > Compact and resizable
 * > Readable and non-scary
 * > Use ninepatches properly and generated tiled panels
 * <p>
 * Layout changes
 * <p>
 * The core is the FullParamTable
 * <p>
 * <p>
 * which panels really have to change?
 * apart from the "look'n'feel"?
 * <p>
 * Weapon
 * > Attack icons will be welcome
 * big tabs with sprites, max space for attacks
 * <p>
 * <p>
 * > Fx and abils - how will I make it for HQ ?
 * :: IN A ROW ! expandable in the future
 */
public class UnitInfoPanelNew extends HqElement implements Blocking {

    public static final int WIDTH = 1350;
    public static final int HEIGHT = 980;
    private static UnitInfoPanelNew instance;
    private final Actor outside;
    //centered?
    private final Array<Actor> children;
    AvatarPanel avatarPanel;
    UnitInfoWeapon weapon;
    UnitInfoWeapon secondWeapon;
    StatusPanel statusPanel;
    PointsPanel pointsPanel;
    HqAttributeTable attributeTable;
    BuffPanelSimple buffPanelSimple;
    HqVerticalValueTable mainValuesPanel;
    HqParamPanel dynamicParamPanel;
    HqParamPanel paramPanel;
    UnitDescriptionPanel descriptionPanel;
    HqScrolledValuePanel scrolledValuePanel;
    ArmorPanel armorPanel;
    ResistInfoTabsPanel resistPanel;
    HqTraitsPanel traitsPanel;

    private UnitInfoPanelNew() {
        super();
//        setBackground(new TextureRegionDrawable(new TextureRegion(
//         TiledNinePatchGenerator.getOrCreateNinePatch(NINE_PATCH.FRAME,
//          BACKGROUND_NINE_PATCH.PATTERN,
//          (int) w,
//          (int) h))));
//        pad(NINE_PATCH_PADDING.FRAME);

        setBackground(NinePatchFactory.getHqDrawable());

        VerticalGroup center = new AdjustingVerticalGroup(400, 0.75f);
        VerticalGroup left = new AdjustingVerticalGroup(455, 0.25f);
        VerticalGroup right = new AdjustingVerticalGroup(455, 0.25f);

        float w1 = left.getWidth();
        setSize(w1*2+center.getWidth(), (int) GdxMaster.adjustHeight(HEIGHT));
        float w = getWidth();
        float h = getHeight();
        add(left).top();
        add(center).top();
        add(right).top();


//        TablePanelX buttonPanel = new TablePanelX();
//        buttonPanel.add(new SmartButton(STD_BUTTON.CANCEL, () -> close()));
//        buttonPanel.add(new SmartButton(STD_BUTTON.HELP, () -> help()));
//        upperTable.add(buttonPanel);

        //column1
        left.addActor(weapon= new UnitInfoWeapon(false));
        right.addActor(secondWeapon=new UnitInfoWeapon(true));
        center.addActor(avatarPanel = new AvatarPanel());

        left.addActor(statusPanel = new StatusPanel());
        // column1.addActor(modePanel= new ModePanel());
        left.addActor(pointsPanel = new PointsPanel());

        left.addActor(attributeTable = new HqAttributeTable());
        attributeTable.setSize(w*0.25f, h*0.45f);
        attributeTable.setEditable(false);
        left.addActor(buffPanelSimple = new BuffPanelSimple());

        //column2
//        column2.addActor(mainValuesPanel = new HqVerticalValueTable(G_PROPS.NAME, PARAMS.LEVEL));
        center.addActor(dynamicParamPanel = new HqParamPanel(true));
        center.addActor(paramPanel = new HqParamPanel(false));
        center.addActor(descriptionPanel = new UnitDescriptionPanel(){
            @Override
            protected float getDefaultHeight() {
                return GdxMaster.adjustHeight(h*0.5f);
            }

            @Override
            protected float getDefaultWidth() {
                return GdxMaster.adjustWidth(w*0.4f);
            }
        });
        descriptionPanel.setSize(GdxMaster.adjustHeight(h*0.5f),
         GdxMaster.adjustWidth(h*0.4f));

        center.addActor(scrolledValuePanel = new HqScrolledValuePanel());

        //column3
        right.addActor(armorPanel = new ArmorPanel());
        right.addActor(resistPanel = new ResistInfoTabsPanel());
        right.addActor(traitsPanel = new HqTraitsPanel());


        outside = new Actor();
        outside.setBounds(0, 0, GdxMaster.getWidth(), GdxMaster.getHeight());
        outside.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                UnitInfoPanelNew.this.close();
                return false;
            }
        });

        children = GdxMaster.getAllChildren(this);


        setVisible(false);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return true;
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                event.stop();
                return true;
            }
        }
        );

    }

    public static UnitInfoPanelNew getInstance() {
        if (instance == null || isNewUnitInfoPanelWIP()) {
            try {
                instance = new UnitInfoPanelNew();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        return instance;
    }


    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);

        children.forEach(ch -> {
            ch.setUserObject(getUserObject());
        });
        if (userObject != null) {
            open();
        }
        weapon.debugAll();
        avatarPanel.debugAll();
        secondWeapon.debugAll();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor actor = super.hit(x, y, touchable);
        if (actor == null) {
            return outside;
        }
        return actor;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        outside.setTouchable(visible ? Touchable.enabled : Touchable.disabled);
    }

    private void help() {
    }


    @Override
    protected void update(float delta) {

    }

    @Override
    public StageWithClosable getStageWithClosable() {
        return (StageWithClosable) getStage();
    }
}
