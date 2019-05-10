package eidolons.libgdx.gui.panels.dc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisWindow;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.system.options.AnimationOptions;
import eidolons.system.options.GameplayOptions;
import eidolons.system.options.OptionsMaster;
import main.data.filesys.PathFinder;

public class SpeedControlPanel extends GroupX {

    private static final String BG_PATH = "ui/components/dc/queue/speed ctrl bg.png";
    private final VisSlider slider;
    private final ImageContainer background;
    SmartButton hideButton; // or roll?

    public SpeedControlPanel() {
        addActor(background = new ImageContainer(BG_PATH));
        if (!VisUI.isLoaded()) {
            VisUI.load(PathFinder.getSkinPath());
        }
        addActor(slider = new VisSlider(50, 400, 50, true));
        setSize(background.getWidth(), background.getHeight());
        GdxMaster.center(slider);
        slider.setValue(OptionsMaster.getGameplayOptions().
                getFloatValue(GameplayOptions.GAMEPLAY_OPTION.GAME_SPEED));

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int value = (int) slider.getValue();
                if (ExplorationMaster.isExplorationOn()) {
                    OptionsMaster.getAnimOptions().setValue(AnimationOptions.ANIMATION_OPTION.SPEED, value);
                } else {
                    OptionsMaster.getGameplayOptions().setValue(GameplayOptions.GAMEPLAY_OPTION.GAME_SPEED, value);
                }
//                Gdx.app.log("Options", option + " -> " + value);
            }
        });
//        addActor(slider = new VisCheckBox(0, 400, 50, true));
//        slider.setStyle(new Slider.SliderStyle());

    }

//    @Override
//    public void act(float delta) {
//        super.act(delta);
//    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (ExplorationMaster.isExplorationOn()) {
            slider.setValue(OptionsMaster.getAnimOptions().getFloatValue(
                    AnimationOptions.ANIMATION_OPTION.SPEED));
        } else {
            slider.setValue(OptionsMaster.getGameplayOptions().
                    getFloatValue(GameplayOptions.GAMEPLAY_OPTION.GAME_SPEED));
        }
        super.draw(batch, parentAlpha);
    }
}