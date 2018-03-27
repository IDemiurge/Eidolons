package main.libgdx.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by JustMe on 3/27/2018.
 */
public class TextureTransformer {

    //must be a power of two size
    public void mirror(){
        Texture imgTexture = new Texture(Gdx.files.internal("badlogic.jpg"));
        imgTexture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        TextureRegion imgTextureRegion = new TextureRegion(imgTexture);
        imgTextureRegion.setRegion(0,0,imgTexture.getWidth()*3,imgTexture.getHeight()*3);
    }
}
