package libgdx.stage;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionHelper;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.GameDialogue;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.story.cinematic.Cinematics;
import eidolons.game.exploration.handlers.ExplorationMaster;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.tips.TipMessageSource;
import libgdx.GdxMaster;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.bf.Fluctuating;
import libgdx.bf.generic.SuperContainer;
import libgdx.bf.menu.GameMenu;
import libgdx.controls.Controller;
import libgdx.controls.GlobalController;
import libgdx.gui.HideButton;
import libgdx.gui.dungeon.controls.radial.RadialContainer;
import libgdx.gui.dungeon.controls.radial.RadialMenu;
import libgdx.gui.generic.GroupX;
import libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.dungeon.menu.OptionsWindow;
import libgdx.gui.dungeon.panels.dc.inventory.container.ContainerPanel;
import libgdx.gui.dungeon.panels.dc.logpanel.ExtendableLogPanel;
import libgdx.gui.dungeon.panels.dc.logpanel.FullLogPanel;
import libgdx.gui.dungeon.panels.dialogue.DialogueContainer;
import libgdx.gui.dungeon.panels.generic.TipMessageWindow;
import libgdx.gui.dungeon.panels.headquarters.HqMaster;
import libgdx.gui.dungeon.panels.headquarters.HqPanel;
import libgdx.gui.dungeon.panels.headquarters.datasource.HqDataMaster;
import libgdx.gui.dungeon.panels.quest.QuestJournal;
import libgdx.gui.dungeon.panels.quest.QuestProgressPanel;
import libgdx.screens.Blackout;
import libgdx.screens.handlers.ScreenMaster;
import libgdx.assets.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.log.FileLogManager;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.threading.WaitMaster;

import java.util.*;

import static main.system.GuiEventType.SHOW_QUESTS_INFO;
import static main.system.GuiEventType.SHOW_TEXT_CENTERED;

/**
 * Created by JustMe on 2/9/2018.
 */
public abstract class GuiStage extends GenericGuiStage implements StageWithClosable {

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
    protected HideButton hideQuests;
    protected ExtendableLogPanel logPanel;
    private GroupX customPanel;
    private long timeLastTyped;


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

        controller = getControllerInstance(DEFAULT_CONTROLLER);
        if (controller == null) {
            controller = new GlobalController();
        }
    }

    private Controller getControllerInstance(Controller.CONTROLLER c) {
        switch (c) {
            case ACTION -> {
            }
        }
        return globalController;
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

    protected void afterInit() {

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

        GuiEventManager.bind(GuiEventType.SHOW_CUSTOM_PANEL, p -> {
            addActor(customPanel = (GroupX) p.get());
            GdxMaster.center(customPanel);

        });
        GuiEventManager.bind(GuiEventType.HIDE_CUSTOM_PANEL, p -> {
            customPanel.fadeOut();
            ActionMasterGdx.addRemoveAfter(customPanel);
            customPanel = null;
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
            ActiveObj active = (ActiveObj) p.get();
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

        if (controller != globalController) {
            if (controller.keyDown(keyCode) || globalController.keyDown(keyCode))
                return true;
        } else {
            if (controller.keyDown(keyCode)) {
                return true;
            }
        }

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
            // if (character == lastTyped) {
            //     if (!charsUp.contains(str)) {
            //         return false;
            //     }
            // }
            charsUp.remove(str);
        }
        if (lastTyped == character) {
            float delta = 450; //TODO gdx quick fix
            if (TimeMaster.getTime()-timeLastTyped  < delta) {
                main.system.auxiliary.log.LogMaster.log(1, character + " - DOUBLE keyTyped!");
                return true;
            }
        }
        timeLastTyped = TimeMaster.getTime();
        lastTyped = character;

        Core.onNonGdxThread(() ->  handleKeyTyped(character));
        return super.keyTyped(character);
    }


    public static Controller.CONTROLLER DEFAULT_CONTROLLER = Controller.CONTROLLER.DEBUG;
    GlobalController globalController = new GlobalController();
    private Controller controller;

    protected boolean handleKeyTyped(char character) {

        if (globalController != null) {
            if (globalController.charTyped(character)) {
                return true;
            }
        }
        if (!Flags.isJar()) {
            //TODO gdx Review
            // if (checkControllerHotkey(keyMod, character)) {
            //     return true;
            // }

            if (controller != null) {
                try {
                    if (controller.charTyped(character)) {
                        return true;
                    }
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                    return false;
                }
            }
        }
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
        ScreenMaster.getScreen().updateInputController();
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
        Core.getGame().getManager().setHighlightedObj(null);
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
