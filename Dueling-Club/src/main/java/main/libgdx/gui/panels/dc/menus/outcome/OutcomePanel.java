package main.libgdx.gui.panels.dc.menus.outcome;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import main.libgdx.StyleHolder;
import main.libgdx.gui.panels.dc.ButtonStyled;
import main.libgdx.gui.panels.dc.ButtonStyled.STD_BUTTON;
import main.libgdx.gui.panels.dc.TabbedPanel;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.texture.TextureCache;
import main.swing.generic.components.G_Panel.VISUALS;

import static main.libgdx.texture.TextureCache.getOrCreateR;

/**
 * Created by JustMe on 8/15/2017.
 */
public class OutcomePanel extends TablePanel implements EventListener {
    private OutcomeDatasource datasource;
    private Image picture;
    private Label message;
    private  TabbedPanel unitStatTabs;
    Cell<ButtonStyled> doneButton;
    Cell<ButtonStyled> exitButton;
    Cell<ButtonStyled> continueButton;

    public OutcomePanel(OutcomeDatasource outcomeDatasource) {
//        setDebug(true);
addListener(this);
        TextureRegion textureRegion = new TextureRegion(getOrCreateR(VISUALS.END_PANEL.getImgPath()));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);

        datasource=outcomeDatasource;
        String imgPath="UI\\big\\victory.jpg";
        if (outcomeDatasource.getOutcome()!=null )
            imgPath = outcomeDatasource.getOutcome() ? "UI\\big\\victory.jpg" : "UI\\big\\defeat.jpg";
        picture = new Image(TextureCache.getOrCreateR(imgPath));

        addActor(picture);
        picture.setAlign(Align.center);

        String messageText="That's it";
        if (outcomeDatasource.getOutcome()!=null )
            messageText = outcomeDatasource.getOutcome() ? "Victory!" : "Defeat!";
        message = new Label(messageText, StyleHolder.getDefaultLabelStyle());
        addActor(message);
        message.setAlignment(Align.top);

        TablePanel<Actor> stats = new TablePanel<>();
        datasource.getPlayerStatsContainers().forEach(c->{
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

        addElement(buttonTable).pad(0, 20, 20, 20);
    }


    @Override
    public boolean handle(Event event) {
        return false;
    }
}
