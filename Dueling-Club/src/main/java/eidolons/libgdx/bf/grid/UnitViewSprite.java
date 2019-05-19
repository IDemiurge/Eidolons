package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.boss.entity.BossUnit;
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

import java.util.*;

/**
 * Created by JustMe on 11/28/2018.
 */
public class UnitViewSprite extends GridUnitView {
    public static final boolean randomEmitter = true;
    public static final boolean TEST_MODE = true;
    private float height;
    private float width;
    private FadeImageContainer glow;
    private Map<EmitterActor, Vector2> emitters= new LinkedHashMap<>();
    SpriteModel spriteModel;

    public UnitViewSprite(UnitViewOptions o) {
        super(o);
//        GuiEventManager.bind(GuiEventType.ACTIVE_UNIT_SELECTED , p-> );
//        GuiEventManager.bind(GuiEventType.ACTION_RESOLVES , p-> );
        addActor(spriteModel = new SpriteModel(SpriteAnimationFactory.getSpriteAnimation(o.getSpritePath()), o.getSpritePath()));
        //queue view
        // on hover?
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        height = getUserObject().getHeight() * GridMaster.CELL_H;
        width = getUserObject().getWidth() * GridMaster.CELL_W;
    }

    @Override
    public BossUnit getUserObject() {
        return (BossUnit) super.getUserObject();
    }

    @Override
    protected FadeImageContainer initPortrait(TextureRegion portraitTexture, String path) {
        return new FadeImageContainer();
    }

    protected EmitterActor createEmitter(String path) {
        path = PathFinder.getVfxAtlasPath() + path;
        EmitterActor emitter = EmitterPools.getEmitterActor(path);
        initEmitter(emitter, 0,0);
        return emitter;
    }

    private void initEmitter(EmitterActor emitter, int offsetX, int offsetY) {
        emitters.put(emitter, new Vector2(offsetX, offsetY));
        addActor(emitter);
        emitter.start();
        emitter.setZIndex(1);
        emitter.setPosition(getWidth() / 2 +offsetX, getHeight() / 2 + offsetY);

    }


    @Override
    protected void init(TextureRegion arrowTexture, int arrowRotation,
                        Texture iconTexture, TextureRegion emblem) {


        initEmitters();

        addActor(glow = new FadeImageContainer(SHADE_CELL.LIGHT_EMITTER.getTexturePath()));
        glow.setAlphaTemplate(ALPHA_TEMPLATE.HIGHLIGHT);
        super.init(arrowTexture, arrowRotation, iconTexture, emblem);
        glow.setScale(getWidth() / glow.getWidth());
        glow.setOrigin(glow.getWidth() / 2, glow.getHeight() / 2);
    }

    protected void initEmitters() {
         createEmitter("unit/");
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
        emitters.keySet().forEach(emitterActor -> emitterActor
                .setPosition(getWidth() / 2 + emitters.get(emitterActor).x,
                        getHeight() / 2+ emitters.get(emitterActor).y));
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        emitters.keySet().forEach(emitterActor -> emitterActor
                .setPosition(getWidth() / 2 + emitters.get(emitterActor).x,
                        getHeight() / 2+ emitters.get(emitterActor).y));
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
    protected void setPortraitTexture(TextureRegion textureRegion) {
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
