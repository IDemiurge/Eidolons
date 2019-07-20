package eidolons.libgdx.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageSource;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageWindow;
import eidolons.game.battlecraft.logic.meta.igg.soul.EidolonLord;
import eidolons.game.battlecraft.logic.meta.igg.soul.panel.LordPanel;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.*;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogueContainer;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.bf.generic.SuperContainer;
import eidolons.libgdx.bf.menu.GameMenu;
import eidolons.libgdx.gui.HideButton;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.RollDecorator;
import eidolons.libgdx.gui.RollDecorator.RollableGroup;
import eidolons.libgdx.gui.controls.radial.RadialMenu;
import eidolons.libgdx.gui.controls.radial.RadialValueContainer;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerPanel;
import eidolons.libgdx.gui.panels.dc.logpanel.FullLogPanel;
import eidolons.libgdx.gui.panels.dc.logpanel.ExtendableLogPanel;
import eidolons.libgdx.gui.panels.dc.logpanel.text.OverlayTextPanel;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.quest.QuestJournal;
import eidolons.libgdx.gui.panels.quest.QuestProgressPanel;
import eidolons.libgdx.gui.tooltips.ToolTipManager;
import eidolons.libgdx.screens.map.layers.Blackout;
import eidolons.libgdx.screens.map.town.navigation.PlaceNavigationPanel;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.libgdx.texture.TextureCache;
import eidolons.libgdx.utils.TextInputPanel;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.OptionsWindow;
import main.data.filesys.PathFinder;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.log.FileLogManager;
import main.system.graphics.FontMaster;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;

import static eidolons.game.core.Eidolons.getScreen;
import static main.system.GuiEventType.SHOW_QUESTS_INFO;
import static main.system.GuiEventType.SHOW_TEXT_CENTERED;

/**
 * Created by JustMe on 2/9/2018.
 */
public class GuiStage extends StageX implements StageWithClosable {

    protected final LabelX actionTooltip = new LabelX("", StyleHolder.getDefaultInfoStyle());
    protected final LabelX infoTooltip = new LabelX("", StyleHolder.getDefaultInfoStyle());
    protected RadialMenu radial;
    protected ContainerPanel containerPanel;
    protected OverlayTextPanel textPanel;
    protected Closable displayedClosable;
    protected GameMenu gameMenu;
    protected Blackout blackout;
    protected TextInputPanel tf;
    protected List<String> charsUp = new ArrayList<>();
    protected char lastTyped;
    protected ToolTipManager tooltips;
    protected HqPanel hqPanel;
    protected boolean blackoutIn;
    protected SuperContainer actionTooltipContainer;
    protected SuperContainer infoTooltipContainer;
    protected boolean blocked;
    protected ConfirmationPanel confirmationPanel;
    protected DragManager dragManager;
    protected Entity draggedEntity;
    protected FullLogPanel logPanel;
    protected QuestProgressPanel questProgressPanel;
    protected QuestJournal journal;
    protected SmartButton menuButton;
    protected boolean town;
    protected boolean dialogueMode;
    protected Set<Actor> hiddenActors = new HashSet<>();
    protected ValueContainer locationLabel;
    PlaceNavigationPanel navigationPanel;
    private TipMessageWindow tipMessageWindow;
    private ArrayList<Actor> townActors;
    private ArrayList<Actor> dialogueActors;
    private DialogueContainer dialogueContainer;
    private Map<GameDialogue, DialogueContainer> cache = new HashMap<>();

    LordPanel lordPanel;

