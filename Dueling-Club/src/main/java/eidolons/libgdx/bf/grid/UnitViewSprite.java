package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.boss.BossUnit;
import eidolons.libgdx.bf.boss.sprite.SpriteModel;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.light.ShadowMap.SHADE_CELL;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.particles.EmitterPools;
import eidolons.libgdx.shaders.DarkShader;
import eidolons.libgdx.shaders.ShaderDrawer;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.FileManager;

/**
 * Created by JustMe on 11/28/2018.
 */
public class UnitViewSprite extends GridUnitView {
    public static final boolean randomEmitter = true;
    public static final boolean TEST_MODE = true;
    private   float height;
    private   float width;
    private FadeImageContainer glow;
    private EmitterActor emitter;

    SpriteModel spriteModel;


    public UnitViewSprite(UnitViewOptions o) {
        super(o);
//        GuiEventManager.bind(GuiEventType.ACTIVE_UNIT_SELECTED , p-> );
//        GuiEventManager.bind(GuiEventType.ACTION_RESOLVES , p-> );

        addActor(spriteModel = new SpriteModel(SpriteAnimationFactory.getSpriteAnimation(o.getSpritePath())));

        //queue view
        // on hover?
    }
    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        height= getUserObject().getHeight()* GridMaster.CELL_H;
        width= getUserObject().getWidth()* GridMaster.CELL_W;
    }

    @Override
    public BossUnit getUserObject() {
        return (BossUnit) super.getUserObject();
    }

    /**
     * Just some demarkation in case sprite fails?
     *
     * For targeting borders?
     *
     * @param portraitTexture
     * @param path
     * @return
     */
    @Override
    protected FadeImageContainer initPortrait(TextureRegion portraitTexture, String path) {
//        SpriteAnimation anim = SpriteAnimationFactory.getSpriteAnimation("sprites/unit/eldritch 5 6.png");
//        FadeSprite sprite = new FadeSprite(anim);
//        return sprite;
        //special background with alpha template?
        return new FadeImageContainer();
//        return super.initPortrait(portraitTexture, path);
    }

    protected EmitterActor createEmitter(String path) {
        path = PathFinder.getVfxAtlasPath() + path;
        if (randomEmitter)
            path = FileManager.getRandomFilePath(path);
        EmitterActor emitter = EmitterPools.getEmitterActor(path);
        initEmitter(emitter);

        return emitter;
    }

    private void initEmitter(EmitterActor emitter) {
        addActor(emitter);
        emitter.start();
        emitter.setZIndex(1);
        emitter.setPosition(getWidth() / 2, getHeight() / 2);

    }

    @Override
    public boolean isHoverResponsive() {
        return false;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    @Override
    protected void init(TextureRegion arrowTexture, int arrowRotation, Texture iconTexture, TextureRegion emblem) {
        emitter = createEmitter("unit/");
        GuiEventManager.bind(GuiEventType.GAME_RESUMED, p -> {
            emitter.remove();
            try {
                if (RandomWizard.random()) {
                    emitter = createEmitter("advanced");
                } else
                    emitter = createEmitter("spell/swirl");
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        });

        addActor(glow = new FadeImageContainer(SHADE_CELL.LIGHT_EMITTER.getTexturePath()));
        glow.setAlphaTemplate(ALPHA_TEMPLATE.HIGHLIGHT);
        super.init(arrowTexture, arrowRotation, iconTexture, emblem);
        glow.setScale(getWidth() / glow.getWidth());
        glow.setOrigin(glow.getWidth() / 2, glow.getHeight() / 2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (getOutline() == null || parentAlpha == ShaderDrawer.SUPER_DRAW)
            super.draw(batch, parentAlpha);
        else
            ShaderDrawer.drawWithCustomShader(this, batch, DarkShader.getDarkShader());
    }

    @Override
    public void act(float delta) {
        spriteModel.setPos(GridMaster.getCenteredPos(getUserObject().getOriginalCoordinates()));


        glow.setRotation(glow.getRotation() + 5 * delta);
        super.act(delta);
        emblemImage.setVisible(false);
        emblemLighting.setVisible(false);
        arrow.setVisible(false);
        if (border != null) {
            border.setVisible(false);
            float a = glow.getColor().a;
            glow.setColor(border.getColor());
            glow.getColor().a = a;
        }
        glow.setZIndex(0);
        modeImage.setVisible(false);

        emitter.setPosition(getWidth() / 2, getHeight() / 2);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        emitter.setPosition(x + getWidth() / 2, y + getHeight() / 2);
    }

    @Override
    public void reset() {
        super.reset();

    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getWidth() {
        return width ;
    }

    @Override
    public void setBorder(TextureRegion texture) {
        //what if our sprite is not really square?
        //custom shape could be used...

        if (texture == CellBorderManager.getTargetTexture()) {
//targeted
        }
        if (texture == CellBorderManager.getTeamcolorTexture()) {
//active
        }
        super.setBorder(texture);
    }

    @Override
    public void setTeamColorBorder(boolean teamColorBorder) {
        super.setTeamColorBorder(teamColorBorder);

    }
    @Override
    protected TextureRegion processPortraitTexture(TextureRegion texture, String path) {
        //sheet!
        return super.processPortraitTexture(texture, path);
    }

    @Override
    protected void setPortraitTexture(TextureRegion textureRegion) {
        //        super.setPortraitTexture(textureRegion);
    }

    @Override
    protected void setDefaultTexture() {
        //        super.setDefaultTexture();
    }

    @Override
    protected void initQueueView(UnitViewOptions o) {
        super.initQueueView(o);
    }

    @Override
    public FadeImageContainer getPortrait() {
        return super.getPortrait();
    }


}
