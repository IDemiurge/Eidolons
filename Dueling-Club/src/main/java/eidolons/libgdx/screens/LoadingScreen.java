package eidolons.libgdx.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import main.data.filesys.PathFinder;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 10/9/2018.
 */
public class LoadingScreen extends ScreenAdapter {
    private final Image background;
    private final Label label;
    private final SpriteBatch batch;

    public LoadingScreen() {
        background = new Image(new Texture(GDX.file(
         PathFinder.getImagePath() + "UI/logo fullscreen.png")));
        background.setPosition(GdxMaster.centerWidth(background),
         GdxMaster.centerHeight(background));

        label = new Label("Welcome. We are loading assets...",
         StyleHolder.getSizedLabelStyle(FONT.AVQ, 22));
        label.pack();
        label.setPosition(GdxMaster.centerWidth(label),
         GdxMaster.getHeight() / 20 + 35);

        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        batch.setColor(1,1,1,1);
        batch.begin();
        background.draw(batch, 1);
        label.draw(batch, 1);
        batch.end();
    }

}
