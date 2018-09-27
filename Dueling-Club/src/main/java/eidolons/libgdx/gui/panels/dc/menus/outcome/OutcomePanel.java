package eidolons.libgdx.gui.panels.dc.menus.outcome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.gui.panels.TabbedPanel;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.secondary.Bools;
import main.system.graphics.FontMaster.FONT;
import main.system.graphics.MigMaster;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 8/15/2017.
 */
public class OutcomePanel extends TablePanel implements EventListener {
    public static final boolean TEST_MODE = false;
    public static final boolean TEST_OUTCOME = false;
    private static final String VICTORY_MESSAGE =
     "+++That's It - You are Victorious!+++";
    private static final String DEFEAT_MESSAGE =
     "***All is lost - the Enemy has Prevailed!***";
    Cell<TextButton> doneButton;
    Cell<TextButton> exitButton;
    Cell<TextButton> continueButton;
    private Boolean outcome;
    private OutcomeDatasource datasource;
    private Image picture;
    private Label message;
    private TabbedPanel unitStatTabs;

    public OutcomePanel(OutcomeDatasource outcomeDatasource) {
        addListener(this);
        Texture background = TiledNinePatchGenerator.getOrCreateNinePatch(
                NINE_PATCH.SAURON, BACKGROUND_NINE_PATCH.PATTERN, (int) GdxMaster.adjustSize(980), (int) GdxMaster.adjustSize(600));
        TextureRegion textureRegion =new TextureRegion(background);
        setBackground(new TextureRegionDrawable(textureRegion));

        outcome = outcomeDatasource.getOutcome();
        if (outcome == null)
            outcome = TEST_OUTCOME;

        datasource = outcomeDatasource;
        String imgPath = "UI\\big\\victory.png";
        if (outcome != null)
            imgPath = outcome ? "UI\\big\\victory.png" : "UI\\big\\defeat.jpg";
        picture = new Image(TextureCache.getOrCreateR(imgPath));

        addActor(picture);
        picture.setAlign(Align.center);
        picture.setScale(GdxMaster.getFontSizeModSquareRoot());
        picture.setPosition(
         MigMaster.center(textureRegion.getRegionWidth(), picture.getWidth()*picture.getScaleX()),
         MigMaster.center(textureRegion.getRegionHeight(), picture.getHeight()*picture.getScaleY()
         ));

        String messageText = VICTORY_MESSAGE;
        if (outcome != null)
            messageText = outcome ? VICTORY_MESSAGE : DEFEAT_MESSAGE;
        message = new Label(messageText, StyleHolder.getSizedColoredLabelStyle(0.25f, FONT.AVQ, 22));
        addActor(message);
        message.setAlignment(Align.top);
        message.setPosition(
         MigMaster.center(textureRegion.getRegionWidth(), message.getWidth()),
         MigMaster.top(textureRegion.getRegionHeight(), message.getHeight() + 55
         ));

        TablePanel<Actor> stats = new TablePanel<>();
        datasource.getPlayerStatsContainers().forEach(c -> {
            stats.addElement(c).fill(false).expand(0, 0).bottom()
             .size(150, 50);
            stats.row();
        });
        addElement(stats);
//        new ScrollPanel<>(stats);


        final TablePanel<TextButton> buttonTable = new TablePanel<>();

//        doneButton = buttonTable.addElement(
//         new TextButton(outcome ? "Next" : "Restart",
//          StyleHolder.getMenuTextButtonStyle(16))
//        ).fill(false).expand(0, 0).right()
//         .pad(20, 10, 20, 10);
//        doneButton.getActor().addListener(this);

        exitButton = buttonTable.addElement(
         new TextButton("Main Menu",
          StyleHolder.getMenuTextButtonStyle(18))
        ).fill(false).expand(0, 0).center()
         .pad(20, 10, 20, 10);
        exitButton.getActor().addListener(this);

//        continueButton = buttonTable.addElement(
//         new TextButton("Explore",
//          StyleHolder.getMenuTextButtonStyle(16))
//        ).fill(false).expand(0, 0).right()
//         .pad(20, 10, 20, 10);
////         .size(50, 30);
//        continueButton.getActor().addListener(this);
        addActor(buttonTable);
        buttonTable.setPosition(
         MigMaster.center(textureRegion.getRegionWidth(), buttonTable.getWidth()),
         55
        );
//        addElement(buttonTable).pad(0, 20, 20, 20);
    }

    @Override
    public boolean remove() {
        return super.remove();
    }

    @Override
    public boolean handle(Event e) {
        if (!(e instanceof InputEvent)) return true;
        InputEvent event = ((InputEvent) e);

        if (event.getType() != Type.touchDown)
            return true;
        if (getActions().size > 0) return true;
        Actor actor = event.getTarget();
        if (actor instanceof Label) {
            if (actor.getParent() instanceof TextButton) {
                ActorMaster.addMoveToAction(this, getX(), GdxMaster.getHeight(), 1.5f);
//                ActorMaster.addRemoveAfter(this);
                final Boolean exit_continue_next =true;
//                 doneButton.getActor().getLabel() == actor ? null :
//                  exitButton.getActor().getLabel() == actor;
//            ActorMaster.addAfter(this, new Action() {
//
//                @Override
//                public boolean act(float delta) {
                if (exit_continue_next == null) {
                    if (!ExplorationMaster.isExplorationOn())
                        Eidolons.getGame().getMaster().nextLevel();

                    if (!Bools.isTrue(outcome))
                        Eidolons.restart();
                    else
                    {

                        Eidolons.nextScenario();
                    }

                } else if (exit_continue_next) {
//                        if (DialogMaster.confirm("Must you really go?.."))
                    if (CoreEngine.isMacro()) {
                        GuiEventManager.trigger(GuiEventType.BATTLE_FINISHED);
                    } else {
                        Eidolons.exitToMenu();
                    }
//                    else DialogMaster.inform("Glad you're still with us! :)");

                } else {
                    WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_FINISHED,
                     false);
//                        DialogMaster.inform("Feel free to roam around, until next round...))");

                }
                remove();
                return true;
//                }
//            });
            }

        }


        return false;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
