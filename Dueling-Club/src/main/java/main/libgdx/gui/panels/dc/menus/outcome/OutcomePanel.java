package main.libgdx.gui.panels.dc.menus.outcome;

import com.badlogic.gdx.Gdx;
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
import main.game.core.Eidolons;
import main.libgdx.StyleHolder;
import main.libgdx.anims.ActorMaster;
import main.libgdx.gui.panels.dc.TabbedPanel;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.texture.TextureCache;
import main.swing.generic.components.G_Panel.VISUALS;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.graphics.MigMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import static main.libgdx.texture.TextureCache.getOrCreateR;

/**
 * Created by JustMe on 8/15/2017.
 */
public class OutcomePanel extends TablePanel implements EventListener {
    public static final boolean TEST_MODE = false;
    private static final String VICTORY_MESSAGE =
     "+++That's It - You are Victorious!+++";
    private static final String DEFEAT_MESSAGE =
     "***Defeat - The Enemy has Prevailed!***";
    Cell<TextButton> doneButton;
    Cell<TextButton> exitButton;
    Cell<TextButton> continueButton;
    private Boolean outcome;
    private OutcomeDatasource datasource;
    private Image picture;
    private Label message;
    private TabbedPanel unitStatTabs;

    public OutcomePanel(OutcomeDatasource outcomeDatasource) {
//        setDebug(true);
        addListener(this);
        TextureRegion textureRegion = new TextureRegion(getOrCreateR(VISUALS.END_PANEL.getImgPath()));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);
        outcome = outcomeDatasource.getOutcome();
        if (outcome == null)
            outcome = true;

        datasource = outcomeDatasource;
        String imgPath = "UI\\big\\victory.png";
        if (outcome != null)
            imgPath = outcome ? "UI\\big\\victory.png" : "UI\\big\\defeat.jpg";
        picture = new Image(TextureCache.getOrCreateR(imgPath));

        addActor(picture);
        picture.setAlign(Align.center);

        picture.setPosition(
         MigMaster.center(textureRegion.getRegionWidth(), picture.getWidth()),
         MigMaster.center(textureRegion.getRegionHeight(), picture.getHeight()
         ));

        String messageText = VICTORY_MESSAGE;
        if (outcome != null)
            messageText = outcome ? VICTORY_MESSAGE : DEFEAT_MESSAGE;
        message = new Label(messageText, StyleHolder.getSizedLabelStyle(FONT.AVQ, 22));
        addActor(message);
        message.setAlignment(Align.top);
        message.setPosition(
         MigMaster.center(textureRegion.getRegionWidth(), message.getWidth()),
         MigMaster.top(textureRegion.getRegionHeight(), message.getHeight() - 55
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

        doneButton = buttonTable.addElement(
         new TextButton(outcome ? "Next" : "Restart",
          StyleHolder.getDefaultTextButtonStyle())
        ).fill(false).expand(0, 0).right()
         .pad(20, 10, 20, 10).size(50, 30);
        doneButton.getActor().addListener(this);

        exitButton = buttonTable.addElement(
         new TextButton("Exit",
          StyleHolder.getDefaultTextButtonStyle())
        ).fill(false).expand(0, 0).right()
//         .pad(20, 10, 20, 10)
         .size(50, 30);
        exitButton.getActor().addListener(this);

        continueButton = buttonTable.addElement(
         new TextButton("Explore",
          StyleHolder.getDefaultTextButtonStyle())
        ).fill(false).expand(0, 0).right()
         .pad(20, 10, 20, 10);
//         .size(50, 30);
        continueButton.getActor().addListener(this);
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
        if (getActions().size>0)            return true;        Actor actor = event.getTarget();
        if (actor instanceof Label) {
            if (actor.getParent() instanceof TextButton) {
                ActorMaster.addMoveToAction(this, getX(), Gdx.graphics.getHeight(), 1.5f);
//                ActorMaster.addRemoveAfter(this);
                final Boolean exit_continue_next =
                 doneButton.getActor().getLabel() == actor ? null :
                  exitButton.getActor().getLabel() == actor;
//            ActorMaster.addAfter(this, new Action() {
//
//                @Override
//                public boolean act(float delta) {
                    if (exit_continue_next == null ) {
                        Eidolons.getGame().getGameLoop().setSkippingToNext(true);
                        WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_INPUT,
                         null  );
                        WaitMaster.WAIT(100);
                        WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_FINISHED,
                         true);
                        //pan camera to main hero
                        // zoom?
                    } else if (exit_continue_next) {
                        if (DialogMaster.confirm("Must you really go?.."))
                        Gdx.app.exit();
                    else DialogMaster.inform("Glad you're still with us! :)");

                    } else   {
                        WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_FINISHED,
                         false);
                        DialogMaster.inform("Feel free to roam around, until next round...))");

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
