package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.Array;
import eidolons.content.PARAMS;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SymbolButton;
import eidolons.libgdx.gui.panels.AdjustingVerticalGroup;
import eidolons.libgdx.gui.panels.dc.actionpanel.BuffPanelSimple;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.hero.HqTraitsPanel;
import eidolons.libgdx.gui.panels.headquarters.hero.HqVerticalValueTable;
import eidolons.libgdx.screens.map.layers.BlackoutOld;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.libgdx.stage.Blocking;
import eidolons.libgdx.stage.StageWithClosable;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.DungeonEnums;
import main.content.values.properties.G_PROPS;
import main.system.auxiliary.RandomWizard;

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

    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    public static final boolean EXAMINE_READY = false;
    private static UnitInfoPanelNew instance;
    public final Group outside;
    //centered?
    private final Array<Actor> children;
    private final AdjustingVerticalGroup center;
    private final AdjustingVerticalGroup left;
    private final AdjustingVerticalGroup right;
    private final UnitInfoTabs infoTabs;

    AvatarPanel avatarPanel;
    UnitInfoWeapon weapon;
    UnitInfoWeapon secondWeapon;
    StatusPanel statusPanel;
    PointsPanel pointsPanel;
    BuffPanelSimple buffPanelSimple;
    ResistInfoTabsPanel resistPanel;
    HqTraitsPanel traitsPanel;
    private final ImageContainer black;

    public static boolean isNewUnitInfoPanelWIP() {
            return true;
//        return CoreEngine.isIDE();
    }

    @Override
    public void layout() {
        super.layout();
        center.setY(getHeight() - center.getHeight());
        center.setY(- 200); //TODO Gdx revamp - this panel..
        secondWeapon.setY(weapon.getY());
        float max = Math.max(left.getHeight(), right.getHeight());
        left.setY(max - left.getHeight());
        right.setY(max - right.getHeight());
//        ta1bs.setPosition(90, -40);
    }

    private UnitInfoPanelNew() {
        super();
        setSize(WIDTH, HEIGHT);
        setBackground(NinePatchFactory.getHqDrawable());
        SymbolButton btn;
        addActor(btn =new SymbolButton(ButtonStyled.STD_BUTTON.CANCEL, this::close));
        btn.setPosition(getWidth()-64, getHeight()-64);
        black = new ImageContainer(BlackoutOld.path);

        center = new AdjustingVerticalGroup(WIDTH/3, 0.75f);
          left = new AdjustingVerticalGroup(WIDTH/3, 0.25f);
          right = new AdjustingVerticalGroup(WIDTH/3, 0.25f);

        float w1 = left.getWidth();
        setSize(w1 * 2 + center.getWidth(),   GdxMaster.getHeight());

        weapon = new UnitInfoWeapon(false);
        add(left).center().padTop(150);//.padTop(weapon.getHeight()/2);
        add(center).center().padTop(50);
        add(right).center().padTop(150);//.padTop(weapon.getHeight()/2);

        center.addActor(avatarPanel = new AvatarPanel());
        HqVerticalValueTable mainInfoPanel;
        center.addActor(mainInfoPanel = new HqVerticalValueTable(false, G_PROPS.NAME,
         G_PROPS.RACE,  PARAMS.LEVEL ));
        center.addActor(  new ArmorPanel());
        mainInfoPanel.setX(30);
        mainInfoPanel.setDisplayPropNames(false);
        mainInfoPanel.setDisplayColumn(false);

        //column1
        addActor(weapon);
        weapon.setPosition((getWidth() - avatarPanel.getWidth() - weapon.getWidth()*2) / 2+125, GDX.top(weapon)+76);

        addActor(secondWeapon = new UnitInfoWeapon(true));
        secondWeapon.setPosition(weapon.getX()+ avatarPanel.getWidth()+280
//                avatarPanel.getWidth() + secondWeapon.getWidth()+86
                , GDX.top(secondWeapon)+76);

        UnitStatTabs tabs;
        left.addActor(tabs =  new UnitStatTabs(left.getWidth(), getHeight()-weapon.getHeight()-64));

          infoTabs = new UnitInfoTabs(center.getWidth(), (getHeight()-400) );
        center.addActor(infoTabs);

        right.addActor(resistPanel = new ResistInfoTabsPanel());
        resistPanel.setY(-42);
        right.addActor(traitsPanel = new HqTraitsPanel());

        center.pack();

        outside = new Group();
        outside.setBounds(getWidth(), 0, GdxMaster.getWidth()-getWidth(), GdxMaster.getHeight());
//  TODO add btn
//   outside.addListener(new InputListener() {
//            @Override
//            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                UnitInfoPanelNew.this.close();
//                return false;
//            }
//        });
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
        infoTabs.tabSelected("Main");

        String bg = DungeonEnums.MAP_BACKGROUND.BASTION_DARK.getBackgroundFilePath();
//        getBackgroundForUnit(userObject)
        for (DungeonEnums.MAP_BACKGROUND value : DungeonEnums.MAP_BACKGROUND.values()) {
            switch (value) {
                case BASTION:
                case BASTION_DARK:
                case CAVE:
                case SHIP:
                case RAVENWOOD:
                case RAVENWOOD_EVENING:
                case TUNNEL:
                case CEMETERY:
                case TOWER:
                case SPIDER_GROVE:
                    if (RandomWizard.chance(10)) {
                        bg = value.getBackgroundFilePath();
                        break;
                    }
            }
        }
        setBackground(TextureCache.getOrCreateTextureRegionDrawable(bg));
        
        //        weapon.debugAll();
        //        avatarPanel.debugAll();
        //        secondWeapon.debugAll();
    }

    @Override
    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        black.draw(batch , parentAlpha);
        black.getContent().getDrawable().draw(batch, 0,0,1920,1080);
        Color c = batch.getColor();
        c.a = 0.2f;
        batch.setColor(c);
        getBackground().draw(batch, 0,0,1920,1080);
        c.a = 1f;
        batch.setColor(c);
        //draw black
    }

    @Override
    public float getPrefWidth() {
        return 1920;
    }
    //
//    private String getBackgroundForUnit(Object userObject) {
//
//        for (DungeonEnums.MAP_BACKGROUND value : DungeonEnums.MAP_BACKGROUND.values()) {
//            switch (value) {
//                case BASTION:
//                case BASTION_DARK:
//                case CAVE:
//                case SHIP:
//                case RAVENWOOD:
//                case RAVENWOOD_EVENING:
//                case TUNNEL:
//                case CEMETERY:
//                case TOWER:
//                case SPIDER_GROVE:
//                    if (RandomWizard.chance(10)) {
//                        return value.getBackgroundFilePath();
//                    }
//            }
//        }
//        return DungeonEnums.MAP_BACKGROUND .BASTION_DARK.getBackgroundFilePath();
//    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (parentAlpha == ShaderDrawer.SUPER_DRAW)
            super.draw(batch, 1);
        else
            ShaderDrawer.drawWithCustomShader(this, batch, null);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        //        if (actor == null) {
//            return outside;
//        }
        return super.hit(x, y, touchable);
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
