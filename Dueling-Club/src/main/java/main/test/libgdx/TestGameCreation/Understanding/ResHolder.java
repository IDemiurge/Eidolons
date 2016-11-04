package main.test.libgdx.TestGameCreation.Understanding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by PC on 04.11.2016.
 */
public class ResHolder  {
    static Skin skin;
    static Button.ButtonStyle buttonStyle;
    static TextureAtlas textureAtlas;
    static BitmapFont font;
    public static void load(){
        skin = new Skin();
        textureAtlas = new TextureAtlas(Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT\\Atlas.atlas"));
        skin.addRegions(textureAtlas);
        buttonStyle = new Button.ButtonStyle();
        buttonStyle.up = skin.getDrawable("e1");
        buttonStyle.down = skin.getDrawable("e17");
//        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("D:\\NewRepos\\battlecraft\\resources\\res\\Fonts\\Starcraft.ttf"));
//        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
//        parameter.color = Color.CYAN;
//        parameter.borderWidth = 1.5f;
//        parameter.borderColor = Color.GOLD;
//        parameter.shadowColor = Color.DARK_GRAY;
//        parameter.shadowOffsetX  =3;
//        parameter.shadowOffsetY  =3;
//        parameter.size = 30;
//        parameter.magFilter = Texture.TextureFilter.Linear;
//        parameter.minFilter = Texture.TextureFilter.Linear;
//        font = gen.generateFont(parameter);
//        font.getData().setScale(0.045f);
    }

}