    public GuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        //don't want invisible stuff to act...
        setRoot(new GroupX() {
            @Override
            public void act(float delta) {
                if (!town) {
                    super.act(delta);
                    return;
                }
                Actor[] actors = getChildren().begin();
                for (int i = 0, n = actors.length; i < n; i++) {
                    if (actors[i] == null) {
                        continue;
                    }
                    if (actors[i].isVisible()) {
                        actors[i].act(delta);
                    }
                }
                getChildren().end();
            }
        });
        getRoot().setStage_(this);
    }

    @Override
    public GroupX getRoot() {
        return (GroupX) super.getRoot();
    }

    public void openOptionsMenu() {
        OptionsWindow.getInstance().open(OptionsMaster.getOptionsMap(), this);

    }

    protected void init() {
        initGameMenu();
        //        ButtonStyled helpButton = new ButtonStyled(STD_BUTTON.HELP, () ->
        //         GuiEventManager.trigger(SHOW_TEXT_CENTERED, HelpMaster.getHelpText()));
        //        helpButton.setPosition(menuButton.getX() - helpButton.getWidth(),
        //         GdxMaster.getHeight() - helpButton.getHeight());
        //        addActor(helpButton);
        tipMessageWindow = new TipMessageWindow(null);
        addActor(dialogueContainer = new DialogueContainer());
        addActor(questProgressPanel = new QuestProgressPanel());
        HideButton hideQuests = new HideButton(questProgressPanel);
        addActor(hideQuests);

        questProgressPanel.setPosition(GdxMaster.right(questProgressPanel),
                GdxMaster.getHeight() - questProgressPanel.getHeight() - GdxMaster.adjustHeight(128));

        hideQuests.setPosition(questProgressPanel.getX()
                        + GdxMaster.adjustSizeBySquareRoot(100),
                questProgressPanel.getY() - 10 + questProgressPanel.getHeight());


        ExtendableLogPanel log = new ExtendableLogPanel();
        RollableGroup decorated = RollDecorator.decorate(log, main.game.bf.directions.FACING_DIRECTION.EAST);
        addActor(decorated);
        decorated.
                setPosition(GdxMaster.getWidth() - decorated.getWidth(), 0);
        addActor(logPanel = new FullLogPanel(100, 200));

        radial = new RadialMenu();
        addActor(radial);

        containerPanel = new ContainerPanel();
        addActor(containerPanel);
        containerPanel.setPosition(GdxMaster.centerWidth(containerPanel),
                GdxMaster.centerHeight(containerPanel));
        containerPanel.setVisible(false);
        bindEvents();

        gameMenu.setZIndex(Integer.MAX_VALUE);

        addActor(actionTooltipContainer = new SuperContainer(actionTooltip) {
            @Override
            public int getFluctuatingAlphaPeriod() {
                return 0;
            }
        });
        actionTooltipContainer.setAlphaTemplate(Fluctuating.ALPHA_TEMPLATE.ATB_POS);
        actionTooltipContainer.setAlphaFluctuationOn(true);

        addActor(hqPanel = new HqPanel());
        hqPanel.setPosition(GdxMaster.centerWidth(hqPanel),
                GdxMaster.centerHeight(hqPanel));
        hqPanel.setVisible(false);


        addActor(lordPanel = LordPanel.getInstance());
        lordPanel.setPosition(GdxMaster.centerWidth(lordPanel),
                GdxMaster.centerHeight(lordPanel));
        lordPanel.setVisible(false);

        addActor(journal = new QuestJournal());
        journal.setPosition(GdxMaster.centerWidth(journal),
                GdxMaster.centerHeight(journal));
        journal.setVisible(false);

        initTooltipsAndMisc();

        addActor(dragManager = DragManager.getInstance());
        dragManager.setGuiStage(this);
        setBlackoutIn(true);
    }

    protected void initTooltipsAndMisc() {

        textPanel = new OverlayTextPanel();
        addActor(textPanel);
        textPanel.setPosition(GdxMaster.centerWidth(textPanel),
                GdxMaster.centerHeight(textPanel));

        addActor(blackout = new Blackout());
        addActor(tooltips = new ToolTipManager(this));

        addActor(infoTooltipContainer = new SuperContainer(infoTooltip) {
            @Override
            public int getFluctuatingAlphaPeriod() {
                return 0;
            }

            @Override
            public void draw(Batch batch, float parentAlpha) {
                if (parentAlpha == ShaderDrawer.SUPER_DRAW)
                    super.draw(batch, 1);
                else
                    ShaderDrawer.drawWithCustomShader(this, batch, null, false, false);
            }
        });
        infoTooltipContainer.setAlphaTemplate(Fluctuating.ALPHA_TEMPLATE.HIGHLIGHT_MAP);
        infoTooltipContainer.setAlphaFluctuationOn(true);

        addActor(confirmationPanel = ConfirmationPanel.getInstance());

    }

    protected void initGameMenu() {
        gameMenu = createGameMenu();
        addActor(gameMenu);
        gameMenu.setPosition(GdxMaster.centerWidth(gameMenu), GdxMaster.centerHeight(gameMenu));
        GroupX group = new GroupX();

        Image btnBg = new Image(TextureCache.getOrCreateR(
                StrPathBuilder.build(PathFinder.getUiPath(),
                        "components", "generic",
                        "buttons", "special", "menu bg.png")
        ));
        group.setSize(btnBg.getImageWidth(), btnBg.getImageHeight());
        group.addActor(btnBg);
        menuButton = new SmartButton(STD_BUTTON.OPTIONS, () ->
                gameMenu.toggle());

        menuButton.setPosition(-4, 13);
        group.addActor(menuButton);

        addActor(group);
        group.setSize(btnBg.getWidth(),
                btnBg.getHeight());
        group.setPosition(GdxMaster.getWidth() - btnBg.getWidth(),
                GdxMaster.getHeight() - btnBg.getHeight() + 16);


        addActor(locationLabel = new ValueContainer("", "") {
            @Override
            protected boolean isVertical() {
                return true;
            }
        });
        locationLabel.setNameStyle(StyleHolder.getAVQLabelStyle(19));
        locationLabel.setValueStyle(StyleHolder.getAVQLabelStyle(17));

        locationLabel.padTop(12);
        locationLabel.padBottom(12);
        locationLabel.setPosition(0,
                GdxMaster.getHeight() - locationLabel.getHeight());
    }

    protected GameMenu createGameMenu() {
        return new GameMenu();
    }

    public boolean isBlocked() {
        return blocked;
    }

    @Override
    public void draw() {
        //can we just pass if in 'cinematic mode'?
        if (CoreEngine.isCinematicMode()) { //|| !EidolonsGame.isHqEnabled()
            getBatch().begin();
            blackout.draw(getBatch(), 1);
            if (gameMenu.isVisible())
                gameMenu.draw(getBatch(), 1);
            if (confirmationPanel.isVisible())
                confirmationPanel.draw(getBatch(), 1);

            if (radial.isVisible())
                radial.draw(getBatch(), 1);

            dragManager.draw(getBatch(), 1);
            getBatch().end();
            return;
        }
        super.draw();
    }


    @Override
    public void act(float delta) {
        blocked = checkBlocked();
        if (!Blackout.isOnNewScreen())
            if (isBlackoutIn()) {
                blackout.fadeOutAndBack(2f);
                setBlackoutIn(false);
            }
        if (actionTooltipContainer != null)
            if (actionTooltipContainer.getActions().size == 0) {
                actionTooltipContainer.setFluctuateAlpha(true);
                if (!Eidolons.getGame().getManager().isSelecting())
                    if (!CoreEngine.isIggDemoRunning()) //TODO igg demo fix
                        hideTooltip(actionTooltip, 1);
            }
        if (infoTooltipContainer != null)
            if (infoTooltipContainer.getActions().size == 0)
                infoTooltipContainer.setFluctuateAlpha(true);
        if (town) {
            for (Actor actor : getRoot().getChildren()) {
                if (getActorsForTown().contains(actor)) {
                    continue;
                }
                if (actor.isVisible()) {
                    actor.setVisible(false);
                    hiddenActors.add(actor);
                }
            }
        }
        if (isFreezeOnBlackout())
        if (blackout.getChildren().get(0). getColor().a!=0)
            if (blackout.getChildren().get(0). getColor().a!=1) {
                blackout.act(delta);
                return;
            }
        if (dialogueMode) {
            dialogueContainer.setX(GdxMaster.centerWidth(dialogueContainer));
            for (Actor actor : getRoot().getChildren()) {
                if (getActorsForDialogue().contains(actor)) {
                    continue;
                }
                if (actor.isVisible()) {
                    actor.setVisible(false);
                    hiddenActors.add(actor);
                }
            }
        } else {
            dialogueContainer.setVisible(false);
        }

        if (locationLabel != null) {
            locationLabel.setPosition(0,
                    GdxMaster.getHeight() - locationLabel.getHeight());
        }
        super.act(delta);
        resetZIndices();
    }

    private boolean isFreezeOnBlackout() {
        return false;
    }

    public List<Actor> getActorsForDialogue() {
        if (dialogueActors == null) {
            dialogueActors =
                    new ArrayList<>(Arrays.asList(new Actor[]{
                            dialogueContainer,
                            confirmationPanel,
                            getMenuButton().getParent(),
                            getGameMenu(),
                            getTooltips(),
                            blackout,
                            actionTooltipContainer,
                            infoTooltipContainer,
                            OptionsWindow.getInstance()
                    }));
        }
        return dialogueActors;
    }

    public List<Actor> getActorsForTown() {
        if (townActors == null) {
            townActors =
                    new ArrayList<>(Arrays.asList(new Actor[]{
                            dragManager,
                            confirmationPanel,
                            hqPanel,
                            textPanel,
                            getMenuButton().getParent(),
                            getGameMenu(),
                            getTooltips(),
                            blackout,
                            actionTooltipContainer,
                            infoTooltipContainer,
                            OptionsWindow.getInstance()
                    }));
        }
        return townActors;
    }

    protected boolean checkBlocked() {
//        if (dialogueMode)
//            return true;
        if (tipMessageWindow != null)
            if (tipMessageWindow.isVisible())
                return true;
        return
                LordPanel.getInstance().isVisible() ||
                        tipMessageWindow.isVisible() || confirmationPanel.isVisible() || textPanel.isVisible() ||
                        HqPanel.getActiveInstance() != null || OptionsWindow.isActive()
                        || GameMenu.menuOpen;
    }

    @Override
    public Actor hit(float stageX, float stageY, boolean touchable) {
        Actor actor = super.hit(stageX, stageY, touchable);

        if (actor != null)
            if (blocked) {
                if (actor instanceof com.badlogic.gdx.scenes.scene2d.ui.List)
                    return actor;

                List<Group> ancestors = GdxMaster.getAncestors(actor);
                if (actor instanceof Group)
                    ancestors.add((Group) actor);
                if (checkContainsNoOverlaying(ancestors)) {
                    if (!ancestors.contains(LordPanel.getInstance()))
                        if (!ancestors.contains(OptionsWindow.getInstance())) {
                            if (GdxMaster.getFirstParentOfClass(
                                    actor, RadialValueContainer.class) == null) {
                                if (HqPanel.getActiveInstance() == null || !ancestors.contains(HqPanel.getActiveInstance()))
                                    return null;
                            } else if (
                                    tipMessageWindow.isVisible() || confirmationPanel.isVisible())
                                return null;
                        } else {
                            return actor;
                        }
                }
            }
        return actor;
    }

    protected boolean checkContainsNoOverlaying(List<Group> ancestors) {
        if (!ancestors.contains(textPanel))
            if (!ancestors.contains(confirmationPanel))
                if (!ancestors.contains(tipMessageWindow))
                    if (!ancestors.contains(gameMenu))
                        return true;

        return false;
    }

    public Closable getDisplayedClosable() {
        return displayedClosable;
    }

    @Override
    public void setDisplayedClosable(Closable displayedClosable) {
        this.displayedClosable = displayedClosable;
        if (displayedClosable == null)
            setDraggedEntity(null);
    }

    protected void bindEvents() {

        GuiEventManager.bind(GuiEventType.TOGGLE_LORD_PANEL, p -> {
            if (lordPanel.isVisible()) {
                GuiEventManager.trigger(GuiEventType.SHOW_LORD_PANEL, null);
            } else {
                GuiEventManager.trigger(GuiEventType.SHOW_LORD_PANEL, EidolonLord.lord);
            }
        });

        GuiEventManager.bind(GuiEventType.OPEN_OPTIONS, p -> {
            if (p.get() == this || p.get() == getClass()) {
                openOptionsMenu();
            }
        });
        GuiEventManager.bind(SHOW_TEXT_CENTERED, p -> {
            showText((String) p.get());
        });

        GuiEventManager.bind(SHOW_QUESTS_INFO, p -> {
            journal.setUserObject(p.get());
            journal.setStage(this);
            journal.fadeIn();
            journal.open();
        });
        GuiEventManager.bind(GuiEventType.FADE_OUT, p -> {
            blackout.fadeOut((Float) p.get());
        });
        GuiEventManager.bind(GuiEventType.FADE_IN, p -> {
            blackout.fadeIn((Float) p.get());
        });
        GuiEventManager.bind(GuiEventType.FADE_OUT_AND_BACK, p -> {
            blackout.fadeOutAndBack((Number) p.get());
        });

        GuiEventManager.bind(GuiEventType.SHOW_HQ_SCREEN, p -> {
            if (p.get() == null) {
                if (HqMaster.isDirty())
                    confirm("Save changes?", true, () ->
                    {
                        HqDataMaster.saveAll();
                        hqPanel.closed();
                    }, () -> hqPanel.closed());
                else
                    hqPanel.closed();
                return;
            }
            hqPanel.init();
            hqPanel.setEditable(ExplorationMaster.isExplorationOn());
            hqPanel.open();
            hqPanel.setUserObject(p.get());
        });
        GuiEventManager.bind(GuiEventType.SHOW_INFO_TEXT, p -> {
            if (p.get() == null) {
                hideTooltip(infoTooltip, 1f);
            } else {
                String text = p.get().toString();
                LABEL_STYLE style = null;
                if (text.contains(EUtils.STYLE)) {
                    String[] parts = text.split(EUtils.STYLE);
                    style = new EnumMaster<LABEL_STYLE>().retrieveEnumConst(LABEL_STYLE.class, parts[0]);
                    text = parts[1];
                }

                //                textToShow.add() queue!
                infoTooltipContainer.setContents(infoTooltip);
                hideTooltip(actionTooltip, 1f);
                showTooltip(style, false, text, infoTooltip, 2f);
            }
        });
        GuiEventManager.bind(GuiEventType.TARGET_SELECTION, p -> {
            hideTooltip(actionTooltip, 0.5f);
        });
        GuiEventManager.bind(GuiEventType.ACTION_BEING_ACTIVATED, p -> {
            DC_ActiveObj active = (DC_ActiveObj) p.get();
            if (!active.isMine()) {
                return;
            }
            actionTooltipContainer.setContents(actionTooltip);
            if (active.getTargeting() instanceof SelectiveTargeting
                    && active.getTargetObj() == null && active.getRef().getTargetObj() == null)
                showTooltip(true, "Select a target for " + active.getName()
                        , actionTooltip, 0);
            else {
                showTooltip(true, active.getName() + "..."
                        , actionTooltip, 2f);
            }
            hideTooltip(infoTooltip, 1f);
        });

        GuiEventManager.bind(GuiEventType.HIDE_ALL_TEXT, p -> {
            hideTooltip(infoTooltip, 1f);
            hideTooltip(actionTooltip, 1f);
            infoTooltip.setVisible(false);
            actionTooltip.setVisible(false);
        });
        GuiEventManager.bind(GuiEventType.HIDE_ACTION_INFO_TEXT, p -> {
            hideTooltip(actionTooltip, 1f);
        });
        GuiEventManager.bind(GuiEventType.HIDE_INFO_TEXT, p -> {
            hideTooltip(infoTooltip, 1f);
        });
        GuiEventManager.bind(GuiEventType.ACTION_BEING_RESOLVED, p -> {
            DC_ActiveObj active = (DC_ActiveObj) p.get();
            if (ExplorationMaster.isExplorationOn()) {
                return;
            }

            showTooltip(true, active.getOwnerUnit().getNameIfKnown()
                    + " activates " + active.getName(), actionTooltip, 3f);
            hideTooltip(infoTooltip, 1f);

        });

        GuiEventManager.bind(GuiEventType.CONFIRM, p -> {
            Triple<String, Object, Runnable> triple = (Triple<String, Object, Runnable>) p.get();
            if (triple.getMiddle() instanceof Runnable) {
                confirm(triple.getLeft(), true, triple.getRight(), ((Runnable) triple.getMiddle()));
            } else
                confirm(triple.getLeft(), (Boolean) triple.getMiddle(), triple.getRight(), null);

        });

        GuiEventManager.bind(GuiEventType.TIP_MESSAGE, p -> {
            tip((TipMessageSource) p.get());

        });
    }

    private void tip(TipMessageSource o) {
        if (o == null) {
            return;
        }
        if (tipMessageWindow != null)
            if (tipMessageWindow.isVisible()) {
//            ActorMaster.addRemoveAfter(tipMessageWindow);
                tipMessageWindow.setOnClose(() -> tip(o));
                return;
            }
        try {
            tipMessageWindow = new TipMessageWindow(o);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        addActor(tipMessageWindow);
        tipMessageWindow.fadeIn();
        tipMessageWindow.setPosition(GdxMaster.centerWidth(tipMessageWindow),
                GdxMaster.centerHeight(tipMessageWindow));

    }

    public void confirm(String text,
                        boolean canCancel,
                        Runnable onConfirm,
                        Runnable onCancel) {
        confirm(text, canCancel, onConfirm, onCancel, false);
    }

    public void confirm(String text,
                        boolean canCancel,
                        Runnable onConfirm,
                        Runnable onCancel,
                        boolean recursion) {
        if (tipMessageWindow.isVisible()) {
//                tipMessageWindow.getOnClose() TODO
            tipMessageWindow.setOnClose(() -> {
                confirm(text, canCancel, onConfirm, onCancel, true);
            });
            return;
        }
        if (!recursion)
            if (confirmationPanel.isVisible()) {
                confirmationPanel.setOnConfirm(
                        () -> {
                            confirmationPanel.getOnConfirm().run();
                            confirm(text, canCancel, onConfirm, onCancel, true);
                        }
                );
                confirmationPanel.setOnCancel(
                        () -> {
                            confirmationPanel.getOnCancel().run();
                            confirm(text, canCancel, onConfirm, onCancel, true);
                        }
                );
            }
        confirmationPanel.setText(text);
        confirmationPanel.setCanCancel(
                canCancel);
        confirmationPanel.setOnConfirm(onConfirm);
        confirmationPanel.setOnCancel(onCancel);
        confirmationPanel.open();

    }

    protected void showTooltip(String s, LabelX tooltip, float dur) {
        showTooltip(false, s, tooltip, dur);
    }

    public enum LABEL_STYLE {
        AVQ_SMALL(17, FontMaster.FONT.AVQ),
        AVQ_MED(20, FontMaster.FONT.AVQ),
        AVQ_LARGE(24, FontMaster.FONT.AVQ),

        MORPH_SMALL(14, FontMaster.FONT.METAMORPH),
        MORPH_MED(16, FontMaster.FONT.METAMORPH),
        MORPH_LARGE(20, FontMaster.FONT.METAMORPH),


        ;

        public int size;
        public FontMaster.FONT font;
        public Color color;

        LABEL_STYLE(int size, FontMaster.FONT font) {
            this(size, font, GdxColorMaster.getDefaultTextColor());
        }

        LABEL_STYLE(int size, FontMaster.FONT font, Color color) {
            this.size = size;
            this.font = font;
            this.color = color;
        }
    }

    protected void showTooltip(boolean action, String s, LabelX tooltip, float dur) {
        showTooltip(null, action, s, tooltip, dur);
    }

    protected void showTooltip(LABEL_STYLE style, boolean action, String s, LabelX tooltip, float dur) {

        infoTooltip.setVisible(true);
        actionTooltip.setVisible(true);

        if (style != null) {
            tooltip.setStyle(StyleHolder.getStyle(style));
        } else {
            tooltip.setStyle(StyleHolder.getDefaultInfoStyle());
        }

        tooltip.setText(s);
        tooltip.getColor().a = 0;
        tooltip.clearActions();
        if (dur != 0) {
            ActionMaster.addFadeInAndOutAction(tooltip, dur, true);
        } else {
            ActionMaster.addFadeInAction(tooltip, 0.5f);
        }
        tooltip.layout();
        tooltip.pack();
        SuperContainer container = (SuperContainer) tooltip.getParent();
        if (container != null)
            container.setFluctuateAlpha(false);
        else
            return;
        tooltip.getParent().setPosition(
                ((GdxMaster.getWidth() - logPanel.getWidth() * 0.88f) - tooltip.getWidth()) / 2,
                action ? GDX.size(175, 0.2f) : GDX.size(200, 0.2f));
    }

    protected void hideTooltip(LabelX tooltip, float dur) {
        SuperContainer container = (SuperContainer) tooltip.getParent();
        ActionMaster.addFadeOutAction(tooltip, dur, true);
        if (container == null)
            return;
        //        tooltip.clearActions();
        container.setFluctuateAlpha(false);

    }

    public void blackout(float dur) {

        blackout.fadeOutAndBack(dur);
    }

    protected void showText(String s) {
        if (s == null) {
            textPanel.close();
            return;
        }
        textPanel.setText(s);
        textPanel.open();
    }

    @Override
    public boolean keyUp(int keyCode) {
        String c = Keys.toString(keyCode);

        FileLogManager.streamInput("Key Up: "+c);
        if (!charsUp.contains(c)) {
            charsUp.add(c);
        }
        return super.keyUp(keyCode);
    }

    @Override
    public boolean keyDown(int keyCode) {
        FileLogManager.streamInput("Key Down: "+Keys.toString(keyCode));
        if (DC_Game.game == null) {
            return false;
        }
        if (DC_Game.game.getKeyManager() == null) {
            return false;
        }
        if (DC_Game.game.getKeyManager().handleKeyDown(keyCode)) {
            return true;
        }
        return super.keyDown(keyCode);
    }

    @Override
    public boolean keyTyped(char character) {
        if ((int) character == 0)
            return false;
        FileLogManager.streamInput("Key Typed: "+character);
        if (dialogueMode) {
            if (dialogueContainer.getCurrent().getInputProcessor().keyTyped(character))
                return true;
        }
        String str = String.valueOf(character).toUpperCase();
        if (Character.isAlphabetic(character)) {
            if (character == lastTyped) {
                if (!charsUp.contains(str)) {
                    return false;
                }
            }
            charsUp.remove(str);
        }

        lastTyped = character;

        boolean result = false;
        try {
            result = handleKeyTyped(character);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (result)
            return true;
        return super.keyTyped(character);
    }

    public void textInput(TextInputListener textInputListener, String title, String text, String hint) {
        if (tf == null)
            tf = new TextInputPanel(title, text, hint, textInputListener);
        tf.setPosition(GdxMaster.centerWidth(tf), GdxMaster.centerHeight(tf));
        //textInputListener.input(text);
        tf.setVisible(true);

    }


    protected boolean handleKeyTyped(char character) {
        return DC_Game.game.getKeyManager().handleKeyTyped(0, character);
    }

    public void outsideClick() {
        if (textPanel.isVisible()) {
            textPanel.close();
        }
        if (containerPanel.isVisible()) {
            containerPanel.close();
        }
        if (gameMenu.isVisible()) {
            gameMenu.close();
        }

    }

    public RadialMenu getRadial() {
        return radial;
    }

    public ContainerPanel getContainerPanel() {
        return containerPanel;
    }

    public OverlayTextPanel getTextPanel() {
        return textPanel;
    }

    public GameMenu getGameMenu() {
        return gameMenu;
    }

    public ToolTipManager getTooltips() {
        return tooltips;
    }

    public SmartButton getMenuButton() {
        return menuButton;
    }


    public void resetZIndices() {

        if (hqPanel != null)
            hqPanel.setZIndex(Integer.MAX_VALUE);
        if (lordPanel != null)
            lordPanel.setZIndex(Integer.MAX_VALUE);
        if (confirmationPanel != null)
            confirmationPanel.setZIndex(Integer.MAX_VALUE);
        if (infoTooltipContainer != null)
            infoTooltipContainer.setZIndex(Integer.MAX_VALUE);
        if (actionTooltipContainer != null)
            actionTooltipContainer.setZIndex(Integer.MAX_VALUE);
        if (tipMessageWindow != null)
            tipMessageWindow.setZIndex(Integer.MAX_VALUE);
        if (tooltips != null)
            tooltips.setZIndex(Integer.MAX_VALUE);
        if (dragManager != null)
            dragManager.setZIndex(Integer.MAX_VALUE);
        if (blackout != null)
            blackout.setZIndex(Integer.MAX_VALUE);
    }

    public Blackout getBlackout() {
        return blackout;
    }

    public boolean isBlackoutIn() {
        return blackoutIn;
    }

    public void setBlackoutIn(boolean blackoutIn) {
        this.blackoutIn = blackoutIn;
    }

    public Entity getDraggedEntity() {
        return draggedEntity;
    }

    public void setDraggedEntity(Entity draggedEntity) {
        this.draggedEntity = draggedEntity;
        dragManager.setDraggedEntity(draggedEntity);
    }

    public DragManager getDragManager() {
        return dragManager;
    }

    @Override
    public boolean setScrollFocus(Actor actor) {
        if (getScrollFocus() != actor)
            EUtils.hideTooltip();
        return super.setScrollFocus(actor);
    }

    public boolean isTown() {
        return town;
    }

    public void setTown(boolean town) {
        this.town = town;
        if (!town) {
            for (Actor actor : new ArrayList<>(hiddenActors)) {
                actor.setVisible(true);
                hiddenActors.remove(actor);
            }
        }
    }

    public void setDialogueMode(boolean dialogueMode) {
        this.dialogueMode = dialogueMode;
        if (!dialogueMode) {
            for (Actor actor : new ArrayList<>(hiddenActors)) {
                actor.setVisible(true);
                hiddenActors.remove(actor);
            }
        }
        getScreen().updateInputController();
    }

    public void afterBlackout(Runnable runnable) {
//TODO         blackout.fadeOutAndBack(runnable);
        runnable.run();
//        Eidolons.onNonGdxThread(() -> {
//            int time=0;
//            while (time < 3000) {
//                time += 100;
//                WaitMaster.WAIT(100);
//                if (blackout.getChildren().get(0). getColor().a == 0) {
//                    Gdx.app.postRunnable(runnable);
//                    break;
//                }
//            }
//        });

    }

    public void playDialogue(DialogueHandler handler) {
        if (dialogueMode) {
            if (isDialogueCached())
                dialogueContainer.remove();
            dialogueDone();
        }
        if (isDialogueCached()) {
            dialogueContainer = cache.get(handler.getDialogue());
            if (dialogueContainer == null) {
                addActor(dialogueContainer = new DialogueContainer());
            }
        }
        dialogueContainer.fadeIn();
        dialogueContainer.play(handler);
        setDialogueMode(true);

        cache.put(handler.getDialogue(), dialogueContainer);
    }

    private boolean isDialogueCached() {
        return false;
    }

    public void dialogueDone() {
        dialogueContainer.fadeOut();
        setDialogueMode(false);
        DialogueManager.setRunning(false);
        dialogueContainer.hide();
        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.DIALOGUE_DONE, dialogueContainer.getDialogue());
        DialogueManager.dialogueDone();
    }

    public boolean isDialogueMode() {
        return dialogueMode;
    }

    public void resetConfirmPanel(ConfirmationPanel instance) {
        confirmationPanel = instance;
    }
}
