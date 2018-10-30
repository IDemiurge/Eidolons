package eidolons.libgdx.gui.panels.headquarters.town;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH_PADDING;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.NoHitImage;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.menu.selection.town.quest.QuestSelectionPanel;
import eidolons.libgdx.gui.menu.selection.town.shops.ShopSelectionPanel;
import eidolons.libgdx.gui.panels.TabbedPanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.inventory.shop.ShopClickHandler;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import eidolons.libgdx.gui.tooltips.DynamicTooltip;
import eidolons.libgdx.texture.TextureCache;
import eidolons.macro.entity.town.Town;
import main.content.enums.DungeonEnums.MAP_BACKGROUND;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.sound.SoundMaster.BUTTON_SOUND_MAP;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 7/18/2018.
 * <p>
 * Overlaid on the map, has:
 * <p>
 * use without macro?
 */
public class TownPanel extends TabbedPanel {
    public static final WAIT_OPERATIONS DONE_OPERATION = WAIT_OPERATIONS.TOWN_DONE;
    public static final boolean TEST_MODE = false;
    private final ShopSelectionPanel shopView;
    private final QuestSelectionPanel questPanel;
    private final LabelX townName;
    private final Texture headerBg;
    private final SmartButton okBtn;
    private final SmartButton hqBtn;
    private static TownPanel activeInstance;
    private final Texture frame;
    private String tooltip;

    public TownPanel() {
        super();
        setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        addActor(new NoHitImage(TextureCache.getOrCreateR(MAP_BACKGROUND.TUNNEL.getBackgroundFilePath())));
        addActor(new NoHitImage(TextureCache.getOrCreateR(BACKGROUND_NINE_PATCH.SEMI.getPath())));

         frame = TiledNinePatchGenerator.getOrCreateNinePatch(NINE_PATCH.FRAME,
         BACKGROUND_NINE_PATCH.TRANSPARENT, GdxMaster.getWidth(), GdxMaster.getHeight());

        initContainer();
        addActor(new NoHitImage(frame));
        addActor(okBtn = new SmartButton("Done", STD_BUTTON.MENU, () -> done()){
            protected BUTTON_SOUND_MAP getSoundMap() {
                return BUTTON_SOUND_MAP.ENTER;
            }
        });
        addActor(hqBtn = new SmartButton("Hero Screen", STD_BUTTON.MENU, () -> openHq()));
        okBtn.addListener(new DynamicTooltip(() -> getDoneTooltip()).getController());
        tabTable.setZIndex(Integer.MAX_VALUE);
        headerBg = TiledNinePatchGenerator.getOrCreateNinePatch(NINE_PATCH.SAURON_ALT,
         BACKGROUND_NINE_PATCH.TRANSPARENT, GdxMaster.adjustFontSize(470), 100);
        tabTable.setBackground(new TextureRegionDrawable(
         new TextureRegion(headerBg)));

        addTab(shopView = new ShopSelectionPanel(), TOWN_VIEWS.SHOPS.toString());
        addTab(questPanel = new QuestSelectionPanel(), TOWN_VIEWS.QUESTS.toString());

        TablePanelX<Actor> labelTable = new TablePanelX<>();
        labelTable.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
        labelTable.setSize(300, 200);
        labelTable.add(
         townName = new LabelX("", StyleHolder.getHqLabelStyle(24))).center()
         .padTop(4);
        tabTable.row();
        tabTable.add(labelTable).colspan(2). center();

        resetCheckedTab();

        GuiEventManager.bind(GuiEventType. SHOW_HQ_SCREEN, p-> {
            if (p.get() == null) {
                updateRequired = true;
                shopView. fadeIn();
            } else {
                shopView. fadeOut();
            }
        });

    }

    private String getDoneTooltip() {
        if (!okBtn.isDisabled()) {
            return "Leave " + getUserObject().getName();
        }
        return tooltip;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        okBtn.setDisabled(isDisabled());
    }

    private boolean isDisabled() {
//        for (Shop shop : getUserObject().getShops()) {
//            if (!shop.isBalanceOk())
//            {
//                tooltip = "You have to pay off your debts at " + shop.getName();
//                return true;
//            }
//        }
        return false;
    }

    @Override
    public Town getUserObject() {
        return (Town) super.getUserObject();
    }

    public static TownPanel getActiveInstance() {
        return activeInstance;
    }

    public static void setActiveInstance(TownPanel activeInstance) {
        TownPanel.activeInstance = activeInstance;
    }

    private void openHq() {
        HqMaster.openHqPanel();
    }

    @Override
    protected Cell<TextButton> addTabActor(TextButton b) {
        return super.addTabActor(b).padTop(53);
    }

    @Override
    protected TextButtonStyle getTabStyle() {
        return StyleHolder.getHqTabStyle();
    }

    @Override
    protected TablePanelX createContentsTable() {
        return new TablePanelX(frame.getWidth(), frame.getHeight());
    }


    @Override
    public void layout() {
        super.layout();
        contentTable.setX(NINE_PATCH_PADDING.FRAME.left);
        okBtn.setX(frame.getWidth()-okBtn.getWidth()-2*NINE_PATCH_PADDING.FRAME.right);
        okBtn.setY(NINE_PATCH_PADDING.FRAME.bottom/4);
        hqBtn.setX(2*NINE_PATCH_PADDING.FRAME.right);
        hqBtn.setY(NINE_PATCH_PADDING.FRAME.bottom/4);
//         contentTable.getX()+contentTable.getWidth()+40);


        tabTable.setSize(headerBg.getWidth(), headerBg.getHeight());
        tabTable.setPosition(GdxMaster.centerWidth(tabTable),
         GdxMaster.top(tabTable));
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        Town town = (Town) userObject;
        shopView.setUserObject(town.getShops());
        questPanel.setUserObject(town.getQuests());
    }

    @Override
    protected void setUserObjectForChildren(Object userObject) {

    }

    @Override
    public void updateAct(float delta) {
        Town town = (Town) getUserObject();
        townName.setText(town.getName());
    }

    public void done() {
        ShopClickHandler.stashOpen = false;
        WaitMaster.receiveInput(DONE_OPERATION, true);
        GuiEventManager.trigger(GuiEventType.SHOW_TOWN_PANEL, null );
//        HqDataMasterDirect.applyModifications();

    }

    public enum TOWN_VIEWS {
        OVERVIEW,
        SHOPS,
        QUESTS,
        TAVERN,
        LIBRARY,;

        @Override
        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }
    }

}
