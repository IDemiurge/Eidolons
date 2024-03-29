package libgdx.gui.dungeon.panels.headquarters.town;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.game.core.EUtils;
import eidolons.game.exploration.story.quest.advanced.Quest;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.TiledNinePatchGenerator;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.gui.LabelX;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.NoHitImage;
import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.dungeon.menu.selection.town.quest.QuestSelectionPanel;
import libgdx.gui.dungeon.menu.selection.town.shops.ShopSelectionPanel;
import libgdx.gui.dungeon.panels.TabbedPanel;
import libgdx.gui.dungeon.panels.TablePanelX;
import libgdx.gui.dungeon.panels.dc.inventory.shop.ShopClickHandler;
import libgdx.gui.dungeon.panels.headquarters.HqMaster;
import libgdx.gui.dungeon.tooltips.DynamicTooltip;
import libgdx.assets.texture.TextureCache;
import libgdx.gui.generic.btn.ButtonStyled;
import eidolons.entity.Town;
import main.content.enums.DungeonEnums.MAP_BACKGROUND;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.launch.Flags;
import main.system.sound.AudioEnums;
import main.system.sound.AudioEnums.BUTTON_SOUND_MAP;
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
    private static TownPanel activeInstance;
    private final ShopSelectionPanel shopView;
    private final QuestSelectionPanel questPanel;
    private final LabelX townName;
    private final Texture headerBg;
    private final SmartTextButton okBtn;
    private final SmartTextButton hqBtn;
    private final Texture frame;
    private String tooltip;
    SpriteAnimation backgroundSprite;

    public TownPanel() {
        super();
        setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        addActor(new NoHitImage(TextureCache.getOrCreateR(MAP_BACKGROUND.ERSIDRIS.getBackgroundFilePath())));
        addActor(new NoHitImage(TextureCache.getOrCreateR(TiledNinePatchGenerator.BACKGROUND_NINE_PATCH.SEMI.getPath())));

        frame = TiledNinePatchGenerator.getOrCreateNinePatch(TiledNinePatchGenerator.NINE_PATCH.FRAME,
         TiledNinePatchGenerator.BACKGROUND_NINE_PATCH.TRANSPARENT, GdxMaster.getWidth(), GdxMaster.getHeight());

        initContainer();
        addActor(new NoHitImage(frame));
        addActor(okBtn = new SmartTextButton("Done", ButtonStyled.STD_BUTTON.MENU, this::done) {
            public BUTTON_SOUND_MAP getSoundMap() {
                return AudioEnums.BUTTON_SOUND_MAP.ENTER;
            }
        });
        okBtn.setDisabledRunnable(() -> {
            EUtils.infoPopup(tooltip);
        });
        addActor(hqBtn = new SmartTextButton("Hero Screen", ButtonStyled.STD_BUTTON.MENU, this::openHq));
        okBtn.addListener(new DynamicTooltip(this::getDoneTooltip).getController());
        tabTable.setZIndex(Integer.MAX_VALUE);
        headerBg = TiledNinePatchGenerator.getOrCreateNinePatch(TiledNinePatchGenerator.NINE_PATCH.SAURON_ALT,
         TiledNinePatchGenerator.BACKGROUND_NINE_PATCH.TRANSPARENT,
         468, 100);
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
        tabTable.add(labelTable).colspan(2).center();

        resetCheckedTab();

        GuiEventManager.bind(GuiEventType.SHOW_HQ_SCREEN, p -> {
            if (getActiveInstance() == null)
                return;
            if (p.get() == null) {
                update();
                shopView.fadeIn();
            } else {
                shopView.fadeOut();
            }
        });

    }

    public static TownPanel getActiveInstance() {
        return activeInstance;
    }

    public static void setActiveInstance(TownPanel activeInstance) {
        TownPanel.activeInstance = activeInstance;
    }

    @Override
    public void update() {
        shopView.update();
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
        if (Flags.isIDE()) {
            return false;
        }
        for (Quest quest : getUserObject().getQuests()) {
            if (quest.isStarted()) {
                return false;
            }
        }
        //        for (Shop shop : getUserObject().getShops()) {
        //            if (!shop.isBalanceOk())
        //            {
        //                tooltip = "You have to pay off your debts at " + shop.getName();
        //                return true;
        //            }
        //        }
        tooltip = "You need a good reason - a quest - to leave the town's safety...";
        return true;
    }

    @Override
    public Town getUserObject() {
        return (Town) super.getUserObject();
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        Town town = (Town) userObject;
        shopView.setUserObject(town.getShops());
        questPanel.setUserObject(town.getQuests());
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
        contentTable.setX(TiledNinePatchGenerator.NINE_PATCH_PADDING.FRAME.left);
        okBtn.setX(frame.getWidth() - okBtn.getWidth() - 2 * TiledNinePatchGenerator.NINE_PATCH_PADDING.FRAME.right);
        okBtn.setY(TiledNinePatchGenerator.NINE_PATCH_PADDING.FRAME.bottom / 4);
        hqBtn.setX(2 * TiledNinePatchGenerator.NINE_PATCH_PADDING.FRAME.right);
        hqBtn.setY(TiledNinePatchGenerator.NINE_PATCH_PADDING.FRAME.bottom / 4);
        //         contentTable.getX()+contentTable.getWidth()+40);


        tabTable.setSize(headerBg.getWidth(), headerBg.getHeight());
        tabTable.setPosition(GdxMaster.centerWidth(tabTable),
         GdxMaster.getTopY(tabTable));
    }

    @Override
    protected void setUserObjectForChildren(Object userObject) {

    }

    @Override
    public void updateAct(float delta) {
        Town town = getUserObject();
        townName.setText(town.getName());
    }

    public void done() {
        ShopClickHandler.stashOpen = false;
        WaitMaster.receiveInput(DONE_OPERATION, true);
        GuiEventManager.trigger(GuiEventType.SHOW_TOWN_PANEL, null);
        GuiEventManager.trigger(GuiEventType.BLACKOUT_AND_BACK);
        //        HqDataMasterDirect.applyModifications();

    }

    public void entered() {
        tabSelected(TOWN_VIEWS.QUESTS.toString());
//        if (questPanel.getInfoPanel() instanceof QuestInfoPanel) {
//            SelectableItemData chosen = ((QuestInfoPanel) questPanel.getInfoPanel()).getChosen();
//            if ( chosen !=null) {
//                chosen
//                try {
//                    questPanel.next();
//                } catch (Exception e) {
//                    main.system.ExceptionMaster.printStackTrace(e);
//                }
//            }
//        }
        //        update();
    }

    public enum TOWN_VIEWS {
        OVERVIEW,
        SHOPS,
        QUESTS,
        TAVERN,
        LIBRARY,;

        @Override
        public String toString() {
            return StringMaster.format(name());
        }
    }

}
