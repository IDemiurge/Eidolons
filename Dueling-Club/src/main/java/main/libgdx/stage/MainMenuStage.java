package main.libgdx.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import main.libgdx.StyleHolder;
import main.libgdx.gui.panels.dc.TablePanel;

public class MainMenuStage extends Stage {
    private TablePanel menu;
    private TablePanel options;
    private TablePanel load;

    public MainMenuStage() {
        menu = new TablePanel();
        menu.left().bottom();
        menu.add(new TextButton("New game", StyleHolder.getCustomButtonStyle("UI/red_button.png")));
        menu.row();
        menu.add(new TextButton("load game", StyleHolder.getCustomButtonStyle("UI/red_button.png")));
        menu.row();

        final TextButton optionsButton = new TextButton("options", StyleHolder.getCustomButtonStyle("UI/red_button.png"));
        optionsButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                switchView(options);
            }
        });
        menu.add(optionsButton);
        menu.row();

        final TextButton exit = new TextButton("exit", StyleHolder.getCustomButtonStyle("UI/red_button.png"));
        exit.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.exit(0);//0(zero) is a normal exit status
            }
        });
        menu.add(exit);

        options = new TablePanel();
        options.left().bottom();
        final TextButton button = new TextButton("toggle full screen", StyleHolder.getCustomButtonStyle("UI/red_button.png"));
        button.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (Gdx.graphics.isFullscreen()) {
                    Gdx.graphics.setWindowedMode(1600, 900);
                } else {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                }
            }
        });


        options.add(button);
        options.row();

        final TextButton back = new TextButton("back", StyleHolder.getCustomButtonStyle("UI/red_button.png"));
        back.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                switchView(menu);
            }
        });
        this.options.add(back);

        load = new TablePanel();


        addActor(menu);
        addActor(options);
        addActor(load);

        switchView(menu);
    }

    private void switchView(TablePanel next) {
        if (menu != next) {
            menu.setVisible(false);
        } else {
            menu.setPosition(
                    Gdx.graphics.getWidth() / 2 - menu.getPrefWidth() / 2,
                    Gdx.graphics.getHeight() / 2 - menu.getPrefHeight() / 2
            );
        }

        if (options != next) {
            options.setVisible(false);
        } else {
            options.setPosition(
                    Gdx.graphics.getWidth() / 2 - options.getPrefWidth() / 2,
                    Gdx.graphics.getHeight() / 2 - options.getPrefHeight() / 2
            );
        }

        if (load != next) {
            options.setVisible(false);
        }

        next.setVisible(true);
    }
}
