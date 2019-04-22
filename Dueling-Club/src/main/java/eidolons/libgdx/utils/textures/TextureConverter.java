package eidolons.libgdx.utils.textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ETC1;
import com.badlogic.gdx.graphics.glutils.KTXTextureData;
import com.badlogic.gdx.tools.etc1.ETC1Compressor;
import com.badlogic.gdx.tools.ktx.KTXProcessor;
import com.badlogic.gdx.utils.Array;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.texture.TextureCache;
import eidolons.libgdx.texture.TextureManager;
import eidolons.system.utils.GdxUtil;
import main.data.filesys.PathFinder;
import main.system.launch.CoreEngine;

public class TextureConverter extends GdxUtil {
    private static final String PATH = "pydolons hc";
    private SpriteBatch spriteBatch;
    private Texture texture;
    private SpriteAnimation sprite;

    public static void main(String[] args) {
        CoreEngine.systemInit();
        new TextureConverter().start();
    }

    @Override
    protected int getWidth() {
        return 1920;
    }

    @Override
    protected int getHeight() {
        return 1080;
    }

    public  void convert(String raw){
        String path=PathFinder.getImagePath()+raw;

//        try {
//            ETC1Compressor.process(
//                    "C:\\drive\\[2019]\\Team\\AE\\tests\\uther\\moe\\medium",
//                    "C:\\drive\\[2019]\\Team\\AE\\tests\\uther\\moe\\medium\\etc1",
//                    false, true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        try {
//            KTXProcessor.convert(path+
//                            ".jpg", path+
//                            ".ktx", false,
//                    true, true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        KTXTextureData ktxTextureData=new KTXTextureData(GDX.file(path+".ktx"), false);
//         texture = new Texture(ktxTextureData);

        path=PathFinder.getImagePath()+"test/long sword";
        try {
            KTXProcessor.convert(path+
                            ".png", path+
                            ".ktx", false,
                    true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextureAtlas atlas = new TextureAtlas(GDX.file(path+".ktx"));
        Array<TextureAtlas.AtlasRegion> regs = atlas.findRegions("thrust");
        sprite = new SpriteAnimation(1, true, regs);


//        Pixmap pixmap = new Pixmap(GDX.file(path +
//                ".png"));
//        ETC1.encodeImagePKM(pixmap).write(Gdx.files.absolute(path +
//                ".etc1"));
//        TextureAtlas atlas = new TextureAtlas(GDX.file(path+".etc1"));
//        Array<TextureAtlas.AtlasRegion> regs = atlas.findRegions("thrust");
//        sprite = new SpriteAnimation(1, true, regs);
    }
    @Override
    public void render() {
        super.render();
        if (spriteBatch == null)
            spriteBatch = new SpriteBatch();

        spriteBatch.draw(texture,0,0);
        sprite.draw(spriteBatch);

    }

    @Override
    protected void execute() {
        convert( PATH);
    }
}
