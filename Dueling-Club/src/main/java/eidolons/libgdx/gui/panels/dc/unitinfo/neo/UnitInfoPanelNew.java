package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.Array;
import eidolons.content.PARAMS;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.AdjustingVerticalGroup;
import eidolons.libgdx.gui.panels.TabbedPanel;
import eidolons.libgdx.gui.panels.dc.actionpanel.BuffPanelSimple;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.hero.HqTraitsPanel;
import eidolons.libgdx.gui.panels.headquarters.hero.HqVerticalValueTable;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.libgdx.stage.Blocking;
import eidolons.libgdx.stage.StageWithClosable;
import main.content.values.properties.G_PROPS;

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
    public final Group outside;
    //centered?
    private final Array<Actor> children;
    private final AdjustingVerticalGroup center;
    private final AdjustingVerticalGroup left;
    private final AdjustingVerticalGroup right;
    private final HqVerticalValueTable mainInfoPanel;
    AvatarPanel avatarPanel;
    UnitInfoWeapon weapon;
    UnitInfoWeapon secondWeapon;
    StatusPanel statusPanel;
    PointsPanel pointsPanel;
    BuffPanelSimple buffPanelSimple;
    ResistInfoTabsPanel resistPanel;
    HqTraitsPanel traitsPanel;

    public static boolean isNewUnitInfoPanelWIP() {
        return true;
    }

    @Override
    public void layout() {
        super.layout();
        center.setY(getHeight() - center.getHeight());
        secondWeapon.setY(weapon.getY());
        float max = Math.max(left.getHeight(), right.getHeight());
        left.setY(max - left.getHeight());
        right.setY(max - right.getHeight());
    }

    private UnitInfoPanelNew() {
        super();

        setBackground(NinePatchFactory.getHqDrawable());

          center = new AdjustingVerticalGroup(400, 0.75f);
          left = new AdjustingVerticalGroup(455, 0.25f);
          right = new AdjustingVerticalGroup(455, 0.25f);

        float w1 = left.getWidth();
        setSize(w1 * 2 + center.getWidth(),   GdxMaster.getHeight());
        float w = getWidth();
        float h = getHeight();

        weapon = new UnitInfoWeapon(false);
        add(left).center().padTop(150);//.padTop(weapon.getHeight()/2);
        add(center).center().padTop(50);
        add(right).center().padTop(150);//.padTop(weapon.getHeight()/2);

        center.addActor(avatarPanel = new AvatarPanel());
        center.addActor(mainInfoPanel = new HqVerticalValueTable(false, G_PROPS.NAME,
         G_PROPS.RACE,  PARAMS.LEVEL ));
        mainInfoPanel.setDisplayPropNames(false);
        mainInfoPanel.setDisplayColumn(false);

        //column1
        addActor(weapon);
        weapon.setPosition((getWidth() - avatarPanel.getWidth() - weapon.getWidth()) / 2, GDX.top(weapon));

        addActor(secondWeapon = new UnitInfoWeapon(true));
        secondWeapon.setPosition(avatarPanel.getWidth() + secondWeapon.getWidth(), GDX.top(secondWeapon));

        left.addActor(pointsPanel = new PointsPanel());

        left.addActor(new UnitStatTabs(left.getWidth(), getHeight()-weapon.getHeight()-64));
        left.addActor(buffPanelSimple = new BuffPanelSimple());

        //column2
        //        column2.addActor(mainValuesPanel = new HqVerticalValueTable(G_PROPS.NAME, PARAMS.LEVEL));
//        center.addActor(armorPanel = new ArmorPanel());
//        center.addActor(dynamicParamPanel = new HqParamPanel(true));
//        center.addActor(paramPanel = new HqParamPanel(false));
        TabbedPanel infoTabs = new UnitInfoTabs(center.getWidth(), getHeight()-avatarPanel.getHeight() );
        center.addActor(infoTabs);

//        right.pack();
//        right.addActor(scrolledValuePanel = new HqScrolledValuePanel(right.getWidth(),
//         getHeight()-right.getHeight()));

        //column3
        right.addActor(statusPanel = new StatusPanel());
        // column3.addActor(modePanel= new ModePanel());
        right.addActor(resistPanel = new ResistInfoTabsPanel());
        right.addActor(traitsPanel = new HqTraitsPanel());

        center.pack();

        outside = new Group();
        outside.setBounds(getWidth(), 0, GdxMaster.getWidth()-getWidth(), GdxMaster.getHeight());
        outside.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                UnitInfoPanelNew.this.close();
                return false;
            }
        });
        setVisible(false); // careful here with override

        children = GdxMaster.getAllChildren(this);


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
        //        weapon.debugAll();
        //        avatarPanel.debugAll();
        //        secondWeapon.debugAll();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (parentAlpha == ShaderDrawer.SUPER_DRAW)
            super.draw(batch, 1);
        else
            ShaderDrawer.drawWithCustomShader(this, batch, null);
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


    //        setBackground(new TextureRegionDrawable(new TextureRegion(
    //         TiledNinePatchGenerator.getOrCreateNinePatch(NINE_PATCH.FRAME,
    //          BACKGROUND_NINE_PATCH.PATTERN,
    //          (int) w,
    //          (int) h))));
    //        pad(NINE_PATCH_PADDING.FRAME);
    //        TablePanelX buttonPanel = new TablePanelX();
    //        buttonPanel.add(new SmartButton(STD_BUTTON.CANCEL, () -> close()));
    //        buttonPanel.add(new SmartButton(STD_BUTTON.HELP, () -> help()));
    //        upperTable.add(buttonPanel);

}
