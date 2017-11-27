package main.libgdx.stage;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import main.game.core.game.DC_Game;
import main.libgdx.GdxMaster;
import main.libgdx.anims.ActorMaster;
import main.libgdx.bf.menu.GameMenu;
import main.libgdx.gui.controls.radial.RadialMenu;
import main.libgdx.gui.panels.dc.ButtonStyled;
import main.libgdx.gui.panels.dc.ButtonStyled.STD_BUTTON;
import main.libgdx.gui.panels.dc.InitiativePanel;
import main.libgdx.gui.panels.dc.actionpanel.ActionPanelController;
import main.libgdx.gui.panels.dc.inventory.InventoryWithAction;
import main.libgdx.gui.panels.dc.inventory.container.ContainerPanel;
import main.libgdx.gui.panels.dc.logpanel.FullLogPanel;
import main.libgdx.gui.panels.dc.logpanel.SimpleLogPanel;
import main.libgdx.gui.panels.dc.logpanel.text.TextPanel;
import main.libgdx.gui.panels.dc.menus.outcome.OutcomeDatasource;
import main.libgdx.gui.panels.dc.menus.outcome.OutcomePanel;
import main.libgdx.gui.panels.dc.unitinfo.UnitInfoPanel;
import main.libgdx.gui.tooltips.ToolTipManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.text.HelpMaster;

import java.util.ArrayList;
import java.util.List;

import static main.system.GuiEventType.SHOW_TEXT_CENTERED;

/**
 * Created by JustMe on 3/31/2017.
 */
public class BattleGuiStage extends Stage implements StageWithClosable {

    private final InitiativePanel initiativePanel;
    private final ActionPanelController bottomPanel;
    private final RadialMenu radial;
    private final ContainerPanel containerPanel;
    TextPanel textPanel;
    Closable displayedClosable;
    private GameMenu gameMenu;
    private OutcomePanel outcomePanel;
    private List<String> charsUp = new ArrayList<>();
    private char lastTyped;

    public BattleGuiStage(ScreenViewport viewport, Batch batch) {
        super(viewport == null ?
          new ScalingViewport(Scaling.stretch, GdxMaster.getWidth(),
           GdxMaster.getHeight(), new OrthographicCamera()) : viewport,
         batch == null ? new SpriteBatch() :
          batch);
        initiativePanel = new InitiativePanel();
        initiativePanel.setPosition(0, GdxMaster.getHeight() - initiativePanel.getHeight());
        addActor(initiativePanel);
        bottomPanel = new ActionPanelController(0, 0);
        addActor(bottomPanel);

        addActor(new UnitInfoPanel(0, 0));

        gameMenu = new GameMenu();
        addActor(gameMenu);
        gameMenu.setPosition(GdxMaster.centerWidth(gameMenu), GdxMaster.centerHeight(gameMenu));

        ButtonStyled menuButton = new ButtonStyled(STD_BUTTON.OPTIONS, () ->
         gameMenu.open());
        menuButton.setPosition(GdxMaster.getWidth() - menuButton.getWidth(),
         GdxMaster.getHeight() - menuButton.getHeight());
        addActor(menuButton);
        ButtonStyled helpButton = new ButtonStyled(STD_BUTTON.HELP, () ->
         GuiEventManager.trigger(SHOW_TEXT_CENTERED, HelpMaster.getHelpText()));
        helpButton.setPosition(menuButton.getX() - helpButton.getWidth(),
         GdxMaster.getHeight() - helpButton.getHeight());
        addActor(helpButton);


        InventoryWithAction inventoryForm = new InventoryWithAction();
        inventoryForm.setPosition(0, GdxMaster.getHeight() - inventoryForm.getHeight());
        this.addActor(inventoryForm);

        SimpleLogPanel log = new SimpleLogPanel();
        log.setPosition(GdxMaster.getWidth() - log.getWidth(), 0);
        addActor(log);

        addActor(new FullLogPanel(100, 200));

        radial = new RadialMenu();
        addActor(radial);
        addActor(new ToolTipManager(this));

        textPanel = new TextPanel();
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

    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.GAME_FINISHED, p -> {
            if (outcomePanel != null)
                outcomePanel.remove();
            outcomePanel = new OutcomePanel(new OutcomeDatasource((DC_Game) p.get()));
            addActor(outcomePanel);
            outcomePanel.setZIndex(getActors().size);
//            outcomePanel.setColor(new Color(1, 1, 1, 0));
//            ActorMaster.addFadeInOrOut(outcomePanel, 2.5f);
            float y = GdxMaster.getHeight() -
             (GdxMaster.getHeight() - outcomePanel.getHeight() / 2);
            float x = (GdxMaster.getWidth() - outcomePanel.getWidth()) / 2;
            outcomePanel.setPosition(x, y + outcomePanel.getHeight());
            ActorMaster.addMoveToAction(outcomePanel, x, y, 2.5f);
        });

        GuiEventManager.bind(SHOW_TEXT_CENTERED, p -> {
            showText((String) p.get());
        });
    }

    private void showText(String s) {
        if (s == null) {
            textPanel.close();
            return;
        }
        textPanel.setText(s);
        textPanel.open();
    }

    @Override
    public void act() {
        super.act();
        if (outcomePanel != null)
            outcomePanel.setZIndex(Integer.MAX_VALUE);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
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
            result = DC_Game.game.getKeyManager().handleKeyTyped(0, character);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result)
            return true;
        return super.keyTyped(character);
    }

/*    @Override
    public void draw() {
        final Matrix4 combined = getCamera().combined.cpy();
        getCamera().update();

        final Group root = getRoot();

        if (!root.isVisible()) return;

        combined.setToOrtho2D(0, 0, GdxMaster.getWidth(), GdxMaster.getHeight());

        Batch batch = this.getBatch();
        batch.setProjectionMatrix(combined);
        batch.begin();
        root.draw(batch, 1);
        batch.end();
    }*/

    public InitiativePanel getInitiativePanel() {
        return initiativePanel;
    }

    public ActionPanelController getBottomPanel() {
        return bottomPanel;
    }

    public RadialMenu getRadial() {
        return radial;
    }

    public GameMenu getGameMenu() {
        return gameMenu;
    }

    public TextPanel getTextPanel() {
        return textPanel;
    }

    public OutcomePanel getOutcomePanel() {
        return outcomePanel;
    }

    public void outsideClick() {
        if (textPanel.isVisible()) {
            textPanel.close();
        }
        if (containerPanel.isVisible()) {
            containerPanel.close();
        }
    }

}
