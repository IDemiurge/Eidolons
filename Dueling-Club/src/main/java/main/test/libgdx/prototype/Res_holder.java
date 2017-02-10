package main.test.libgdx.prototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import main.data.filesys.PathFinder;

/**
 * Created by PC on 07.11.2016.
 */
public class Res_holder {
    static Skin skin, exit_button_skin;
    static Button.ButtonStyle buttonStyle;
    static TextureAtlas textureAtlas,exit_button_atlas;
    static BitmapFont font,exit_button_font;
    static TextButton.TextButtonStyle exit_button_style;
    public static void load(){
        skin = new Skin();
//        textureAtlas = new TextureAtlas(Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT\\Atlas.atlas"));
        textureAtlas = new TextureAtlas(Gdx.files.internal(PathFinder.getImagePath() + "\\myFolder\\Atlas.atlas"));
        skin.addRegions(textureAtlas);
        buttonStyle = new Button.ButtonStyle();
        buttonStyle.up = skin.getDrawable("e1");
        buttonStyle.down = skin.getDrawable("e17");
        exit_button_skin = new Skin();
//        exit_button_atlas = new TextureAtlas(Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT\\exit_button.atlas"));
        exit_button_atlas = new TextureAtlas(Gdx.files.internal(PathFinder.getImagePath() + "\\myFolder\\exit_button.atlas"));
        exit_button_skin.addRegions(exit_button_atlas);
        exit_button_style = new TextButton.TextButtonStyle();
        exit_button_style.up = exit_button_skin.getDrawable("ex_on");
        exit_button_style.down = exit_button_skin.getDrawable("ex_off");

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(PathFinder.getFontPath() + "\\Starcraft.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.RED;
        parameter.borderWidth = 1.5f;
        parameter.borderColor = Color.GOLD;
        parameter.shadowColor = Color.DARK_GRAY;
        parameter.shadowOffsetX  = 3;
        parameter.shadowOffsetY  = 3;
        parameter.size = 60;
        parameter.hinting = FreeTypeFontGenerator.Hinting.Slight;
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.borderStraight = true;
        font = gen.generateFont(parameter);
        font.getData().setScale(0.75f);
        FreeTypeFontGenerator.FreeTypeFontParameter exit_par = new FreeTypeFontGenerator.FreeTypeFontParameter();
        exit_par.color = Color.RED;
        exit_par.borderWidth = 1.5f;
        exit_par.borderColor = Color.GOLD;
        exit_par.shadowColor = Color.DARK_GRAY;
        exit_par.shadowOffsetX  =3;
        exit_par.shadowOffsetY  =3;
        exit_par.size = 27;
        exit_par.magFilter = Texture.TextureFilter.Linear;
        exit_par.minFilter = Texture.TextureFilter.Linear;
        exit_button_font = gen.generateFont(exit_par);
        exit_button_style.font = exit_button_font;
    }
}
