package eidolons.libgdx.gui.panels.dc.topleft;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.generic.GearCluster;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.panels.dc.clock.ClockActor;
import eidolons.libgdx.texture.Images;
import main.system.graphics.FontMaster;

public class ClockPanel extends GroupX {

    Label timeLabel;
    Label roundLabel;
    protected GearCluster gears;
    protected ClockActor clock;
    protected FadeImageContainer light;

    public ClockPanel() {
//        DynamicTooltip tooltip = new DynamicTooltip(() -> "Time:" + NumberUtils.getFloatWithDigitsAfterPeriod(DC_Game.game.getLoop().getTime(), 1));
        setSize(192, 200);
        //gear bg mech
        ImageContainer bg;
        addActor(bg = new ImageContainer( (Images.TIME_BG)));
        GdxMaster.top(bg); //165 84
        bg.setY(bg.getY() +15);
        bg.setX(bg.getX() -5);

        addActor(gears = new GearCluster(1f));
        addActor(clock = new ClockActor());
        gears.setPosition(95, 17);
        clock.setX(GdxMaster.centerWidth(clock));
        clock.setX(clock.getX()-12);

        Label.LabelStyle style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 19);
        addActor(timeLabel = new Label("", style));
        style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 21);
        addActor(roundLabel = new Label("Round 1", style));
//        light = new FadeImageContainer(ShadowMap.SHADE_CELL.LIGHT_EMITTER.getTexturePath());
        timeLabel.pack();
        roundLabel.pack();
        timeLabel.setY(100+timeLabel.getHeight()+ 16);
        roundLabel.setY(timeLabel.getY()+roundLabel.getHeight() -4);
        clock.addListener(getClockListener());
    }
    @Override
    public void act(float delta) {
        super.act(delta);
        updateTime();
    }
    public void updateTime() {
        String time = "";
        if (ExplorationMaster.isExplorationOn()) {
            roundLabel.setText("Exploration");
            time = Eidolons.game.getDungeonMaster().getExplorationMaster().getTimeMaster().getDisplayedTime();
        } else {
            roundLabel.setText("Round "+Eidolons.game.getState().getRoundDisplayedNumber());
            time =
                            Eidolons.game.getTurnManager().getTimeString();
        }
        timeLabel.setText(time);
        timeLabel.pack();
        roundLabel.pack();
        timeLabel.setX(GdxMaster.centerWidth(timeLabel)-12);
        roundLabel.setX(GdxMaster.centerWidth(roundLabel)-12);
    }



    protected EventListener getClockListener() {

        return new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!GdxMaster.isWithin(event.getTarget(),
                        new Vector2(event.getStageX(), event.getStageY()), true))
                    return;
                if (button == 0) {
                    DC_Game.game.getLoop().togglePaused();
                }

                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 1) {
                    if (!DC_Game.game.getLoop().isPaused())
                        DC_Game.game.getDungeonMaster().
                                getExplorationMaster().
                                getTimeMaster().playerWaits();
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        };
    }

    public GearCluster getGears() {
        return gears;
    }
}
