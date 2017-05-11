package main.libgdx.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.libgdx.StyleHolder;
import main.libgdx.gui.panels.dc.TablePanel;

public class MainMenuStage extends Stage {
    private TablePanel menu;
    private TablePanel config;
    private TablePanel load;

    public MainMenuStage() {
        menu = new TablePanel();
        menu.add(new TextButton("New game", StyleHolder.getTextButtonStyle()));
        menu.row();
        menu.add(new TextButton("load game", StyleHolder.getTextButtonStyle()));
        menu.row();

        final TextButton options = new TextButton("options", StyleHolder.getTextButtonStyle());
        options.addCaptureListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

            }
        });
        menu.add(options);
        menu.row();

        final TextButton exit = new TextButton("exit", StyleHolder.getTextButtonStyle());
        exit.addCaptureListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.exit(0);  //0-normal exit status
            }
        });
        menu.add(exit);

        config = new TablePanel();
        final TextButton button = new TextButton("toggle full screen", StyleHolder.getTextButtonStyle());
        button.addCaptureListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (Gdx.graphics.isFullscreen()) {
                    Gdx.graphics.setWindowedMode(1600, 900);
                } else {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                }
            }
        });
        config.add(button);
        config.row();

        final TextButton back = new TextButton("back", StyleHolder.getTextButtonStyle());
        back.addCaptureListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                switchView(menu);
            }
        });
        config.add(back);

        load = new TablePanel();


        addActor(menu);
        addActor(config);
        addActor(load);
    }

    private void switchView(TablePanel next) {
        if (menu != next) {
            menu.setVisible(false);
        }

        if (config != next) {
            config.setVisible(false);
        }

        if (load != next) {
            config.setVisible(false);
        }

        next.setVisible(true);
    }
}
