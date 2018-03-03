package main.libgdx.stage;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.core.game.DC_Game;
import main.libgdx.GdxMaster;
import main.libgdx.bf.menu.GameMenu;
import main.libgdx.gui.RollDecorator;
import main.libgdx.gui.RollDecorator.RollableGroup;
import main.libgdx.gui.controls.radial.RadialMenu;
import main.libgdx.gui.panels.dc.ButtonStyled;
import main.libgdx.gui.panels.dc.ButtonStyled.STD_BUTTON;
import main.libgdx.gui.panels.dc.inventory.container.ContainerPanel;
import main.libgdx.gui.panels.dc.logpanel.FullLogPanel;
import main.libgdx.gui.panels.dc.logpanel.SimpleLogPanel;
import main.libgdx.gui.panels.dc.logpanel.text.OverlayTextPanel;
import main.libgdx.gui.panels.dc.menus.outcome.OutcomePanel;
import main.libgdx.gui.tooltips.ToolTipManager;
import main.libgdx.screens.map.sfx.Blackout;
import main.libgdx.utils.TextInputPanel;
import main.system.GuiEventManager;

import java.util.ArrayList;
import java.util.List;

import static main.system.GuiEventType.SHOW_TEXT_CENTERED;

/**
 * Created by JustMe on 2/9/2018.
 */
public class GuiStage extends Stage implements StageWithClosable {

    protected RadialMenu radial;
    protected ContainerPanel containerPanel;
    protected OverlayTextPanel textPanel;
    protected Closable displayedClosable;
    protected GameMenu gameMenu;
    protected OutcomePanel outcomePanel;
    protected Blackout blackout;
    private TextInputPanel tf;
    private List<String> charsUp = new ArrayList<>();
    private char lastTyped;
    private ToolTipManager tooltips;

    public GuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);

    }

    public void blackout(float dur) {
        blackout.fadeOutAndBack(dur);
    }

    protected void init() {
        gameMenu = createGameMenu();
        addActor(gameMenu);
        gameMenu.setPosition(GdxMaster.centerWidth(gameMenu), GdxMaster.centerHeight(gameMenu));

        ButtonStyled menuButton = new ButtonStyled(STD_BUTTON.OPTIONS, () ->
         gameMenu.open());
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
        addActor(new FullLogPanel(100, 200));

        radial = new RadialMenu();
        addActor(radial);
        addActor(tooltips=new ToolTipManager(this));

        textPanel = new OverlayTextPanel();
        addActor(textPanel);
        textPanel.setPosition(GdxMaster.centerWidth(textPanel),
         GdxMaster.centerHeight(textPanel));
        textPanel.close();

        containerPanel = new ContainerPanel();
        addActor(containerPanel);
        containerPanel.setPosition(GdxMaster.centerWidth(containerPanel),
         GdxMaster.centerHeight(containerPanel));
        containerPanel.setVisible(false);
        bindEvents();

        gameMenu.setZIndex(Integer.MAX_VALUE);

        setDebugAll(false);
    }

    protected GameMenu createGameMenu() {
        return new GameMenu();
    }

    public boolean closeDisplayed() {
        if (getDisplayedClosable() == null)
            return false;
        getDisplayedClosable().close();
        displayedClosable = null;
        return true;
    }

    public Closable getDisplayedClosable() {
        return displayedClosable;
    }

    @Override
    public void setDisplayedClosable(Closable displayedClosable) {
        this.displayedClosable = displayedClosable;
    }

    protected void bindEvents() {
        GuiEventManager.bind(SHOW_TEXT_CENTERED, p -> {
            showText((String) p.get());
        });

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
        if (tooltips!=null )
        tooltips.setZIndex(Integer.MAX_VALUE);
        if (blackout!=null )
        blackout.setZIndex(Integer.MAX_VALUE);
    }

    public Blackout getBlackout() {
        return blackout;
    }
}
