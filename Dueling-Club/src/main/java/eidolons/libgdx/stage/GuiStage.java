package eidolons.libgdx.stage;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionHelper;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.GameDialogue;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogueContainer;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.netherflame.main.event.TipMessageSource;
import eidolons.game.netherflame.main.event.TipMessageWindow;
import eidolons.game.netherflame.main.soul.EidolonLord;
import eidolons.game.netherflame.main.soul.panel.LordPanel;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.bf.generic.SuperContainer;
import eidolons.libgdx.bf.menu.GameMenu;
import eidolons.libgdx.gui.HideButton;
import eidolons.libgdx.gui.controls.radial.RadialContainer;
import eidolons.libgdx.gui.controls.radial.RadialMenu;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SymbolButton;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerPanel;
import eidolons.libgdx.gui.panels.dc.logpanel.ExtendableLogPanel;
import eidolons.libgdx.gui.panels.dc.logpanel.FullLogPanel;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.quest.QuestJournal;
import eidolons.libgdx.gui.panels.quest.QuestProgressPanel;
import eidolons.libgdx.screens.AtlasGenSpriteBatch;
import eidolons.libgdx.screens.Blackout;
import eidolons.libgdx.screens.map.town.navigation.PlaceNavigationPanel;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.OptionsWindow;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.log.FileLogManager;
import main.system.graphics.FontMaster;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.threading.WaitMaster;

import java.util.*;

import static eidolons.libgdx.screens.ScreenMaster.getScreen;
import static main.system.GuiEventType.SHOW_QUESTS_INFO;
import static main.system.GuiEventType.SHOW_TEXT_CENTERED;

/**
 * Created by JustMe on 2/9/2018.
 */
public class GuiStage extends GenericGuiStage implements StageWithClosable {

    private final Blackout blackout;
    protected List<String> charsUp = new ArrayList<>();
    protected char lastTyped;


    protected RadialMenu radial;
    protected ContainerPanel containerPanel;
    protected GameMenu gameMenu;
    protected SymbolButton menuButton;

    protected HqPanel hqPanel;
    protected boolean blackoutIn;
    protected boolean blocked;

    protected FullLogPanel fullLogPanel;
    protected QuestProgressPanel questProgressPanel;
    protected QuestJournal journal;

    protected ArrayList<Actor> townActors;
    protected boolean town;
    protected Set<Actor> hiddenActors = new HashSet<>();

    protected boolean dialogueMode;
    protected ArrayList<Actor> dialogueActors;
    protected DialogueContainer dialogueContainer;
    protected Map<GameDialogue, DialogueContainer> dialogueCache = new HashMap<>();
    protected LordPanel lordPanel;
    protected PlaceNavigationPanel navigationPanel;
    protected HideButton hideQuests;
    protected ExtendableLogPanel logPanel;


    public GuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        //don't want invisible stuff to act...
        blackout = new Blackout();
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

        //        ButtonStyled helpButton = new ButtonStyled(STD_BUTTON.HELP, () ->
        //         GuiEventManager.trigger(SHOW_TEXT_CENTERED, HelpMaster.getHelpText()));
        //        helpButton.setPosition(menuButton.getX() - helpButton.getWidth(),
        //         GdxMaster.getHeight() - helpButton.getHeight());
        //        addActor(helpButton);

        tipMessageWindow = new TipMessageWindow(null);
        addActor(dialogueContainer = new DialogueContainer());
        addActor(questProgressPanel = new QuestProgressPanel());
        hideQuests = new HideButton(questProgressPanel);
        addActor(hideQuests);

        questProgressPanel.setPosition(GdxMaster.right(questProgressPanel),
                GdxMaster.getHeight() - questProgressPanel.getHeight() - GdxMaster.adjustHeight(128));

        logPanel = new ExtendableLogPanel(true);
        // RollDecorator.decorate(log, main.game.bf.directions.FACING_DIRECTION.EAST);
        addActor(logPanel);
        //        logPanel.setOnClose(()->{
        //            GuiEventManager.trigger(GuiEventType. LOG_ROLLED_OUT);
        //        });
        //        logPanel.setOnOpen(()->{
        //            GuiEventManager.trigger(GuiEventType. LOG_ROLLED_IN);
        //        });
        logPanel.
                setPosition(GdxMaster.getWidth() - logPanel.getWidth(), GdxMaster.getTopY(logPanel));
        addActor(fullLogPanel = new FullLogPanel(100, 200));

