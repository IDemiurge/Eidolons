package main.libgdx.gui.panels.dc.menus.outcome;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import main.libgdx.StyleHolder;
import main.libgdx.anims.ActorMaster;
import main.libgdx.gui.panels.dc.ButtonStyled;
import main.libgdx.gui.panels.dc.ButtonStyled.STD_BUTTON;
import main.libgdx.gui.panels.dc.TabbedPanel;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.texture.TextureCache;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.graphics.FontMaster.FONT;

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
    Cell<ButtonStyled> doneButton;
    Cell<ButtonStyled> exitButton;
    Cell<ButtonStyled> continueButton;
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

        datasource = outcomeDatasource;
        String imgPath = "UI\\big\\victory.png";
        if (outcomeDatasource.getOutcome() != null)
            imgPath = outcomeDatasource.getOutcome() ? "UI\\big\\victory.png" : "UI\\big\\defeat.jpg";
        picture = new Image(TextureCache.getOrCreateR(imgPath));

        addActor(picture);
        picture.setAlign(Align.center);

        String messageText = VICTORY_MESSAGE;
        if (outcomeDatasource.getOutcome() != null)
            messageText = outcomeDatasource.getOutcome() ? VICTORY_MESSAGE : DEFEAT_MESSAGE;
        message = new Label(messageText, StyleHolder.getSizedLabelStyle(FONT.AVQ, 22));
        addActor(message);
        message.setAlignment(Align.top);

        TablePanel<Actor> stats = new TablePanel<>();
        datasource.getPlayerStatsContainers().forEach(c -> {
            stats.addElement(c).fill(false).expand(0, 0).bottom()
             .size(150, 50);
            stats.row();
        });
        addElement(stats);
//        new ScrollPanel<>(stats);


        final TablePanel<ButtonStyled> buttonTable = new TablePanel<>();

        doneButton = buttonTable.addElement(
         new ButtonStyled(STD_BUTTON.OK))
         .fill(false).expand(0, 0).right()
         .pad(20, 10, 20, 10).size(50, 50);

        doneButton.getActor().addListener(this);

        addElement(buttonTable).pad(0, 20, 20, 20);
    }

    @Override
    public boolean remove() {
        return super.remove();
    }

    @Override
    public boolean handle(Event e) {
        if (!(e instanceof InputEvent)) return true;
        InputEvent event = ((InputEvent) e);

        if (event.getType()!= Type.touchDown) return true;
        Actor actor = event.getTarget();
        if (doneButton.getActor() == actor) {
//            datasource.getHandler().done();
//            GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, );
            //credits?
            ActorMaster.addMoveToAction(this, getX(), Gdx.graphics.getHeight(), 1.5f);
            ActorMaster.addRemoveAfter(this);
            ActorMaster.addAfter(this, new Action() {
                @Override
                public boolean act(float delta) {
//                    Gdx.app.exit();
                    return false;
                }
            });
//            DungeonScreen.getInstance().getGridPanel()
//greyscale shader?
            //pan camera to main hero
            // zoom?
            //
        }

        return false;
    }
}
