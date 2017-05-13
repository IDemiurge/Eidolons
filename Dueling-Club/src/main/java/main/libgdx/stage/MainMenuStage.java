package main.libgdx.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
        menu.add(getTextButton("new game"));
        menu.row();
        menu.add(getTextButton("load game"));
        menu.row();

        final TextButton optionsButton = getTextButton("options");
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

        final TextButton exit = getTextButton("exit");
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

        final TextButton button = getTextButton("toggle full screen");
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

        final TextButton back = getTextButton("back");
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

    private TextButton getTextButton(String text) {
        final TextButton.TextButtonStyle customButtonStyle = StyleHolder.getCustomButtonStyle("UI/red_button.png");
        customButtonStyle.checkedFontColor = Color.WHITE;
        return new TextButton(text, customButtonStyle);
    }

    private void switchView(TablePanel next) {
        if (menu != next) {
            menu.setVisible(false);
        }

        if (options != next) {
            options.setVisible(false);
        }

        if (load != next) {
            options.setVisible(false);
        }

        next.setVisible(true);
        recalcPos();
    }

    private void recalcPos() {
        menu.setPosition(
                Gdx.graphics.getWidth() / 2 - menu.getPrefWidth() / 2,
                Gdx.graphics.getHeight() / 2 - menu.getPrefHeight() / 2
        );
        options.setPosition(
                Gdx.graphics.getWidth() / 2 - options.getPrefWidth() / 2,
                Gdx.graphics.getHeight() / 2 - options.getPrefHeight() / 2
        );
    }

    public void updateViewPort(int width, int height) {
        getViewport().update(width, height, true);
        getCamera().update();
        recalcPos();
    }
}