        radial = new RadialMenu();
        addActor(radial);

        if (!CoreEngine.TEST_LAUNCH) {
            containerPanel = new ContainerPanel();
            addActor(containerPanel);
            containerPanel.setPosition(GdxMaster.centerWidth(containerPanel),
                    GdxMaster.centerHeight(containerPanel));
            containerPanel.setVisible(false);
        }
        bindEvents();

        initGameMenu();
        gameMenu.setZIndex(Integer.MAX_VALUE);

        addActor(actionTooltipContainer = new SuperContainer(actionTooltip) {
            @Override
            public int getFluctuatingAlphaPeriod() {
                return 0;
            }
        });
        actionTooltipContainer.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.ATB_POS);
        Fluctuating.setAlphaFluctuationOn(true);

        if (!CoreEngine.TEST_LAUNCH) {
            addActor(hqPanel = new HqPanel());
            hqPanel.setPosition(GdxMaster.centerWidth(hqPanel),
                    GdxMaster.centerHeight(hqPanel));
            hqPanel.setVisible(false);
            if (LordPanel.ON) {
                addActor(lordPanel = LordPanel.getInstance());
                lordPanel.setPosition(GdxMaster.centerWidth(lordPanel),
                        GdxMaster.centerHeight(lordPanel));
                lordPanel.setVisible(false);
            }
            addActor(journal = new QuestJournal());
            journal.setPosition(GdxMaster.centerWidth(journal),
                    GdxMaster.centerHeight(journal));
            journal.setVisible(false);

        }
        initTooltipsAndMisc();

        addActor(dragManager = DragManager.getInstance());
        dragManager.setGuiStage(this);
    }


    protected void initGameMenu() {
        gameMenu = createGameMenu();
        addActor(gameMenu);
        gameMenu.setPosition(GdxMaster.centerWidth(gameMenu), GdxMaster.centerHeight(gameMenu));
        GroupX menuButton = new GroupX();
        Image btnBg = new Image(TextureCache.getOrCreateR(
                StrPathBuilder.build(PathFinder.getUiPath(),
                        "components", "generic",
                        "buttons", "special", "menu bg.png")
        ));
        menuButton.setSize(btnBg.getImageWidth(), btnBg.getImageHeight());
        menuButton.addActor(btnBg);
        this.menuButton = new SymbolButton(STD_BUTTON.OPTIONS, () ->
                gameMenu.toggle());
        this.menuButton.setPosition(-4, 13);
        menuButton.addActor(this.menuButton);

        addActor(menuButton);
        menuButton.setSize(btnBg.getWidth(),
                btnBg.getHeight());
        menuButton.setPosition(GdxMaster.getWidth() - btnBg.getWidth(),
                GdxMaster.getHeight() - btnBg.getHeight() + 16);

    }

    protected GameMenu createGameMenu() {
        return new GameMenu();
    }

    public boolean isBlocked() {
        return blocked;
    }

    @Override
    public void draw() {
        if (GdxMaster.WRITE_ATLAS_IMAGES) {
            if (getBatch() instanceof AtlasGenSpriteBatch) {
                ((AtlasGenSpriteBatch) getBatch()).setAtlas(AtlasGenSpriteBatch.ATLAS.ui);
            }
        }
        //can we just pass if in 'cinematic mode'?

        //        if (Cinematics.ON) TODO could it be useful?
        //            if (dialogueContainer.getCurrent().getColor().a == 0) {
        //                getBatch().begin();
        //                drawCinematicMode(getBatch());
        //                blackout.draw(getCustomSpriteBatch());
        //                getBatch().end();
        //                return;
        //            }
        //        if (hqPanel.isVisible()) {
        //            getBatch().begin();
        //            hqPanel.draw(getBatch(), 1f);
        //            getBatch().end();
        //            return;
        //        }
        if (Flags.isFootageMode()) { //|| !EidolonsGame.isHqEnabled()
            getBatch().begin();
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
        blackout.draw(getCustomSpriteBatch());
    }

    protected void drawCinematicMode(Batch batch) {
    }


    @Override
    public void act(float delta) {
        blackout.act(delta);
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
            if (dialogueContainer.getCurrent().getColor().a == 0) {

                for (Actor actor : getActorsForDialogue()) {
                    actor.act(delta);
                }
                if (tipMessageWindow != null) {
                    tipMessageWindow.act(delta);
                }
                return;
            }
        } else {
            dialogueContainer.setVisible(false);
        }

        blocked = checkBlocked();

        ////TODO GDx revamp - can't do this all the time!
        // if (actionTooltipContainer != null)
        //     if (actionTooltipContainer.getActions().size == 0) {
        //         actionTooltipContainer.setFluctuateAlpha(true);
        //         if (!Eidolons.getGame().getManager().isSelecting())
        //                 hideTooltip(actionTooltip, 1);
        //     }
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


        super.act(delta);
        resetZIndices();

    }


    public List<Actor> getActorsForDialogue() {
        if (dialogueActors == null) {
            dialogueActors =
                    new ArrayList<>(Arrays.asList(getDialogueActors()));
        }
        if (!dialogueActors.contains(tipMessageWindow)) {
            dialogueActors.add(tipMessageWindow);
        }
        return dialogueActors;
    }

    protected Actor[] getDialogueActors() {
        return new Actor[]{
                dialogueContainer,
                confirmationPanel,
        };
    }

    public List<Actor> getActorsForTown() {
        if (townActors == null) {
            townActors =
                    new ArrayList<>(Arrays.asList(dragManager,
                            confirmationPanel,
                            hqPanel,
                            textPanel,
                            getMenuButton().getParent(),
                            getGameMenu(),
                            getTooltips(),
                            actionTooltipContainer,
                            infoTooltipContainer,
                            OptionsWindow.getInstance()));
        }
        return townActors;
    }

    protected boolean checkBlocked() {
        //        if (dialogueMode)
        //            return true;
        if (tipMessageWindow != null)
            if (tipMessageWindow.isVisible())
                if (tipMessageWindow.getColor().a != 0) //TODO why are there such cases?!
                    return true;
        return
                LordPanel.visibleNotNull() ||
                        confirmationPanel.isVisible() || GdxMaster.isVisibleEffectively(textPanel) ||
                        HqPanel.getActiveInstance() != null || OptionsWindow.isActive()
                        || GameMenu.menuOpen;
    }

    @Override
    public Actor hit(float stageX, float stageY, boolean touchable) {
        Actor actor = super.hit(stageX, stageY, touchable);

        if (actor != null)
            if (blocked) { //if an overlay has blocked other UI but we want IT to be touchable
                if (actor instanceof com.badlogic.gdx.scenes.scene2d.ui.List)
                    return actor;

                List<Group> ancestors = GdxMaster.getAncestors(actor);
                for (Group ancestor : ancestors) {
                    if (ancestor == overlayPanel) {
                        return actor;
                    }
                }
                if (actor instanceof Group)
                    ancestors.add((Group) actor);
                if (checkContainsNoOverlaying(ancestors)) {
                    if (!ancestors.contains(LordPanel.getInstance()))
                        if (!ancestors.contains(OptionsWindow.getInstance())) {
                            if (GdxMaster.getFirstParentOfClass(
                                    actor, RadialContainer.class) == null) {
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
        for (Group ancestor : ancestors) {
            if (ancestor instanceof OverlayingUI) {
                return false;
            }
        }
        if (!ancestors.contains(textPanel))
            if (!ancestors.contains(confirmationPanel))
                if (!ancestors.contains(tipMessageWindow))
                    return !ancestors.contains(gameMenu);

        return false;
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

        GuiEventManager.bind(GuiEventType.TIP_MESSAGE, p -> {
            tip((TipMessageSource) p.get());

        });
    }

    protected void tip(TipMessageSource o) {
        if (o == null) {
            return;
        }
        if (tipMessageWindow != null)
            if (tipMessageWindow.isVisible())
                if (tipMessageWindow.getColor().a > 0) {
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


    @Override
    public boolean keyUp(int keyCode) {
        if (overlayPanel != null)
            if (!overlayPanel.keyUp(keyCode))
                return true;
        String c = Keys.toString(keyCode);
        // if (Analystics.inOn())
        //     FileLogManager.streamInput("Key Up: " + c);
        if (!charsUp.contains(c)) {
            charsUp.add(c);
        }
        return super.keyUp(keyCode);
    }

    @Override
    public boolean keyDown(int keyCode) {
        if (overlayPanel != null)
            if (!overlayPanel.keyDown(keyCode))
                return true;
        FileLogManager.streamInput("Key Down: " + Keys.toString(keyCode));
        if (DC_Game.game == null) {
            return false;
        }
        if (DC_Game.game.getKeyManager() == null) {
            return false;
        }
        try {
            if (DC_Game.game.getKeyManager().handleKeyDown(keyCode)) {
                return true;
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return super.keyDown(keyCode);
    }

    @Override
    public boolean keyTyped(char character) {
        if (GdxMaster.isVisibleEffectively(textInputPanel)) {
            textInputPanel.keyTyped(character);
            return true;
        }
        if (overlayPanel != null)
            if (!overlayPanel.keyTyped(character))
                return true;
        if ((int) character == 0)
            return false;
        FileLogManager.streamInput("Key Typed: " + character);
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


    protected boolean handleKeyTyped(char character) {
        return DC_Game.game.getKeyManager().handleKeyTyped(0, character);
    }

    public void outsideClick() {
        if (textPanel != null)
            if (textPanel.isVisible()) {
                textPanel.close();
            }
        if (containerPanel != null)
            if (containerPanel.isVisible()) {
                containerPanel.close();
            }
        if (gameMenu != null)
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


    public GameMenu getGameMenu() {
        return gameMenu;
    }

    public SymbolButton getMenuButton() {
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
        if (cursorDecorator != null)
            cursorDecorator.setZIndex(Integer.MAX_VALUE);
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
        //                if (blackout.getChildren().getVar(0). getColor().a == 0) {
        //                    Gdx.app.postRunnable(runnable);
        //                    break;
        //                }
        //            }
        //        });

    }

    public void dialogueStarted(DialogueHandler handler) {
        if (dialogueMode) {
            if (isDialogueCached())
                dialogueContainer.remove();
            dialogueDone();
        }
        if (isDialogueCached()) {
            dialogueContainer = dialogueCache.get(handler.getDialogue());
            if (dialogueContainer == null) {
                addActor(dialogueContainer = new DialogueContainer());
            }
        }

        dialogueToggle(true);
        dialogueContainer.fadeIn();
        dialogueContainer.play(handler);

        dialogueCache.put(handler.getDialogue(), dialogueContainer);

        //        Eidolons.getScreen().toBlack();
        //        Eidolons.getScreen().blackout(5, 0);
    }

    protected void dialogueToggle(boolean on) {
        //        if (!DialogueManager.TEST) {
        //            VisionManager.setCinematicVision(on);
        //        }
        setDialogueMode(on);
        DialogueManager.setRunning(on);
        Cinematics.ON = on;
    }

    protected boolean isDialogueCached() {
        return false;
    }

    public void dialogueDone() {
        Eidolons.getGame().getManager().setHighlightedObj(null);
        dialogueContainer.fadeOut();
        dialogueToggle(false);
        dialogueContainer.hide();
        GdxMaster.setDefaultCursor();
        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.DIALOGUE_DONE, dialogueContainer.getDialogue());
        DialogueManager.dialogueDone();
        VisionHelper.setCinematicVision(false);
    }

    public boolean isDialogueMode() {
        return dialogueMode;
    }

    public void resetConfirmPanel(ConfirmationPanel instance) {
        confirmationPanel = instance;
        dialogueActors = null;
        townActors = null;
    }

    public DialogueContainer getDialogueContainer() {
        return dialogueContainer;
    }

    public Blackout getBlackout() {
        return blackout;
    }
}
