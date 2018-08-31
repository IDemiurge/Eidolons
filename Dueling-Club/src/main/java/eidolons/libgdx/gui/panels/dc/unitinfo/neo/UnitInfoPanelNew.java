package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.actionpanel.BuffPanelSimple;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.headquarters.hero.HqParamPanel;
import eidolons.libgdx.gui.panels.headquarters.hero.HqScrolledValuePanel;
import eidolons.libgdx.gui.panels.headquarters.hero.HqTraitsPanel;
import eidolons.libgdx.gui.panels.headquarters.hero.HqVerticalValueTable;
import eidolons.libgdx.gui.panels.headquarters.tabs.stats.HqAttributeTable;
import eidolons.libgdx.stage.Blocking;
import eidolons.libgdx.stage.StageWithClosable;
import main.system.GuiEventManager;
import main.system.GuiEventType;

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

    public static final int WIDTH = 1200;
    public static final int HEIGHT = 900;
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
        super(WIDTH, HEIGHT);
        addActor(new ImageContainer(new Image(TiledNinePatchGenerator.getOrCreateNinePatch(NINE_PATCH.FRAME,
         BACKGROUND_NINE_PATCH.PATTERN, WIDTH, HEIGHT))));

        TablePanelX<Actor> rootTable = new TablePanelX<>(WIDTH, HEIGHT);
        TablePanelX<Actor> upperTable = new TablePanelX<>(WIDTH, HEIGHT / 4);
        TablePanelX<Actor> lowerTable = new TablePanelX<>(WIDTH, HEIGHT * 3 / 4);

        VerticalGroup column1 = new VerticalGroup();
        VerticalGroup column2 = new VerticalGroup();
        VerticalGroup column3 = new VerticalGroup();
        column1.setWidth(330);
        column2.setWidth(540);
        column3.setWidth(330);
        lowerTable.add(column1).width(330).padLeft(30);
        lowerTable.add(column2).width(540);
        lowerTable.add(column3).width(330).padRight(30);

        rootTable.add(upperTable).row();
        rootTable.add(lowerTable).row();

        upperTable.add(weapon = new UnitInfoWeapon(false));
        upperTable.add(avatarPanel = new AvatarPanel());
        upperTable.add(secondWeapon = new UnitInfoWeapon(true));

        TablePanelX buttonPanel = new TablePanelX();
        buttonPanel.add(new TextButtonX(STD_BUTTON.CANCEL, () -> close()));
        buttonPanel.add(new TextButtonX(STD_BUTTON.HELP, () -> help()));

        upperTable.add(buttonPanel);

        //column1
        column1.addActor(statusPanel = new StatusPanel());
        //        column1.addActor(modePanel= new ModePanel());
        column1.addActor(pointsPanel = new PointsPanel());

        column1.addActor(attributeTable = new HqAttributeTable());
        attributeTable.setSize(330, 400);
        attributeTable.setEditable(false);
        column1.addActor(buffPanelSimple = new BuffPanelSimple());

        //column2
//        column2.addActor(mainValuesPanel = new HqVerticalValueTable(G_PROPS.NAME, PARAMS.LEVEL));
        column2.addActor(dynamicParamPanel = new HqParamPanel(true));
        column2.addActor(paramPanel = new HqParamPanel(false));
        column2.addActor(descriptionPanel = new UnitDescriptionPanel());
        column2.addActor(scrolledValuePanel = new HqScrolledValuePanel());

        //column3
        column3.addActor(armorPanel = new ArmorPanel());
        column3.addActor(resistPanel = new ResistInfoTabsPanel());
        column3.addActor(traitsPanel = new HqTraitsPanel());


        outside = new Actor();
        outside.setBounds(0, 0, GdxMaster.getWidth(), GdxMaster.getHeight());
        outside.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                UnitInfoPanelNew.this.close();
                return false;
            }
        });
        GuiEventManager.bind(GuiEventType.SHOW_UNIT_INFO_PANEL, (obj) -> {
            Unit unit = (Unit) obj.get();
            setUserObject(HqDataMaster.getHeroDataSource(unit));
            outside.setTouchable(Touchable.enabled);
        });
        add(rootTable);
        children = GdxMaster.getAllChildren(this);

        addActor(new ImageContainer(new Image(TiledNinePatchGenerator.getOrCreateNinePatch(NINE_PATCH.FRAME,
         BACKGROUND_NINE_PATCH.TRANSPARENT, WIDTH, HEIGHT))));
        setVisible(false);

//        debugAll();
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
        if (instance == null) {
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
