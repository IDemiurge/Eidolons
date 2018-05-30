package eidolons.libgdx.stage;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.bf.SuperActor.ALPHA_TEMPLATE;
import eidolons.libgdx.bf.generic.SuperContainer;
import eidolons.libgdx.bf.menu.GameMenu;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.RollDecorator;
import eidolons.libgdx.gui.RollDecorator.RollableGroup;
import eidolons.libgdx.gui.controls.radial.RadialMenu;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerPanel;
import eidolons.libgdx.gui.panels.dc.logpanel.FullLogPanel;
import eidolons.libgdx.gui.panels.dc.logpanel.SimpleLogPanel;
import eidolons.libgdx.gui.panels.dc.logpanel.text.OverlayTextPanel;
import eidolons.libgdx.gui.panels.dc.menus.outcome.OutcomePanel;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.tooltips.ToolTipManager;
import eidolons.libgdx.screens.map.layers.Blackout;
import eidolons.libgdx.shaders.ShaderMaster;
import eidolons.libgdx.utils.TextInputPanel;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.OptionsWindow;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Entity;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

import static main.system.GuiEventType.SHOW_TEXT_CENTERED;

/**
 * Created by JustMe on 2/9/2018.
 */
public class GuiStage extends StageX implements StageWithClosable {

    protected RadialMenu radial;
    protected ContainerPanel containerPanel;
    protected OverlayTextPanel textPanel;
    protected Closable displayedClosable;
    protected GameMenu gameMenu;
    protected OutcomePanel outcomePanel;
    protected Blackout blackout;
    protected TextInputPanel tf;
    protected List<String> charsUp = new ArrayList<>();
    protected char lastTyped;
    protected ToolTipManager tooltips;
    protected HqPanel hqPanel;
    protected boolean blackoutIn;
    protected   SuperContainer actionTooltipContainer;
    protected final LabelX actionTooltip = new LabelX("", 16);

    protected   SuperContainer infoTooltipContainer;
    protected final LabelX infoTooltip = new LabelX("", 16);
    protected boolean blocked;
    protected ConfirmationPanel confirmationPanel;
    protected DragManager dragManager;
    private Entity draggedEntity;
    private FullLogPanel logPanel;

    public GuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);

    }

    public void openOptionsMenu() {
        OptionsWindow.getInstance().open(OptionsMaster.getOptionsMap(), this);

    }

    protected void init() {
        gameMenu = createGameMenu();
        addActor(gameMenu);
        gameMenu.setPosition(GdxMaster.centerWidth(gameMenu), GdxMaster.centerHeight(gameMenu));

        ButtonStyled menuButton = new ButtonStyled(STD_BUTTON.OPTIONS, () ->
         gameMenu.toggle());
        menuButton.setPosition(GdxMaster.getWidth() - menuButton.getWidth(),
         GdxMaster.getHeight() - menuButton.getHeight());
        addActor(menuButton);
//        ButtonStyled helpButton = new ButtonStyled(STD_BUTTON.HELP, () ->
//         GuiEventManager.trigger(SHOW_TEXT_CENTERED, HelpMaster.getHelpText()));
//        helpButton.setPosition(menuButton.getX() - helpButton.getWidth(),
//         GdxMaster.getHeight() - helpButton.getHeight());
//        addActor(helpButton);


        SimpleLogPanel log = new SimpleLogPanel();
        RollableGroup decorated = RollDecorator.decorate(log, FACING_DIRECTION.EAST);
        addActor(decorated);
        decorated.
         setPosition(GdxMaster.getWidth() - decorated.getWidth(), 0);
        addActor(blackout = new Blackout());
        addActor(logPanel = new FullLogPanel(100, 200));

        radial = new RadialMenu();
        addActor(radial);
        addActor(tooltips = new ToolTipManager(this));

        textPanel = new OverlayTextPanel();
        addActor(textPanel);
        textPanel.setPosition(GdxMaster.centerWidth(textPanel),
         GdxMaster.centerHeight(textPanel));


        containerPanel = new ContainerPanel();
        addActor(containerPanel);
        containerPanel.setPosition(GdxMaster.centerWidth(containerPanel),
         GdxMaster.centerHeight(containerPanel));
        containerPanel.setVisible(false);
        bindEvents();

        gameMenu.setZIndex(Integer.MAX_VALUE);

        addActor(actionTooltipContainer = new SuperContainer(actionTooltip));
        actionTooltipContainer.setAlphaTemplate(ALPHA_TEMPLATE.ATB_POS);
        actionTooltipContainer.setAlphaFluctuationOn(true);

        addActor(hqPanel = new HqPanel());
        hqPanel.setPosition(GdxMaster.centerWidth(hqPanel),
         GdxMaster.centerHeight(hqPanel));
        hqPanel.setVisible(false);

        addActor(infoTooltipContainer = new SuperContainer(infoTooltip){
            @Override
            public void draw(Batch batch, float parentAlpha) {
                if (parentAlpha==ShaderMaster.SUPER_DRAW)
                    super.draw(batch, 1);
                else
                    ShaderMaster.drawWithCustomShader(this, batch, null);
            }
        });
        infoTooltipContainer.setAlphaTemplate(ALPHA_TEMPLATE.HIGHLIGHT_MAP);
        infoTooltipContainer.setAlphaFluctuationOn(true);

        addActor(confirmationPanel = ConfirmationPanel.getInstance());
        setDebugAll(false);

        addActor(dragManager  = DragManager.getInstance());
        setBlackoutIn(true);
    }

    protected GameMenu createGameMenu() {
        return new GameMenu();
    }
