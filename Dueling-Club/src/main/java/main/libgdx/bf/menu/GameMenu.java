package main.libgdx.bf.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import main.game.core.game.DC_Game;
import main.libgdx.GdxColorMaster;
import main.libgdx.StyleHolder;
import main.libgdx.gui.panels.dc.ButtonStyled.STD_BUTTON;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.stage.Closable;
import main.libgdx.stage.StageWithClosable;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/24/2017.
 */
public class GameMenu extends TablePanel implements Closable {
    public static boolean menuOpen;
    GameMenuHandler handler;

    public GameMenu() {
        Drawable texture = TextureCache.getOrCreateTextureRegionDrawable(StrPathBuilder.build(
         "UI", "components", "2017", "game menu", "background.png"));
        setBackground(texture);
        handler = new GameMenuHandler();
        pad(280, 80, 200, 80);
        for (GAME_MENU_ITEM sub : GAME_MENU_ITEM.values()) {
            TextButton button = new TextButton(
             StringMaster.getWellFormattedString(sub.name()),
             StyleHolder.getTextButtonStyle(STD_BUTTON.GAME_MENU,
              FONT.DARK, GdxColorMaster.GOLDEN_WHITE, 20));
            addElement(button).top().pad(10, 10, 10, 10);
            row();
            button.addListener(getClickListener(sub));
        }
        pack();
        setVisible(false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ShaderProgram bufferedShader = batch.getShader();
        if (batch.getShader() != null)
            batch.setShader(null);
        super.draw(batch, parentAlpha);
        if (batch.getShader() != null)
            batch.setShader(bufferedShader);
    }

    private EventListener getClickListener(GAME_MENU_ITEM sub) {
        return new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean result = handler.clicked(sub);
                if (result)
                    close();
                return super.touchDown(event, x, y, pointer, button);
            }
        };
    }

    public void open() {
        DC_Game.game.getLoop().setPaused(true);
        menuOpen = true;
        setVisible(true);
        if (getStage() instanceof StageWithClosable) {
            ((StageWithClosable) getStage()).closeDisplayed();
            ((StageWithClosable) getStage()).setDisplayedClosable(this);

        }

    }

    public void close() {
        DC_Game.game.getLoop().setPaused(false);
        menuOpen = false;
        setVisible(false);

    }

    public enum GAME_MENU_ITEM {
        HELP,
        HERO_INFO,
        FEEDBACK,
        OPTIONS,
        RESTART,
        PASS_TIME,
        RESUME,
    }
}
