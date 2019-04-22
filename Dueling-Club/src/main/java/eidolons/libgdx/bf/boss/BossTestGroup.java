package eidolons.libgdx.bf.boss;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.tools.ktx.KTXProcessor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.bf.SpriteActor;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.stage.StageX;
import eidolons.libgdx.texture.SmartTextureAtlas;
import main.data.filesys.PathFinder;
import main.system.auxiliary.RandomWizard;

public class BossTestGroup extends GroupX {
    int nOfSprites = 700;
    int fps;
    String spritePath = "";
    String[] anims = {"great_sword_sword_thrust_anim_from",
            "great_sword_sweep_anim_from",
            "great_sword_twohanded_sword_swing_anim_from"};
    public BossTestGroup( ) {
        TextureAtlas atlas = new SmartTextureAtlas(
                PathFinder.getImagePath() +"test/great sword"
                +".txt");

        for (int i = 0; i < nOfSprites; i++) {
            for (String anim : anims) {
                addAnim(atlas, anim);
            }
        }

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for (Actor child : getChildren()) {
            child.setPosition(
                    child.getX()+delta*RandomWizard.getRandomIntBetween(-10, 10)
                    ,child.getY()+delta*RandomWizard.getRandomIntBetween(-10, 10)
            );
            child.setRotation(child.getRotation()+delta*10);
            child.setScale(2+delta*RandomWizard.getRandomIntBetween(-10, 10)
                    ,2+delta*RandomWizard.getRandomIntBetween(-10, 10)
            );
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public float getWidth() {
        return 1920;
    }

    @Override
    public float getHeight() {
        return 1080;
    }

    private void addAnim(TextureAtlas atlas, String animName) {
        Array<TextureAtlas.AtlasRegion> regs = atlas.findRegions(animName);
        SpriteActor sprite= new SpriteActor(new SpriteAnimation(0.1f, true, regs));
        sprite.setX(RandomWizard.getRandomInt(1600));
        sprite.setY(RandomWizard.getRandomInt(800));
        addActor(sprite);
        sprite.act(RandomWizard.getRandomInt(100));
    }

}