//
//    @Override
//    public void closeClosable(Closable closable) {
//        if (closable instanceof GroupX) {
//            ((GroupX) closable).fadeOut();
//        } else ((Actor) closable).setVisible(false);
//        if (closable instanceof Blocking) {
////            GuiEventManager.trigger(GuiEventType.GAME_RESUMED);
//            if (DC_Game.game!=null )
//                DC_Game.game.getLoop().setPaused(false, false);
//        }
//        setDisplayedClosable(null);
//    }
//
//    @Override
//    public void openClosable(Closable closable) {
//         closeDisplayed(closable);
//
//        if (closable instanceof Blocking) {
////            GuiEventManager.trigger(GuiEventType.GAME_PAUSED);
//            if (DC_Game.game!=null )
//                DC_Game.game.getLoop().setPaused(true, false);
//        }
//
//         setDisplayedClosable(closable);
//
//        if (closable instanceof GroupX) {
//            ((GroupX) closable).fadeIn();
//        } else ((Actor) closable).setVisible(true);
//    }
//
//    @Override
//    public boolean closeDisplayed(Closable newClosable) {
//        if (getDisplayedClosable() == newClosable)
//            return false;
//        return closeDisplayed();
//    }
//        public boolean closeDisplayed() {
//        if (getDisplayedClosable() == null)
//            return false;
//        getDisplayedClosable().close();
//        displayedClosable = null;
//        return true;
//    }

    public boolean isBlocked() {
        return blocked;
    }

    @Override
    public void draw() {
        super.draw();
    }


    @Override
    public void act(float delta) {
        blocked = checkBlocked();
        if (!Blackout.isOnNewScreen())
        if (isBlackoutIn())
        {
            blackout.fadeOutAndBack(2f);
            setBlackoutIn(false);
        }

        if (actionTooltipContainer.getActions().size==0)
        {
            actionTooltipContainer.setFluctuateAlpha(true);

        }
        if (infoTooltipContainer.getActions().size==0)
            infoTooltipContainer.setFluctuateAlpha(true);


        super.act(delta);
        resetZIndices();
    }

    protected boolean checkBlocked() {
        return confirmationPanel.isVisible() ||textPanel.isVisible() ||
        HqPanel.getActiveInstance()!=null || OptionsWindow.isActive()
         || GameMenu.menuOpen ;
    }

    public Closable getDisplayedClosable() {
        return displayedClosable;
    }

    @Override
    public void setDisplayedClosable(Closable displayedClosable) {
        this.displayedClosable = displayedClosable;
        if (displayedClosable==null )
            setDraggedEntity(null);
    }

    protected void bindEvents() {
        GuiEventManager.bind(GuiEventType.OPEN_OPTIONS, p -> {
            if (p.get() == this || p.get() == getClass()) {
                openOptionsMenu();
            }
        });
        GuiEventManager.bind(SHOW_TEXT_CENTERED, p -> {
            showText((String) p.get());
        });

        GuiEventManager.bind(GuiEventType.FADE_OUT, p -> {
            blackout.fadeOut((Float) p.get());
        });
        GuiEventManager.bind(GuiEventType.FADE_IN, p -> {
            blackout.fadeIn((Float) p.get());
        });
        GuiEventManager.bind(GuiEventType.FADE_OUT_AND_BACK, p -> {
            blackout.fadeOutAndBack((Float) p.get());
        });

        GuiEventManager.bind(GuiEventType.SHOW_HQ_SCREEN, p -> {
            if (p.get() == null) {
                if (HqMaster.isDirty())
                confirm("Save changes?", true, ()->
                 {
                     HqDataMaster.saveAll();
                     hqPanel.close();
                 }, ()-> hqPanel.close());
                else
                    hqPanel.close() ;
                return;
            }

            hqPanel.setEditable(ExplorationMaster.isExplorationOn());
            hqPanel.open();
            hqPanel.setUserObject(p.get());
        });
        GuiEventManager.bind(GuiEventType.SHOW_INFO_TEXT, p -> {
            if (p.get() == null) {
                hideTooltip(infoTooltip, 1f);
            } else {
                infoTooltipContainer.setContents(infoTooltip);
                hideTooltip(actionTooltip, 1f);
                showTooltip(p.get().toString(), infoTooltip, 2f);
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
             && active.getTargetObj()==null && active .getRef(). getTargetObj()==null )
                showTooltip(true, "Select a target for " + active.getName()
                 , actionTooltip, 0);
            else {
                showTooltip(true, active.getName()+"..."
                 , actionTooltip, 2f);
            }
            hideTooltip(infoTooltip, 1f);
        });

        GuiEventManager.bind(GuiEventType.ACTION_BEING_RESOLVED, p -> {
            DC_ActiveObj active = (DC_ActiveObj) p.get();
            if (ExplorationMaster.isExplorationOn()) {
                return;
            }

            showTooltip(true, active.getOwnerObj().getNameIfKnown()
             + " activates " + active.getName(), actionTooltip, 3f);
            hideTooltip(infoTooltip, 1f);

        });

        GuiEventManager.bind(GuiEventType.CONFIRM, p -> {
            Triple<String, Boolean, Runnable> triple = (Triple<String, Boolean, Runnable>) p.get();
            confirm(triple.getLeft(), triple.getMiddle(), triple.getRight(), null );

        });
    }
    public   void confirm(String text,
                                  boolean canCancel,
                          Runnable onConfirm,
                          Runnable onCancel) {
        confirmationPanel.setText(text);
        confirmationPanel.setCanCancel(
         canCancel);
        confirmationPanel.setOnConfirm(onConfirm);
        confirmationPanel.setOnCancel(onCancel);
        confirmationPanel.open();

    }
    protected void showTooltip(  String s, LabelX tooltip, float dur) {
        showTooltip(false, s, tooltip, dur);
    }
        protected void showTooltip(boolean action, String s, LabelX tooltip, float dur) {

        tooltip.setText(s);
        tooltip.getColor().a = 0;
        tooltip.clearActions();
        if (dur != 0) {
            ActorMaster.addFadeInAndOutAction(tooltip, dur, true);
        } else {
            ActorMaster.addFadeInAction(tooltip, 0.5f);
        }
        tooltip.layout();
        tooltip.pack();
        SuperContainer container= (SuperContainer) tooltip.getParent();

        container.setFluctuateAlpha(false);
        tooltip.getParent().setPosition(
         ((GdxMaster.getWidth()-logPanel.getWidth()*0.88f) - tooltip.getWidth()) / 2,
            action? GDX.size(175, 0.2f) : GDX.size(200, 0.2f));
    }

    protected void hideTooltip(LabelX tooltip, float dur) {
        SuperContainer container= (SuperContainer) tooltip.getParent();
        ActorMaster.addFadeOutAction(tooltip, dur, false);
        if (container==null )
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

        if (!charsUp.contains(c)) {
            charsUp.add(c);
        }
        return super.keyUp(keyCode);
    }

    @Override
    public boolean keyDown(int keyCode) {
        DC_Game.game.getKeyManager().handleKeyDown(keyCode);
        return super.keyDown(keyCode);
    }

    @Override
    public boolean keyTyped(char character) {
        if ((int) character == 0)
            return false;
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

    public OutcomePanel getOutcomePanel() {
        return outcomePanel;
    }

    public void resetZIndices() {
        if (infoTooltipContainer != null)
            infoTooltipContainer.setZIndex(Integer.MAX_VALUE);
        if (actionTooltipContainer != null)
            actionTooltipContainer.setZIndex(Integer.MAX_VALUE);
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
}
