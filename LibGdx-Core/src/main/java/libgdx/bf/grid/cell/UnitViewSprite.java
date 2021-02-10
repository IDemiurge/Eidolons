package libgdx.bf.grid.cell;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import eidolons.game.netherflame.boss.anims.old.SpriteModel;
import eidolons.game.netherflame.boss.logic.entity.BossUnit;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.bf.GridMaster;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.particles.EmitterActor;
import libgdx.particles.EmitterPools;
import libgdx.shaders.DarkShader;
import libgdx.shaders.ShaderDrawer;
import libgdx.bf.light.ShadowMap;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by JustMe on 11/28/2018.
 */
public abstract class UnitViewSprite extends UnitGridView {
    public static final boolean randomEmitter = true;
    public static final boolean TEST_MODE = false;
    private float height;
    private float width;
    private FadeImageContainer glow;
    private Map<EmitterActor, Vector2> emitters;
    SpriteModel spriteModel;

    public UnitViewSprite(UnitViewOptions o) {
        super(o);
//        GuiEventManager.bind(GuiEventType.ACTIVE_UNIT_SELECTED , p-> );
//        GuiEventManager.bind(GuiEventType.ACTION_RESOLVES , p-> );
        addActor(spriteModel = new SpriteModel(
                SpriteAnimationFactory.getSpriteAnimation(getSpritePath()), getName()));
        setUserObject(o.getObj());

//        spriteModel.setBlending(getBlending());
        //queue view
        // on hover?
    }

    protected String getSpritePath() {
        return null;
    }

    @Override
    public void init(UnitViewOptions o) {
        super.init(o);
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
//        if (userObject == null) {
//            userObject=
//        }
//        height = getUserObject().getHeight() * GridMaster.CELL_H;
//        width = getUserObject().getWidth() * GridMaster.CELL_W;
    }

    @Override
    public BossUnit getUserObject() {
        return (BossUnit) super.getUserObject();
    }

    @Override
    protected FadeImageContainer initPortrait(TextureRegion portraitTexture, String path) {
        return new FadeImageContainer();
    }

    protected EmitterActor createEmitter(String path, int offsetX, int offsetY) {
        path = PathFinder.getVfxAtlasPath() + path;
        EmitterActor emitter = EmitterPools.getEmitterActor(path);
        initEmitter(emitter, offsetX, offsetY);
        return emitter;
    }

    private void initEmitter(EmitterActor emitter, int offsetX, int offsetY) {
        if (emitters == null) {
            emitters = new LinkedHashMap<>();
        }
        emitters.put(emitter, new Vector2(offsetX, offsetY));
        addActor(emitter);
        emitter.start();
        emitter.setZIndex(1);
        emitter.setPosition(getWidth() / 2 + offsetX, getHeight() / 2 + offsetY);

    }


    protected void init(TextureRegion arrowTexture, int arrowRotation,
                        TextureRegion emblem) {


        initEmitters();

        addActor(glow = new FadeImageContainer(ShadowMap.SHADE_CELL.LIGHT_EMITTER.getTexturePath()));
        glow.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.HIGHLIGHT);
        super.init(arrowTexture, arrowRotation,  emblem);
        glow.setScale(getWidth() / glow.getWidth());
        glow.setOrigin(glow.getWidth() / 2, glow.getHeight() / 2);
    }

    protected void initEmitters() {
        createEmitter("unit/black soul bleed 3", 64, 64);
        createEmitter("unit/chaotic dark", 32, 32);
        createEmitter("unit/black soul bleed 3", -64, 64);
        createEmitter("unit/chaotic dark", -32, 32);
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
        if (spriteModel != null) {
            spriteModel.setPos(
                    GridMaster.getCenteredPos(getUserObject().getOriginalCoordinates()));
        }

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
        emitters.keySet().forEach(emitterActor -> emitterActor
                .setPosition(getWidth() / 2 + emitters.get(emitterActor).x,
                        getHeight() / 2 + emitters.get(emitterActor).y));
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        emitters.keySet().forEach(emitterActor -> emitterActor
                .setPosition(getWidth() / 2 + emitters.get(emitterActor).x,
                        getHeight() / 2 + emitters.get(emitterActor).y));
    }


    @Override
    public void setBorder(TextureRegion texture) {
        //what if our sprite is not really square?
        //custom shape could be used...

        if (texture == CellBorderManager.getTargetTexture()) {
//TODO targeted
        }
        if (texture == CellBorderManager.getTeamcolorTexture()) {
//TODO active
        }
        super.setBorder(texture);
    }


    @Override
    public boolean isHoverResponsive() {
        return false;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public SpriteModel getSpriteModel() {
        return spriteModel;
    }


    @Override
    protected void initQueueView(UnitViewOptions o) {
        super.initQueueView(o);
    }

    @Override
    public void setPortraitTexture(TextureRegion textureRegion) {
        //        super.setPortraitTexture(textureRegion);
    }

    @Override
    protected void setDefaultTexture() {
        //        super.setDefaultTexture();
    }

    @Override
    public FadeImageContainer getPortrait() {
        return super.getPortrait();
    }


}
